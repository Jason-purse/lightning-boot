package com.jianyue.lightning.boot.starter.generic.crud.service.support.entity

import com.jianyue.lightning.boot.starter.generic.crud.service.entity.IdSupport
import java.io.Serializable

/**
 * @date 2022/12/10
 * @time 10:35
 * @author FLJ
 * @since 2022/12/10
 *
 * Entity 基本信息,需要可序列化
 **/
interface Entity<ID> : EntitySupport,IdSupport<ID>,Serializable {

    /**
     * 保存填充
     */
    fun saveFill();

    /**
     * 更新填充
     */
    fun updateFill();
}