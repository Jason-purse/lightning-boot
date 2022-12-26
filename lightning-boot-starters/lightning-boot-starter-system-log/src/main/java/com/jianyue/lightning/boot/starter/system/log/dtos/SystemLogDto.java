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
public class SystemLogDto {

    /**
     * 主键
     */
    private String id;

    /**
     * 操作人姓名
     */
    private String username;

    /**
     * 操作
     */
    private String operation;

    /**
     * 执行耗时，单位ms
     */
    private Integer time;

    /**
     * 执行方法
     */
    private String method;

    /**
     * 参数
     */
    private String params;

    /**
     * 调用ip
     */
    private String ip;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
