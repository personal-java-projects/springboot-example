package com.example.schedule;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 添加定时任务注册类，用来增加、删除定时任务。
 **/
@Component
public class CronTaskRegistrar implements DisposableBean {

    private final Map<Integer, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

    @Autowired
    private TaskScheduler taskScheduler;

    /**
     * 新增定时任务
     * @param task
     * @param cronExpression
     */
    public void addCronTask(SchedulingRunnable task, String cronExpression) {
        addCronTask(task.getId(), new CronTask(task, cronExpression));
    }

    public void addCronTask(int id, CronTask cronTask) {
        if (cronTask != null) {
            if (this.scheduledTasks.containsKey(id)) {
                removeCronTask(id);
            }

            this.scheduledTasks.put(id, scheduleCronTask(cronTask));
        }
    }

    /**
     * 移除定时任务
     * @param id
     */
    public void removeCronTask(int id) {
        ScheduledTask scheduledTask = this.scheduledTasks.remove(id);
        if (scheduledTask != null) {
            scheduledTask.cancel();
        }
    }

    public ScheduledTask scheduleCronTask(CronTask cronTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());

        return scheduledTask;
    }


    @Override
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks.values()) {
            task.cancel();
        }
        this.scheduledTasks.clear();
    }
}
