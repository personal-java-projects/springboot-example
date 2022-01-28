package com.example.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义日期类型转换器
 * 前端格式: "2022-01-28 10:11:00"
 * 用于mapStruct中：将字符串类型的日期转换为TimeStamp格式
 */
public class DateFormatUtil {
    public static Timestamp parse(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateStr);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
