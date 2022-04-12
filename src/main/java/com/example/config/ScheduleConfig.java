package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @program: simple-demo
 * @description: 定时任务配置类
 **/
@Configuration
@EnableScheduling
@EnableAsync
public class ScheduleConfig {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleConfig.class);

    @Autowired
    private ScheduleProperties scheduleProperties;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 定时任务执行线程池核心线程数
        taskScheduler.setPoolSize(scheduleProperties.getPoolSize());
        taskScheduler.setThreadGroupName(scheduleProperties.getThreadGroupName());
        taskScheduler.setRemoveOnCancelPolicy(scheduleProperties.getRemoveOnCancelPolicy());
        taskScheduler.setThreadNamePrefix(scheduleProperties.getThreadNamePrefix());
        return taskScheduler;
    }
}
