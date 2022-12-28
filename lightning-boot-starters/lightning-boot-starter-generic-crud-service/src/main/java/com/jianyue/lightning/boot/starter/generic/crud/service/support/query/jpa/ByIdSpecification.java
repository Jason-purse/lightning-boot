package com.jianyue.lightning.boot.starter.generic.crud.service.support.query.jpa;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
/**
 * @author FLJ
 * @date 2022/12/28
 * @time 14:07
 * @Description id equals speicification
 */
public class ByIdSpecification<ID,T> implements Specification<T> {

    private final JpaEntityInformation<T, ?> entityInformation;

    private final ID id;

    public ByIdSpecification(JpaEntityInformation<T, ?> entityInformation,ID id) {
        this.entityInformation = entityInformation;
        this.id = id;
    }

    @Override
    public Predicate toPredicate(@NotNull Root<T> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
        SingularAttribute<? super T, ?> idAttribute = entityInformation.getIdAttribute();
        return criteriaBuilder.equal(root.get(idAttribute),id);
    }
}
