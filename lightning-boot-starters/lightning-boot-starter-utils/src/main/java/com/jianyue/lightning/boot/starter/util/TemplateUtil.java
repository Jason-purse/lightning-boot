package com.jianyue.lightning.boot.starter.util;

import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author FLJ
 * @date 2022/11/2
 * @time 12:25
 * @Description 模板工具类
 */
public class TemplateUtil {

    private final static Map<String, Pattern> patternMap = new ConcurrentHashMap<String, Pattern>();

    /**
     * 将source 中的特殊标记进行替换
     * <p>
     * 第一次 正则表达式 构建或许要慢很多 ...
     * 但是后续调用 比normalize 快 10000 nanos ...
     *
     * @param source source
     * @param mark   mark
     * @param args   参数
     * @return 格式化字符串 ...
     */
    public static String normalizeStr(String source, String mark, String... args) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(args);
        Objects.requireNonNull(mark);

        if (args.length == 0) {
            return source;
        }
        val pattern = patternMap.computeIfAbsent(mark.trim(), Pattern::compile);

        val matcher = pattern.matcher(source);

        // 注释代码和 normalize 性能差不多 ..
        //StringBuffer builder = new StringBuffer();
        //
        //int count = 0;
        //int lastEnd = -1;
        //while (matcher.find()) {
        //    if (count < args.length) {
        //        matcher.appendReplacement(builder, args[count++]);
        //    }
        //    lastEnd = matcher.end();
        //}
        //if(lastEnd < source.length() - 1) {
        //    matcher.appendTail(builder);
        //}
        //return builder.toString();


        StringBuilder builder = new StringBuilder();
        int count = 0;
        int lastEnd = -1;
        while (matcher.find()) {
            val start = matcher.start();
            val end = matcher.end();
            if (count == 0) {
                if (start != 0) {
                    builder.append(source, 0, start);
                }
            } else {
                builder.append(source, lastEnd, start);
            }
            // 不用 ++ 了,已经没有可以替换的参数了 ...
            if (count < args.length) {
                builder.append(args[count++]);
            }

            lastEnd = end;
        }

        if (count != 0) {
            if (lastEnd < source.length() - 1) {
                return builder.append(source, lastEnd, source.length()).toString();
            }
            return builder.toString();
        }

        return source;

    }

    public static String normalizeStr(String source, String mark, List<String> args) {
        return normalizeStr(source,mark,args.toArray(new String[0]));
    }

    /**
     * 这个效率比 normalizeStr 整整低 10000 nanos ...
     *
     * @param source source
     * @param mark   mark
     * @param args   args
     * @return 最终的字符串 ...
     */
    public static String normalize(String source, String mark, String... args) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(args);
        Objects.requireNonNull(mark);
        mark = mark.trim();
        val split = source.split(mark);
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String section : split) {
            builder.append(section);
            if (count < args.length) {
                builder.append(args[count++]);
            } else if (section.length() > 0) {
                builder.append(mark);
            }
        }

        return builder.toString();
    }


}
