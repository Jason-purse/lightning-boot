package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters;

import com.jianyue.lightning.boot.starter.util.BeanUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface EntityConverter<SOURCE,Entity> extends Converter<SOURCE, Entity> {

    @Override
    default boolean support(@NotNull Object param) {
        // 判断是否支持这种参数转换,默认行为,没有考虑复杂的类体系结构
        // 例如参数化类型 assignable 处理

        // 当Source 是 param的父接口,或许你可能需要重写此方法,判断是否为具体类型(也就是协变处理) ..
        // 否则你可能会得到一些错误 ...
        if (getSourceClass() instanceof Class<?>) {
            return ((Class<?>) getSourceClass()).isInstance(param);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    default Entity convert(Object param) {
        return convertToEntity((SOURCE)param);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    default Entity convertToEntity(SOURCE param) {
        try {
            return (Entity) Objects.requireNonNull(BeanUtils.transformFrom(param, ((Class<?>) getTargetClass())));
        } catch (Exception e) {
            // for developer
            throw new IllegalArgumentException("can't convert to Entity for current param " + param);
        }
    }
}
