package com.example.util;

import com.example.config.OssProperties;
import com.example.config.RestExceptionHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 文件服务器工具类
 */
@Component
public class MinioTemplate {

    @Resource
    CustomMinioClient customMinioClient;

    @Resource
    OssProperties ossProperties;

    /**
     * 查看存储bucket是否存在
     * @return boolean
     */
    public Boolean bucketExists(String bucketName) {
        Boolean found;

        try {
            found = customMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }

        return found;
    }

    /**
     * 创建存储bucket
     * @return Boolean
     */
    public Boolean makeBucket(String bucketName) {
        try {
            customMinioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 不存在存储桶，则新建一个
     */
    @PostConstruct
    public void init() {
        //方便管理分片文件，则单独创建一个分片文件的存储桶
        if (!bucketExists(ossProperties.getChunkBucket())) {
            makeBucket(ossProperties.getChunkBucket());
        }

        if (!bucketExists(ossProperties.getDefaultBucket())) {
            makeBucket(ossProperties.getDefaultBucket());
        }
    }

    /**
     * 检查文件是否存在
     * @param filename
     * @return
     */
    @SneakyThrows
    public StatObjectResponse fileExited(String filename) {
        return customMinioClient.statObject(StatObjectArgs.builder().bucket(ossProperties.getDefaultBucket()).object(filename).build());
    }

    /**
     * 以流的形式获取一个文件对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = customMinioClient.getObjectInfo(customMinioClient, bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return customMinioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
                );
            }
        }
        return null;
    }

    /**
     *  上传分片上传请求，返回uploadId
     */
    @SneakyThrows
    public String getUploadId(String bucket, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) {
        return customMinioClient.createMultipartUpload(bucket, region, objectName, headers, extraQueryParams).result().uploadId();
    }

    /**
     *
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @param totalPart 总分片数
     * @return
     */
    @SneakyThrows
    public Map<String, Object> initMultiPartUpload(String bucketName, String objectName, int totalPart, String fileType) {
        Map<String, Object> result = new HashMap<>();

        // 设置header可以用来更改文件的content-type，用来避免MP4文件的链接被迅雷自动劫持，导致视频无法播放
        Multimap<String, String> headers= HashMultimap.create();
        if (fileType != null) {
            headers.put("Content-Type", fileType);
        }

        String uploadId = getUploadId(bucketName, null, objectName, headers, null);

        result.put("uploadId", uploadId);

        List<String> partList = new ArrayList<>();
        Map<String, String> reqParams = new HashMap<>();

//            reqParams.put("response-content-type", "application/json");
        reqParams.put("uploadId", uploadId);

        for (int i = 1; i <= totalPart; i++) {
            reqParams.put("partNumber", String.valueOf(i));

            String uploadUrl = customMinioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(1, TimeUnit.DAYS)
                            .extraQueryParams(reqParams)
                            .build());

            partList.add(uploadUrl);
        }

        result.put("uploadUrls", partList);

        return result;
    }

    /**
     * 分片合并
     * @param bucketName
     * @param objectName
     * @param uploadId
     * @return
     */
    @SneakyThrows
    public ObjectWriteResponse mergeMultipartUpload(String bucketName, String objectName, String uploadId) {
        Part[] parts = new Part[10000];
        //此方法注意2020.02.04之前的minio服务端有bug
        ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 10000, 0, uploadId, null, null);
        int partNumber = 1;

        for (Part part : partResult.result().partList()) {
            parts[partNumber - 1] = new Part(partNumber, part.etag());
            partNumber++;
        }

        ObjectWriteResponse completeMultipartUpload = customMinioClient.completeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);

        return completeMultipartUpload;
    }

    /**
     * 文件上传
     * @param file 文件
     * @return Boolean
     */
    @SneakyThrows
    public String upload(MultipartFile file) {
        // 修饰过的文件名 非源文件名
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM/dd");
        String dirName = dateFormat.format(new Date()) + "/";
        String fullPath = dirName + file.getOriginalFilename();

        PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(ossProperties.getDefaultBucket()).object(fullPath)
                .stream(file.getInputStream(),file.getSize(),-1).contentType(file.getContentType()).build();

        //文件名称相同会覆盖
        customMinioClient.putObject(objectArgs);

        return fullPath;
    }

    /**
     * 预览
     * @param fileName 是上传图片的fullPath=>eg:2021-12/27/typora-setup-x64.exe
     * @return
     */
    @SneakyThrows
    public String preview(String fileName) {
        customMinioClient.statObject(StatObjectArgs.builder().bucket(ossProperties.getDefaultBucket()).object(fileName).build());

        return customMinioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(ossProperties.getDefaultBucket()).expiry(7, TimeUnit.DAYS).object(fileName).build());
    }

    @SneakyThrows
    public void MultipartDownload(String filename) {
        StatObjectResponse file = fileExited(filename);

        if (file != null) {
            InputStream stream = customMinioClient.getObject(GetObjectArgs.builder().bucket(ossProperties.getDefaultBucket()).object(filename).offset(1024L).length(4096L).build());

            System.out.println("input: " + file.size() + "->");
        }
    }
}
