package com.jianyue.lightning.framework.web.util;

import org.springframework.util.ClassUtils;

/**
 * 类工具类
 */
public class ClassUtil {

    public static String getPackageNameForClass(String mainApplicationClass) {
        try {
            Class<?> aClass = ClassUtils.forName(mainApplicationClass, ClassUtils.getDefaultClassLoader());
            return aClass.getPackageName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("can't find package name of  the application class !!!");
        }
    }

}
