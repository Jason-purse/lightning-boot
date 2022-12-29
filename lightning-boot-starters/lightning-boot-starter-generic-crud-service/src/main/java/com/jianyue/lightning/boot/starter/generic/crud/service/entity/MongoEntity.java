package com.jianyue.lightning.boot.starter.generic.crud.service.entity;

import com.jianyue.lightning.boot.starter.generic.crud.service.util.DateTimeUtil;
import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 9:24
 * @Description 实体类的基本父类
 */
@Data
@NoArgsConstructor
public abstract class MongoEntity implements Entity {


    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建时间字符串
     */
    private String createTimeStr;


    private Long updateTime;

    private String updateTimeStr;

    @Override
    public void saveFill() {
        Date date = new Date();
        createTime = DateTimeUtil.Companion.getDateTimeFormatter().getTimeStamp(date);
        createTimeStr = DateTimeUtil.Companion.getDateTimeFormatter().getDateTimeFormatText(date);

        updateTime = createTime;
        updateTimeStr = createTimeStr;
    }

    @Override
    public void updateFill() {
        Date date = new Date();
        updateTime = DateTimeUtil.Companion.getDateTimeFormatter().getTimeStamp(date);
        updateTimeStr = DateTimeUtil.Companion.getDateTimeFormatter().getDateTimeFormatText(date);
    }
}
