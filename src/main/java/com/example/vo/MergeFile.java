package com.example.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class MergeFile {

    @ApiModelProperty(value = "文件名", required = true)
    @NotNull(message = "文件名不能为空")
    private String filename;

    @ApiModelProperty(value = "uploadId", required = true)
    @NotNull(message = "uploadId不能为空")
    private String uploadId;

    @ApiModelProperty(value = "存储桶", required = true)
    @NotNull(message = "存储桶不能为空")
    private String bucketName;

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
}
