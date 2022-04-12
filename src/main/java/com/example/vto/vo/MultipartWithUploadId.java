package com.example.vto.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class MultipartWithUploadId {

    @ApiModelProperty(value = "md5", required = true)
    @NotNull(message = "md5不能为空")
    private String md5;

    @ApiModelProperty(value = "存储桶", required = true)
    @NotNull(message = "存储桶名不能为空")
    private String bucketName;

    @ApiModelProperty(value = "文件名", required = true)
    @NotNull(message = "文件名不能为空")
    private String filename;

    @ApiModelProperty(value = "总分片数", required = true)
    @NotNull(message = "总分片数不能为空")
    private int totalPart;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

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

    public int getTotalPart() {
        return totalPart;
    }

    public void setTotalPart(int totalPart) {
        this.totalPart = totalPart;
    }
}
