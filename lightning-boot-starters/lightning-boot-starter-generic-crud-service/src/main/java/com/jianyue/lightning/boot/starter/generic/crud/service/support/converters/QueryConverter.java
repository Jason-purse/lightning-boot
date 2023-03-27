package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Type;
/**
 * @date 2022/12/8
 * @time 21:03
 * @author FLJ
 * @since 2022/12/8
 *
 *
 * query 参数转换器,一般param 会接收前台的 query数据,实现了这种接口的param 能够直接转换为Query,进行数据库条件查询
 *
 *
 * 此Converter 可以结合
 * @see com.jianyue.lightning.boot.starter.generic.crud.service.config.ControllerValidationAopAspectConfiguration 来基于线程安全的方式,获取当前存储的有关参数的校验组
 * @see com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy.StrategyGroupSupport 来根据不同校验组形成,不同的Query ..
 *
 * 同样,此类可以使用一个接口P 作为许多子类型的处理的ConverterAdapter(或者代理 / 包装器模式,然后代理到具体子类的转换器实现) ...
 * 减少代码修改范围 ...
 **/
public interface QueryConverter<P> extends Converter<P, QuerySupport> {
    @Override
    default boolean support(@NotNull Object param) {
        return ResolvableType.forType(getSourceClass()).isInstance(param);
    }

    @NotNull
    @Override
    default Type getTargetClass() {
        return QuerySupport.class;
    }
}
