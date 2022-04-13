package com.example.service.impl;

import com.example.enums.ScheduleStatus;
import com.example.mapper.ScheduleMapper;
import com.example.pojo.Schedule;
import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.SchedulingRunnable;
import com.example.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public boolean isDoubleOrFloat(String str) {
        Pattern patternFloat = Pattern.compile("^[-\\+]?(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$");
        return patternFloat.matcher(str).matches();
    }

    @Override
    public boolean addSchedule(Schedule schedule) {
        schedule.setCreateTime(new Date());
        schedule.setUpdateTime(new Date());
        scheduleMapper.insertSchedule(schedule);

        if (schedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            List params = new ArrayList();
            SchedulingRunnable task = null;

            if (!schedule.getMethodParams().equals("")) {
                for ( String param : schedule.getMethodParams().split(",")) {
                    if (isDoubleOrFloat(param)) {
                        params.add(Double.parseDouble(param));
                    }

                    if (isNumeric(param)) {
                        params.add(Integer.parseInt(param));
                    }

                    if (!isNumeric(param) && !isDoubleOrFloat(param)) {
                        params.add(param);
                    }
                }

                task = new SchedulingRunnable(schedule.getId(), schedule.getBeanName(), schedule.getMethodName(), params.toArray());
            }

            if (schedule.getMethodParams().equals("")) {
                task = new SchedulingRunnable(schedule.getId(), schedule.getBeanName(), schedule.getMethodName(), null);
            }

            cronTaskRegistrar.addCronTask(task, schedule.getCronExpression());
        }

        return true;
    }

    @Override
    public boolean editSchedule(Schedule schedule) {
        Schedule exitedSchedule = scheduleMapper.selectSchedule(schedule.getId());

        //先移除再添加
        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            cronTaskRegistrar.removeCronTask(exitedSchedule.getId());
        }

        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(schedule.getId(), schedule.getBeanName(), schedule.getMethodName(), schedule.getMethodParams());
            cronTaskRegistrar.addCronTask(task, schedule.getCronExpression());
        }

        scheduleMapper.updateSchedule(schedule);

        return true;
    }

    @Override
    public boolean deleteSchedule(int id) {
        Schedule exitedSchedule = scheduleMapper.selectSchedule(id);

        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            cronTaskRegistrar.removeCronTask(exitedSchedule.getId());
        }

        scheduleMapper.deleteScheduleById(id);

        return true;
    }

    @Override
    public boolean changeScheduleStatus(int id, int status) {
        Schedule exitedSchedule = scheduleMapper.selectSchedule(id);

        exitedSchedule.setStatus(status);

        if (exitedSchedule.getStatus().equals(ScheduleStatus.NORMAL.ordinal())) {
            SchedulingRunnable task = new SchedulingRunnable(exitedSchedule.getId(), exitedSchedule.getBeanName(), exitedSchedule.getMethodName(), exitedSchedule.getMethodParams());
            cronTaskRegistrar.addCronTask(task, exitedSchedule.getCronExpression());
        } else {
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
