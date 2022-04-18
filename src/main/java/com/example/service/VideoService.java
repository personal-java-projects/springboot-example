package com.example.service;

import com.example.pojo.Video;

import java.util.List;

public interface VideoService {

    boolean publishVideo(Video video, String publishTime, int immediately);

    boolean auditVideo(int id, int audited);

    boolean changeSelf(int id, int status);

    boolean deleteVideos(List<Integer> ids);

    List<Video> getVideoByKeyword(String keyword);
}
