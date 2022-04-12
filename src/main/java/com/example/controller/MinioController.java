package com.example.controller;

import com.example.config.OssProperties;
import com.example.service.UploadService;
import com.example.util.MinioTemplate;
import com.example.util.ResponseResult;
import com.example.vto.vo.MergeFile;
import com.example.vto.vo.MultipartWithUploadId;
import com.example.vto.voToPo.FileVoToPo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
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

        String fullPath = minioTemplate.upload(file);
        String fileUrl = "";

        System.out.println("file: " + file);

        if (fullPath == "") {
            return ResponseResult.error().message("上传失败");
        }

        System.out.println("fullpath: " + fullPath);

//        fileUrl = ossProperties.getEndpoint() + "/" + ossProperties.getDefaultBucket() + "/" + fullPath;
        fileUrl = minioTemplate.preview(fullPath);

        resultMap.put("fileUrl", fileUrl);
        return ResponseResult.ok().data(resultMap).message("上传成功");
    }

    @PostMapping("/initMultiPartUpload")
    @SneakyThrows
    public ResponseResult initMultiPartUpload (@Valid @RequestBody MultipartWithUploadId multipartWithUploadId) {
        Map<String, Object> multipartFile = uploadService.getMultipartFile(multipartWithUploadId.getBucketName(), multipartWithUploadId.getFilename(), multipartWithUploadId.getTotalPart());

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("chunkUrls", multipartFile);

        return ResponseResult.ok().data(resultMap);
    }

    @PostMapping("/mergeMultipartUpload")
    @SneakyThrows
    public ResponseResult mergeMultipartUpload (@Valid @RequestBody MergeFile mergeFile) {
        String fileUrl = uploadService.mergeFile(mergeFile.getUserId(), mergeFile.getMd5(), mergeFile.getBucketName(), mergeFile.getFilename(), mergeFile.getUploadId(), mergeFile.getChunkCount());

        Map<String, Object> resultMap = new HashMap<>();



//        resultMap.put("fileUrl", mergedFile.region());

        resultMap.put("fileUrl", fileUrl);

        return ResponseResult.ok().data(resultMap);
    }
}
