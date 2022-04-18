package com.example.service;

import com.example.pojo.VideoCategory;

import java.util.List;

public interface VideoCategoryService {
    List<VideoCategory> getVideoCategory(Integer id);
}
