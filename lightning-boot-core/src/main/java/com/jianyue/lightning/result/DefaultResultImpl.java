package com.jianyue.lightning.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;
import java.util.List;

@ToString
public class DefaultResultImpl<T> implements Result<T> {

    private final Integer code;

    private final String message;

    @Nullable
    private final List<T> results;

    @Nullable
    private final T result;


    @JsonCreator
    public DefaultResultImpl(@JsonProperty("code") Integer code, @JsonProperty("message") String message,
                             @JsonProperty("results") @Nullable List<T> results,
                             @JsonProperty("result") @Nullable T result) {
        this.results = results;
        this.result = result;
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean hasResult() {
        return result != null;
    }

    @Override
    public boolean hasResults() {
        return results != null;
    }

    @Override
    public List<T> getResults() {
        return results;
    }

    @Override
    public T getResult() {
        return result;
    }
}