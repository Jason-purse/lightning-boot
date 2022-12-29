package com.jianyue.lightning.boot.starter.generic.crud.service.entity;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@NoArgsConstructor
public abstract class MongoBaseEntity<ID> extends MongoEntity implements Entity, IdSupport<ID> {

    @Id
    private ID id;

}