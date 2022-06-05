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

    @Bean("taskScheduler")
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 定时任务执行线程池核心线程数
        taskScheduler.setPoolSize(scheduleProperties.getPoolSize());
        taskScheduler.setThreadGroupName(scheduleProperties.getThreadGroupName());
        taskScheduler.setRemoveOnCancelPolicy(scheduleProperties.getRemoveOnCancelPolicy());
        taskScheduler.setThreadNamePrefix(scheduleProperties.getThreadNamePrefix());
        return taskScheduler;
    }

    /**
     * 线程池参数根据minIO设置，如果开启线程太多会被MinIO拒绝
     * @return ：
     */
    @Bean("minIOUploadTreadPool")
    public ThreadPoolTaskExecutor asyncServiceExecutorForMinIo() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数，采用IO密集 h/(1-拥塞)
        executor.setCorePoolSize(1);
        // 设置最大线程数,由于minIO连接数量有限，此处尽力设计大点
        executor.setMaxPoolSize(500);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(30);
        // 设置默认线程名称
        executor.setThreadNamePrefix("minio-upload-task-");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(60);
        /**
         * 拒绝处理策略
         * CallerRunsPolicy()：交由调用方线程运行，比如 main 线程。
         * AbortPolicy()：直接抛出异常。
         * DiscardPolicy()：直接丢弃。
         * DiscardOldestPolicy()：丢弃队列中最老的任务。
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
