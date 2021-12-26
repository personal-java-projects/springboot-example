package com.example.pojo;

import java.sql.Date;

public class File {
    private int id;

    private String filename;

    private String fileUrl;

    private String extension;

    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", extension='" + extension + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
