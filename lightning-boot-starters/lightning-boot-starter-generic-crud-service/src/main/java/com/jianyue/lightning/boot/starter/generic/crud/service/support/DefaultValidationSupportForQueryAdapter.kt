package com.jianyue.lightning.boot.starter.generic.crud.service.support

import com.jianyue.lightning.boot.starter.generic.crud.service.entity.IdSupport
import com.jianyue.lightning.boot.starter.generic.crud.service.query.QueryAssist
import com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.Converter
import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.QuerySupport
import com.jianyue.lightning.boot.starter.generic.crud.service.support.validates.DefaultValidationSupportAdapter

/**
 * @author FLJ
 * @date 2022/12/12
 * @time 15:59
 * @Description 能够转换为QuerySupport的 适配器
 */
interface DefaultValidationSupportForQueryAdapter<SOURCE : IdSupport<*>> :
    DefaultValidationSupportAdapter<SOURCE, QuerySupport>,
    Converter<SOURCE, QuerySupport> {


    override fun selectByIdGroupHandle(s: SOURCE): QuerySupport? {
        return QueryAssist.byId(s.id)
    }


    override fun deleteByIdGroupHandle(s: SOURCE): QuerySupport? {
        return QueryAssist.byId(s.id)
    }

    override fun updateGroupHandle(s: SOURCE): QuerySupport {
        return QueryAssist.byId(s.id)
    }


    override fun convert(param: SOURCE): QuerySupport? {
        return validationHandle(param);
    }
}