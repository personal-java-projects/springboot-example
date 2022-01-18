package com.example.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(Integer chunkCount) {
        this.chunkCount = chunkCount;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(Integer uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    @Override
    public String toString() {
        return "FilePO{" +
                "id=" + id +
                ", userId=" + userId +
                ", chunkCount=" + chunkCount +
                ", fileMd5='" + fileMd5 + '\'' +
                ", suffix='" + suffix + '\'' +
                ", fileName='" + fileName + '\'' +
                ", uploadStatus=" + uploadStatus +
                ", uploadUrl='" + uploadUrl + '\'' +
                '}';
    }
}