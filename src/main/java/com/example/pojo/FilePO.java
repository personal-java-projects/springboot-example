package com.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties
public class FilePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    private int id;

    /**
     * 上传文件的用户的id
     */
    private int userId;

    /**
     * 分片数量
     */
    private Integer chunkCount;

    /**
     * 上传文件的md5
     */
    private String fileMd5;

    /**
     * 上传文件/合并文件的格式
     */
    private String suffix;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 上传状态 0.上传完成而且合并成功   1.已上传部分  2 分片全部上传完成仅需合并
     */
    private Integer uploadStatus;

    /**
     * 上传地址
     */
    private String uploadUrl;
}