package com.jianyue.lightning.boot.starter.design.patterns.strategy;


import com.jianyue.lightning.boot.starter.util.ClassUtil;
import com.jianyue.lightning.exception.DefaultApplicationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author FLJ
 * @dateTime 2022/1/25 10:27
 * @description 能够注册bean definition 的后置处理器
 */
@AutoConfigureOrder(Integer.MAX_VALUE)
public class StrategyServiceBeanPostProcessor implements BeanPostProcessor, ApplicationListener<ApplicationPreparedEvent> {

    private final Map<Class<?>, Object> globalCache = new ConcurrentHashMap<>();

    /**
     * 初始化之后处理..
     *
     * @param bean     bean
     * @param beanName beanName
     * @return 目标bean
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return internalHandleStrategyService(bean, beanName);
    }

    /**
     * 创建这些服务的策略服务代理
     *
     * @param bean     目标bean
     * @param beanName beanName
     */
    Object internalHandleStrategyService(Object bean, String beanName) {

        // 取消AbstractStrategyService 的代 理..
        if (AbstractStrategyService.class.isAssignableFrom(bean.getClass())) {
            return bean;
        }
        // 判断是否为代理类
        Class<?> userClass = ClassUtils.getUserClass(bean);

        // 判断是否为StrategyService 的子类
        if (StrategyService.class.isAssignableFrom(userClass)) {
            // 根据我们的策略 ,只需要解析StrategyService 即可
            Type[] genericInterfaces = userClass.getGenericInterfaces();
            // 表示存在,直接解析
            if (genericInterfaces.length > 0) {
                // 必然是一个参数化类型...
                try {
                    Type genericInterfaceForClass = ClassUtil.findGenericInterfaceForClass(userClass, StrategyService.class);
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterfaceForClass;
                    assert parameterizedType != null; // 生产环境下  自动移除 ...
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Type actualTypeArgument = actualTypeArguments[0];
                    // 如果不是Class
                    if (!(actualTypeArgument instanceof Class<?>)) {
                        // 不允许解析..
                        throw DefaultApplicationException.of("StrategyService<T> T 必须是一个Class,而非泛型!!");
                    }
                    Class<?> aClass = (Class<?>) actualTypeArgument;
                    return createProxyBy(bean, aClass, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw DefaultApplicationException.of("解析策略服务错误,当前策略服务接口存在异常,请检查!!!");
                }
            }
        }
        return bean;
    }

    /**
     * 创建一个代理
     *
     * @param bean  bean
     * @param clazz 目标类(真实目标类接口..)
     * @return 代理类...
     */
    Object createProxyBy(Object bean, Class<?> clazz, BeanFactory beanFactory) {
        StrategyService<?> strategyService = (StrategyService<?>) bean;
        Object proxy = globalCache.get(clazz);

        if (proxy == null) {
            synchronized (this) {
                proxy = globalCache.get(clazz);
                if (proxy == null) {
                    // 新建
                    proxy = createStrategyProxy(bean, clazz, beanFactory, strategyService);
                    globalCache.put(clazz, proxy);
                }
            }
        }
        // 增加策略服务..
        DefaultStrategyServiceInvocationHandler invocationHandler = (DefaultStrategyServiceInvocationHandler) Proxy.getInvocationHandler(proxy);
        invocationHandler.getTarget().addStrategyService(strategyService);
        return proxy;
    }

    Object createStrategyProxy(Object bean, Class<?> clazz, BeanFactory beanFactory, StrategyService<?> strategyService) {
        List<Class<?>> list = new ArrayList<>();
        if (!clazz.isInterface()) {
            list.addAll(Arrays.asList(clazz.getInterfaces()));
        } else {
            list.add(clazz);
        }
        // 它本身就是接口..
        AbstractStrategyService abstractStrategyService = new AbstractStrategyService(clazz);
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                list.toArray(new Class<?>[0]),
                new DefaultStrategyServiceInvocationHandler(abstractStrategyService));
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationPreparedEvent applicationPreparedEvent) {
        // 清理掉缓存..
        // 已经构造完毕...
        globalCache.clear();
    }
}
