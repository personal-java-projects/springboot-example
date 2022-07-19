package com.example;

import com.example.enums.CronType;
import com.example.schedule.CronTaskRegistrar;
import com.example.schedule.ScheduleModel;
import com.example.schedule.SchedulingRunnable;
import com.example.util.JdbcUtils;
import com.example.util.Time2CronUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.ResultSetMetaData;
import java.util.Date;
import java.util.List;

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

	@Test
	public void test() throws Exception {
		System.out.println("获取所有数据库的表名" + JdbcUtils.getAllTableName());

		String tableName = "ex_user";
		System.out.println("获取建表语句：" + JdbcUtils.getCreateTableDDL(tableName));
		System.out.println("获取表的备注：" + JdbcUtils.getTableCommnet(tableName));

		System.out.println("打印字段信息：");
		List<String> columnComments = JdbcUtils.getColumnComments(tableName);
		ResultSetMetaData resultSetMetaData = JdbcUtils.getResultSetMetaData(tableName);
		int columnCount = resultSetMetaData.getColumnCount();
		System.out.println(tableName + "表中字段个数为：" + columnCount);
		for (int i = 1; i <= columnCount; i++) {
			System.out.print("java类型：" + resultSetMetaData.getColumnClassName(i));
			System.out.print("  数据库类型:" + resultSetMetaData.getColumnTypeName(i));
			System.out.print("  字段名称:" + resultSetMetaData.getColumnName(i));
			System.out.print("  字段长度:" + resultSetMetaData.getColumnDisplaySize(i));
			System.out.print(" notNull：" + (resultSetMetaData.isNullable(i) == 1 ? false : true));
			System.out.println(" 注释为：" + columnComments.get(i - 1));
		}
	}
}
