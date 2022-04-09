package com.example;

import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.SchedulingRunnable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringbootExampleApplicationTests {

	@Autowired
	CronTaskRegistrar cronTaskRegistrar;

	@Test
	public void testTask() throws InterruptedException {
		SchedulingRunnable task = new SchedulingRunnable("demoTask", "taskNoParams", null);
		cronTaskRegistrar.addCronTask(task, "0/10 * * * * ?");

		// 便于观察
		Thread.sleep(3000000);
	}

	@Test
	public void testHaveParamsTask() throws InterruptedException {
		SchedulingRunnable task = new SchedulingRunnable("demoTask", "taskWithParams", "haha", 23);
		cronTaskRegistrar.addCronTask(task, "0/10 * * * * ?");

		// 便于观察
		Thread.sleep(3000000);
	}


}
