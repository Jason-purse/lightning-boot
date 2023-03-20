package com.jianyue.lightning.boot.starter.util.factory;

public interface Handler {

        /**
         * 返回此handler 包装的内部Handler ..
         * 子类可以覆盖,实现不同的逻辑 ...
         *
         * @throws IllegalStateException 如果强转的类型不兼容,则抛出异常 ...
         */
        @SuppressWarnings("unchecked")
        default <T extends Handler> T nativeHandler() {
            try {
                return (T) this;
            } catch (Exception e) {
                throw new IllegalStateException("can't cast to native Handler,current handler is " + this.getClass().getName());
            }
        }
    }