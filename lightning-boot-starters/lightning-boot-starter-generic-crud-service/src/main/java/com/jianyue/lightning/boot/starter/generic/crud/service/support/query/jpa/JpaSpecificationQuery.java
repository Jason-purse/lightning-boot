package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author FLJ
 * @date 2023/3/28
 * @time 9:46
 * @Description jpa specification query
 * <p>
 * T 标识一个Entity类型 ..
 */
public class JpaSpecificationQuery<T> implements JpaQuery<Specification<T>> {

    private final JpaQueryInfo<Specification<T>> queryInfo;

    public JpaSpecificationQuery(JpaQueryInfo<Specification<T>> queryInfo) {
        this.queryInfo = queryInfo;
    }

    @Override
    public JpaQueryInfo<Specification<T>> getQueryInfo() {
        return queryInfo;
    }
}
