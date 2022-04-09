package com.example.mapper;

import com.example.pojo.Schedule;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ScheduleMapper {

    @Insert("INSERT INTO ex_schedule(id, createId, method, params, cron, status, remark, createTime, updateTime) " +
            "VALUES(#{id}, #{createId}, #{method}, #{params}, #{cron}, #{status}, #{remark}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSchedule(Schedule schedule);

    @Select({
            "<script>",
            "SELECT * ",
            "FROM ex_schedule",
            "<where>",
            "<if test = 'id != null'>",
            "createId = #{id}",
            "</if>",
            "</where>",
            "</script>"
    })
    Schedule selectSchedules(@Param("id") int createId);
}
