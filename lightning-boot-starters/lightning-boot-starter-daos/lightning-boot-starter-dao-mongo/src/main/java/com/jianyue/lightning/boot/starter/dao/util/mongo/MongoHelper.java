package com.jianyue.lightning.boot.starter.dao.util.mongo;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author FLJ
 * @dateTime 2022/1/25 14:19
 * @description 它是一个Mongo 操作 db的工具类
 * @apiNote 主要是为了MongoTemplate 增强,并没有重写所有方法,主要是为了减少MongoTemplate的一个使用...
 *  但是复杂查询应该还是依靠MongoTemplate...
 */
@ConditionalOnBean(MongoTemplate.class)
@AutoConfigureOrder(Integer.MAX_VALUE)
public class MongoHelper {

    private final MongoTemplate mongoTemplate;

    public MongoHelper(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public <T> T findOne(MongoQuery<T> mongoQuery, @Nullable String collectionName) {
        if(collectionName == null) {
            return mongoTemplate.findOne(mongoQuery.getQuery(),mongoQuery.getEntityClass());
        }
        return mongoTemplate.findOne(mongoQuery.getQuery(),mongoQuery.getEntityClass(),collectionName);
    }

    public <T> T findOne(MongoQuery<T> mongoQuery) {
        return findOne(mongoQuery,null);
    }

    public <T> List<T> find(MongoQuery<T> mongoQuery, String collectionName) {
        if(collectionName == null) {
            return mongoTemplate.find(mongoQuery.getQuery(),mongoQuery.getEntityClass());
        }
        return mongoTemplate.find(mongoQuery.getQuery(),mongoQuery.getEntityClass(),collectionName);
    }

    public <T> List<T> find(MongoQuery<T> mongoQuery) {
        return find(mongoQuery,null);
    }

    public <T>  List<T> findAll(Class<T> tClass,String collectionName) {
        if(collectionName == null) {
            return mongoTemplate.findAll(tClass);
        }
        return mongoTemplate.findAll(tClass,collectionName);
    }
    public <T>  List<T> findAll(Class<T> tClass) {
        return findAll(tClass,null);
    }

    public <T> Long count(MongoQuery<T> mongoQuery, String collectionName) {
        if(collectionName == null) {
            return mongoTemplate.count(mongoQuery.getQuery(),mongoQuery.getEntityClass());
        }
        return mongoTemplate.count(mongoQuery.getQuery(),mongoQuery.getEntityClass(),collectionName);
    }
    public <T> Long count(MongoQuery<T> mongoQuery) {
        return count(mongoQuery,null);
    }

    public <T> T insert(T t,String collectionName) {
        if(collectionName == null) {
            return  mongoTemplate.insert(t);
        }
        return mongoTemplate.insert(t,collectionName);
    }

    public <T> T insert(T t) {
        return insert(t,null);
    }

    public <T> T save(T t,String collectionName) {
        if(collectionName == null) {
            return  mongoTemplate.save(t);
        }
        return mongoTemplate.save(t,collectionName);
    }

    public <T> T save(T t) {
        return save(t,null);
    }

    public <T> Collection<T> insertAll(Collection<T> ts, String collectionName) {
        if(collectionName == null) {
            return  mongoTemplate.insert(ts);
        }
        return mongoTemplate.insert(ts,collectionName);
    }
    public <T> Collection<T> insertAll(Collection<T> ts,Class<T> tClass) {
        if(tClass == null) {
            return  mongoTemplate.insert(ts);
        }
        return mongoTemplate.insert(ts,tClass);
    }




}
