package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa

import com.jianyue.lightning.boot.starter.generic.crud.service.support.query.AbstractQueryInfo

/**
 * @author FLJ
 * @date 2022/12/28
 * @time 13:49
 * @Description 基于 T 的通用 Jpa QueryInfo ..
 */
class JpaQueryInfo<T>(entity: T): AbstractQueryInfo<T>(entity) {

}