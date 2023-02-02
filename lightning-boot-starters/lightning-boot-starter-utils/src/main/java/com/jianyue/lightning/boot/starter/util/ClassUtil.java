package com.jianyue.lightning.boot.starter.util;

import org.springframework.core.ResolvableType;
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



    // --------------- logic covariant equals ---------------------

    /**
     * 就是判断可达性,虽然Collection<String>  和 List<String> 是两个类型,但是本质上List<String>属于Collection<String>的子类型,
     * 除了这些已经允许的规则之外,希望List<String> 其实可以逆变为 Collection<Object>,但是这是完全不同的类型,并且由于不符合
     * Collection<T>的规则,因为当T 为Object的时候,List作为Collection的子类,那么要么直接指定类型参数为Object 或者是也作为一个类型变量,
     * <p>
     * 不管如此,最终需要的是类型变量T 所呈现的实际的类型参数是Object,而不是String,这就是为什么两个接口不一样的原因 ..
     * <p>
     * 其次,Java做出泛型约束,是为了让泛型类中包含的元素都是符合约定的,
     * 例如一个员工集合中可以加入管理员,但是管理员中不能够加入员工 ..
     * 但其实一个管理员集合是一个员工集合 ... 也许我们可以写出这种逆变差异性的使用点差异(use site variance)
     * Collection<? extends Object>,这样就意味着一个管理员集合可以作为员工集合表示 ...
     * 这个接口的意义也就是能够生产出Object的元素的集合,那么管理员集合当然可以,这属于生产者概念(PESC) ..
     * 那么由于java 只有使用点差异,这就导致,判断两个类上的可能是否存在使用点差异的类型参数是不可能的 ..
     * <p>
     * 也就是Collection<Object> 在接口声明上 永远不可能等于List<String>,
     * 此方法就是为了将接口声明上的一个类型进行断言,判断是否能够逻辑上逆变为一个更大(可以包容它)的类型 !!!
     * 也就是说管理员集合(它使用的容器是List<String>) 逆变为 员工集合(Collection<Object>)的一个判断 ..
     * <p>
     * <p>
     * 但是使用的情况下,需要将具体的参数化类型放在当前方法上进行使用 ...
     *
     * 根据baseType 进行逻辑上的可达性 处理
     *
     * @param parent   parent
     * @param child    child
     * @param baseType baseType
     * @return true / false
     */
    public static boolean logicCovariantAssert(Type parent, Type child, Class<?> baseType) {
        // 类型断言
        if (!typeAssert(parent, child)) {
            return false;
        }
        // 简单类判断 ..
        if (isAssignableFromForClass(parent, child)) {
            return true;
        }
        // class / parameterizedType
        if (typeAssert(parent, child)) {
            ResolvableType resolvableType = ResolvableType.forType(parent);
            ResolvableType childResolvableType = ResolvableType.forType(child);
            ResolvableType baseResolvedType = ResolvableType.forType(baseType);
            // 必须要有这个接口 或者类
            if (baseResolvedType.isAssignableFrom(resolvableType) && baseResolvedType.isAssignableFrom(childResolvableType)) {
                ResolvableType parentParameterizedType = resolvableType.as(baseType);
                ResolvableType childParameterizedType = childResolvableType.as(baseType);
                if (parentParameterizedType.isAssignableFrom(childParameterizedType)) {
                    return true;
                } else {
                    // "逻辑" - 使用点差异判断
                    ResolvableType[] generics = resolvableType.getGenerics();
                    ResolvableType[] generics1 = childParameterizedType.getGenerics();
                    // 必须相同,否则不相同
                    if (generics.length == generics1.length) {
                        // 相同的情况下
                        for (int i = 0; i < generics.length; i++) {
                            // 先尝试使用点差异判断
                            if (!generics[i].isAssignableFrom(generics1[i])) {
                                // 强转类型,在尝试匹配
                                if (!logicCovariantAssert(generics[i].getType(), generics1[i].getType(), generics[i].getRawClass())) {
                                    return false;
                                }
                            }
                        }
                        // 完全匹配
                        return true;
                    }
                }
            }
        }


        return false;
    }

    private static boolean typeAssert(Type parent, Type child) {
        return (parent instanceof ParameterizedType || parent instanceof Class) && (child instanceof ParameterizedType || child instanceof Class);
    }


    private static boolean isAssignableFromForClass(Type parent, Type child) {
        // 使用java 语义判断 ..
        if (parent instanceof Class<?> && child instanceof Class<?>) {
            return ((Class<?>) parent).isAssignableFrom(((Class<?>) child));
        }
        return false;
    }

}

