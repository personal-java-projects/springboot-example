package com.example.task;

import org.springframework.stereotype.Component;

@Component("task")
public class Task {
    public void timingPublish(Integer userId, Integer status) {
        System.out.println("定时发布：" + userId + "->" + status);
    }
}
