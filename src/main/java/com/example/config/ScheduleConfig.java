package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

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
        taskScheduler.setPoolSize(4);
        taskScheduler.setThreadGroupName("syncTg");
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setThreadNamePrefix(scheduleProperties.getThreadNamePrefix());
        return taskScheduler;
    }

//    @Bean
//    public Executor asyncServiceExecutor() {
//        logger.info("start asyncServiceExecutor");
//
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//
//        // 配置核心线程数
//        executor.setCorePoolSize(scheduleProperties.getCorePoolSize());
//        // 配置最大线程数
//        executor.setMaxPoolSize(scheduleProperties.getMaxPoolSize());
//        // 配置线程最大空闲时间
//        executor.setKeepAliveSeconds(scheduleProperties.getKeepAliveSeconds());
//        // 配置队列大小
//        executor.setQueueCapacity(scheduleProperties.getQueueCapacity());
//        // 配置线程池中的线程的名称前缀
//        executor.setThreadNamePrefix(scheduleProperties.getThreadNamePrefix());
//
//        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
//        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.setWaitForTasksToCompleteOnShutdown(true);
//
//        //执行初始化
//        executor.initialize();
//
//        return executor;
//    }
}
