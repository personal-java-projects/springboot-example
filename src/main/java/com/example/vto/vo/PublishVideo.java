package com.example.vto.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("发布视频")
public class PublishVideo {

    @ApiModelProperty(value = "用户id", example = "范德萨")
    @NotNull(message = "用户id不能为空")
    private int userId;

    @ApiModelProperty(value = "视频名称", example = "范德萨")
    @NotNull(message = "视频名称不能为空")
    private String videoName;

    @ApiModelProperty(value = "视频文件id", example = "https://**")
    @NotNull(message = "视频文件id不能为空")
    private int videoId;

    @ApiModelProperty(value = "视频时长", example = "https://**")
    @NotNull(message = "视频时长不能为空")
    private double duration;

    @ApiModelProperty(value = "封面地址", example = "https://**")
    @NotNull(message = "封面地址不能为空")
    private String coverUrl;

    @ApiModelProperty(value = "视频类别", example = "0")
    @NotNull(message = "视频类别不能为空")
    private int category;

    @ApiModelProperty(value = "视频状态", example = "0")
    private String remark;

    @ApiModelProperty(value = "是否立即发布", example = "0")
    @NotNull(message = "immediately不能为空")
    private int immediately;

    @ApiModelProperty(value = "发布时间", example = "0")
    private String publishTime;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getImmediately() {
        return immediately;
    }

    public void setImmediately(int immediately) {
        this.immediately = immediately;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
}
