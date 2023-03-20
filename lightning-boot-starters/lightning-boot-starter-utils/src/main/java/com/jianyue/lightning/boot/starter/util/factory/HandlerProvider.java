package com.jianyue.lightning.boot.starter.util.factory;

import com.jianyue.lightning.boot.starter.util.dataflow.impl.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public interface HandlerProvider {

        Object key();

        boolean support(Object predicate);

        @NotNull
        Handler getHandler();

        @SuppressWarnings("unchecked")
        default <T extends HandlerProvider> T nativeHandlerProvider() {
            try {
                return (T) this;
            } catch (Exception e) {
                throw new IllegalStateException("can't cast to native Handler provider,current handler provider is " + this.getClass().getName());
            }
        }

        static <Key, H extends Handler> HandlerProvider of(
                Key key, Predicate<Object> predicate, Handler handler
        ) {
            return new DefaultGenericHandlerProvider<>(key, predicate, handler);
        }

        static <Key, H extends Handler> List<HandlerProvider> list(
                Key key, List<Tuple<Predicate<Object>, H>> handlers
        ) {
            return handlers.stream()
                    .map(ele -> (HandlerProvider) new DefaultGenericHandlerProvider<>(key, ele.getFirst(), ele.getSecond()))
                    .toList();
        }

}
class DefaultGenericHandlerProvider<Key, H extends Handler> implements HandlerProvider {
        private final Key key;
        private final H handler;
        private final Predicate<Object> predicate;

        public DefaultGenericHandlerProvider(Key key, Predicate<Object> predicate, H handler) {
            this.key = key;
            this.predicate = predicate;
            this.handler = handler;
        }

        @Override
        public Object key() {
            return key;
        }

        @Override
        public boolean support(Object predicate) {
            return this.predicate.test(predicate);
        }

        @NotNull
        @Override
        public Handler getHandler() {
            return handler;
        }
    }