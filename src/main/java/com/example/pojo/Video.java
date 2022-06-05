package com.example.pojo;

import java.io.Serializable;
import java.util.Date;

public class Video implements Serializable {
    private int id;

    private int userId;

    private String userNickname;

    private String videoName;

    private Integer videoId;

    private Integer m3u8Id;

    private String coverUrl;

    private int category;

    private int transcoded;

    private Integer status;

    private int audited;

    private String remark;

    private long views;

    private long stars;

    private long downloads;

    private double duration;

    private double currentTime;

    private Date publishTime;

    private Date createTime;

    private Date updateTime;

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

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public Integer getM3u8Id() {
        return m3u8Id;
    }

    public void setM3u8Id(Integer m3u8Id) {
        this.m3u8Id = m3u8Id;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getTranscoded() {
        return transcoded;
    }

    public void setTranscoded(int transcoded) {
        this.transcoded = transcoded;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getAudited() {
        return audited;
    }

    public void setAudited(int audited) {
        this.audited = audited;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public long getStars() {
        return stars;
    }

    public void setStars(long stars) {
        this.stars = stars;
    }

    public long getDownloads() {
        return downloads;
    }

    public void setDownloads(long downloads) {
        this.downloads = downloads;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", userId=" + userId +
                ", userNickname='" + userNickname + '\'' +
                ", videoName='" + videoName + '\'' +
                ", videoId=" + videoId +
                ", videoUrl='" + m3u8Id + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", category=" + category +
                ", transcoded=" + transcoded +
                ", status=" + status +
                ", audited=" + audited +
                ", remark='" + remark + '\'' +
                ", views=" + views +
                ", stars=" + stars +
                ", downloads=" + downloads +
                ", duration=" + duration +
                ", currentTime=" + currentTime +
                ", publishTime=" + publishTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
