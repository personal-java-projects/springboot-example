package com.example.service.impl;

import com.example.enums.CronType;
import com.example.enums.ScheduleStatus;
import com.example.enums.VideoStatus;
import com.example.ffmpeg.FFmpegProperties;
import com.example.ffmpeg.FFmpegUtils;
import com.example.ffmpeg.TranscodeConfig;
import com.example.mapper.FileMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.VideoMapper;
import com.example.pojo.Schedule;
import com.example.pojo.User;
import com.example.pojo.Video;
import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.ScheduleModel;
import com.example.service.ScheduleService;
import com.example.service.UploadService;
import com.example.service.VideoService;
import com.example.util.Time2CronUtil;
import com.example.vto.dto.VideoDto;
import com.example.vto.po2Dto.Video2Dto;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("videoService")
public class VideoServiceImpl implements VideoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Autowired
    private Video2Dto video2Dto;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Autowired
    private FFmpegProperties ffmpegProperties;

    @Override
    @SneakyThrows
    public boolean publishVideo(Video video,String publishTime, int immediately) {

        if (video.getPublishTime() == null) {
            video.setPublishTime(new Date());
        }

        User user = userMapper.selectUserById(video.getUserId());

        video.setCreateTime(new Date());
        video.setUpdateTime(new Date());
        video.setUserNickname(user.getNickname());

        if (immediately == 0) {
            video.setStatus(VideoStatus.ON_SHELF.ordinal());

            videoMapper.insertVideo(video);
        }

        // 当属于定时发布
        if (immediately == 1) {
            video.setStatus(VideoStatus.OFF_SHELF.ordinal());

            videoMapper.insertVideo(video);
            
            StringBuffer stringBuffer = new StringBuffer();

            ScheduleModel scheduleModel = new ScheduleModel();
            scheduleModel.setJobType(CronType.DAY.getCode());
            scheduleModel.setStartDate(publishTime);

            // 创建对应的定时器对象
            Schedule schedule = new Schedule();
            schedule.setBeanName("task");
            schedule.setMethodName("timingPublish");
            String methodParams = stringBuffer.append(video.getId()).toString();
            schedule.setMethodParams(methodParams);
            String cronExpression = Time2CronUtil.createCronExpression(scheduleModel);
            schedule.setCronExpression(cronExpression);
            schedule.setStatus(ScheduleStatus.NORMAL.ordinal());
            schedule.setRemark("定时发布视频：" + video.getVideoName() + "，备注：" + video.getRemark());

            int scheduleId = scheduleService.addSchedule(schedule);

            stringBuffer.append(",").append(scheduleId);
            methodParams = stringBuffer.toString();
            schedule.setMethodParams(methodParams);

            scheduleService.editSchedule(schedule);
        }

        return true;
    }

    @Override
    public boolean auditVideo(int id, int audited) {
        Video video = videoMapper.selectVideoById(id);

        video.setAudited(audited);
        video.setUpdateTime(new Date());

        videoMapper.updateVideo(video);

        return true;
    }

    @Override
    public boolean changeSelf(int id, int status) {
        Video video = videoMapper.selectVideoById(id);

        video.setStatus(status);
        video.setUpdateTime(new Date());

        videoMapper.updateVideo(video);

        return true;
    }

    @Override
    public boolean deleteVideos(List<Integer> ids) {
        videoMapper.deleteVideos(ids);

        return true;
    }

    @Override
    @SneakyThrows
    public String convertVideo2M3u8(Video video, TranscodeConfig transcodeConfig) {
        LOGGER.info("转码配置：{}", transcodeConfig);

        // 原始文件名称，也就是视频的标题
        String title = video.getVideoName();

        // 按照日期生成子目录
        String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());

        // 尝试创建视频目录
        Path targetFolder = Files.createDirectories(Paths.get(FFmpegUtils.getProjectDir(), ffmpegProperties.getM3u8Dir(), today, title));

        LOGGER.info("创建文件夹目录：{}", targetFolder);
        Files.createDirectories(targetFolder);

        // 执行转码操作
        LOGGER.info("开始转码");
        FFmpegUtils.transcodeToM3u8(uploadService.getFileUrl(video.getVideoId()), targetFolder.toString(), transcodeConfig);

        // 封装结果
        Map<String, Object> videoInfo = new HashMap<>();
        videoInfo.put("title", title);
        videoInfo.put("m3u8", String.join("/", "", today, title, "index.m3u8"));
        videoInfo.put("poster", String.join("/", "", today, title, "poster.jpg"));

        return null;
    }

    @Override
    @SneakyThrows
    public List<VideoDto> getVideoByKeyword(String keyword) {
        List<Video> videos = videoMapper.selectVideoByKeyword(keyword);
        List<VideoDto> videoList = new ArrayList<>();

        for( Video video:videos ) {
            VideoDto videoDto = video2Dto.video2VideoDto(video);
            String fileUrl = uploadService.getFileUrl(video.getVideoId());

            videoDto.setVideoUrl(fileUrl);

//            TranscodeConfig transcodeConfig = new TranscodeConfig();
//            transcodeConfig.setPoster("00:10:00.000");
//            transcodeConfig.setTsSeconds("1000");

//            convertVideo2M3u8(video, transcodeConfig);

            videoList.add(videoDto);
        }

        return videoList;
    }
}
