package com.example.util;

import com.example.config.OssProperties;
import com.example.config.RestExceptionHandler;
import com.google.common.collect.Multimap;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

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
    public Map<String, Object> initMultiPartUpload(String bucketName, String objectName, int totalPart) {
        Map<String, Object> result = new HashMap<>();
        try {
            String uploadId = getUploadId(bucketName, null, objectName, null, null);

            result.put("uploadId", uploadId);

            List<String> partList = new ArrayList<>();
            Map<String, String> reqParams = new HashMap<>();

            //reqParams.put("response-content-type", "application/json");
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
        } catch (Exception e) {
            logger.error("error: {}", e.getMessage(), e);
            return null;
        }

        return result;
    }

    @SneakyThrows
    public ObjectWriteResponse mergeMultipartUpload(String bucketName, String objectName, String uploadId) {
        Part[] parts = new Part[10000];
        //此方法注意2020.02.04之前的minio服务端有bug
        ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
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
}
