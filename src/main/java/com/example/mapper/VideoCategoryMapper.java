package com.example.mapper;

import com.example.pojo.VideoCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface VideoCategoryMapper {
    List<VideoCategory> selectVideoMapperById(Integer id);
}
