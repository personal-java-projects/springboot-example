package com.example.controller;

import com.example.pojo.VideoCategory;
import com.example.service.VideoCategoryService;
import com.example.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/video-category")
public class VideoCategoryController {

    @Autowired
    private VideoCategoryService videoCategoryService;

    @GetMapping("/getVideoCategory")
    public ResponseResult getVideoCategory(@RequestParam(required = false) Integer id) {
        List<VideoCategory> videoCategories = videoCategoryService.getVideoCategory(id);

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("videoCategories", videoCategories);

        return ResponseResult.ok().data(resultMap);
    }
}
