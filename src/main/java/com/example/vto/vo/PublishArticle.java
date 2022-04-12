package com.example.vto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@ApiModel(value = "发布文章")
public class PublishArticle {

    @ApiModelProperty(value = "文章标题", example = "范德萨")
    @NotNull(message = "标题不能为空")
    private String title;

    @ApiModelProperty(value = "作者名", example = "疯狂的")
    @NotNull(message = "作者名称不能为空")
    private String author;

    @ApiModelProperty(value = "作者id", example = "1")
    @NotNull(message = "作者id不能为空")
    private int authorId;

    @ApiModelProperty(value = "发布时间", example = "2021-12-01")
    @NotNull(message = "发布时间不能为空")
    private String publishTime;

    @ApiModelProperty(value = "重要程度", example = "0")
    @Range(min = 0, max = 3, message = "必须在0-3")
    private int importance;

    @ApiModelProperty(value = "文章概述", example = "格拉夫的方式")
    private String summary;

    @ApiModelProperty(value = "文章内容", example = "佛安抚")
    @NotNull(message = "文章内容不能为空")
    private String richText;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRichText() {
        return richText;
    }

    public void setRichText(String richText) {
        this.richText = richText;
    }
}
