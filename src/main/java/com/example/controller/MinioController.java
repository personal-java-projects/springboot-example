package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.example.util.MinioUtil;
import com.example.util.ResponseResult;
import com.example.util.ResultCodeEnum;
import io.minio.messages.Bucket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "文件管理模块")
@RestController
@RequestMapping("/api/file/v1")
@Slf4j
public class MinioController {

    @Autowired
    private MinioUtil minioUtil;

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
}
