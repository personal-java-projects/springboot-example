package com.example.task;

import cn.hutool.core.date.DateUtil;
import com.example.config.OssProperties;
import com.example.enums.ScheduleStatus;
import com.example.enums.UploadStatus;
import com.example.ffmpeg.FFmpegProperties;
import com.example.ffmpeg.FFmpegUtils;
import com.example.ffmpeg.TranscodeConfig;
import com.example.mapper.FileMapper;
import com.example.mapper.VideoMapper;
import com.example.pojo.FilePO;
import com.example.pojo.Video;
import com.example.service.ScheduleService;
import com.example.service.UploadService;
import com.example.service.impl.VideoServiceImpl;
import com.example.util.FileUtils;
import com.example.util.MinioTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Component("transcodingTask")
public class TranscodingTask {

    private static final Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Autowired
    private FFmpegProperties ffmpegProperties;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private OssProperties ossProperties;

    @Resource(name = "minIOUploadTreadPool")
    private ThreadPoolTaskExecutor poolTaskExecutor;

    public void transcodingVideo(Integer id, Integer taskId, String poster, Integer tsSeconds) throws IOException, InterruptedException {
        TranscodeConfig transcodeConfig = new TranscodeConfig();
        transcodeConfig.setPoster(poster);
        transcodeConfig.setTsSeconds(String.valueOf(tsSeconds));

        Video video = videoMapper.selectVideoById(id);

        Map<String, Object> m3u8Info = convertVideo2M3u8(video, transcodeConfig);

        Path targetFolder = Paths.get(String.valueOf((Path) m3u8Info.get("targetFolder")), "ts");
        String m3u8Name = (String) m3u8Info.get("m3u8Name");

        int m3u8Id = uploadM3u8(targetFolder, m3u8Name, id);

        System.out.println("????????????>>> " + targetFolder.toFile().toString());

        if (m3u8Id != 0) {
            video.setM3u8Id(m3u8Id);
            video.setTranscoded(1);

            videoMapper.updateVideo(video);
            scheduleService.changeScheduleStatus(taskId, ScheduleStatus.PAUSE.ordinal());
        }
    }

    public int uploadM3u8 (Path path, String filename, int userId) throws InterruptedException, IOException {
        File allFile = path.toFile();
        File[] files = allFile.listFiles();
        MultipartFile[] multipartFile = new MultipartFile[1];

        if (null == files || files.length == 0) {
            return 0;
        }

        String patch = DateUtil.format(LocalDateTime.now(), "yyyy/MM/") + filename.substring(0, filename.lastIndexOf(".")) + "/";
        List<File> errorFile = new ArrayList<>();

        //????????????
        CountDownLatch countDownLatch = new CountDownLatch(files.length);
        Arrays.stream(files).forEach(li -> poolTaskExecutor.execute(() -> {
            try (FileInputStream fileInputStream = new FileInputStream(li)) {
                if (filename.indexOf(li.getName()) != -1) {
                    multipartFile[0] = FileUtils.fileToMultipartFile(li);
                }
                logger.info("?????????{} ????????????", li.getName(), fileInputStream.getChannel().size());
                minioTemplate.upload(patch + li.getName(), fileInputStream);
            } catch (Exception e) {
                errorFile.add(li);
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }));

        countDownLatch.await();

        logger.info("????????????????????????,?????????{} ?????????,?????????{},???????????? {}ms", files.length, errorFile.size());

        //????????????????????????
        poolTaskExecutor.execute(() -> {
            FileUtils.deleteFile(String.valueOf(path));
        });

        if (CollectionUtils.isEmpty(errorFile)) {
            String m3u8Url = minioTemplate.preview(ossProperties.getM3u8(), patch + filename);

            System.out.println("m3u8Url>>>" + m3u8Url);

            int fileId = (int) uploadService.insertFileInfo2Database(userId, DigestUtils.md5DigestAsHex(multipartFile[0].getInputStream()), filename, 1, m3u8Url).get("id");


            return fileId;
        }

        return 0;
    }

    public void cacheFile (Video video) throws IOException {
        String fileUrl = uploadService.getFileUrl(video.getVideoId());

        // ?????????????????????????????????????????????
        String filename = uploadService.getFileById(video.getVideoId()).getFileName();

        // ???????????????????????????
        String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        String path = FFmpegUtils.getProjectDir().concat("/").concat(ffmpegProperties.getM3u8Dir()).concat("/").concat(today).concat("/").concat(filename);

        // ????????????????????????
        Path targetFolder = Files.createFile(Paths.get(path));

        logger.info("??????>>>", targetFolder.toString());

        FileUtils.downloadNet(fileUrl, String.valueOf(targetFolder));
    }

    public void transcodingVideo(Integer id, Integer taskId, String poster, Integer tsSeconds, String cutStart, String cutEnd) throws IOException, InterruptedException {
        TranscodeConfig transcodeConfig = new TranscodeConfig();
        transcodeConfig.setPoster(poster);
        transcodeConfig.setTsSeconds(String.valueOf(tsSeconds));
        transcodeConfig.setCutStart(cutStart);
        transcodeConfig.setCutEnd(cutEnd);

        Video video = videoMapper.selectVideoById(id);

        Map<String, Object> m3u8Info = convertVideo2M3u8(video, transcodeConfig);

        Path targetFolder = Paths.get(String.valueOf((Path) m3u8Info.get("targetFolder")), "ts");
        String m3u8Name = (String) m3u8Info.get("m3u8Name");

        int m3u8Id = uploadM3u8(targetFolder, m3u8Name, id);

        if (m3u8Id != 0) {
            video.setM3u8Id(m3u8Id);
            video.setTranscoded(1);
            scheduleService.changeScheduleStatus(taskId, ScheduleStatus.PAUSE.ordinal());
        }
    }

    public Map<String, Object> convertVideo2M3u8(Video video, TranscodeConfig transcodeConfig) throws IOException, InterruptedException {
        logger.info("???????????????{}", transcodeConfig);

        // ?????????????????????????????????????????????
        String title = video.getVideoName();

        // ???????????????????????????
        String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());

        // ????????????????????????
        Path targetFolder = Files.createDirectories(Paths.get(FFmpegUtils.getProjectDir(), ffmpegProperties.getM3u8Dir(), today, title));

        logger.info("????????????????????????{}", targetFolder);
        Files.createDirectories(targetFolder);

        // ??????????????????
        logger.info("????????????");
        FFmpegUtils.transcodeToM3u8(title, uploadService.getFileUrl(video.getVideoId()), targetFolder.toString(), transcodeConfig);

        Map<String, Object> result = new HashMap<>();
        result.put("m3u8Name", title + ".m3u8");
        result.put("targetFolder", targetFolder);

        return result;
    }
}
