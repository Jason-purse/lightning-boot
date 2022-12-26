package com.jianyue.lightning.framework.generic.crud.abstracted.param;

/**
 * @author FLJ
 * @date 2022/12/10
 * @time 8:55
 * @since 2022/12/10
 * <p>
 * as 语义是强转,协变为具体类型 ..
 * <p>
 * S 原始类型, T 它的协变类型
 * <p>
 * 此接口的实例方法 / 扩展方法都有可能抛出ClassCastException ...
 **/
public interface AsSupport<S extends AsSupport<S>> {


}
