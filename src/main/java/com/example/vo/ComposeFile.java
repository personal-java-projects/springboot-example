package com.example.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
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
}
