package com.jianyue.lightning.boot.starter.util.time;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 中国周工具类
 *
 * @author konghang
 */
public class ChineseWeekUtil {

    public final static List<String> weeks = Lists.newArrayList("周一", "周二", "周三", "周四", "周五", "周六", "周日");

    public static String getWeek(int week){
        return weeks.get(week-1);
    }
}
