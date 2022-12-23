package com.jianyue.lightning.boot.starter.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author FLJ
 * @dateTime 2022/1/18 13:48
 * @description 反射工具类, 屏蔽框架底层
 */
public class ReflectionUtil {

    /**
     * 设置可访问对象的可访问权限为 true
     *
     * @param object 可访问的对象
     * @param <T>    类型
     * @return 返回设置后的对象
     */
    public static <T extends AccessibleObject> T setAccessible(T object) {
        if(object instanceof Method) {
            ReflectionUtils.makeAccessible(((Method) object));
        }
        else if(object instanceof Constructor) {
            ReflectionUtils.makeAccessible(((Constructor<?>) object));
        }
        else if(object instanceof Field) {
            ReflectionUtils.makeAccessible(((Field) object));
        }
        else {
            if (!object.isAccessible()) {
                object.setAccessible(true);
            }
        }
        return object;
    }
}