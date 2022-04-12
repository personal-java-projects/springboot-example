package com.example.vto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("文件合并")
public class ComposeFile {

    @ApiModelProperty(value = "文件的md5", required = true)
    @NotNull(message = "md5不能为空")
    private String fileMd5;

    @ApiModelProperty(value = "文件名", required = true)
    @NotNull(message = "文件名不能为空")
    private String fileName;

    @ApiModelProperty(value = "分片数量", required = true)
    @NotNull(message = "分片数量不能为空")
    private int chunkCount;

    @ApiModelProperty(value = "要存储到哪个存储空间（存储桶）", required = true)
    @NotNull(message = "存储空间名不能为空")
    private String bucketName;

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
