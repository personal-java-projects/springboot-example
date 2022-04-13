package com.example.mapper;

import com.example.pojo.Schedule;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ScheduleMapper {

    int insertSchedule(Schedule schedule);

    Schedule selectSchedule(@Param("id") int id);

    List<Schedule> selectSchedules(List<Integer> ids);

    List<Schedule> selectSchedulesByStatus(int status);

    void deleteScheduleById(int id);

    void updateSchedule(Schedule schedule);

    List<Schedule> selectSchedulesByKeyword(String keyword);
}
