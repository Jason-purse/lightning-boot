package com.jianyue.lightning.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.jianyue.lightning.exception.DefaultApplicationException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author FLJ
 * @date 2021/11/15 12:39
 * @description json util
 * <p>
 * 安全的 json 工具类, 用于json 解析,会适当的抛出 DefaultApplicationException异常 ..
 */
public class JsonUtil {

    private final ObjectMapper objectMapper;

    private JsonUtil() {
        this.objectMapper = new ObjectMapper();
        initialize();
    }

    private void initialize() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    private final static JsonUtil jsonUtil = JsonUtil.withDefaultDateOfChina();

    public static JsonUtil getDefaultJsonUtil() {
        return jsonUtil;
    }

    public JsonUtil registerModule(Module module) {
        objectMapper.registerModule(module);
        return this;
    }

    public JsonUtil registerModules(Module... modules) {
        objectMapper.registerModules(modules);
        return this;
    }

    public JsonUtil registerModules(Iterable<Module> modules) {
        objectMapper.registerModules(modules);
        return this;
    }

    public JsonUtil configureObjectMapper(Consumer<ObjectMapper> consumer) {
        consumer.accept(objectMapper);
        return this;
    }

    public static JsonUtil of() {
        return new JsonUtil();
    }


    public static JsonUtil withDefaultDateOfChina() {

        JsonUtil jsonUtil = new JsonUtil();
        jsonUtil.configureObjectMapper(objectMapper -> {
            // 根据毫秒时间读取 并反序列化instant
            objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
            objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
            // 至少保证getter方法的字段正确序列化出来
            // 切记getter 需要注意形式
            objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY);
        });

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        simpleModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);

        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        simpleModule.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        jsonUtil.registerModule(simpleModule);

        return jsonUtil;
    }


    /**
     * 从输入流中获取一个map
     *
     * @param stream stream data
     * @return Map<?, ?>
     * @throws DefaultApplicationException 解析失败 / 或者空参数问题
     */
    @NotNull
    public Map<String, ?> asMap(@Nullable InputStream stream) {
        if (stream != null) {
            try {
                return objectMapper.readValue(stream, new TypeReference<>() {
                });
            } catch (Exception e) {
                // pass
                throw DefaultApplicationException.of(e.getMessage(), e);
            }
        }
        throw DefaultApplicationException.of("stream must not be null !!!");
    }

    /**
     * 解析http Servlet request parameters
     *
     * @param request request
     * @param clazz   clazz
     * @param <T>     type
     * @return instance
     * @throws DefaultApplicationException 解析失败 / 或者空参数问题
     */
    @NotNull
    public <T> T fromRequest(HttpServletRequest request, Class<T> clazz) {
        if (request != null) {
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (ObjectUtils.isNotEmpty(parameterMap)) {
                HashMap<String, String> map = new HashMap<>();
                parameterMap.forEach((key, value) -> {
                    if (value != null && StringUtils.isNotBlank(value[0])) {
                        map.put(key, value[0]);
                    }
                });
                return convertTo(map, clazz);
            }
        }
        throw DefaultApplicationException.of("request must not be null !!!");
    }

    /**
     * 转换到目标类对象
     *
     * @param object origin 不能是 string json ..
     * @param clazz  target
     * @param <T>    class type
     * @return instance
     * @throws DefaultApplicationException 解析失败 / 或者空参数问题
     */
    @NotNull
    public <T> T convertTo(@Nullable Object object, @Nullable Class<T> clazz) {

        if (clazz != null && object != null) {
            try {
                return objectMapper.convertValue(object, clazz);
            } catch (Exception e) {
                throw DefaultApplicationException.of(e.getMessage(), e);
            }
        }

        throw DefaultApplicationException.of("can't convert to instance of target class, object or clazz must not be null !!!");
    }

    /**
     * 从Json 进行读取
     *
     * @return instance
     * @throws DefaultApplicationException 解析失败 / 或者空参数问题
     */
    @NotNull
    public <T> T fromJson(@Nullable String json, @Nullable Class<T> clazz) {
        if (clazz != null && json != null) {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (Exception e) {
                throw DefaultApplicationException.of(e.getMessage(), e);
            }
        }
        throw DefaultApplicationException.of("can't convert to instance of target class, json or clazz must not be null !!!");
    }

    /**
     * 从inputStream 进行读取
     *
     * @return instance
     * @throws DefaultApplicationException 解析失败 / 或者空参数问题
     */
    @NotNull
    public <T> T fromJson(@Nullable InputStream stream, @Nullable Class<T> clazz) {
        if (stream != null && clazz != null) {
            try {
                return objectMapper.readValue(stream, clazz);
            } catch (Exception e) {
                throw DefaultApplicationException.of(e.getMessage(), e);
            }
        }
        throw DefaultApplicationException.of("can't convert to instance of target class, stream or clazz must not be null !!!");
    }

    /**
     * 同上
     *
     * @return instance
     * @throws DefaultApplicationException 解析失败 / 或者空参数问题
     */
    @NotNull
    public <T> T fromJson(@Nullable String json, @Nullable TypeReference<T> typeReference) {
        if (json != null && typeReference != null) {
            try {
                return objectMapper.readValue(json, typeReference);
            } catch (Exception e) {
                throw DefaultApplicationException.of(e.getMessage(), e);
            }
        }
        throw DefaultApplicationException.of("can't convert to instance of typeReference, json or typeReference must not be null !!!");
    }

    /**
     * 同上
     *
     * @return instance
     * @throws DefaultApplicationException 解析失败 / 或者空参数问题
     */
    @NotNull
    public <T> T fromJson(@Nullable InputStream stream, @Nullable TypeReference<T> clazzRef) {
        if (stream != null && clazzRef != null) {
            try {
                return objectMapper.readValue(stream, clazzRef);
            } catch (Exception e) {
                throw DefaultApplicationException.of(e.getMessage(), e);
            }
        }
        throw DefaultApplicationException.of("can't convert to instance of target clazzRef, stream or clazzRef must not be null !!!");
    }

    @NotNull
    public <T> T fromJson(@NotNull String value, @NotNull JavaType javaType) {
        try {
            return objectMapper.readValue(value, javaType);
        } catch (Exception e) {
            throw DefaultApplicationException.of(e.getMessage(), e);
        }
    }

    /**
     * 创建一个参数化类型
     */
    @NotNull
    public <R, T> JavaType createJavaType(@NotNull Class<R> rawType) {
        return objectMapper.getTypeFactory().constructType(rawType);
    }

    /**
     * 创建一个参数化类型
     */
    @NotNull
    public <R, T> JavaType createJavaType(@NotNull Class<R> rawType, @NotNull Class<T> parameterClass) {
        return objectMapper.getTypeFactory().constructParametricType(rawType, parameterClass);
    }

    /**
     * 创建一个参数化类型 ..
     *
     * @param rawType       rawType
     * @param parameterType 参数化type
     */
    @NotNull
    public <R> JavaType createJavaType(@NotNull Class<R> rawType, @NotNull JavaType parameterType) {
        return objectMapper.getTypeFactory().constructParametricType(rawType, parameterType);
    }

    /**
     * 创建一个参数化类型 ..
     *
     * @param rawType        rawType
     * @param parameterTypes 参数化types
     */
    @NotNull
    public <R> JavaType createJavaType(@NotNull Class<R> rawType, @NotNull JavaType... parameterTypes) {
        return objectMapper.getTypeFactory().constructParametricType(rawType, parameterTypes);
    }

    /**
     * 创建一个参数化类型 .
     *
     * @param typeReference typeReference
     */
    public <R> JavaType createJavaType(@NotNull TypeReference<R> typeReference) {
        return objectMapper.getTypeFactory().constructType(typeReference);
    }

    /**
     * 创建一个参数化类型
     */
    @NotNull
    public <R> JavaType createJavaType(@NotNull Class<R> rawType, @NotNull Class<?>... parameterClasses) {
        return objectMapper.getTypeFactory().constructParametricType(rawType, parameterClasses);
    }


    /**
     * 使用typeReference 解析实例
     *
     * @param object        data 不能是字符串json
     * @param typeReference obtain class type reference
     * @param <T>           class type
     * @return instance or null
     */
    @NotNull
    public <T> T convertTo(@Nullable Object object, @Nullable TypeReference<T> typeReference) {
        if (typeReference != null && object != null) {
            try {
                return objectMapper.convertValue(object, typeReference);
            } catch (Exception e) {
                throw DefaultApplicationException.of(e.getMessage(), e);
            }
        }
        throw DefaultApplicationException.of("can't convert to instance of typeReference, object or typeReference must not be null !!!");
    }

    /**
     * 转为json
     *
     * @param object target
     * @return object json
     */
    @NotNull
    public String asJSON(@Nullable Object object) {
        if (object != null) {
            try {
                return objectMapper.writeValueAsString(object);
            } catch (Exception e) {
                throw DefaultApplicationException.of(e.getMessage(), e);
            }
        }
        throw DefaultApplicationException.of("can't write json , object must not be null !!!");
    }
}
