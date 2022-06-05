package com.example.service.impl;

import cn.hutool.core.date.DateTime;
import com.example.config.OssProperties;
import com.example.enums.UploadStatus;
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

//        FilePO currentFile = new FilePO();
//
//        currentFile.setUploadStatus(UploadStatus.UPLOAD_SUCCESS.ordinal());
//        currentFile.setFileName(file.getOriginalFilename());
//        currentFile.setUploadUrl(fileUrl);
//        currentFile.setSuffix(suffix);
//        currentFile.setFileMd5(DigestUtils.md5DigestAsHex(file.getInputStream()));
//        currentFile.setChunkCount(1);
//        currentFile.setUserId(userId);
//        currentFile.setExpireTime(0L);
//        currentFile.setCreateTime(new Timestamp(currentDateTime));
//        currentFile.setUpdateTime(new Timestamp(currentDateTime));

        //保存到数据库中
//        fileMapper.insertFile(currentFile);

//        System.out.println("file: " + file);

        return insertFileInfo2Database(userId, DigestUtils.md5DigestAsHex(file.getInputStream()), file.getOriginalFilename(), 0, fileUrl).get("fileUrl").toString();
    }

    @Override
    public Map<String, Object> getMultipartFile(String bucketName, String filename, int totalPart, String fileType) {

        Map<String, Object> partUpload = minioTemplate.initMultiPartUpload(bucketName, filename, totalPart, fileType);

        return partUpload;
    }

    @Override
    @SneakyThrows
    public Map<String, Object> insertFileInfo2Database(int userId, String md5, String filename, int chunkCount, String fileUrl) {

        //自定义文件名称
        String suffix = filename.substring(filename.lastIndexOf("."));
        FilePO currentFile = new FilePO();

        // 将文件信息存储到数据库中
        currentFile.setUploadStatus(UploadStatus.UPLOAD_SUCCESS.ordinal());
        currentFile.setFileName(filename);
        currentFile.setUploadUrl(fileUrl);
        currentFile.setSuffix(suffix);
        currentFile.setFileMd5(md5);
        currentFile.setChunkCount(chunkCount);
        currentFile.setUserId(userId);
        if (HttpClientComponent.getParameter(fileUrl).get("X-Amz-Expires") != null) {
            currentFile.setExpireTime(handleFileExpireTime(fileUrl));
        } else {
            currentFile.setExpireTime(0L);
        }
        currentFile.setCreateTime(new Timestamp(new Date().getTime()));
        currentFile.setUpdateTime(new Timestamp(new Date().getTime()));

        //保存到数据库中
        fileMapper.insertFile(currentFile);

        Map<String, Object> result = new HashMap<>();

        int fileId = currentFile.getId();

        result.put("id", fileId);
        result.put("fileUrl", fileUrl);

        return result;
    }

    @Override
    public Map<String, Object> mergeFile(int userId, String md5, String bucketName, String objectName, String uploadId, int chunkCount) throws UnsupportedEncodingException {
        ObjectWriteResponse mergedFile = minioTemplate.mergeMultipartUpload(bucketName, objectName, uploadId);

        String fileUrl = minioTemplate.preview(bucketName, mergedFile.object());

//        Long currentDateTime = new Date().getTime();
//
//        //自定义文件名称
//        String suffix = objectName.substring(objectName.lastIndexOf("."));
//        FilePO currentFile = new FilePO();
//
//        // 将文件信息存储到数据库中
//        currentFile.setUploadStatus(UploadStatus.UPLOAD_SUCCESS.ordinal());
//        currentFile.setFileName(objectName);
//        currentFile.setUploadUrl(fileUrl);
//        currentFile.setSuffix(suffix);
//        currentFile.setFileMd5(md5);
//        currentFile.setChunkCount(chunkCount);
//        currentFile.setUserId(userId);
//        currentFile.setExpireTime(handleFileExpireTime(fileUrl));
//        currentFile.setCreateTime(new Timestamp(currentDateTime));
//        currentFile.setUpdateTime(new Timestamp(currentDateTime));
//
//        //保存到数据库中
//         fileMapper.insertFile(currentFile);
//
//         int fileId = currentFile.getId();
//
//        Map<String, Object> result = new HashMap<>();
//
//        result.put("id", fileId);
//        result.put("fileUrl", fileUrl);

        return insertFileInfo2Database(userId, md5, objectName, 0, fileUrl);
    }

    @Override
    @SneakyThrows
    public String getFileUrl(int id) {
        FilePO exitedFile = fileMapper.selectFileById(id);
        Long currentDateTime = new Date().getTime();

        // 链接过期,重新生成访问链接，并更新过期时间
        if (exitedFile.getExpireTime() < currentDateTime) {
            String fileUrl = minioTemplate.preview(ossProperties.getDefaultBucket(),exitedFile.getFileName());
            exitedFile.setUploadUrl(fileUrl);
            exitedFile.setExpireTime(handleFileExpireTime(fileUrl));
            exitedFile.setUpdateTime(new Timestamp(currentDateTime));

            fileMapper.updateFile(exitedFile);

            return fileUrl;
        }

        return exitedFile.getUploadUrl();
    }

    @Override
    public FilePO getFileById(int id) {
        return fileMapper.selectFileById(id);
    }
}
