package com.jianyue.lightning.boot.starter.generic.crud.service.support.entity

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 9:28
 * @Description 基础Entity 支持
 */
interface BaseEntitySupport {

}

/**
 * 转换为BaseEntity
 *
 * 没有做任何检查
 */
inline fun <reified T> BaseEntitySupport.convertBaseEntity(): T {
    return this as T;
}