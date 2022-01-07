package com.example.service;

import com.example.pojo.FilePO;

import java.util.List;

public interface UploadService {
    /**
     * 文件是否已上传
     * @param filePO
     * @return
     */
    boolean fileExisted(FilePO filePO);

    /**
     * 获取文件
     * @param md5 文件的md5
     * @return
     */
    FilePO getUploadFile(String md5);

    /**
     * 未上传 或者是已上传部分分片，获取到该文件已上传分片
     * @param chunkBucKet 存储分片的桶
     * @param uploadDto 要上传的文件
     * @return
     */
    List<FilePO> getMultipartFile(String chunkBucKet, FilePO uploadDto);


    /**
     * 合并分片文件并上传到存储文件的桶中
     * @param uploadDto
     * @return
     */
    FilePO mergeFile(String chunkBucKet, String targetBucket, FilePO uploadDto);
}
