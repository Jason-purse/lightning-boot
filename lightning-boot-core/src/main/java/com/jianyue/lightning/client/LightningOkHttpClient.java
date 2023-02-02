package com.jianyue.lightning.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jianyue.lightning.exception.DefaultApplicationException;
import com.jianyue.lightning.result.Result;
import com.jianyue.lightning.util.JsonUtil;
import okhttp3.*;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Okhttp客户端
 *
 * @author konghang
 */
public class LightningOkHttpClient {

    final static String EMPTY_STR = "";
    final static String QUESTION_MARK = "?";
    final static String AND = "&";
    final static String EQ = "=";

    private final OkHttpClient okHttpClient;

    /**
     * 默认构造器：不开启日志和代理
     */
    public LightningOkHttpClient() {
        this(Boolean.FALSE, null);
    }

    /**
     * 默认不开启代理
     *
     * @param log
     */
    public LightningOkHttpClient(Boolean log) {
        this(log, null);
    }

    /**
     * 可开启日志和代理
     *
     * @param log
     * @param proxyConfig
     */
    public LightningOkHttpClient(Boolean log, ProxyConfig proxyConfig) {
        this(log, proxyConfig, null, null, null);
    }

    /**
     * 可开启日志和代理
     *
     * @param log
     * @param proxyConfig
     */
    public LightningOkHttpClient(Boolean log, ProxyConfig proxyConfig, Integer connectTimeout, Integer readTimeout, Integer writeTimeout) {
        Proxy proxy = null;
        boolean isProxy = Objects.nonNull(proxyConfig) && proxyConfig.getIsProxy();
        if (Objects.nonNull(proxyConfig) && proxyConfig.getIsProxy()) {
            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort()));
        }
        this.okHttpClient = OkHttpClientFactory.getOkHttpClient(log, isProxy, proxy, connectTimeout, readTimeout, writeTimeout);
    }

    /**
     * 同okhttp比较仅返回比较有用的信息，
     * 且将连接释放回连接池供其他连接使用
     */
    public LightningResponse execute(Request request) {
        Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            ResponseBody body = response.body();
            return LightningResponse.builder()
                    .code(response.code())
                    // 不一定会有响应体 ..
                    .body(body != null ? body.toString() : "")
                    .build();
        } catch (IOException e) {
            throw new DefaultApplicationException("request execute failure !!!", e);
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
    }

    /**
     * 获取Okhttp
     *
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        return this.okHttpClient;
    }



    public <T> T getResultForGet(String param, String path, Class<T> targetClazz) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(path + param)).newBuilder();
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();
        LightningResponse lightningResponse = this.execute(request);
        String responseBody = lightningResponse.getBody();
        final Result<T> data = JsonUtil.getDefaultJsonUtil().fromJson(responseBody, JsonUtil.getDefaultJsonUtil().createJavaType(Result.getDefaultImplementClass(), targetClazz));
        // 都看作 success
        if (data.getCode() >= 200 && data.getCode() < 300) {
            return data.getResult();
        }
        throw DefaultApplicationException.of(data.getCode(),data.getMessage());
    }


    /**
     * Get 数组方法统一返回List对象
     */
    public <T> List<T> getListOfResultForGet(String param, String path, Class<T> clazz) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(path + param)).newBuilder();
        Request request = (new okhttp3.Request.Builder()).url(urlBuilder.build()).get().build();
        LightningResponse lightningResponse = this.execute(request);
        String responseBody = lightningResponse.getBody();
        Result<T> result =
                JsonUtil.getDefaultJsonUtil().fromJson(
                        responseBody, JsonUtil.getDefaultJsonUtil().createJavaType(Result.getDefaultImplementClass(),
                                clazz)
                );

        if (result.getCode() >= 200 && result.getCode() < 300) {
            if (result.hasResults()) {
                return result.getResults();
            }
        }

        throw DefaultApplicationException.of(result.getCode(),result.getMessage());
    }

    /**
     * Post 返回对象
     */
    public <T> T getResultForJsonPost(Object o, String path, Class<? extends T> clazz) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(path)).newBuilder();
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(RequestBody.create( JsonUtil.getDefaultJsonUtil().asJSON(o),json))
                .build();
        LightningResponse lightningResponse = this.execute(request);
        String responseBody = lightningResponse.getBody();
        Result<T> result =
                JsonUtil.getDefaultJsonUtil().fromJson(
                        responseBody, JsonUtil.getDefaultJsonUtil().createJavaType(Result.getDefaultImplementClass(),
                                clazz)
                );

        if (result.getCode() >= 200 && result.getCode() < 300) {
            if (result.hasResult()) {
                return result.getResult();
            }
        }
        throw DefaultApplicationException.of(result.getCode(), result.getMessage());
    }

    /**
     * 第三方请求 Get 对象方法统一返回对象
     *
     * @param param 参数
     * @param path  请求路径
     * @return 不确定的格式, 自己去转
     */
    public Map<?, ?> getDataForOtherGet(String param, String path, Map<String, String> headers) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(path + param)).newBuilder();
        Request.Builder builder = new Request.Builder()
                .url(urlBuilder.build())
                .get();
        Request request;
        if (ObjectUtils.isEmpty(headers)) {
            request = builder.build();
        } else {
            request = builder.headers(genHeaders(headers)).build();
        }

        LightningResponse lightningResponse = this.execute(request);
        String responseBody = lightningResponse.getBody();
        // 有可能就是没有
        return JsonUtil.getDefaultJsonUtil().fromJson(responseBody, Map.class);
    }

    public <T> T getDataForOtherGet(String param, String path, Map<String, String> headers, TypeReference<T> typeReference) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(path + param)).newBuilder();
        Request.Builder builder = new Request.Builder()
                .url(urlBuilder.build())
                .get();
        Request request;
        if (ObjectUtils.isEmpty(headers)) {
            request = builder.build();
        } else {
            request = builder.headers(genHeaders(headers)).build();
        }

        LightningResponse lightningResponse = this.execute(request);
        String responseBody = lightningResponse.getBody();

        // 有可能就是没有
        return JsonUtil.getDefaultJsonUtil().fromJson(responseBody, typeReference);
    }

    /**
     * 第三方请求 Get 对象方法统一返回对象
     *
     * @param param 参数
     * @param path  请求路径
     * @return 不确定的格式, 自己去转
     */
    public <T> T getDataForOtherGet(String param, String path, Map<String, String> headers, String key, com.fasterxml.jackson.core.type.TypeReference<T> typeReference) {
        Map<?, ?> resultForGetThird = this.getDataForOtherGet(param, path, headers);
        Object o = resultForGetThird.get(key);
        return JsonUtil.getDefaultJsonUtil().convertTo(o, typeReference);
    }


    /**
     * 添加header
     *
     * @param headers header
     * @return 返回header
     */
    public Headers genHeaders(Map<String, String> headers) {
        Headers.Builder builder = new Headers.Builder();
        if (ObjectUtils.isNotEmpty(headers)) {
            headers.forEach(builder::set);
        }
        return builder.build();
    }

    /**
     * Post 返回对象
     * 注意使用jackson反序列化时,boolean类型最好不用isXXX格式
     * 如果使用了必须将类型改为 Boolean不要用boolean切记.
     *
     * @param o             查询对象
     * @param path          请求路径
     * @param <T>           泛型
     * @param key           返回的结果获取具体数据的key
     * @param headers       请求头
     * @param typeReference 返回的类型
     * @return t 有可能为null(无法转换) ..
     */
    public <T> T getResultForOtherPost(
            Object o,
            String path,
            TypeReference<T> typeReference,
            Map<String, String> headers,
            String key) {

        Map<?, ?> map = getDataForOtherPost(o, path, headers);
        if (ObjectUtils.isEmpty(key)) {
            return JsonUtil.getDefaultJsonUtil().convertTo(map, typeReference);
        }
        Object data = map.get(key);
        return JsonUtil.getDefaultJsonUtil().convertTo(data, typeReference);
    }


    /**
     * Post 返回对象
     * 注意使用jackson反序列化时,boolean类型最好不用isXXX格式
     * 如果使用了必须将类型改为 Boolean不要用boolean切记.
     *
     * @param formData 查询对象
     * @param path     请求路径
     * @param headers  请求头
     * @return t
     */
    public Map<?, ?> getDataForOtherPost (
            Object formData,
            String path,
            Map<String, String> headers) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(path)).newBuilder();
        FormBody f = null;
        if (FormBody.class.isAssignableFrom(formData.getClass())) {
            f = (FormBody) formData;
        } else if (Map.class.isAssignableFrom(formData.getClass())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) formData;
            FormBody.Builder builder = new FormBody.Builder();
            map.forEach((k, v) -> builder.add(k, v.toString()));
            f = builder.build();
        }
        else {
            f = new FormBody.Builder().build();
        }
        Request.Builder builder = new Request.Builder()
                .url(urlBuilder.build())
                .post(f);
        if (ObjectUtils.isNotEmpty(headers)) {
            builder.headers(genHeaders(headers));
        }
        Request request = builder.build();
        LightningResponse lightningResponse = this.execute(request);
        String responseBody = lightningResponse.getBody();
        return JsonUtil.getDefaultJsonUtil().fromJson(responseBody, Map.class);
    }


    /**
     * Post 返回对象
     * 注意使用jackson反序列化时,boolean类型最好不用isXXX格式
     * 如果使用了必须将类型改为 Boolean不要用boolean切记.
     *
     * @param o       查询对象
     * @param path    请求路径
     * @param headers 请求头
     * @return t
     */
    public Map<?, ?> getDataForOtherJsonPost(
            Object o,
            String path,
            Map<String, String> headers) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(path)).newBuilder();
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        Request.Builder builder = new Request.Builder()
                .url(urlBuilder.build())
                .post(RequestBody.create(json, JsonUtil.getDefaultJsonUtil().asJSON(o)));
        if (ObjectUtils.isNotEmpty(headers)) {
            builder.headers(genHeaders(headers));
        }
        Request request = builder.build();
        LightningResponse lightningResponse = this.execute(request);
        return JsonUtil.getDefaultJsonUtil().fromJson(lightningResponse.getBody(), Map.class);
    }


}
