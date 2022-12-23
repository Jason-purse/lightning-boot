package com.jianyue.lightning.boot.starter.web.logs;
/**
 * @author FLJ
 * @date 2021/11/19 11:04
 * @description 当前线程下的 HandleMethod方法 参数拦截
 */
public class DataUtil {

    private final static ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    private DataUtil() {

    }
    public static Object get() {
        return threadLocal.get();
    }
    public static Object set(Object args) {
        Object o = threadLocal.get();
        if(args == null) {
            threadLocal.remove();
        }
        else {
            threadLocal.set(args);
        }
        return o;
    }

    public static Object remove() {
        return set(null);
    }

}
