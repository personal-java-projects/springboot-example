package com.example.vto.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class MergeFile {

    @ApiModelProperty(value = "userId", required = true)
    @NotNull(message = "userId不能为空")
    private int userId;

    @ApiModelProperty(value = "md5", required = true)
    @NotNull(message = "md5不能为空")
    private String md5;

    @ApiModelProperty(value = "文件名", required = true)
    @NotNull(message = "文件名不能为空")
    private String filename;

    @ApiModelProperty(value = "uploadId", required = true)
    @NotNull(message = "uploadId不能为空")
    private String uploadId;

    @ApiModelProperty(value = "存储桶", required = true)
    @NotNull(message = "存储桶不能为空")
    private String bucketName;

    @ApiModelProperty(value = "分片数", required = true)
    @NotNull(message = "分片数不能为空")
    private int chunkCount;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }
}
