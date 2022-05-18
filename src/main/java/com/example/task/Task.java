package com.example.task;

import com.example.enums.ScheduleStatus;
import com.example.enums.VideoStatus;
import com.example.mapper.VideoMapper;
import com.example.pojo.Video;
import com.example.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("task")
public class Task {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private ScheduleService scheduleService;

    public void timingPublish(Integer id, Integer taskId) {
        Video video = videoMapper.selectVideoById(id);

        // 如果视频已发布，则直接关闭定时器
        if (video != null && video.getStatus().equals(VideoStatus.ON_SHELF.ordinal())) {
            scheduleService.changeScheduleStatus(taskId, ScheduleStatus.PAUSE.ordinal());
        }

        if (video != null && video.getStatus().equals(VideoStatus.OFF_SHELF.ordinal())) {
            video.setStatus(VideoStatus.ON_SHELF.ordinal());
            video.setUpdateTime(new Date());

            videoMapper.updateVideo(video);

            scheduleService.changeScheduleStatus(taskId, ScheduleStatus.PAUSE.ordinal());
        }
    }
}
