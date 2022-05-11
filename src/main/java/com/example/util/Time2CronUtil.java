package com.example.util;

import com.example.schedule.ScheduleModel;

import java.util.Calendar;

public class Time2CronUtil {
    /**
     * 方法摘要：构建Cron表达式
     *
     * @param scheduleModel
     * @return String
     */
    public static String createCronExpression(ScheduleModel scheduleModel) throws Exception{

        //拆分时间字符串 年，月，日，时，分，秒
        String[] split1 = scheduleModel.getStartDate().split(" |-|:");

        StringBuffer cronExp = new StringBuffer("");

        if (null == scheduleModel.getJobType()) {
            System.out.println("执行周期未配置");//执行周期未配置
        }

        if (null != split1[5] && null != split1[4] && null != split1[3]) {

            if(scheduleModel.getJobType().intValue() == 0){

                //秒
                cronExp.append(split1[5]).append(" ");

                //每分钟
                cronExp.append("* ").append(" ");
                cronExp.append("* ");//小时
                cronExp.append("* ");//日
                cronExp.append("* ");//月
                cronExp.append("?");//周
            }else if(scheduleModel.getJobType().intValue() == 1){

                //秒
                cronExp.append(split1[5]).append(" ");
                //分
                cronExp.append(split1[4]).append(" ");

                //每小时
                cronExp.append("* ");//小时
                cronExp.append("* ");//日
                cronExp.append("* ");//月
                cronExp.append("?");//周
            }else if(scheduleModel.getJobType().intValue() == 2 || scheduleModel.getJobType().intValue() == 3 ||
                    scheduleModel.getJobType().intValue() == 4 || scheduleModel.getJobType().intValue() == 5){

                //秒
                cronExp.append(split1[5]).append(" ");
                //分
                cronExp.append(split1[4]).append(" ");
                //时
                cronExp.append(split1[3]).append(" ");
            }

            //按每日
            if (scheduleModel.getJobType().intValue() == 2) {

                cronExp.append("* ");//日
                cronExp.append("* ");//月
                cronExp.append("?");//周
            }
            //按每周
            else if (scheduleModel.getJobType().intValue() == 3) {

                String[] split2 = scheduleModel.getStartDate().split(" ");
                Calendar instance = Calendar.getInstance();//创建格林威治时间
//                instance.setTime(date);//将传来的时间设置到格林威治时间内
                int dayForWeek = instance.get(Calendar.DAY_OF_WEEK) - 1;
                //获取本周的周几
//                int dayForWeek = DataUtils.dayForWeek(split2[0]);
                //一个月中第几天
                cronExp.append("? ");
                //月份
                cronExp.append("* ");
                //周
                cronExp.append(dayForWeek + 1);
            }
            //按每月
            else if (scheduleModel.getJobType().intValue() == 4) {
                //一个月中的哪几天
                cronExp.append(split1[2]);
                //月份
                cronExp.append(" * ");
                //周
                cronExp.append("?");
            }
            //按每年
            else if (scheduleModel.getJobType().intValue() == 5) {

                //一个月中的哪几天
                cronExp.append(split1[2]).append(" ");
                //月份
                cronExp.append(split1[1]).append(" ");
                //周
                cronExp.append("?");
            }

        } else {
            System.out.println("时或分或秒参数未配置");//时或分或秒参数未配置
        }
        return cronExp.toString();
    }
}
