package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters;

import java.lang.reflect.Type;

public interface Converter<S,T> extends ConverterSupport {
    /**
     * 是否支持转换
     */
    boolean support(Object param);


//    /**
//     * 由于 kotlin的使用点 差异,导致有可能拿到的converter 是一个S == Noting的情况(例如使用 out(生产者的情况下),只能生产不能消费)
//     * 借助此方法实现,一个伪强转 ..
//     *
//     * java 好像不再需要 ..
//     */
//    @SuppressWarnings("unchecked")
//    default T convertForAny(Object param) {
//        return convert(((S) param));
//    }


    /**
     * 转换过程
     */
    T convert(S param);


    Type getSourceClass();

    Type getTargetClass();
}
