package com.example.util;

import com.example.pojo.Schedule;
import com.example.schedule.SchedulingRunnable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ScheduleUtil {
    /**
     * 判断字符串是否是整数
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断字符串是否是双精度或单精度
     * @param str
     * @return
     */
    public boolean isDoubleOrFloat(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?\\d*[.]\\d+$"); // 之前这里正则表达式错误，现更正
        return pattern.matcher(str).matches();
    }

    /**
     * 前端传过来的定时器的参数是以逗号隔开的字符串，且有多种类型，在这里进行处理，定义新的ScheduleRunnble动态参数必须是数组或者是null
     * @param schedule
     * @return
     */
    public SchedulingRunnable handleScheduleParams(Schedule schedule) {
        List params = new ArrayList();
        SchedulingRunnable task = null;

        if (!schedule.getMethodParams().equals("")) {
            for ( String param : schedule.getMethodParams().split(",") ) {
                // 是否是双精度或单精度
                if (isDoubleOrFloat(param)) {
                    params.add(Double.parseDouble(param));
                }

                // 是否是整数
                if (isNumeric(param)) {
                    params.add(Integer.parseInt(param));
                }

                // 字符串类型
                if (!isNumeric(param) && !isDoubleOrFloat(param)) {
                    params.add(param);
                }
            }

            task = new SchedulingRunnable(schedule.getId(), schedule.getBeanName(), schedule.getMethodName(), params.toArray());
        }

        if (schedule.getMethodParams().equals("")) {
            task = new SchedulingRunnable(schedule.getId(), schedule.getBeanName(), schedule.getMethodName(), null);
        }

        return task;
    }
}
