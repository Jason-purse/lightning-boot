package com.jianyue.lightning.boot.starter.system.log.service;

import com.jianyue.lightning.boot.starter.system.log.dtos.SystemLogDto;
import com.jianyue.lightning.boot.starter.system.log.dtos.SystemLogQueryDto;
import com.jianyue.lightning.boot.starter.system.log.models.SystemLogEntity;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统日志接口
 *
 * @author WangMingLiang
 * @date 2020/10/13 19:28
 */
@AllArgsConstructor
@Service
public class SystemLogService {

    private final MongoTemplate mongoTemplate;

    /**
     * 分页查询接口
     *
     * @param param 查询条件
     */
    public List<SystemLogDto> list(SystemLogQueryDto param) {
        int pageNum = 0;
        if (Objects.nonNull(param.getPageNum()) && param.getPageNum() > 1) {
            pageNum = param.getPageNum() - 1;
        }
        int pageSize = 10;
        if (Objects.nonNull(param.getPageSize()) && param.getPageSize() > 0) {
            pageSize = param.getPageSize();
        }
        PageRequest pageParam = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(param.getCreateTimeStart()) && Objects.nonNull(param.getCreateTimeEnd())) {
            criteria.and("createTime").gt(param.getCreateTimeStart()).lt(param.getCreateTimeEnd());
        }
        if (StringUtils.isNotEmpty(param.getIp())) {
            criteria.and("ip").regex(param.getIp());
        }
        if (StringUtils.isNotEmpty(param.getMethod())) {
            criteria.and("method").regex(param.getMethod());
        }
        if (StringUtils.isNotEmpty(param.getOperation())) {
            criteria.and("operation").regex(param.getOperation());
        }
        if (StringUtils.isNotEmpty(param.getUsername())) {
            criteria.and("username").regex(param.getUsername());
        }
        query.addCriteria(criteria);
        long count = mongoTemplate.count(query, SystemLogEntity.class);
        query.with(pageParam);
        query.skip((long) pageParam.getPageNumber() * pageParam.getPageSize());
        query.limit(pageParam.getPageSize());
        List<SystemLogEntity> entities = mongoTemplate.find(query, SystemLogEntity.class);
        return entities.stream().map(e -> {
            SystemLogDto dto = new SystemLogDto();
            BeanUtils.copyProperties(e, dto);
            return dto;
        }).collect(Collectors.toList());
    }

}
