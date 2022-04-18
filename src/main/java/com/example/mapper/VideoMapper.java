package com.example.mapper;

import com.example.pojo.Video;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface VideoMapper {
    void insertVideo(Video video);

    Video selectVideoById(int id);

    List<Video> selectVideoByKeyword(String keyword);

    void updateVideo(Video video);

    void deleteVideos(List<Integer> ids);
}
