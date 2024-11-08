package com.dg.communication.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.communication.utils
 * @author: XBin
 * @description: 时间工具类
 * @createTime: 2024-01-09 10:04
 */

public class TimeUtils {

    public static String getLocalTime(String timeFormatter){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timeFormatter);
        LocalDateTime now = LocalDateTime.now();
        return now.format(dateTimeFormatter);
    }

    public static String getDateTime(Date submitTime, String timeFormatter){
        // 使用提供的时间格式创建SimpleDateFormat对象
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormatter);
        // 将提交时间（Date对象）格式化为字符串
        return sdf.format(submitTime);
    }
}
