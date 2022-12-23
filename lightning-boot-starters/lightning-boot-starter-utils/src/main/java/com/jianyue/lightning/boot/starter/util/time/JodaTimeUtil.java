package com.jianyue.lightning.boot.starter.util.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DateTime工具
 *
 * int year,
 *      *             int monthOfYear,
 *      *             int dayOfMonth,
 *      *             int hourOfDay,
 *      *             int minuteOfHour,
 *      *             int secondOfMinute,
 *      *             int millisOfSecond,
 *      *             DateTimeZone zone
 * @author konghang
 */
public class JodaTimeUtil {

    /**
     * 一天开始
     *
     * @param dateTime
     * @return
     */
    public static DateTime beginOfDay(DateTime dateTime){
        int y = dateTime.getYear();
        int m = dateTime.getMonthOfYear();
        int d = dateTime.getDayOfMonth();
        return new DateTime(y, m, d, 0, 0, 0, 0, dateTime.getZone());
    }

    /**
     * 一天结束
     *
     * @param dateTime
     * @return
     */
    public static DateTime endOfDay(DateTime dateTime){
        int y = dateTime.getYear();
        int m = dateTime.getMonthOfYear();
        int d = dateTime.getDayOfMonth();
        return new DateTime(y, m, d, 23, 59, 59, 999, dateTime.getZone());
    }

    /**
     * 是否是同一天
     * @param day
     * @param compare
     * @return
     */
    public static Boolean isSameDay(DateTime day, DateTime compare){
        if (!day.getZone().equals(compare.getZone())){
            return false;
        }

        int y = day.getYear();
        int m = day.getMonthOfYear();
        int d = day.getDayOfMonth();

        int cy = compare.getYear();
        int cm = compare.getMonthOfYear();
        int cd = compare.getDayOfMonth();
        return y == cy && m == cm && d == cd;
    }

    public static List<DateTime> getDateTimeDays(DateTime begin, DateTime end){
        if (end.getMillis() < begin.getMillis()){
            return Collections.emptyList();
        }

        if (isSameDay(begin, end)){
            return Collections.singletonList(begin);
        }
        List<DateTime> days = new ArrayList<>();
        while (true){
            days.add(begin);
            if (isSameDay(begin, end)){
                break;
            }
            begin = begin.plusDays(1);
        }
        return days;
    }

    /**
     * 查询一天每小时
     * @param day
     * @return
     */
    public static List<DateTime> getDateTimeHours(DateTime day){

        DateTime begin = beginOfDay(day);
        DateTime nextBegin = begin.plusDays(1);

        List<DateTime> days = new ArrayList<>();
        while (true){
            if (isSameDay(begin, nextBegin)){
                break;
            }
            days.add(begin);
            begin = begin.plusHours(1);
        }
        return days;
    }

    public static List<DateTime> getDateTimeWeekFirstDays(DateTime begin, DateTime end){
        begin = beginOfWeek(begin);
        end = endOfWeek(end);

        List<DateTime> days = new ArrayList<>();
        while (true){
            if (begin.isAfter(end)){
                break;
            }
            days.add(begin);
            begin = begin.plusDays(7);
        }
        return days;
    }

    public static DateTime beginOfWeek(DateTime dateTime){
        return beginOfDay(dateTime).minusDays(dateTime.getDayOfWeek() - 1);
    }


    public static DateTime endOfWeek(DateTime dateTime){
        return beginOfWeek(dateTime).plusDays(6);
    }

    public static DateTime beginOfMonth(DateTime dateTime){
        return beginOfDay(dateTime.withDayOfMonth(1));
    }


    public static DateTime endOfMonth(DateTime dateTime){
        return beginOfDay(dateTime.dayOfMonth().withMaximumValue());
    }

    public static List<DateTime> getDateTimeMonthFirstDays(DateTime begin, DateTime end){
        begin = beginOfMonth(begin);
        end = endOfMonth(end);

        List<DateTime> days = new ArrayList<>();
        while (true){
            if (begin.isAfter(end)){
                break;
            }
            days.add(begin);
            begin = begin.plusMonths(1);
        }
        return days;
    }

    /**
     * 获取相差天数
     * @param begin
     * @param end
     * @return
     */
    public static Integer getIntervalDays(DateTime begin, DateTime end){
        begin = beginOfDay(begin);
        end = beginOfDay(end);
        long beginMillis = begin.getMillis();
        long endMillis = end.getMillis();
        long oneDayMillis = 1000*3600*24L;
        Long daysMillis = (endMillis-beginMillis)/oneDayMillis;
        return daysMillis.intValue();

    }

    public static void main(String[] args) {
        DateTimeZone dateTimeZone = DateTimeZone.forID("Asia/Shanghai");
        DateTime dateTime = new DateTime(dateTimeZone);
        DateTime end = dateTime.minusDays(5);
        List<DateTime> timeHours = getDateTimeHours(dateTime);
        List<DateTime> days = getDateTimeDays(end,dateTime);
        timeHours.forEach(date ->{
            String s = date.toString(DateFormatUtil.PATTERN_ISO);
            System.out.println(s);
        });
        days.forEach(dateTime1 -> {
            String s = dateTime1.toString(DateFormatUtil.PATTERN_ISO);
            System.out.println(s);
        });
        Integer days1 = JodaTimeUtil.getIntervalDays(dateTime, end);
        System.out.println("days:"+days1);
    }
}
