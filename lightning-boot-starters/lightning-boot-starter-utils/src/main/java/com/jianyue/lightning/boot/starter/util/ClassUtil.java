package com.jianyue.lightning.boot.starter.util;

import org.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author FLJ
 * @dateTime 2022/1/25 13:22
 * @description 类工具
 */
public class ClassUtil {

    public static Type findGenericInterfaceForClass(Class<?> target,Class<?> interfaceClass) {
        if(interfaceClass.isInterface()) {
            Class<?> userClass = ClassUtils.getUserClass(target);
            if(Object.class != userClass) {
                Type[] genericInterfaces = target.getGenericInterfaces();
                for (Type genericInterface : genericInterfaces) {
                    // 参数化类型..
                    if(genericInterface instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                        if(interfaceClass.isAssignableFrom(rawType)) {
                            return parameterizedType;
                        }
                    }
                    if(genericInterface instanceof Class<?>) {
                        Class<?> aClass = (Class<?>) genericInterface;
                        if(interfaceClass.isAssignableFrom(aClass)) {
                            return genericInterface;
                        }
                    }
                }

                // 向上递归查找
                return findGenericInterfaceForClass(target.getSuperclass(),interfaceClass);
            }
        }
        return null;
    }

    public static Type findGenericInterfaceForInstance(Object target,Class<?> interfaceClass) {
        return findGenericInterfaceForClass(target.getClass(),interfaceClass);
    }
}

