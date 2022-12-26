package com.jianyue.lightning.boot.starter.generic.crud.service.entity;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JdbcBaseEntity<ID> extends BaseEntity implements Entity, IdSupport<ID> {

    private ID id;

}
