package com.example.controller;

import com.example.enums.ScheduleStatus;
import com.example.pojo.Schedule;
import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.SchedulingRunnable;
import com.example.service.ScheduleService;
import com.example.util.ResponseResult;
import com.example.vto.dto.PageDto;
import com.example.vto.vo.AddSchedule;
import com.example.vto.vo.EditSchedule;
import com.example.vto.vo.Page;
import com.example.vto.voToPo.Schedule2PO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Autowired
    private Schedule2PO schedule2PO;

    @PostMapping("/addSchedule")
    public ResponseResult addSchedule(@RequestBody AddSchedule addSchedule) {
        Schedule schedule = schedule2PO.addSchedule2PO(addSchedule);

        scheduleService.addSchedule(schedule);

        return ResponseResult.ok().message("添加成功");
    }

    @PatchMapping("/editSchedule")
    public ResponseResult editSchedule(@RequestBody EditSchedule editSchedule) {
        Schedule schedule = schedule2PO.editSchedule2PO(editSchedule);

        scheduleService.editSchedule(schedule);

        return ResponseResult.ok().message("修改成功");
    }

    @DeleteMapping("/deleteSchedule/{id}")
    public ResponseResult deleteSchedule(@PathVariable("id") int id) {
        scheduleService.deleteSchedule(id);

        return ResponseResult.ok().message("删除成功");
    }

    @PatchMapping("/changeScheduleStatus/{id}")
    public ResponseResult changeScheduleStatus(@PathVariable("id") int id, @RequestParam int status) {
        scheduleService.changeScheduleStatus(id, status);

        return ResponseResult.ok().message("启动或停止成功");
    }

    @PostMapping("/getAllSchedules")
    public ResponseResult getAllSchedules(@RequestParam(required = false) String keyword, @RequestBody(required = false) Page page) {
        List<Schedule> scheduleList = scheduleService.getSchedulesByKeyword(keyword);

        if (page != null) {
            PageDto.initPageHelper(page.getPageIndex(), page.getPageSize());

            PageDto pagesInfo = PageDto.pageList(scheduleList, "scheduleList");

            return ResponseResult.ok().data(pagesInfo.getResultMap());
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("scheduleList", scheduleList);

        return ResponseResult.ok().data(resultMap);
    }
}
