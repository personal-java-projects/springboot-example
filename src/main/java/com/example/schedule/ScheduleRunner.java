package com.example.schedule;

import com.example.enums.ScheduleStatus;
import com.example.mapper.ScheduleMapper;
import com.example.pojo.Schedule;
import com.example.util.ScheduleUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleRunner.class);

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Autowired
    private ScheduleUtil scheduleUtil;

    @Override
    @SneakyThrows
    public void run(String... args) {// 初始加载数据库里状态为正常的定时任务
        List<Schedule> jobList = scheduleMapper.selectSchedulesByStatus(ScheduleStatus.NORMAL.ordinal());

        if (CollectionUtils.isNotEmpty(jobList)) {
            for (Schedule job : jobList) {
                SchedulingRunnable task = scheduleUtil.handleScheduleParams(job);

                cronTaskRegistrar.addCronTask(task, job.getCronExpression());
            }

            logger.info("定时任务已加载完毕...");
        }

    }
}
