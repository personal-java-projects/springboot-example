package com.example.service.impl;

import com.example.enums.ScheduleStatus;
import com.example.mapper.ScheduleMapper;
import com.example.pojo.Schedule;
import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.SchedulingRunnable;
import com.example.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Override
    public boolean addSchedule(Schedule schedule) {
        schedule.setCreateTime(new Date());
        schedule.setUpdateTime(new Date());
        scheduleMapper.insertSchedule(schedule);

        if (schedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(schedule.getBeanName(), schedule.getMethodName(), schedule.getMethodParams());
            cronTaskRegistrar.addCronTask(task, schedule.getCronExpression());
        }

        return true;
    }

    @Override
    public boolean editSchedule(Schedule schedule) {
        Schedule exitedSchedule = scheduleMapper.selectSchedule(schedule.getId());

        //先移除再添加
        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(exitedSchedule.getBeanName(), exitedSchedule.getMethodName(), exitedSchedule.getMethodParams());
            cronTaskRegistrar.removeCronTask(task);
        }

        if (schedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(schedule.getBeanName(), schedule.getMethodName(), schedule.getMethodParams());
            cronTaskRegistrar.addCronTask(task, schedule.getCronExpression());
        }

        scheduleMapper.updateSchedule(schedule);

        return true;
    }

    @Override
    public boolean deleteSchedule(int id) {
        scheduleMapper.deleteScheduleById(id);

        Schedule exitedSchedule = scheduleMapper.selectSchedule(id);

        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(exitedSchedule.getBeanName(), exitedSchedule.getMethodName(), exitedSchedule.getMethodParams());
            cronTaskRegistrar.removeCronTask(task);
        }

        return true;
    }

    @Override
    public boolean changeScheduleStatus(int id, int status) {
        Schedule exitedSchedule = scheduleMapper.selectSchedule(id);

        exitedSchedule.setStatus(status);

        scheduleMapper.updateSchedule(exitedSchedule);

        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(exitedSchedule.getBeanName(), exitedSchedule.getMethodName(), exitedSchedule.getMethodParams());
            cronTaskRegistrar.addCronTask(task, exitedSchedule.getCronExpression());
        } else {
            SchedulingRunnable task = new SchedulingRunnable(exitedSchedule.getBeanName(), exitedSchedule.getMethodName(), exitedSchedule.getMethodParams());
            cronTaskRegistrar.removeCronTask(task);
        }

        return true;
    }

    @Override
    public List<Schedule> getSchedulesByKeyword(String keyword) {
        List<Schedule> scheduleList = scheduleMapper.selectSchedulesByKeyword(keyword);

        return scheduleList;
    }
}
