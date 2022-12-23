package com.jianyue.lightning.boot.starter.dao.util.mongo;


import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import com.jianyue.lightning.boot.starter.util.lambda.PropertyNamer;
import com.jianyue.lightning.boot.starter.util.lambda.SFunction;
import com.jianyue.lightning.exception.DefaultApplicationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author FLJ
 * @dateTime 2022/1/25 14:23
 * @description mongoQuery 组装器  简单封装..
 * 主要是去除regex 的特殊字符错误...
 */
public class MongoQuery<T> {
    // 特殊字符  无法用于正则匹配..
    private final List<String> speciesCharacters = Arrays.asList("\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|");
    private final List<String> escapedSpeciesCharacters = Arrays.asList("\\\\", "\\$", "\\(", "\\)", "\\*", "\\+", "\\.", "\\[", "\\]", "\\?", "\\^", "\\{", "\\}", "\\|");
    private final Query query;
    private Criteria criteria;
    private final Class<T> tClass;

    private MongoQuery(Class<T> tClass) {
        this.query = new Query();
        this.criteria = new Criteria();
        this.query.addCriteria(criteria);
        this.tClass = tClass;
    }

    public Class<T> getEntityClass() {
        return tClass;
    }

    public Query getQuery() {
        return query;
    }

    @SuppressWarnings("unchecked")
    public static <T> MongoQuery<T> of(T target) {
       return of((Class<T>) target.getClass());
    }

    public static <T> MongoQuery<T> of(Class<T> tClass) {
        return new MongoQuery<T>(tClass);
    }

    // 对象解析

    public MongoQuery<T> is(SFunction<T, ?> fn, Object value) {
        return exec(fn, value, Criteria::is);
    }

    public MongoQuery<T> not(SFunction<T, ?> fn) {
        return exec(fn, field -> criteria.not());
    }

    public MongoQuery<T> gt(SFunction<T, ?> fn, Object value) {
        return exec(fn, value, Criteria::gt);
    }

    public MongoQuery<T> gte(SFunction<T, ?> fn, Object value) {
        return exec(fn, value, Criteria::gte);
    }


    public MongoQuery<T> in(SFunction<T, ?> fn, Object... values) {
        return exec(fn, values, criteria::in);
    }

    public MongoQuery<T> in(SFunction<T, ?> fn, Collection<?> collection) {
        return exec(fn, collection, criteria::in);
    }

    public MongoQuery<T> lt(SFunction<T, ?> fn, Object value) {
        return exec(fn, value, Criteria::lt);
    }

    public MongoQuery<T> lte(SFunction<T, ?> fn, Object value) {
        return exec(fn, value, Criteria::lte);
    }

    public MongoQuery<T> ne(SFunction<T, ?> fn, Object value) {
        return exec(fn, value, (Criteria::ne));
    }

    public MongoQuery<T> nin(SFunction<T, ?> fn, Object... values) {
        return exec(fn, values, criteria::nin);
    }

    public MongoQuery<T> nin(SFunction<T, ?> fn, Collection<?> collection) {
        return exec(fn, collection, criteria::nin);
    }

    public MongoQuery<T> regex(SFunction<T, ?> fn, String value) {
        int i = speciesCharacters.indexOf(value);
        if (i > -1) {
            value = escapedSpeciesCharacters.get(i);
        }
        return exec(fn, value,(Criteria::regex));
    }

    public MongoQuery<T> orOperator() {
        Criteria criteriaTemp = new Criteria();
        criteria.orOperator(criteriaTemp);
        this.criteria = criteriaTemp;
        return this;
    }

    public MongoQuery<T> norOperator() {
        Criteria criteriaTemp = new Criteria();
        criteria.norOperator(criteriaTemp);
        this.criteria = criteriaTemp;
        return this;
    }

    public MongoQuery<T> exists(SFunction<T, ?> fn, boolean status) {
        return exec(fn, status, (Criteria::exists));
    }


    public MongoQuery<T> limit(int limit) {
        query.limit(limit);
        return this;
    }

    public MongoQuery<T> skip(int skip) {
        query.skip(skip);
        return this;
    }

    public MongoQuery<T> with(Sort sort) {
        query.with(sort);
        return this;
    }

    public MongoQuery<T> withSort(Sort.Direction direction, String... key) {
        query.with(Sort.by(direction, key));
        return this;
    }

    @SafeVarargs
    public final MongoQuery<T> withSort(Sort.Direction direction, SFunction<T, ?>... keys) {
        query.with(Sort.by(direction, Arrays.stream(keys).map(ele -> PropertyNamer.methodToProperty(LambdaUtils.getPropertyNameForLambda(ele))).toArray(String[]::new)));
        return this;

    }

    public MongoQuery<T> withSort(String... key) {
        query.with(Sort.by(key));
        return this;
    }

    public MongoQuery<T> withSort(Sort.Order... order) {

        query.with(Sort.by(order));
        return this;
    }

    public MongoQuery<T> withSort(List<Sort.Order> orders) {
        query.with(Sort.by(orders));
        return this;
    }

    public MongoQuery<T> with(Pageable pageable) {
        query.with(pageable);
        return this;
    }

    public MongoQuery<T> withByOne(int page, int pageSize) {
        query.with(PageRequest.of(page > 0 ? page - 1 : 0, pageSize));
        return this;
    }

    public MongoQuery<T> withByOne(Pageable pageable) {
        query.with(PageRequest.of(pageable.getPageNumber() > 0 ? -1 : 0, pageable.getPageSize(), pageable.getSort()));
        return this;
    }


    public MongoQuery<T> withByZero(int index, int pageSize) {
        query.with(PageRequest.of(Math.max(index, 0), pageSize));
        return this;
    }

    public MongoQuery<T> withByZero(Pageable pageable) {
        query.with(PageRequest.of(Math.max(pageable.getPageNumber(), 0), pageable.getPageSize(), pageable.getSort()));
        return this;
    }


    private <R> MongoQuery<T> exec(SFunction<T, ?> fn, R value, Function<String,Criteria> consumer, @Nullable BiConsumer<Criteria,R> valueConsumer) {
        try {
            String fieldName = PropertyNamer.methodToProperty(LambdaUtils.getPropertyNameForLambda(fn));
            Criteria apply = consumer.apply(fieldName);
            if (valueConsumer != null) {
                valueConsumer.accept(apply,value);
            }
        } catch (Exception e) {
            throw DefaultApplicationException.of("field resolve error!", e);
        }
        return this;
    }

    private MongoQuery<T> exec(SFunction<T, ?> fu, Function<String,Criteria> consumer) {
        return exec(fu, null, consumer, null);
    }

    private <R> MongoQuery<T> exec(SFunction<T, ?> fn, R value, BiConsumer<Criteria,R> valueConsumer) {
        return exec(fn, value, criteria::and, valueConsumer);
    }


}
