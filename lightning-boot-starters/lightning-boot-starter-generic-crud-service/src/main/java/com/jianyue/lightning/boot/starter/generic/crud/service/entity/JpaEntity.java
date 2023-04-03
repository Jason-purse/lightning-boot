package com.jianyue.lightning.boot.starter.generic.crud.service.entity;

import com.jianyue.lightning.boot.starter.generic.crud.service.support.entity.Entity;
import com.jianyue.lightning.boot.starter.generic.crud.service.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class JpaEntity<ID> implements Entity<ID> {
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Long createTime;

    /**
     * 创建时间字符串
     */
    @Column(name = "create_time_str")
    private String createTimeStr;

    @Column(name = "update_time")
    private Long updateTime;

    @Column(name = "update_time_str")
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
