package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy;

import com.jianyue.lightning.boot.starter.generic.crud.service.entity.IdSupport;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.Converter;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.QueryConverter;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport;

/**
 * @author FLJ
 * @date 2022/12/12
 * @time 15:59
 * @Description 能够转换为QuerySupport的 在校验组的情况下支持的实现 适配器
 * <p>
 * 基于验证组的情况下,来实现 对应分类组的 参数转换为查询实现 !!!!
 */
public interface DefaultStrategySupportForQueryAdapter<SOURCE extends IdSupport<?>> extends
        DefaultStrategySupportAdapter<SOURCE, QuerySupport>,
        QueryConverter<SOURCE> {

    default QuerySupport convert(SOURCE param) {
        return validationHandle(param);
    }
}
