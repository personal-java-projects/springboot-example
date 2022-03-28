package com.example.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class MultipartWithUploadId {

    @ApiModelProperty(value = "存储桶", required = true)
    @NotNull(message = "存储桶名不能为空")
    private String bucketName;

    @ApiModelProperty(value = "文件名", required = true)
    @NotNull(message = "文件名不能为空")
    private String filename;

    @ApiModelProperty(value = "分片大小", required = true)
    @NotNull(message = "分片大小不能为空")
    private Integer chunkSize;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }
}
