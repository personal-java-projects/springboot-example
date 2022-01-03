package com.example.schedule;

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


//    quartz定时任务cron表达式例子：
//
//    0 * * * * ? 每1分钟触发一次
//    0 0 * * * ? 每天每1小时触发一次
//    0 0 10 * * ? 每天10点触发一次
//    0 * 14 * * ? 在每天下午2点到下午2:59期间的每1分钟触发
//    0 30 9 1 * ? 每月1号上午9点半执行
//    0 15 10 15 * ? 每月15日上午10:15触发
//
//    */5 * * * * ? 每隔5秒执行一次
//    0 */1 * * * ? 每隔1分钟执行一次
//    0 0 5-15 * * ? 每天5-15点整点触发
//    0 0/3 * * * ? 每三分钟触发一次
//    0 0-5 14 * * ? 在每天下午2点到下午2:05期间的每1分钟触发
//    0 0/5 14 * * ? 在每天下午2点到下午2:55期间的每5分钟触发
//    0 0/5 14,18 * * ? 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
//    0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时执行
//    0 0 10,14,16 * * ? 每天上午10点，下午2点，4点 执行
//
//    0 0 22 ? * SUN 每周日晚上22点执行
//    0 0 17 ? * TUES,THUR,SAT 每周二、四、六下午五点执行
//    0 10,44 14 ? 3 WED 每年三月的星期三的下午2:10和2:44触发
//    0 15 10 ? * MON-FRI 周一至周五的上午10:15触发
//
//    0 0 23 L * ? 每月最后一天23点执行一次
//    0 15 10 L * ? 每月最后一日的上午10:15触发
//    0 15 10 ? * 6L 每月的最后一个星期五上午10:15触发
//
//    0 15 10 * * ? 2005 2005年的每天上午10:15触发
//    0 15 10 ? * 6L 2002-2005 2002年至2005年的每月的最后一个星期五上午10:15触发
//    0 15 10 ? * 6#3 每月的第三个星期五上午10:15触发

    @Autowired
    private UserService userService;

//    @Scheduled(cron = "0/10 * * * * ?")  //每10秒执行一次
//    @Async
//    public void excTask() throws Exception {
//        System.out.println("定时任务执行，执行时间是："+new Date());
//        List<User> users = userService.getUsers();
//        System.out.println("查询数据库user表的全部值是:"+users);
//
//        Map<String, Object> map = new HashMap<>();
//
//        map.put("X-Shopify-Access-Token", "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");
//
//        StringBuffer responseBody = HttpClientComponent.doGet("https://amelie-home-shop.myshopify.com/admin/api/2021-10/customers.json", "UTF-8", map);
//
////        System.out.println("responseBody: " + responseBody);
//
//        String location = "/";
//        String filename = "io_json_" + new Date().getTime();
//        String extension = ".json";
//
////        SaveAndExportFile.saveFile(location, filename, extension, responseBody.toString());
//
////        JSONObject jsonObject = new JSONObject();
////        jsonObject.put("commentId", "13026194071");
////        HttpClientComponent.doPost("http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=13026194071", jsonObject);
//    }


    @Scheduled(cron = "0 0 22 ? * SUN")  //每周日晚上22点执行
    @Async
    public void excTask1() throws Exception {
        System.out.println("定时任务执行，执行时间是："+new Date());
        List<User> users = userService.getUsers();
        System.out.println("查询数据库user表的全部值是:"+users);

        Map<String, Object> map = new HashMap<>();

        map.put("X-Shopify-Access-Token", "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");

        StringBuffer jsonBody = HttpClientComponent.doGet("https://amelie-home-shop.myshopify.com/admin/api/2021-10/customers.json", "UTF-8", map);



//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("commentId", "13026194071");
//        HttpClientComponent.doPost("http://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=13026194071", jsonObject);
    }
}
