package com.jianyue.lightning.boot.starter.generic.crud.service.entity;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class JpaBaseEntity<ID>  extends JpaEntity implements Entity, IdSupport<ID>{

}
