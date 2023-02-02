package com.jianyue.lightning.boot.starter.test.crud.service;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.service.AbstractCrudService;
import org.springframework.stereotype.Service;

/**
 * @author FLJ
 * @date 2022/12/26
 * @time 12:21
 * @Description default Object Service handle
 */
@Service
public class DefaultObjectService extends AbstractCrudService<DefaultParam, Entity> implements ObjectService {

}
