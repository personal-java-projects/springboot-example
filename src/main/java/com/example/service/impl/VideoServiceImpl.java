package com.example.service.impl;

import com.example.config.OssProperties;
import com.example.enums.CronType;
import com.example.enums.ScheduleStatus;
import com.example.enums.VideoStatus;
import com.example.ffmpeg.FFmpegProperties;
import com.example.ffmpeg.FFmpegUtils;
import com.example.ffmpeg.TranscodeConfig;
import com.example.mapper.FileMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.VideoMapper;
import com.example.pojo.Schedule;
import com.example.pojo.User;
import com.example.pojo.Video;
import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.ScheduleModel;
import com.example.service.ScheduleService;
import com.example.service.UploadService;
import com.example.service.VideoService;
import com.example.util.FileUtils;
import com.example.util.Time2CronUtil;
import com.example.vto.dto.VideoDto;
import com.example.vto.po2Dto.Video2Dto;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("videoService")
public class VideoServiceImpl implements VideoService {
    @Autowired
    private Video2Dto video2Dto;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Override
    @SneakyThrows
    public boolean publishVideo(Video video,String publishTime, int immediately) {
        if (publishTime.equals("")) {
            video.setPublishTime(new Date());
        }

        User user = userMapper.selectUserById(video.getUserId());

        video.setCreateTime(new Date());
        video.setUpdateTime(new Date());
        video.setUserNickname(user.getNickname());

        if (immediately == 0) {
            video.setStatus(VideoStatus.ON_SHELF.ordinal());

            videoMapper.insertVideo(video);
        }

        // 当属于定时发布
        if (immediately == 1) {
            video.setStatus(VideoStatus.OFF_SHELF.ordinal());

            videoMapper.insertVideo(video);

            StringBuffer stringBuffer = new StringBuffer();

            ScheduleModel scheduleModel = new ScheduleModel();
            scheduleModel.setJobType(CronType.DAY.getCode());
            scheduleModel.setStartDate(publishTime);

            // 创建对应的定时器对象
            Schedule schedule = new Schedule();
            schedule.setBeanName("task");
            schedule.setMethodName("timingPublish");
            String methodParams = stringBuffer.append(video.getId()).toString();
            schedule.setMethodParams(methodParams);
            String cronExpression = Time2CronUtil.createCronExpression(scheduleModel);
            schedule.setCronExpression(cronExpression);
            schedule.setStatus(ScheduleStatus.NORMAL.ordinal());
            schedule.setRemark("定时发布视频：" + video.getVideoName() + "，备注：" + video.getRemark());

            int scheduleId = scheduleService.addSchedule(schedule);

            stringBuffer.append(",").append(scheduleId);
            methodParams = stringBuffer.toString();
            schedule.setMethodParams(methodParams);

            scheduleService.editSchedule(schedule);
        }

        // 转码定时任务
        StringBuffer stringBuffer = new StringBuffer();

        ScheduleModel scheduleModel = new ScheduleModel();
        scheduleModel.setJobType(CronType.DAY.getCode());
        if (!publishTime.equals("")) {
            scheduleModel.setStartDate(publishTime);
        } else {
            String startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            scheduleModel.setStartDate(startDate);
        }

        Schedule schedule = new Schedule();
        schedule.setBeanName("transcodingTask");
        schedule.setMethodName("transcodingVideo");
        String methodParams = stringBuffer.append(video.getId()).toString();
        schedule.setMethodParams(methodParams);
        String cronExpression = Time2CronUtil.createCronExpression(scheduleModel);
        schedule.setCronExpression(cronExpression);
        schedule.setStatus(ScheduleStatus.NORMAL.ordinal());
        schedule.setRemark("定时转码：" + video.getVideoName() + "，备注：" + video.getRemark());

        int scheduleId = scheduleService.addSchedule(schedule);

        stringBuffer.append(",").append(scheduleId);
        methodParams = stringBuffer.toString();
        schedule.setMethodParams(methodParams);

        scheduleService.editSchedule(schedule);

        return true;
    }

    @Override
    public boolean auditVideo(int id, int audited) {
        Video video = videoMapper.selectVideoById(id);

        video.setAudited(audited);
        video.setUpdateTime(new Date());

        videoMapper.updateVideo(video);

        return true;
    }

    @Override
    public boolean changeSelf(int id, int status) {
        Video video = videoMapper.selectVideoById(id);

        video.setStatus(status);
        video.setUpdateTime(new Date());

        videoMapper.updateVideo(video);

        return true;
    }

    @Override
    public boolean deleteVideos(List<Integer> ids) {
        videoMapper.deleteVideos(ids);

        return true;
    }

    @Override
    @SneakyThrows
    public List<VideoDto> getVideoByKeyword(String keyword) {
        List<Video> videos = videoMapper.selectVideoByKeyword(keyword);
        List<VideoDto> videoList = new ArrayList<>();

        for( Video video:videos ) {
            VideoDto videoDto = video2Dto.video2VideoDto(video);
            String fileUrl = uploadService.getFileUrl(video.getVideoId());

            if (video.getM3u8Id() != null) {
                String m3u8Url = uploadService.getFileUrl(video.getM3u8Id());
                videoDto.setM3u8Url(m3u8Url);
            }

            videoDto.setVideoUrl(fileUrl);

            videoList.add(videoDto);
        }

        return videoList;
    }
}
