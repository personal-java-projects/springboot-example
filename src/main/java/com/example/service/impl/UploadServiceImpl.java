package com.example.service.impl;

import com.example.mapper.FileMapper;
import com.example.pojo.FilePO;
import com.example.service.UploadService;
import com.example.util.MinioTemplate;
import com.google.common.collect.HashMultimap;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.ObjectWriteResponse;
import io.minio.errors.*;
import io.minio.messages.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("uploadService")
public class UploadServiceImpl implements UploadService {

    // 上传成功
    private int UPLOAD_SUCCESS_STATUS = 0;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private MinioTemplate minioTemplate;

    @Override
    public boolean fileExisted(FilePO uploadDto) {
        FilePO uploadedFile = fileMapper.selectFileByMD5(uploadDto.getFileMd5());
        if (uploadedFile != null) {
            return true;
        }

        return false;
    }

    @Override
    public FilePO getUploadFile(String md5) {
        FilePO uploadedFile = fileMapper.selectFileByMD5(md5);

        if (uploadedFile != null) {
            return uploadedFile;
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> getMultipartFile(String chunkBucKet, FilePO uploadDto) {
        //获取到该文件已上传分片
        Map<Integer, String> okChunkMap = minioTemplate.mapChunkObjectNames(chunkBucKet, uploadDto.getFileMd5());
        List<Map<String, Object>> chunkUploadUrls = new ArrayList<>();

        // 已上传部分分片
        if (!CollectionUtils.isEmpty(okChunkMap)) {
            for (int i=1; i<= uploadDto.getChunkCount(); i++) {
                // 判断当前分片是否已经上传过
                if (!okChunkMap.containsKey(i)) {
                    Map<String, Object> chunkMap = new HashMap<>();
                    // 生成分片上传url
                    chunkMap.put("partNumber", i);
                    chunkMap.put("uploadUrl", minioTemplate.createUploadChunkUrl(chunkBucKet, uploadDto.getFileMd5(), i));
                    chunkUploadUrls.add(chunkMap);
                }
            }

            return chunkUploadUrls;
        }

        // 未上传过分片
        if (CollectionUtils.isEmpty(okChunkMap)) {
            List<String> uploadUrls = minioTemplate.createUploadChunkUrlList(chunkBucKet, uploadDto.getFileMd5(), uploadDto.getChunkCount());
            for (int i = 1; i <= uploadUrls.size(); i++) {
                Map<String, Object> chunkMap = new HashMap<>();
                chunkMap.put("partNumber", i);
                chunkMap.put("uploadUrl", minioTemplate.createUploadChunkUrl(chunkBucKet, uploadDto.getFileMd5(), i));
                chunkUploadUrls.add(chunkMap);
            }

            return chunkUploadUrls;
        }

        return chunkUploadUrls;
    }

    @Override
    public Map<String, Object> getMultipartFile(String bucketName, String filename, Integer chunkSize) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException {
        // 1. 根据文件名创建签名
        Map<String, Object> result = new HashMap<>();

        // 2. 获取uploadId
        String contentType = "application/octet-stream";
        HashMultimap<String, String> headers = HashMultimap.create();
        headers.put("Content-Type", contentType);
        CreateMultipartUploadResponse response = minioTemplate.uploadId(bucketName, null, filename, headers, null);
        String uploadId = response.result().uploadId();
        result.put("uploadId", uploadId);

        // 3. 请求Minio 服务，获取每个分块带签名的上传URL
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("uploadId", uploadId);

        // 4. 循环分块数 从1开始,MinIO 存储服务定义分片索引却是从1开始的
        for (int i = 1; i <= chunkSize; i++) {
            reqParams.put("partNumber", String.valueOf(i));
            String uploadUrl = minioTemplate.createUploadUrl(bucketName, filename, reqParams);// 获取URL,主要这里前端上传的时候，要传递二进制流，而不是file
            result.put("chunk_" + (i - 1), uploadUrl); // 添加到集合
        }

        System.out.println("result: " + result);

        return result;
    }

    @Override
    public FilePO mergeFile(String chunkBucKet, String targetBucket, FilePO uploadDto, int userId) {
        //根据md5获取所有分片文件名称(minio的文件名称 = 文件path)
        List<String> chunks = minioTemplate.listChunkObjectNames(chunkBucKet, uploadDto.getFileMd5());

        //自定义文件名称
        String fileName = uploadDto.getFileName();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        //合并文件
        if (minioTemplate.composeObject(chunkBucKet, targetBucket, chunks, fileName)) {
            //获取文件访问外链(1小时过期)
            String url = minioTemplate.getObjectUrl(targetBucket, fileName, 60);
            FilePO currentFile = new FilePO();
            currentFile.setUploadStatus(UPLOAD_SUCCESS_STATUS);
            currentFile.setFileName(fileName);
            currentFile.setUploadUrl(url);
            currentFile.setSuffix(suffix);
            currentFile.setFileMd5(uploadDto.getFileMd5());
            currentFile.setChunkCount(uploadDto.getChunkCount());
            currentFile.setUserId(userId);

            //保存到数据库中
            fileMapper.insertFile(currentFile);

            return currentFile;
        }

        return null;
    }

    @Override
    public void mergeFile(String bucketName, String objectName, String uploadId) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException {
        Part[] parts = new Part[10000];
        int partNumber = 1; // 分片序列从1开始

        // 1. 查询分片
        ListPartsResponse partResult = minioTemplate.listMultipart(bucketName, null, objectName, 10000, 0, uploadId, null, null);
        System.err.println(partResult.result().partList().size() + "========================");

        // 2. 循环获取到的分片信息
        List<Part> partList = partResult.result().partList();
        for (int i = 0; i < partList.size(); i++) {
            // 3. 将分片标记传递给Part对象
            parts[partNumber - 1] = new Part(partNumber, partList.get(i).etag());
            partNumber++;
        }

        ObjectWriteResponse response = minioTemplate.completeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);

        String fileUrl = response.region();
        String suffix = response.object().substring(response.object().lastIndexOf("."));

        System.out.println("etag: " + response.etag() + "\nversionId: " + response.versionId() + "\nobject: " + response.object() + "\nbucket: " + response.bucket() + "\nregion: " + response.region());
    }
}
