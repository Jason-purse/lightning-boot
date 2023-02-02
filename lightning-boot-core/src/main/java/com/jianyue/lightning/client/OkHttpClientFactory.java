package com.jianyue.lightning.client;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.net.Proxy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author konghang
 */
public class OkHttpClientFactory {

    public static OkHttpClient getOkHttpClient(boolean log, boolean isproxy, Proxy proxy) {
        return getOkHttpClient(log, isproxy, proxy, null, null, null);
    }

    public static OkHttpClient getOkHttpClient(boolean log, boolean isproxy, Proxy proxy, Integer connectTimeout, Integer readTimeout, Integer writeTimeout) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (log) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addNetworkInterceptor(logInterceptor);
        }
        if (Objects.nonNull(connectTimeout)) {
            builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        }
        if (Objects.nonNull(readTimeout)) {
            builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        }
        if (Objects.nonNull(writeTimeout)) {
            builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        }
        if (isproxy){
            builder.proxy(proxy);
        }
        return builder.build();
    }
}
