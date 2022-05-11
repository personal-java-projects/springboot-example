package com.example.service.impl;

import cn.hutool.core.date.DateTime;
import com.example.config.OssProperties;
import com.example.mapper.FileMapper;
import com.example.pojo.FilePO;
import com.example.service.UploadService;
import com.example.util.DateFormatUtil;
import com.example.util.HttpClientComponent;
import com.example.util.MinioTemplate;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import io.minio.ObjectWriteResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("uploadService")
public class UploadServiceImpl implements UploadService {

    // 上传成功
    private int UPLOAD_SUCCESS_STATUS = 0;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private OssProperties ossProperties;

    private Date currentDate;

    private Long handleFileExpireTime(String fileUrl) throws UnsupportedEncodingException {
        Map<String, Object> urlParams = HttpClientComponent.getParameter(fileUrl);

        currentDate = new Date();
        Long currentDateTime = currentDate.getTime();
        Long expire = Long.parseLong((String) urlParams.get("X-Amz-Expires")) * 1000;
        Long expireTime = currentDateTime + expire;

        return expireTime;
    }

    @Override
    public FilePO fileExisted(String md5) {
        FilePO file = fileMapper.selectFileByMD5(md5);

        return file;
    }

    @Override
    @SneakyThrows
    public String uploadSingleFile(MultipartFile file, int userId) {

        FilePO exitedFile = fileMapper.selectFileByMD5(DigestUtils.md5DigestAsHex(file.getInputStream()));

        if (exitedFile != null) {
            return exitedFile.getUploadUrl();
        }

        String fullPath = minioTemplate.upload(file);
        String fileUrl = "";

        Long currentDateTime = new Date().getTime();
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        fileUrl = ossProperties.getEndpoint() + "/" + ossProperties.getDefaultBucket() + "/" + fullPath;

        FilePO currentFile = new FilePO();

        currentFile.setUploadStatus(UPLOAD_SUCCESS_STATUS);
        currentFile.setFileName(file.getOriginalFilename());
        currentFile.setUploadUrl(fileUrl);
        currentFile.setSuffix(suffix);
        currentFile.setFileMd5(DigestUtils.md5DigestAsHex(file.getInputStream()));
        currentFile.setChunkCount(1);
        currentFile.setUserId(userId);
        currentFile.setExpireTime(0L);
        currentFile.setCreateTime(new Timestamp(currentDateTime));
        currentFile.setUpdateTime(new Timestamp(currentDateTime));

        //保存到数据库中
        fileMapper.insertFile(currentFile);

        System.out.println("file: " + file);

        return fileUrl;
    }

    @Override
    public Map<String, Object> getMultipartFile(String bucketName, String filename, int totalPart, String fileType) {

        Map<String, Object> partUpload = minioTemplate.initMultiPartUpload(bucketName, filename, totalPart, fileType);

        return partUpload;
    }

    @Override
    public Map<String, Object> mergeFile(int userId, String md5, String bucketName, String objectName, String uploadId, int chunkCount) throws UnsupportedEncodingException {
        ObjectWriteResponse mergedFile = minioTemplate.mergeMultipartUpload(bucketName, objectName, uploadId);

        String fileUrl = minioTemplate.preview(mergedFile.object());

        Long currentDateTime = new Date().getTime();

        //自定义文件名称
        String suffix = objectName.substring(objectName.lastIndexOf("."));
        FilePO currentFile = new FilePO();

        // 将文件信息存储到数据库中
        currentFile.setUploadStatus(UPLOAD_SUCCESS_STATUS);
        currentFile.setFileName(objectName);
        currentFile.setUploadUrl(fileUrl);
        currentFile.setSuffix(suffix);
        currentFile.setFileMd5(md5);
        currentFile.setChunkCount(chunkCount);
        currentFile.setUserId(userId);
        currentFile.setExpireTime(handleFileExpireTime(fileUrl));
        currentFile.setCreateTime(new Timestamp(currentDateTime));
        currentFile.setUpdateTime(new Timestamp(currentDateTime));

        //保存到数据库中
         fileMapper.insertFile(currentFile);

         int fileId = currentFile.getId();

        Map<String, Object> result = new HashMap<>();

        result.put("id", fileId);
        result.put("fileUrl", fileUrl);

        return result;
    }

    @Override
    @SneakyThrows
    public String getFileUrl(int id) {
        FilePO exitedFile = fileMapper.selectFileById(id);
        Long currentDateTime = new Date().getTime();

        // 链接过期,重新生成访问链接，并更新过期时间
        if (exitedFile.getExpireTime() < currentDateTime) {
            String fileUrl = minioTemplate.preview(exitedFile.getFileName());
            exitedFile.setUploadUrl(fileUrl);
            exitedFile.setExpireTime(handleFileExpireTime(fileUrl));
            exitedFile.setUpdateTime(new Timestamp(currentDateTime));

            fileMapper.updateFile(exitedFile);

            return fileUrl;
        }

        return exitedFile.getUploadUrl();
    }

    @Override
    public void createMultipartDownload(String filename) {
        minioTemplate.MultipartDownload(filename);
    }
}
