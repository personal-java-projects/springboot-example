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
    public List<FilePO> getMultipartFile(String chunkBucKet, FilePO uploadDto) {
        //未上传 或者是已上传部分分片
        //获取到该文件已上传分片
        Map<Integer, String> okChunkMap = minioUtil.mapChunkObjectNames(chunkBucKet, uploadDto.getFileMd5());
        List<FilePO> chunkUploadUrls = new ArrayList<>();

        if (!CollectionUtils.isEmpty(okChunkMap)) {
            for (int i=1; i<= uploadDto.getChunkCount(); i++) {
                // 判断当前分片是否已经上传过
                if (!okChunkMap.containsKey(i)) {
                    // 生成分片上传url
                    FilePO multipartUrl = new FilePO();
                    multipartUrl.setPartNumber(i);
                    multipartUrl.setUploadUrl(minioUtil.createUploadChunkUrl(chunkBucKet, uploadDto.getFileMd5(), i));
                    chunkUploadUrls.add(multipartUrl);
                }
            }

            return chunkUploadUrls;
        }

        if (CollectionUtils.isEmpty(okChunkMap)) {
            List<String> uploadUrls = minioUtil.createUploadChunkUrlList(chunkBucKet, uploadDto.getFileMd5(), uploadDto.getChunkCount());
            for (int i = 1; i <= uploadUrls.size(); i++) {
                FilePO mulitpartUrl = new FilePO();
                mulitpartUrl.setPartNumber(i);
                mulitpartUrl.setUploadUrl(minioUtil.createUploadChunkUrl(chunkBucKet, uploadDto.getFileMd5(), i));
                chunkUploadUrls.add(mulitpartUrl);
            }

            return chunkUploadUrls;
        }

        return chunkUploadUrls;
    }

    @Override
    public FilePO mergeFile(String chunkBucKet, String targetBucket, FilePO uploadDto) {
        //根据md5获取所有分片文件名称(minio的文件名称 = 文件path)
        List<String> chunks = minioUtil.listObjectNames(chunkBucKet, uploadDto.getFileMd5());

        //自定义文件名称
        String fileName = uploadDto.getFileName();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        fileName = df.format(LocalDateTime.now()) + suffix;

        //合并文件
        if (minioUtil.composeObject(chunkBucKet, targetBucket, chunks, fileName)) {
            //获取文件访问外链(1小时过期)
            String url = minioUtil.getObjectUrl(targetBucket, fileName, 60);
            FilePO currentFile = new FilePO();
            currentFile.setUploadStatus(UPLOAD_SUCCESS_STATUS);
            currentFile.setFileName(fileName);
            currentFile.setFilePath(url);
            currentFile.setSuffix(suffix);
            currentFile.setFileMd5(uploadDto.getFileMd5());
            //保存到数据库中
            fileMapper.insertFile(currentFile);

            return currentFile;
        }
        return null;
    }
}
