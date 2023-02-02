package com.jianyue.lightning.boot.starter.system.log.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 系统日志实体
 *
 * @author WangMingLiang
 * @date 2020/10/13 18:26
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document("d_system_log")
public class SystemLogEntity {

    @Id
    private String id;

    private String username;

    private String operation;

    private Integer time;

    private String method;

    private String params;

    private String ip;

    private LocalDateTime createTime;

}
