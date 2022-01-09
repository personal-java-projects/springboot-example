package com.example.service.impl;

import com.example.mapper.FileMapper;
import com.example.pojo.FilePO;
import com.example.service.UploadService;
import com.example.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private MinioUtil minioUtil;

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
        Map<Integer, String> okChunkMap = minioUtil.mapChunkObjectNames(chunkBucKet, uploadDto.getFileMd5());
        List<Map<String, Object>> chunkUploadUrls = new ArrayList<>();

        // 已上传部分分片
        if (!CollectionUtils.isEmpty(okChunkMap)) {
            for (int i=1; i<= uploadDto.getChunkCount(); i++) {
                // 判断当前分片是否已经上传过
                if (!okChunkMap.containsKey(i)) {
                    Map<String, Object> chunkMap = new HashMap<>();
                    // 生成分片上传url
                    chunkMap.put("partNumber", i);
                    chunkMap.put("uploadUrl", minioUtil.createUploadChunkUrl(chunkBucKet, uploadDto.getFileMd5(), i));
                    chunkUploadUrls.add(chunkMap);
                }
            }

            return chunkUploadUrls;
        }

        // 未上传过分片
        if (CollectionUtils.isEmpty(okChunkMap)) {
            List<String> uploadUrls = minioUtil.createUploadChunkUrlList(chunkBucKet, uploadDto.getFileMd5(), uploadDto.getChunkCount());
            for (int i = 1; i <= uploadUrls.size(); i++) {
                Map<String, Object> chunkMap = new HashMap<>();
                chunkMap.put("partNumber", i);
                chunkMap.put("uploadUrl", minioUtil.createUploadChunkUrl(chunkBucKet, uploadDto.getFileMd5(), i));
                chunkUploadUrls.add(chunkMap);
            }

            return chunkUploadUrls;
        }

        return chunkUploadUrls;
    }

    @Override
    public FilePO mergeFile(String chunkBucKet, String targetBucket, FilePO uploadDto, int userId) {
        //根据md5获取所有分片文件名称(minio的文件名称 = 文件path)
        List<String> chunks = minioUtil.listChunkObjectNames(chunkBucKet, uploadDto.getFileMd5());

        //自定义文件名称
        String fileName = uploadDto.getFileName();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        //合并文件
        if (minioUtil.composeObject(chunkBucKet, targetBucket, chunks, fileName)) {
            //获取文件访问外链(1小时过期)
            String url = minioUtil.getObjectUrl(targetBucket, fileName, 60);
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
}
