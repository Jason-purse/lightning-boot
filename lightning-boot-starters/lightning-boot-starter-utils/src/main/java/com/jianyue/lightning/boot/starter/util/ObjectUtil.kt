package com.jianyue.lightning.boot.starter.util

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 10:08
 * @Description 对象相关的工具方法
 */
fun <T : Any?> T.isNull(): Boolean {
    return this == null
}

fun <T : Any?> T.isNotNull(): Boolean {
    return !isNull()
}

fun CharSequence?.isNotBlank(): Boolean {
    return !this.isNullOrBlank()
}

fun CharSequence?.isBlank(): Boolean {
    return this.isNullOrBlank()
}


