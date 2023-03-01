package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters

import com.jianyue.lightning.framework.generic.crud.abstracted.param.DefaultAsSupport

/**
 * 隔离,泛型的繁琐 ... QueryConverter 应该直接实现QueryConverter,而不应该实现此接口 ..
 *
 * 和ConverterSupport 同理 !!!
 */
interface QueryConverterSupport : DefaultAsSupport<QueryConverterSupport> {

}