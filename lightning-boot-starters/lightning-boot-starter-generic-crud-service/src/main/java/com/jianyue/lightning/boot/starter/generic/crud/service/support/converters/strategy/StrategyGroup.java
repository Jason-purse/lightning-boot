package com.jianyue.lightning.boot.starter.generic.crud.service.support.converters.strategy;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 13:45
 * @Description 验证组,还可以增加
 *
 * 为这些验证组开启aop aspect 支持
 *
 *
 * 对于自定义的参数支持(因为这些通用类型的校验组根本不够) ..
 * @see StrategyGroupSupport 能够设置自定义基于线程绑定的变量值(additionalState 伴生变量) ... 进行额外的处理 ..
 *
 * 注意,最好不要和已有的通用的校验组联合使用,本身已有的校验组有对应的aop,进行代理,如果重复使用将导致代理负担 ..
 */
public interface StrategyGroup {

    /**
     * 每一个新的验证组需要注册 ..
     * @param group group
     */
    static void registerValidationGroup(Class<? extends StrategyGroup> group) {
        StrategyGroupAssist.register(group);
    }

    static Set<Class<? extends StrategyGroup>> getAllValidationGroups() {
        return StrategyGroupAssist.getAllValidationGroups();
    }

}

class StrategyGroupAssist {
    private final static Set<Class<? extends StrategyGroup>> validationGroups = new LinkedHashSet<>();

    static  {
        validationGroups.add(ADD.class);
        validationGroups.add(SELECT_LIST.class);
        validationGroups.add(SELECT_ONE.class);
        validationGroups.add(SELECT_BY_ID.class);
        validationGroups.add(UPDATE.class);
        validationGroups.add(DELETE.class);
        validationGroups.add(DELETE_BY_ID.class);
        validationGroups.add(SELECT_LIST_AND_PAGE.class);
    }

    static void register(Class<? extends StrategyGroup> group) {
        validationGroups.add(group);
    }

    static Set<Class<? extends StrategyGroup>> getAllValidationGroups() {
        return Collections.unmodifiableSet(validationGroups);
    }
}
