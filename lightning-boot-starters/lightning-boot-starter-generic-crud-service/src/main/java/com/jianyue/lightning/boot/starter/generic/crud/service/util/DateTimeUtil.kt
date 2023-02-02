package com.jianyue.lightning.boot.starter.generic.crud.service.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
/**
 * @author FLJ
 * @date 2022/12/26
 * @time 11:19
 * @Description 内部的日期时间 工具类
 */
internal class DateTimeUtil(dateTimeFormatter: DateTimeFormatter) {

    private val defaultDateTimeFormatter: DateTimeFormatter

    init {
        defaultDateTimeFormatter = dateTimeFormatter
    }

    constructor() : this(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA))


    fun getTimeStamp(date: Date): Long {
        return date.time
    }

    fun getCurrentTimeStamp(): Long {
        return getTimeStamp(Date())
    }

    fun getCurrentTimeDateFormatText(): String {
        return getDateTimeFormatText(Date())
    }

    fun getDateTimeFormatText(date: Date): String {
        return defaultDateTimeFormatter.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()))
    }


    companion object {
        /**
         * 全局的
         */
        val dateTimeFormatter = DateTimeUtil()
    }
}