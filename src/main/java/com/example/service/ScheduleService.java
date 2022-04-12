package com.example.service;

import com.example.pojo.Schedule;

import java.util.List;

public interface ScheduleService {

    boolean addSchedule(Schedule schedule);

    boolean editSchedule(Schedule schedule);

    boolean deleteSchedule(int id);

    boolean changeScheduleStatus(int id, int status);

    List<Schedule> getSchedulesByKeyword(String keyword);
}