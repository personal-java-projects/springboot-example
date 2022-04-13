package com.example.schedule;

import com.example.enums.ScheduleStatus;
import com.example.mapper.ScheduleMapper;
import com.example.pojo.Schedule;
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

    @Override
    @SneakyThrows
    public void run(String... args) {// 初始加载数据库里状态为正常的定时任务
        List<Schedule> jobList = scheduleMapper.selectSchedulesByStatus(ScheduleStatus.NORMAL.ordinal());

        if (CollectionUtils.isNotEmpty(jobList)) {
            for (Schedule job : jobList) {
                SchedulingRunnable task = null;

                if (job.getMethodParams().equals("")) {
                    task = new SchedulingRunnable(job.getId(), job.getBeanName(), job.getMethodName(), null);
                }

                if (!job.getMethodParams().equals("")) {
                    task = new SchedulingRunnable(job.getId(), job.getBeanName(), job.getMethodName(), job.getMethodParams());
                }

                cronTaskRegistrar.addCronTask(task, job.getCronExpression());
            }

            logger.info("定时任务已加载完毕...");
        }

    }
}
