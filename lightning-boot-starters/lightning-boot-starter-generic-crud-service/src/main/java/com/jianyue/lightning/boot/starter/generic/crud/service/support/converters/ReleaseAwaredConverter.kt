package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters

/**
 * 能够管理去释放资源 ...
 *
 * 仅限非Spring 管理的Converter ... 如果被Spring 管理,
 * 使用DisposableBean ..(如果在spring的管理情况下使用 ReleaseAwaredConverter可能会有意想不到的问题)
 */
interface ReleaseAwaredConverter {
    /**
     * 释放
     */
    fun release();
}