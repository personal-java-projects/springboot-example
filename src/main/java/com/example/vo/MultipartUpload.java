package com.example.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("分片上传")
public class MultipartUpload {

    @ApiModelProperty(value = "分片数量", required = true)
    @NotNull(message = "分片数量不能为空")
    private int chunkCount;

    @ApiModelProperty(value = "文件的MD5", required = true)
    @NotNull(message = "MD5不能为空")
    private String fileMd5;
}
