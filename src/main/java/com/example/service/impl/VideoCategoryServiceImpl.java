package com.example.service.impl;

import com.example.mapper.VideoCategoryMapper;
import com.example.pojo.VideoCategory;
import com.example.service.VideoCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("videoCategoryService")
public class VideoCategoryServiceImpl implements VideoCategoryService {

    @Autowired
    private VideoCategoryMapper videoCategoryMapper;

    @Override
    public List<VideoCategory> getVideoCategory(Integer id) {
        List<VideoCategory> videoCategories = videoCategoryMapper.selectVideoMapperById(id);

        return videoCategories;
    }
}
