package com.jianyue.lightning.framework.web.method.argument.resolver.strategy;

import com.jianyue.lightning.framework.web.annotations.method.ArgumentResolveStrategy;
import com.jianyue.lightning.framework.web.annotations.method.ArgumentSpiStrategy;
import com.jianyue.lightning.framework.web.annotations.method.ArgumentStrategy;
import com.jianyue.lightning.framework.web.method.argument.resolver.FirstClassSupportHandlerMethodArgumentResolver;
import com.jianyue.lightning.util.JsonUtil;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.*;
import org.springframework.validation.annotation.ValidationAnnotationUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 基于 spi 进行参数解析器 !!!
 */
@AllArgsConstructor
public class SpiHandlerMethodArgumentStrategyResolver implements FirstClassSupportHandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return !ClassUtils.isPrimitiveOrWrapper(parameter.getParameterType()) &&
                (assertArgumentStrategyIsSpi(parameter.getParameter()) ||
                        assertArgumentStrategyIsSpi(parameter.getParameterType()));
    }

    private boolean assertArgumentStrategyIsSpi(AnnotatedElement parameter) {
        ArgumentStrategy annotation = AnnotationUtils.findAnnotation(parameter, ArgumentStrategy.class);
        if (annotation != null) {
            Class<? extends ArgumentResolveStrategy> value = annotation.value();
            return value == ArgumentResolveStrategy.SPI;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        MethodParameter nestedParameter = parameter.nestedIfOptional();
        Class<?> parameterType = nestedParameter.getNestedParameterType();

        Object attribute = null;
        List<String> list = SpringFactoriesLoader.loadFactoryNames(parameterType, getClass().getClassLoader());
        if (list.size() > 0) {
            Class<?> aClass = ClassUtils.forName(list.get(0), this.getClass().getClassLoader());
            if (parameter.hasParameterAnnotation(RequestBody.class)) {
                HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
                assert nativeRequest != null;
                attribute = JsonUtil.getDefaultJsonUtil().fromJson(nativeRequest.getInputStream(), aClass);
                WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, "");
                if (binder.getTarget() != null) {
                    this.validateIfApplicable(binder, parameter);
                    if (binder.getBindingResult().hasErrors() && this.isBindExceptionRequired(binder, parameter)) {
                        throw new BindException(binder.getBindingResult());
                    }
                }

                BindingResult bindingResult = binder.getBindingResult();

                Map<String, Object> bindingResultModel = bindingResult.getModel();
                mavContainer.removeAttributes(bindingResultModel);
                mavContainer.addAllAttributes(bindingResultModel);
                return attribute;
            }
            // 仅仅支持 request parameter上进行处理 ...
            else {
                return fromRequestParameters(parameter, mavContainer, webRequest, binderFactory);
            }
        }

        // 否则无法解析 !!!
        return null;
    }

    @org.jetbrains.annotations.Nullable
    private Object fromRequestParameters(MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object attribute;
        Assert.state(mavContainer != null, "requires ModelAndViewContainer");
        Assert.state(binderFactory != null, "requires WebDataBinderFactory");
        String name = ModelFactory.getNameForParameter(parameter);
        ModelAttribute ann = parameter.getParameterAnnotation(ModelAttribute.class);
        if (ann != null) {
            mavContainer.setBinding(name, ann.binding());
        }

        BindingResult bindingResult = null;
        if (mavContainer.containsAttribute(name)) {
            attribute = mavContainer.getModel().get(name);
        } else {
            try {
                attribute = this.createAttribute(name, parameter, binderFactory, webRequest);
            } catch (BindException var10) {
                if (this.isBindExceptionRequired(parameter)) {
                    throw var10;
                }

                if (parameter.getParameterType() == Optional.class) {
                    attribute = Optional.empty();
                } else {
                    attribute = var10.getTarget();
                }

                bindingResult = var10.getBindingResult();
            }
        }

        if (bindingResult == null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
            if (binder.getTarget() != null) {
                if (!mavContainer.isBindingDisabled(name)) {
                    this.bindRequestParameters(binder, webRequest);
                }

                this.validateIfApplicable(binder, parameter);
                if (binder.getBindingResult().hasErrors() && this.isBindExceptionRequired(binder, parameter)) {
                    throw new BindException(binder.getBindingResult());
                }
            }

            if (!parameter.getParameterType().isInstance(attribute)) {
                attribute = binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
            }

            bindingResult = binder.getBindingResult();
        }

        Map<String, Object> bindingResultModel = bindingResult.getModel();
        mavContainer.removeAttributes(bindingResultModel);
        mavContainer.addAllAttributes(bindingResultModel);
        return attribute;
    }

    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        return this.isBindExceptionRequired(parameter);
    }

    protected boolean isBindExceptionRequired(MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        boolean hasBindingResult = paramTypes.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]);
        return !hasBindingResult;
    }


    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = (ServletRequest) request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null, "No ServletRequest");
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
        servletBinder.bind(servletRequest);
    }

    @Nullable
    public Object resolveConstructorArgument(String paramName, Class<?> paramType, NativeWebRequest request) throws Exception {
        MultipartRequest multipartRequest = (MultipartRequest) request.getNativeRequest(MultipartRequest.class);
        if (multipartRequest != null) {
            List<MultipartFile> files = multipartRequest.getFiles(paramName);
            if (!files.isEmpty()) {
                return files.size() == 1 ? files.get(0) : files;
            }
        } else if (StringUtils.startsWithIgnoreCase(request.getHeader("Content-Type"), "multipart/form-data")) {
            HttpServletRequest servletRequest = (HttpServletRequest) request.getNativeRequest(HttpServletRequest.class);
            if (servletRequest != null && HttpMethod.POST.matches(servletRequest.getMethod())) {
                List<Part> parts = StandardServletPartUtils.getParts(servletRequest, paramName);
                if (!parts.isEmpty()) {
                    return parts.size() == 1 ? parts.get(0) : parts;
                }
            }
        } else {
            ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
            if (servletRequest != null) {
                String attr = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
                Map<String, String> uriVars = (Map<String, String>) servletRequest.getAttribute(attr);
                return uriVars.get(paramName);
            } else {
                return null;
            }
        }
        return null;
    }

    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] var3 = parameter.getParameterAnnotations();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Annotation ann = var3[var5];
            Object[] validationHints = ValidationAnnotationUtils.determineValidationHints(ann);
            if (validationHints != null) {
                binder.validate(validationHints);
                break;
            }
        }

    }

    protected void validateValueIfApplicable(WebDataBinder binder, MethodParameter parameter, Class<?> targetType, String fieldName, @Nullable Object value) {
        Annotation[] var6 = parameter.getParameterAnnotations();
        int var7 = var6.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            Annotation ann = var6[var8];
            Object[] validationHints = ValidationAnnotationUtils.determineValidationHints(ann);
            if (validationHints != null) {
                Iterator var11 = binder.getValidators().iterator();

                while (var11.hasNext()) {
                    Validator validator = (Validator) var11.next();
                    if (validator instanceof SmartValidator) {
                        try {
                            ((SmartValidator) validator).validateValue(targetType, fieldName, value, binder.getBindingResult(), validationHints);
                        } catch (IllegalArgumentException var14) {
                        }
                    }
                }

                return;
            }
        }

    }

    protected Object createAttribute(String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {

        String value = this.getRequestValueForAttribute(attributeName, webRequest);
        if (value != null) {
            Object attribute = this.createAttributeFromRequestValue(value, attributeName, parameter, binderFactory, webRequest);
            if (attribute != null) {
                return attribute;
            }
        }

        MethodParameter nestedParameter = parameter.nestedIfOptional();
        Class<?> clazz = nestedParameter.getNestedParameterType();
        Constructor<?> ctor = BeanUtils.getResolvableConstructor(clazz);
        Object attribute = this.constructAttribute(ctor, attributeName, parameter, binderFactory, webRequest);
        if (parameter != nestedParameter) {
            attribute = Optional.of(attribute);
        }

        return attribute;
    }

    protected Object constructAttribute(Constructor<?> ctor, String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest webRequest) throws Exception {
        if (ctor.getParameterCount() == 0) {
            return BeanUtils.instantiateClass(ctor, new Object[0]);
        } else {
            String[] paramNames = BeanUtils.getParameterNames(ctor);
            Class<?>[] paramTypes = ctor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];
            WebDataBinder binder = binderFactory.createBinder(webRequest, (Object) null, attributeName);
            String fieldDefaultPrefix = binder.getFieldDefaultPrefix();
            String fieldMarkerPrefix = binder.getFieldMarkerPrefix();
            boolean bindingFailure = false;
            Set<String> failedParams = new HashSet(4);

            Object value;
            for (int i = 0; i < paramNames.length; ++i) {
                String paramName = paramNames[i];
                Class<?> paramType = paramTypes[i];
                value = webRequest.getParameterValues(paramName);
                if (ObjectUtils.isArray(value) && Array.getLength(value) == 1) {
                    value = Array.get(value, 0);
                }

                if (value == null) {
                    if (fieldDefaultPrefix != null) {
                        value = webRequest.getParameter(fieldDefaultPrefix + paramName);
                    }

                    if (value == null) {
                        if (fieldMarkerPrefix != null && webRequest.getParameter(fieldMarkerPrefix + paramName) != null) {
                            value = binder.getEmptyValue(paramType);
                        } else {
                            value = this.resolveConstructorArgument(paramName, paramType, webRequest);
                        }
                    }
                }

                try {
                    MethodParameter methodParam = new FieldAwareConstructorParameter(ctor, i, paramName);
                    if (value == null && methodParam.isOptional()) {
                        args[i] = methodParam.getParameterType() == Optional.class ? Optional.empty() : null;
                    } else {
                        args[i] = binder.convertIfNecessary(value, paramType, methodParam);
                    }
                } catch (TypeMismatchException var20) {
                    var20.initPropertyName(paramName);
                    args[i] = null;
                    failedParams.add(paramName);
                    binder.getBindingResult().recordFieldValue(paramName, paramType, value);
                    binder.getBindingErrorProcessor().processPropertyAccessException(var20, binder.getBindingResult());
                    bindingFailure = true;
                }
            }

            if (!bindingFailure) {
                return BeanUtils.instantiateClass(ctor, args);
            } else {
                BindingResult result = binder.getBindingResult();

                for (int i = 0; i < paramNames.length; ++i) {
                    String paramName = paramNames[i];
                    if (!failedParams.contains(paramName)) {
                        value = args[i];
                        result.recordFieldValue(paramName, paramTypes[i], value);
                        this.validateValueIfApplicable(binder, parameter, ctor.getDeclaringClass(), paramName, value);
                    }
                }

                if (!parameter.isOptional()) {
                    try {
                        final Object target = BeanUtils.instantiateClass(ctor, args);
                        throw new BindException(result) {
                            public Object getTarget() {
                                return target;
                            }
                        };
                    } catch (BeanInstantiationException var19) {
                    }
                }

                throw new BindException(result);
            }
        }
    }


    @Nullable
    protected String getRequestValueForAttribute(String attributeName, NativeWebRequest request) {
        Map<String, String> variables = this.getUriTemplateVariables(request);
        String variableValue = (String) variables.get(attributeName);
        if (StringUtils.hasText(variableValue)) {
            return variableValue;
        } else {
            String parameterValue = request.getParameter(attributeName);
            return StringUtils.hasText(parameterValue) ? parameterValue : null;
        }
    }

    protected final Map<String, String> getUriTemplateVariables(NativeWebRequest request) {
        Map<String, String> variables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0);
        return variables != null ? variables : Collections.emptyMap();
    }

    @Nullable
    protected Object createAttributeFromRequestValue(String sourceValue, String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {
        DataBinder binder = binderFactory.createBinder(request, (Object) null, attributeName);
        ConversionService conversionService = binder.getConversionService();
        if (conversionService != null) {
            TypeDescriptor source = TypeDescriptor.valueOf(String.class);
            TypeDescriptor target = new TypeDescriptor(parameter);
            if (conversionService.canConvert(source, target)) {
                return binder.convertIfNecessary(sourceValue, parameter.getParameterType(), parameter);
            }
        }

        return null;
    }

    private static class FieldAwareConstructorParameter extends MethodParameter {
        private final String parameterName;
        @Nullable
        private volatile Annotation[] combinedAnnotations;

        public FieldAwareConstructorParameter(Constructor<?> constructor, int parameterIndex, String parameterName) {
            super(constructor, parameterIndex);
            this.parameterName = parameterName;
        }

        public Annotation[] getParameterAnnotations() {
            Annotation[] anns = this.combinedAnnotations;
            if (anns == null) {
                anns = super.getParameterAnnotations();

                try {
                    Field field = this.getDeclaringClass().getDeclaredField(this.parameterName);
                    Annotation[] fieldAnns = field.getAnnotations();
                    if (fieldAnns.length > 0) {
                        List<Annotation> merged = new ArrayList(anns.length + fieldAnns.length);
                        merged.addAll(Arrays.asList(anns));
                        Annotation[] var5 = fieldAnns;
                        int var6 = fieldAnns.length;
                        int var7 = 0;

                        while (true) {
                            if (var7 >= var6) {
                                anns = (Annotation[]) merged.toArray(new Annotation[0]);
                                break;
                            }

                            Annotation fieldAnn = var5[var7];
                            boolean existingType = false;
                            Annotation[] var10 = anns;
                            int var11 = anns.length;

                            for (int var12 = 0; var12 < var11; ++var12) {
                                Annotation ann = var10[var12];
                                if (ann.annotationType() == fieldAnn.annotationType()) {
                                    existingType = true;
                                    break;
                                }
                            }

                            if (!existingType) {
                                merged.add(fieldAnn);
                            }

                            ++var7;
                        }
                    }
                } catch (SecurityException | NoSuchFieldException var14) {
                }

                this.combinedAnnotations = anns;
            }

            return anns;
        }

        public String getParameterName() {
            return this.parameterName;
        }
    }
}
