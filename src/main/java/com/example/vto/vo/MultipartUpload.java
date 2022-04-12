package com.example.vto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("分片上传")
public class MultipartUpload {

    @ApiModelProperty(value = "分片数量", required = true)
    @NotNull(message = "分片数量不能为空")
    private int chunkCount;

    @ApiModelProperty(value = "文件的MD5", required = true)
    @NotNull(message = "MD5不能为空")
    private String fileMd5;

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }
}
