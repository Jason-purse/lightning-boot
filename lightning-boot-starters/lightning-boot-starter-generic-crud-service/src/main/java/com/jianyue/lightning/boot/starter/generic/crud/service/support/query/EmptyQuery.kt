package com.jianyue.lightning.boot.starter.generic.crud.service.support.query

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 20:57
 * @Description 空QUery,不关心 QueryInfo
 */
interface EmptyQuery<QI> : Query<QI>,
    EmptyQuerySupport {

}