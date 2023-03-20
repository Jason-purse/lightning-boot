package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters

import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.EntityConverter
import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity
import com.jianyue.lightning.framework.generic.crud.abstracted.param.Param
import java.lang.reflect.Type

/**
 * @author FLJ
 * @date 2022/12/14
 * @time 9:38
 * @Description 基于参数自由的 默认的Entity 转换器
 */
class BasedParamFreeEntityConverter(
    private val paramClazz: Class<out Param>,
    private val entityClazz: Class<out Entity>
) : EntityConverter<Param, Entity> {

    override fun getSourceClass(): Type {
        return paramClazz;
    }

    override fun getTargetClass(): Type {
        return entityClazz;
    }
}