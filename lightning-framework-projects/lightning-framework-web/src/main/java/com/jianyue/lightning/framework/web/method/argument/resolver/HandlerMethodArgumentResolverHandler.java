package com.jianyue.lightning.framework.web.method.argument.resolver;

import com.jianyue.lightning.boot.starter.util.factory.TransformHandler;
/**
 * @author FLJ
 * @date 2023/2/23
 * @time 10:43
 * @Description 提供方法参数和WebRequest 然后进行对象处理 !!!
 *
 * 通过将 MethodArgumentContext 转换为 一个对象 !!!!!
 * 这就是基于{@link com.jianyue.lightning.boot.starter.util.factory.HandlerFactory}的最终{@link org.springframework.web.method.support.HandlerMethodArgumentResolver}
 * 的具体委派接口类 !!!
 */
public interface HandlerMethodArgumentResolverHandler extends TransformHandler<MethodArgumentContext,Object> {

}
