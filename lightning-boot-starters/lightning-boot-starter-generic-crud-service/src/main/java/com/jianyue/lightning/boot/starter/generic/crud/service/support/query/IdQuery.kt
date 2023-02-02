package com.jianyue.lightning.boot.starter.generic.crud.service.support.query

/**
 * @date 2022/12/8
 * @time 20:55
 * @author FLJ
 * @since 2022/12/8
 *
 *
 * 实现了此接口,就可以进行Id获取
 **/
interface IdQuery<ID,QueryInfo> : Query<QueryInfo>, IDQuerySupport {

    /**
     * Id: any ..
     */
    fun  getId(): ID


    fun getIdClass(): Class<*>
}