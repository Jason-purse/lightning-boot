package com.jianyue.lightning.boot.starter.util.time;

import com.jianyue.lightning.exception.DefaultApplicationException;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springside.modules.utils.base.annotation.NotNull;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期格式化类
 *
 * 提供运行时异常
 * @author konghang
 */
public final class DateFormatUtil extends org.springside.modules.utils.time.DateFormatUtil {

    public static Date pareDate(@NotNull String pattern, @NotNull String dateString) {
        try {
            return FastDateFormat.getInstance(pattern).parse(dateString);
        } catch (ParseException e) {
            throw DefaultApplicationException.of("date parse failure !!!",e);
        }
    }

    public static Date pareDate(@NotNull String pattern, @NotNull String dateString, @NotNull String timezone) {
        try {
            return FastDateFormat.getInstance(pattern, TimeZone.getTimeZone(timezone)).parse(dateString);
        } catch (ParseException e) {
            throw DefaultApplicationException.of("date parse failure !!!",e);
        }
    }
}
