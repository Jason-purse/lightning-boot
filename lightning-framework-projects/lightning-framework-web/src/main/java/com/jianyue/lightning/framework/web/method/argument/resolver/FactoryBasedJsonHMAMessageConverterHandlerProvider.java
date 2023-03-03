package com.jianyue.lightning.framework.web.method.argument.resolver;

import com.jianyue.lightning.boot.starter.util.factory.HandlerProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @author FLJ
 * @date 2023/3/3
 * @time 16:51
 * @Description {@link FactoryBasedJsonHMAMessageConverterHandler} 的提供器 !!
 *
 * 可以基于一批策略 !!!
 */
public interface FactoryBasedJsonHMAMessageConverterHandlerProvider extends HandlerProvider {


    @NotNull
    @Override
    FactoryBasedJsonHMAMessageConverterHandler getHandler();
}
