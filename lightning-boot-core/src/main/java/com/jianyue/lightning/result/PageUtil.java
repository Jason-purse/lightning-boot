package com.jianyue.lightning.result;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author FLJ
 * @date 2023/3/21
 * @time 16:03
 * @Description lightning page util
 * <p>
 * 简单的将接收 前端传递的参数
 */
@Getter
public class PageUtil<T> {

    private PageUtil(int page, int size, long total) {
        this(page, size, total, Collections.emptyList());
    }

    private PageUtil(int page, int size, long total, @Nullable List<T> content) {

        this.page = page > 0 ? page : 1;
        this.size = size > 0 ? size : 10;
        this.total = total > 0 ? total : 0;

        if (this.page > 1) {
            this.previousPage = true;
        } else {
            this.previousPage = false;
        }

        if (content == null) {
            content = Collections.emptyList();
        }
        this.content = content;

        // 偏距
        this.offset = this.page > 1 ? (this.page - 1) * this.size : 0;
    }

    private final List<T> content;

    private final int page;

    private final int size;

    private final int offset;


    private final long total;

    private final boolean previousPage;

    /**
     * 拿到下一页,但是复制了当前分页的内容 ..
     */
    public PageUtil<T> next() {
        // 全部复制 ..
        return new PageUtil<>(page + 1, size, total, content);
    }


    public static <T> PageUtil<T> of(int page, int size) {
        return new PageUtil<>(page, size, 0);
    }


    public static <T> PageUtil<T> first() {
        return new PageUtil<>(1, 10, 0);
    }

    public static <T> PageUtil<T> first(int size) {
        return new PageUtil<>(1, size, 0);
    }


    public static <T> PageUtil<T> of(int page, int size, long total, @NotNull List<T> content) {
        return new PageUtil<>(page, size, total, content);
    }

    /**
     * @param pageObj 可以提供 page / size 参数
     * @param total   需要总数
     * @param content 内容数据
     */
    public static <T> PageUtil<T> copyOf(PageUtil<T> pageObj, int total, @NotNull List<T> content) {
        // 内容为空
        if (content == null) {
            throw new IllegalArgumentException("content must not be null !!!");
        }
        return new PageUtil<>(pageObj.page, pageObj.size, total, content);
    }

    /**
     * 例如条件处理的 而给出的一个empty ..
     */
    public static <T> PageUtil<T> empty(int page, int size, long total) {
        return new PageUtil<>(page, size, total);
    }

    /**
     * 当前页空 .. 或者完全空 ...
     *
     * @param pageObj 可以提供 page / size 参数
     * @param total   需要总数
     */
    public static <T> PageUtil<T> empty(PageUtil<T> pageObj, long total) {
        return empty(pageObj.page, pageObj.size, total);
    }

    /**
     * 完全为空
     *
     * @param pageObj 提供 page /size
     */
    public static <T> PageUtil<T> fullEmpty(PageUtil<T> pageObj) {
        return empty(pageObj, 0);
    }

    public static <T> PageUtil<T> fullEmpty(int page, int size) {
        return empty(page, size, 0);
    }

}
