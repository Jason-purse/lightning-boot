package com.jianyue.lightning.boot.starter.system.log.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统日志查询封装类
 *
 * @author WangMingLiang
 * @date 2020/10/13 19:24
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SystemLogQueryDto {

    /**
     * 操作人姓名
     */
    private String username;

    /**
     * 操作
     */
    private String operation;

    /**
     * 执行方法
     */
    private String method;

    /**
     * 调用ip
     */
    private String ip;

    /**
     * 创建时间，左区间
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间，右区间
     */
    private LocalDateTime createTimeEnd;

    /**
     * 分页参数，起始页码，默认为1
     */
    private Integer pageNum;

    /**
     * 分页参数，页面数据量，默认10
     */
    private Integer pageSize;

}
