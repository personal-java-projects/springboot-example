package com.example.service.impl;

import com.example.mapper.FileMapper;
import com.example.pojo.FilePO;
import com.example.service.UploadService;
import com.example.util.MinioTemplate;
import io.minio.ObjectWriteResponse;
import lombok.SneakyThrows;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.sql.Timestamp;
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

    private Date currentDate;

    @SneakyThrows
    static Map<String, Object> getParameter(String url) {
        Map<String, Object> map = new HashMap<>();
        final String charset = "utf-8";

        url = URLDecoder.decode(url, charset);

        if (url.indexOf('?') != -1) {
            final String contents = url.substring(url.indexOf('?') + 1);
            String[] keyValues = contents.split("&");
            for (int i = 0; i < keyValues.length; i++) {
                String key = keyValues[i].substring(0, keyValues[i].indexOf("="));
                String value = keyValues[i].substring(keyValues[i].indexOf("=") + 1);
                map.put(key, value);
            }
        }

        return map;
    }


    @Override
    public FilePO fileExisted(String md5) {
        FilePO file = fileMapper.selectFileByMD5(md5);

        return file;
    }

    @Override
    public Map<String, Object> getMultipartFile(String bucketName, String filename, int totalPart, String fileType) {

        Map<String, Object> partUpload = minioTemplate.initMultiPartUpload(bucketName, filename, totalPart, fileType);

        return partUpload;
    }

    @Override
    public String mergeFile(int userId, String md5, String bucketName, String objectName, String uploadId, int chunkCount) {
        ObjectWriteResponse mergedFile = minioTemplate.mergeMultipartUpload(bucketName, objectName, uploadId);

        String fileUrl = minioTemplate.preview(mergedFile.object());
        Map<String, Object> urlParams = getParameter(fileUrl);

        currentDate = new Date();
        Long currentDateTime = currentDate.getTime();
        Long expire = Long.parseLong((String) urlParams.get("X-Amz-Expires")) * 1000;
        Long expireTime = currentDateTime + expire;

        System.out.println("fileUrl: " + fileUrl);
        System.out.println("getParameter: " + getParameter(fileUrl));
        System.out.println("currentDate: " + currentDate);
        System.out.println("currentDateTime: " + currentDateTime);
        System.out.println("expire： " + expire);
        System.out.println("currentDateTime + expire: " + (currentDateTime + expire));
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
        currentFile.setExpireTime(expireTime);
        currentFile.setCreateTime(new Timestamp(currentDateTime));
        currentFile.setUpdateTime(new Timestamp(currentDateTime));

        //保存到数据库中
        fileMapper.insertFile(currentFile);

        return fileUrl;
    }

    @Override
    public void createMultipartDownload(String filename) {
        minioTemplate.MultipartDownload(filename);
    }
}
