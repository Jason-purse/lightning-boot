package com.jianyue.lightning.boot.starter.system.log;

import org.jetbrains.annotations.Nullable;

/**
 * @author FLJ
 * @date 2022/12/26
 * @time 11:02
 * @Description user scope
 * <p>
 * 用于获取当前用户信息
 */
public interface UserScope {
    /**
     * 获取用户名称
     *
     * @return 当前登录用户 / null
     */
    @Nullable
    String getUserName();
}
