package com.example.service.impl;

import com.example.enums.ScheduleStatus;
import com.example.mapper.ScheduleMapper;
import com.example.pojo.Schedule;
import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.SchedulingRunnable;
import com.example.service.ScheduleService;
import com.example.util.ScheduleUtil;
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

    @Autowired
    private ScheduleUtil scheduleUtil;

    @Override
    public int addSchedule(Schedule schedule) {
        schedule.setCreateTime(new Date());
        schedule.setUpdateTime(new Date());

        scheduleMapper.insertSchedule(schedule);

        int scheduleId = schedule.getId();

        if (schedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = scheduleUtil.handleScheduleParams(schedule);

            cronTaskRegistrar.addCronTask(task, schedule.getCronExpression());
        }

        return scheduleId;
    }

    @Override
    public boolean editSchedule(Schedule schedule) {
        Schedule exitedSchedule = scheduleMapper.selectSchedule(schedule.getId());

        exitedSchedule.setBeanName(schedule.getBeanName());
        exitedSchedule.setMethodName(schedule.getMethodName());
        exitedSchedule.setMethodParams(schedule.getMethodParams());
        exitedSchedule.setCronExpression(schedule.getCronExpression());
        exitedSchedule.setStatus(schedule.getStatus());
        exitedSchedule.setRemark(schedule.getRemark());
        exitedSchedule.setUpdateTime(new Date());

        //先移除再添加
        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            cronTaskRegistrar.removeCronTask(exitedSchedule.getId());
        }

        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = scheduleUtil.handleScheduleParams(schedule);

            cronTaskRegistrar.addCronTask(task, schedule.getCronExpression());
        }

        scheduleMapper.updateSchedule(exitedSchedule);

        return true;
    }

    @Override
    public boolean deleteSchedule(List<Integer> ids) {
        List<Schedule> scheduleList = scheduleMapper.selectSchedules(ids);

        for (Schedule schedule:scheduleList) {
            if (schedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
                cronTaskRegistrar.removeCronTask(schedule.getId());
            }

            scheduleMapper.deleteScheduleById(schedule.getId());
        }

        return true;
    }

    @Override
    public boolean changeScheduleStatus(int id, int status) {
        Schedule exitedSchedule = scheduleMapper.selectSchedule(id);

        exitedSchedule.setStatus(status);
        exitedSchedule.setUpdateTime(new Date());

        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(exitedSchedule.getId(), exitedSchedule.getBeanName(), exitedSchedule.getMethodName(), exitedSchedule.getMethodParams());
            cronTaskRegistrar.addCronTask(task, exitedSchedule.getCronExpression());
        }

        if (exitedSchedule.getStatus().equals(ScheduleStatus.PAUSE.ordinal())) {
            cronTaskRegistrar.removeCronTask(exitedSchedule.getId());
        }

        scheduleMapper.updateSchedule(exitedSchedule);

        return true;
    }

    @Override
    public List<Schedule> getSchedulesByKeyword(String keyword) {
        List<Schedule> scheduleList = scheduleMapper.selectSchedulesByKeyword(keyword);

        return scheduleList;
    }
}
