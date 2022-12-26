package com.jianyue.lightning.boot.starter.generic.crud.service.support.entity

/**
 * @date 2022/12/10
 * @time 10:35
 * @author FLJ
 * @since 2022/12/10
 *
 * Entity 基本信息
 **/
interface Entity : EntitySupport {

    /**
     * 保存填充
     */
    fun saveFill();

    /**
     * 更新填充
     */
    fun updateFill();
}