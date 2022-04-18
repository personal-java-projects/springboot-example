package com.example.service;

import com.example.pojo.Schedule;

import java.util.List;

public interface ScheduleService {

    int addSchedule(Schedule schedule);

    boolean editSchedule(Schedule schedule);

    boolean deleteSchedule(List<Integer> ids);

    boolean changeScheduleStatus(int id, int status);

    List<Schedule> getSchedulesByKeyword(String keyword);
}
