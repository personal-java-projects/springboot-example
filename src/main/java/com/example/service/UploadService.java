package com.example.service;

import com.example.pojo.FilePO;
import io.minio.ObjectWriteResponse;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface UploadService {
    /**
     * 文件是否已上传
     * @param md5
     * @return
     */
    FilePO fileExisted(String md5);

    String uploadSingleFile(MultipartFile file, int userId);

    /**
     * 结合vue-simple-uploader的接口，获取分片url和uploadId
     * @param bucketName
     * @param filename
     * @param totalPart
     * @return
     */
    Map<String, Object> getMultipartFile(String bucketName, String filename, int totalPart, String fileType) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException;

    /**
     * 结合vue-simple-uploader的合并分片
     * @param bucketName
     * @param objectName
     * @param uploadId
     */
    Map<String, Object> mergeFile(int userId, String md5, String bucketName, String objectName, String uploadId, int chunkCount) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException;

    Map<String, Object> insertFileInfo2Database(int userId, String md5, String objectName, int chunkCount, String fileUrl);
    String getFileUrl(int id);

    FilePO getFileById(int id);
}
