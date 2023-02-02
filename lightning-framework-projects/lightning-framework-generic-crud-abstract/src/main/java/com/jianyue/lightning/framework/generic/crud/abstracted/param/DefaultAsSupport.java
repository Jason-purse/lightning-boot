package com.jianyue.lightning.framework.generic.crud.abstracted.param;

/**
 * @author FLJ
 * @date 2022/12/10
 * @time 12:05
 * @since 2022/12/10
 * <p>
 * <p>
 * java 必须实现kotlin 父接口的default 方法,但是编译依旧会报错, java method can't call method of  parent interface directly ...
 **/
public interface DefaultAsSupport<S extends AsSupport<S>> extends AsSupport<S> {

    /**
     * @throws ClassCastException 可能会抛出类强转问题
     */
    default <T extends S> T asNativeObject(Class<T> clazz) {
        return clazz.cast(this);
    }
}
