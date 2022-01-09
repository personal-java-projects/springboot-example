package com.example.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.pojo.FilePO;
import com.example.service.UploadService;
import com.example.util.MinioUtil;
import com.example.util.ResponseResult;
import com.example.util.ResultCodeEnum;
import com.example.util.TokenUtil;
import io.minio.messages.Bucket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "文件管理模块")
@RestController
@RequestMapping("/minio")
@Slf4j
public class MinioController {

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private UploadService uploadService;

    @Value("${minio.bucket.chunk}")
    private String chunkBucKet;

    @Value("${minio.bucket.bucketName}")
    private String bucketName;

    @ApiOperation(value = "查看存储bucket是否存在")
    @GetMapping("/bucketExists")
    public ResponseResult bucketExists(String bucketName) {
        System.out.println("bucketName: " + minioUtil.bucketExists(bucketName));
        boolean extised = minioUtil.bucketExists(bucketName);
        if (!extised) {
            return ResponseResult.ok().success(false).message("文件不存在");
        }

        return ResponseResult.ok().message("文件存在");
    }

    @ApiOperation(value = "创建存储bucket")
    @GetMapping("/makeBucket")
    public ResponseResult makeBucket(String bucketName) throws Exception {
        boolean created = minioUtil.makeBucket(bucketName);
        System.out.println("minioUtil.makeBucket(bucketName): " + minioUtil.makeBucket(bucketName));
        if (!created) {
//            throw new Exception("存储空间已存在");
            return ResponseResult.ok().success(false).message("存储空间已存在");
        }

        return ResponseResult.ok().message("创建成功");
    }

    @ApiOperation(value = "删除存储bucket")
    @DeleteMapping("/removeBucket")
    public ResponseResult removeBucket(String bucketName) {

        boolean removed = minioUtil.removeBucket(bucketName);

        if (!removed) {
            return ResponseResult.ok().success(false).message("删除失败，存储空间不存在");
        }

        return ResponseResult.ok().message("删除成功");
    }

    @ApiOperation(value = "获取全部bucket")
    @GetMapping("/getAllBuckets")
    public ResponseResult getAllBuckets() {
        List<Bucket> allBuckets = minioUtil.getAllBuckets();
        System.out.println("minioUtil.getAllBuckets(): " + allBuckets);
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("bucketsList", allBuckets);
        System.out.println("resultMap: " + resultMap);
        return ResponseResult.ok().data(resultMap);
    }

    /**
     * consumes： 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html, multipart/form-data;
     * produces: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回；
     * @param files
     * @return
     */
    @ApiOperation(value = "文件上传")
    @PostMapping(value = "/upload", consumes = { "multipart/form-data" })
    @ResponseBody
    public ResponseResult upload(MultipartFile[] files) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file: files) {
            System.out.println("file: " + file);
            String fullPath = minioUtil.upload(file);
            String fileUrl = "";

            if (fullPath == "") {
                return ResponseResult.error().message("上传失败");
            }

            fileUrl = minioUtil.preview(fullPath);
            fileUrls.add(fileUrl);
        }

        resultMap.put("fileUrls", fileUrls);
        return ResponseResult.ok().data(resultMap).message("上传成功");
    }

    @ApiOperation(value = "图片预览")
    @GetMapping("/preview")
    public ResponseResult preview(String fileName) {
        Map<String, Object> resultMap = new HashMap<>();
        String previewUrl = minioUtil.preview(fileName);

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
        minioUtil.download(fileName, res);

        return null;
    }

    @ApiOperation(value = "文件删除")
    @DeleteMapping("/remove")
    public ResponseResult remove(String fileName) {
        boolean deleted = minioUtil.remove(fileName);

        if (!deleted) {
            return ResponseResult.error().message("文件已删除或不存在");
        }

        return ResponseResult.ok().message("文件删除成功");
    }

    @ApiOperation(value = "获取所有文件")
    @GetMapping("/getAllContents")
    public ResponseResult getAllFiles() {

        minioUtil.listObjects();

        System.out.println("minioUtil.listObjects()：" + minioUtil.listObjects());

        return null;
    }

    /**
     * 初始化大文件上传
     * 未做优化，业务逻辑请根据实际情况修改
     *
     * @param uploadDto
     * @return
     */
    @PostMapping("/init-chunk-upload")
    public ResponseResult initChunkUpload (@RequestBody FilePO uploadDto) {
        // 校验文件md5，该文件是否上传过
        boolean exited = uploadService.fileExisted(uploadDto);
        Map<String, Object> resultMap = new HashMap<>();

        if (exited) {
            FilePO uploadFile = uploadService.getUploadFile(uploadDto.getFileMd5());

            resultMap.put("uploadStatus", 0);
            resultMap.put("uploadFile", uploadFile);

            return ResponseResult.ok().message("文件已上传").data(resultMap);
        }

        List<Map<String, Object>> chunkUploadUrls = uploadService.getMultipartFile(chunkBucKet, uploadDto);

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
     * @param uploadDto
     * @return
     */
    @PostMapping("/compose-file")
    public ResponseResult composeFile(@RequestBody FilePO uploadDto, @RequestHeader("Authorization") String token) {
        DecodedJWT jwt = TokenUtil.parseToken(token);
        int userId = jwt.getClaim("id").asInt();

        FilePO currentFile = uploadService.mergeFile(chunkBucKet, bucketName, uploadDto, userId);

        if (currentFile == null) {
            return ResponseResult.setResult(ResultCodeEnum.PARAM_ERROR).message("文件上传失败");
        }

        return ResponseResult.ok().data(currentFile);
    }
}
