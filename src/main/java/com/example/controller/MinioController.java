package com.example.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.config.OssProperties;
import com.example.pojo.FilePO;
import com.example.service.UploadService;
import com.example.util.MinioTemplate;
import com.example.util.ResponseResult;
import com.example.util.ResultCodeEnum;
import com.example.util.TokenUtil;
import com.example.vo.ComposeFile;
import com.example.vo.MergeFile;
import com.example.vo.MultipartUpload;
import com.example.vo.MultipartWithUploadId;
import com.example.voToPo.FileVoToPo;
import com.google.common.collect.HashMultimap;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Part;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "minio文件服务器")
@RestController
@RequestMapping("/minio")
@Slf4j
public class MinioController {

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private OssProperties ossProperties;

    @Autowired
    private FileVoToPo fileVoToPo;

    @Autowired
    private UploadService uploadService;

    /**
     * consumes： 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html, multipart/form-data;
     * produces: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回；
     * @param file
     * @return
     */
    @ApiOperation(value = "文件上传")
    @PostMapping(value = "/upload", consumes = { "multipart/form-data" })
    @ResponseBody
    public ResponseResult upload(MultipartFile file) {
        Map<String, Object> resultMap = new HashMap<>();

        System.out.println("file: " + file);
        String fullPath = minioTemplate.upload(file);
        String fileUrl = "";

        if (fullPath == "") {
            return ResponseResult.error().message("上传失败");
        }

        System.out.println("fullpath: " + fullPath);

//        fileUrl = ossProperties.getEndpoint() + "/" + ossProperties.getDefaultBucket() + "/" + fullPath;
        fileUrl = minioTemplate.preview(fullPath);

        resultMap.put("fileUrl", fileUrl);
        return ResponseResult.ok().data(resultMap).message("上传成功");
    }

    @ApiOperation(value = "图片预览")
    @GetMapping("/preview")
    public ResponseResult preview(String fileName) {
        Map<String, Object> resultMap = new HashMap<>();
        String previewUrl = minioTemplate.preview(fileName);

        System.out.println("preview: " + previewUrl);

        if (previewUrl == "") {
            return ResponseResult.error().message("图片不存在");
        }

        resultMap.put("previewUrl:", previewUrl);

        return ResponseResult.ok().data(resultMap);
    }

    @ApiOperation(value = "文件下载")
    @GetMapping("/download")
    public ResponseResult download(String fileName, HttpServletResponse res) {
        minioTemplate.download(fileName, res);

        return null;
    }

    /**
     * 初始化大文件上传
     * 未做优化，业务逻辑请根据实际情况修改
     *
     * @param MultipartUpload
     * @return
     */
    @ApiOperation("文件分片上传")
    @PostMapping("/init-chunk-upload")
    public ResponseResult initChunkUpload (@Valid @RequestBody MultipartUpload MultipartUpload) {
        // 校验文件md5，该文件是否上传过
        FilePO filePO = fileVoToPo.multipart(MultipartUpload);
        boolean exited = uploadService.fileExisted(filePO);
        Map<String, Object> resultMap = new HashMap<>();

        if (exited) {
            FilePO uploadFile = uploadService.getUploadFile(filePO.getFileMd5());

            resultMap.put("uploadStatus", 0);
            resultMap.put("uploadFile", uploadFile);

            return ResponseResult.ok().message("文件已上传").data(resultMap);
        }

        List<Map<String, Object>> chunkUploadUrls = uploadService.getMultipartFile(ossProperties.getChunkBucket(), filePO);

        if (chunkUploadUrls.size() == 0) {
            resultMap.put("uploadStatus", 2);
            resultMap.put("chunkUploadUrls", chunkUploadUrls);
            return ResponseResult.ok().message("分片上传成功，仅需合并").data(resultMap);
        }

        resultMap.put("uploadStatus", 1);
        resultMap.put("chunkUploadUrls", chunkUploadUrls);

        return ResponseResult.ok().message("分片部分上传").data(resultMap);
    }

    /**
     * 合并文件并返回文件信息
     *
     * @param composeFile
     * @return
     */
    @ApiOperation("上传完成之后，合并所有分片")
    @PostMapping("/compose-file")
    public ResponseResult composeFile(@RequestBody ComposeFile composeFile, @ApiParam(hidden = true) @RequestHeader("Authorization") String token) {
        DecodedJWT jwt = TokenUtil.parseToken(token);
        int userId = jwt.getClaim("id").asInt();

        FilePO filePO = fileVoToPo.composeFile(composeFile);

        FilePO currentFile = uploadService.mergeFile(ossProperties.getChunkBucket(), ossProperties.getDefaultBucket(), filePO, userId);

        if (currentFile == null) {
            return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("文件上传失败");
        }

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("uploadStatus", 0);
        resultMap.put("uploadFile", currentFile);

        return ResponseResult.ok().data(resultMap);
    }

    /**
     * 返回分片上传需要的签名数据URL及 uploadId
     * 前端结合vue-simple-uploader的分片上传接口
     *
     * @return
     */
    @PostMapping("/createMultipartUpload")
    @SneakyThrows
    public ResponseResult createMultipartUpload(@Valid @RequestBody MultipartWithUploadId multipartWithUploadId) {
        Map<String, Object> multipartFile = uploadService.getMultipartFile(multipartWithUploadId.getBucketName(), multipartWithUploadId.getFilename(), multipartWithUploadId.getChunkSize());

        if (multipartFile == null) {
            return ResponseResult.error().message("文件分片失败");
        }

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("chunkUrls", multipartFile);

        return ResponseResult.ok().data(resultMap);
    }

    /**
     * 分片上传完后合并
     *
     * @return
     */
    @PostMapping("/completeMultipartUpload")
    public ResponseResult completeMultipartUpload(@Valid @RequestBody MergeFile mergeFile) throws ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, IOException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException {
        uploadService.mergeFile(mergeFile.getBucketName(), mergeFile.getFilename(), mergeFile.getUploadId());
        return null;
    }
}
