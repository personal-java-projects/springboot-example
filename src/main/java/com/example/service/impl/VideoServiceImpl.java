package com.example.service.impl;

import com.example.enums.CronType;
import com.example.enums.ScheduleStatus;
import com.example.enums.VideoStatus;
import com.example.mapper.UserMapper;
import com.example.mapper.VideoMapper;
import com.example.pojo.Schedule;
import com.example.pojo.User;
import com.example.pojo.Video;
import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.ScheduleModel;
import com.example.service.ScheduleService;
import com.example.service.VideoService;
import com.example.util.Time2CronUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("videoService")
public class VideoServiceImpl implements VideoService {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    CronTaskRegistrar cronTaskRegistrar;

    @Override
    @SneakyThrows
    public boolean publishVideo(Video video,String publishTime, int immediately) {

        if (video.getPublishTime() == null) {
            video.setPublishTime(new Date());
        }

        if (immediately == 0) {
            video.setStatus(VideoStatus.ON_SHELF.ordinal());
        }

        if (immediately == 1) {
            video.setStatus(VideoStatus.OF_SHELF.ordinal());
        }

        User user = userMapper.selectUserById(video.getUserId());

        video.setCreateTime(new Date());
        video.setUpdateTime(new Date());
        video.setUserNickname(user.getNickname());

        videoMapper.insertVideo(video);

        // 当属于定时发布
        if (immediately == 1) {
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
            schedule.setRemark("定时发布视频："+video.getVideoName());

            int scheduleId = scheduleService.addSchedule(schedule);

            stringBuffer.append(",").append(scheduleId);
            methodParams = stringBuffer.toString();
            schedule.setMethodParams(methodParams);

            scheduleService.editSchedule(schedule);
        }

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
    public List<Video> getVideoByKeyword(String keyword) {
        List<Video> videos = videoMapper.selectVideoByKeyword(keyword);

        for( Video video:videos ) {

        }

        return videos;
    }
}
