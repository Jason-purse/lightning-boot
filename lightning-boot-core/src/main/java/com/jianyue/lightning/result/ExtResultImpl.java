package com.jianyue.lightning.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ToString
public class ExtResultImpl<T> implements Result<T> {

    private final Integer code;

    private final String message;

    private final Long totalCount;

    @Nullable
    private final List<T> results;

    @Nullable
    private final T result;


    @JsonCreator
    public ExtResultImpl(@JsonProperty("code") Integer code,
                         @JsonProperty("message") String message,
                         @JsonProperty("totalCount") Long totalCount,
                         @JsonProperty("results") @Nullable List<T> results,
                         @JsonProperty("result") @Nullable T result) {
        this.results = results;
        this.result = result;
        this.code = code;
        this.message = message;
        this.totalCount = totalCount;
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
    public Long getTotalCount() {
        return totalCount;
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