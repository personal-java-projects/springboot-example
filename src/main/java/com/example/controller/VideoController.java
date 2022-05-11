package com.example.controller;

import com.example.pojo.Video;
import com.example.service.VideoService;
import com.example.util.ResponseResult;
import com.example.vto.dto.PageDto;
import com.example.vto.dto.VideoDto;
import com.example.vto.vo.Page;
import com.example.vto.vo.PublishVideo;
import com.example.vto.vo2Po.Video2PO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private Video2PO video2PO;

    @PostMapping("/uploadVideo")
    public ResponseResult uploadVideo(@RequestBody PublishVideo publishVideo) {
        Video video = video2PO.publishVideo2Video(publishVideo);
        videoService.publishVideo(video, publishVideo.getPublishTime(), publishVideo.getImmediately());

        return ResponseResult.ok().message("上传成功");
    }

    @PatchMapping("/auditVideo")
    public ResponseResult auditVideo(@RequestParam int id, @RequestParam int audited) {

        videoService.auditVideo(id, audited);

        return ResponseResult.ok().message("审核通过");
    }

    @PatchMapping("/changeSelf")
    public ResponseResult changeSelf(@RequestParam int id, @RequestParam int status) {
        videoService.changeSelf(id, status);

        return ResponseResult.ok();
    }

    @DeleteMapping("/deleteVideo")
    public ResponseResult deleteVideo(@RequestParam("ids[]") List<Integer> ids) {

        videoService.deleteVideos(ids);

        return ResponseResult.ok().message("删除成功");
    }

    @PostMapping("/getVideo")
    public ResponseResult getVideo(@RequestParam String keyword, @RequestBody Page page) {
        List<VideoDto> videosList = videoService.getVideoByKeyword(keyword);

        if (page != null) {
            PageDto.initPageHelper(page.getPageIndex(), page.getPageSize());

            PageDto pageInfo = PageDto.pageList(videosList, "videosList");

            return ResponseResult.ok().data(pageInfo.getResultMap());
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("videosList", videosList);

        return ResponseResult.ok().data(resultMap);
    }
}
