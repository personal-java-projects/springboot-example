package com.example.task;

import com.example.pojo.User;
import com.example.service.UserService;
import com.example.util.HttpClientComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务
 */
@Component
public class ScheduleExample {

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 0 22 ? * SUN")  //每周日晚上22点执行
    @Async
    public void excTask1() throws Exception {
        System.out.println("定时任务执行，执行时间是："+new Date());
        List<User> users = userService.getUsersByUsername(null);
        System.out.println("查询数据库user表的全部值是:"+users);

        Map<String, Object> map = new HashMap<>();

        map.put("X-Shopify-Access-Token", "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");

        StringBuffer jsonBody = HttpClientComponent.doGet("https://amelie-home-shop.myshopify.com/admin/api/2021-10/customers.json", "UTF-8", map);
    }
}
