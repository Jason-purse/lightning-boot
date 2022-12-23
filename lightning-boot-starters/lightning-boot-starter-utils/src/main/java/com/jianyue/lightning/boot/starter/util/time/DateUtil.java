package com.jianyue.lightning.boot.starter.util.time;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 *
 * @author konghang
 */
public class DateUtil extends org.springside.modules.utils.time.DateUtil {

    /**
     * 获取连续的天
     *
     * @param begin
     * @param end
     * @return
     */
    public static List<Date> getDays(Date begin, Date end){
        if (org.springside.modules.utils.time.DateUtil.isSameDay(begin, end)){
            return Lists.newArrayList(begin);
        }

        List<Date> days = Lists.newArrayList();
        while (true){
            days.add(begin);
            if (org.springside.modules.utils.time.DateUtil.isSameDay(begin, end)){
                break;
            }
            begin = org.springside.modules.utils.time.DateUtil.addDays(begin, 1);
        }
        return days;
    }
}
