package com.example;

import com.example.enums.CronType;
import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.ScheduleModel;
import com.example.schedule.SchedulingRunnable;
import com.example.util.Time2CronUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringbootExampleApplicationTests {

	@Autowired
	CronTaskRegistrar cronTaskRegistrar;

	@Test
	public void testTask() throws InterruptedException {
		SchedulingRunnable task = new SchedulingRunnable(0, "demoTask", "taskNoParams", null);
		cronTaskRegistrar.addCronTask(task, "0/10 * * * * ?");

		// 便于观察
		Thread.sleep(3000000);
	}

	@Test
	public void testHaveParamsTask() throws InterruptedException {
		SchedulingRunnable task = new SchedulingRunnable(-1,"demoTask", "taskWithParams", "haha", 13);
		cronTaskRegistrar.addCronTask(task, "0/10 * * * * ?");

		// 便于观察
		Thread.sleep(3000000);
	}

	@Test
	public void test_getCron() throws InterruptedException {
		ScheduleModel scheduleModel = new ScheduleModel();
		scheduleModel.setJobType(CronType.DAY.getCode());
		scheduleModel.setStartDate("2022-4-14 14:10:50");
		SchedulingRunnable task = new SchedulingRunnable(-2,"demoTask", "taskWithParams", "haha", 13);

		try {
			String cronExpression = Time2CronUtil.createCronExpression(scheduleModel);
			System.out.println(cronExpression);
			cronTaskRegistrar.addCronTask(task, cronExpression);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 便于观察
		Thread.sleep(3000000);
	}
}
