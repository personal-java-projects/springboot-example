package com.example.task;

import org.springframework.stereotype.Component;

/**
 * @program: simple-demo
 **/
@Component("demoTask")
public class DemoTask {

    public void taskWithParams(String param) {
        System.out.println("联调接口有参示例任务：" + param);
    }

    public void taskWithParams(String param1, Integer param2) {
        System.out.println("这是有两个参示例任务：" + param1 + param2);
    }

    public void taskWithParams(String param1, Double param2) {
        System.out.println("这是带有双精度参数示例任务：" + param1 + param2);
    }

    public void taskWithThreeParams(String param1, Integer param2, String param3) {
        System.out.println("这是三个参数示例任务：" + param1 + param2 + "," + param3);
    }

    public void taskWithThreeParams(String param1, Double param2, String param3) {
        System.out.println("这是三个参数带双精度示例任务：" + param1 + param2 + "," + param3);
    }

    public void taskNoParams() {
        System.out.println("这是无参示例任务");
    }
}
