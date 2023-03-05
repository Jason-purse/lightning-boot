package com.jianyue.lightning.framework.web.annotations.method;

public interface ArgumentResolveStrategy {

    String type();

    /**
     * 支持 类路径的形式加载参数 !!!
     */
    public static final Class<? extends ArgumentResolveStrategy> SPI = SpiArgumentResolveStrategy.class;

    /**
     * 支持工厂的形式加载参数 !!!
     */
    public static final Class<? extends ArgumentResolveStrategy> FACTORY = FactoryArgumentResolveStrategy.class;
}

class SpiArgumentResolveStrategy implements ArgumentResolveStrategy {
    @Override
    public String type() {
        return "SPI";
    }
}

class FactoryArgumentResolveStrategy implements ArgumentResolveStrategy {
    @Override
    public String type() {
        return "Factory";
    }
}
