package com.jianyue.lightning.framework.web.method.argument.resolver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
/**
 * @author FLJ
 * @date 2023/2/23
 * @time 10:45
 * @Description 方法参数上下文 ..
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MethodArgumentContext {

    @NotNull
    private MethodParameter methodParameter;

    @Nullable
    private ModelAndViewContainer mavContainer;

    @NotNull
    private NativeWebRequest request;

    @Nullable
    private WebDataBinderFactory binderFactory;
}
