package com.jianyue.lightning.boot.starter.system.log;

import com.jianyue.lightning.boot.starter.system.log.aop.LogAspect;
import com.jianyue.lightning.boot.starter.system.log.service.SystemLogService;
import org.springframework.context.annotation.Import;

/**
 * @author FLJ
 * @date 2022/12/26
 * @time 11:05
 * @Description 系统日志自动配置
 */
@Import({LogAspect.class, SystemLogService.class})
public class SystemLogAutoConfiguration {
}
