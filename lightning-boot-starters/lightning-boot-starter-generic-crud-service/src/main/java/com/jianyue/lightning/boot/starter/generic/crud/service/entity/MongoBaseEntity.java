package com.jianyue.lightning.boot.starter.generic.crud.service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@NoArgsConstructor
public abstract class MongoBaseEntity<ID> extends MongoEntity<ID> {

    @Id
    private ID id;

}