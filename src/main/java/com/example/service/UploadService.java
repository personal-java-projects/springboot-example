package com.example.service;

import com.example.pojo.FilePO;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

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
    List<Map<String, Object>> getMultipartFile(String chunkBucKet, FilePO uploadDto);

    /**
     * 结合vue-simple-uploader的接口，获取分片url和uploadId
     * @param bucketName
     * @param filename
     * @param chunkSize
     * @return
     */
    Map<String, Object> getMultipartFile(String bucketName, String filename, Integer chunkSize) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException;

    /**
     * 合并分片文件并上传到存储文件的桶中
     * @param uploadDto
     * @return
     */
    FilePO mergeFile(String chunkBucKet, String targetBucket, FilePO uploadDto, int userId);

    /**
     * 结合vue-simple-uploader的合并分片
     * @param bucketName
     * @param objectName
     * @param uploadId
     */
    void mergeFile(String bucketName, String objectName, String uploadId) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException;
}
