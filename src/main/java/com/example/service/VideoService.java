package com.example.service;

import com.example.ffmpeg.TranscodeConfig;
import com.example.pojo.Video;
import com.example.vto.dto.VideoDto;

import java.util.List;

public interface VideoService {

    boolean publishVideo(Video video, String publishTime, int immediately);

    boolean auditVideo(int id, int audited);

    boolean changeSelf(int id, int status);

    boolean deleteVideos(List<Integer> ids);

    String convertVideo2M3u8(Video video, TranscodeConfig transcodeConfig);

    List<VideoDto> getVideoByKeyword(String keyword);
}
