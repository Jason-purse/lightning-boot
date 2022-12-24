//package com.jianyue.lightning.boot.starter.web.logs;
//
//
//import com.jianyue.lightning.util.JsonUtil;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.util.ClassUtils;
//import org.springframework.util.ConcurrentReferenceHashMap;
//import org.springframework.util.ObjectUtils;
//import org.springframework.validation.Errors;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.method.support.ModelAndViewContainer;
//import org.springframework.web.servlet.AsyncHandlerInterceptor;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.lang.reflect.Method;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//
///**
// * @author Liupan
// * @date 2021/11/13 17:45
// *
// *
// * 基于 weak reference 的形式 尽量加快同步处理过程 ..
// * 如果接口中存在HttpRequest,那么如果想改成异步,需要在获取的参数的时候,就将数据进行处理 ..
// * 暂时不处理 ..
// * @see ProfilerServletInvocableHandlerMethod#getMethodArgumentValues(NativeWebRequest, ModelAndViewContainer, Object...)
// * @see ProfilerRequestMappingHandlerAdapter#createInvocableHandlerMethod(HandlerMethod)
// */
//@Slf4j
//@AllArgsConstructor
//@NoArgsConstructor
//public class OperatorLogInterceptor implements HandlerInterceptor {
//
//
//    static final List<String> IGNORE_URL;
//
//
//    private KafkaTemplate<String, Object> kafkaTemplate;
//
//
//    private ThreadPoolTaskExecutor executor;
//
//
//    // 64
//    // class
//    private static ConcurrentReferenceHashMap<String, List<String>> classMethodCache = new ConcurrentReferenceHashMap<>(64);
//    // method
//    private static ConcurrentReferenceHashMap<String, Boolean> methodCache = new ConcurrentReferenceHashMap<>(64);
//
//    /**
//     * Intercept the execution of a handler. Called after HandlerAdapter actually
//     * invoked the handler, but before the DispatcherServlet renders the view.
//     * Can expose additional model objects to the view via the given ModelAndView.
//     * <p>DispatcherServlet processes a handler in an execution chain, consisting
//     * of any number of interceptors, with the handler itself at the end.
//     * With this method, each interceptor can post-process an execution,
//     * getting applied in inverse order of the execution chain.
//     * <p><strong>Note:</strong> special considerations apply for asynchronous
//     * request processing. For more details see
//     * {@link AsyncHandlerInterceptor}.
//     * <p>The default implementation is empty.
//     *
//     * @param request      current HTTP request
//     * @param response     current HTTP response
//     * @param handler      handler (or {@link HandlerMethod}) that started asynchronous
//     *                     execution, for type and/or instance examination
//     * @param modelAndView the {@code ModelAndView} that the handler returned
//     *                     (can also be {@code null})
//     * @throws Exception in case of errors
//     */
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//
//        String isLog = request.getHeader("isLog");
//        if (StringUtils.isNotBlank(isLog) && "true".equalsIgnoreCase(isLog)) {
//            System.out.printf("handleMethod: %s,parameters: %s%n", handler, JsonUtil.asJSON(DataUtil.get()));
//        }
//        String requestURI = request.getRequestURI();
//        try {
//            Oauth2UserContext userContext = NewUserUtil.get();
//            // 仅仅只有处理器方法才需要发送请求信息
//            if (handler instanceof HandlerMethod) {
//                // 表示feign 接口调用并不会记录
//                if ("/api/authz/oauth2/token.json".equals(requestURI) || Objects.nonNull(userContext)) {
//                    HandlerMethod handlerMethod = (HandlerMethod) handler;
//                    // 判断是否被忽略了(例如某些轮询接口)
//                    Class<?> beanType = handlerMethod.getBeanType();
//                    // 拿取原始bean class(进行 注解判断) ,每一个controller 类型都能够通过名称进行区分
//                    beanType = ClassUtils.getUserClass(beanType);
//                    if (decideByClass(handlerMethod, beanType)) return;
//                    // 否则尝试从 方法缓存中处理 ...
//                    if(decideByMethod(handlerMethod, beanType)) return;
//                    Method method = handlerMethod.getMethod();
//                    doLog(request, requestURI, userContext, method);
//                }
//            }
//        } catch (Exception e) {
//            log.warn("操作日志推送Kafka异常！");
//            e.printStackTrace();
//        }
//    }
//
//    private void doLog(HttpServletRequest request, String requestURI, Oauth2UserContext userContext, Method method) {
//        // 否则可以开始正常解析 ...
//        Map<String, Object> logMap = new HashMap<>(16);
//        //feature:添加账户名 22-08-03
//        logMap.put("account", Objects.nonNull(userContext) ? userContext.getAccount() : "");
//        logMap.put("operatorId", Objects.nonNull(userContext) ? userContext.getUserId() : "");
//        logMap.put("operatorName", Objects.nonNull(userContext) ? userContext.getUserName() : "");
//        logMap.put("operatorTime", System.currentTimeMillis());
//        logMap.put("requestIp", IpUtil.getIpAddr(request));
//        logMap.put("requestUrl", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + requestURI);
//        // todo  当参数为request的时候,不能直接让它序列化请求,因为存在问题
//        Object o = DataUtil.get();
//        // 减少它在当前线程持有的时间!
//        // 所以防止当前线程的DataUtil 被误用,所以移除它;
//        DataUtil.set(null);
//        if (!ObjectUtils.isEmpty(o)) {
//            if (o instanceof Object[]) {
//                Object[] objects = (Object[]) o;
//                Object[] parameters = new Object[objects.length];
//                Object temp = null;
//                for (int i = 0; i < objects.length; i++) {
//                    temp = objects[i];
//                    if (objects[i] instanceof ServletRequest) {
//                        temp = ServletRequest.class.getName();
//                    } else if (objects[i] instanceof Errors) {
//                        temp = Errors.class.getName();
//                    }
//                    parameters[i] = temp;
//                }
//                o = parameters;
//            }
//        }
//        logMap.put("parameters", o);
//        logMap.put("method", method);
//
//        logMap.put("token", Objects.nonNull(userContext) ? userContext.getToken() : "");
//        logMap.put("schoolId", Objects.nonNull(userContext) ? userContext.getSchoolId() : "");
//        logMap.put("schoolName", Objects.nonNull(userContext) ? userContext.getSchoolName() : "");
//        CompletableFuture.runAsync(() -> kafkaTemplate.send("OPERATOR_LOG", JsonUtil.asJSON(logMap)), executor);
//    }
//
//    private boolean decideByMethod(HandlerMethod handlerMethod, Class<?> beanType) {
//
//        String format = String.format("%s-%s", beanType.getName(), handlerMethod.getMethod().getName());
//        if (methodCache.containsKey(format)) {
//            // 不需要处理 ...
//            return true;
//        }
//        if (handlerMethod.getMethod().isAnnotationPresent(OperatorLogIgnore.class)) {
//            // 只要有,则插入
//            methodCache.putIfAbsent(format, Boolean.TRUE);
//            return true;
//        }
//        return false;
//    }
//
//    private boolean decideByClass(HandlerMethod handlerMethod, Class<?> beanType) {
//        if (isPresentClass(handlerMethod, beanType)) return true;
//
//        if (beanType.isAnnotationPresent(OperatorLogIgnore.class)) {
//            // 尝试解析一次,这种情况有可能本身没有注解,有注解 ,未知
//            OperatorLogIgnore ano = beanType.getAnnotation(OperatorLogIgnore.class);
//            Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(ano);
//
//            String[] methods = ((String[]) annotationAttributes.get("methods"));
//
//            List<String> list = Arrays.asList(methods);
//            classMethodCache.putIfAbsent(beanType.getName(), list);
//            if (isPresentClass(handlerMethod, beanType)){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isPresentClass(HandlerMethod handlerMethod, Class<?> beanType) {
//        // 首先判断是否有没有
//        if (classMethodCache.containsKey(beanType.getName())) {
//            List<String> methodCaches = classMethodCache.get(beanType.getName());
//            if (methodCaches != null && methodCaches.contains(handlerMethod.getMethod().getName())) {
//                // 不处理
//                return true;
//            }
//        }
//        return false;
//    }
//
//    static {
//        IGNORE_URL = new ArrayList<>();
//        IGNORE_URL.add("/api/safe/warehouse/pushToHive");
//        IGNORE_URL.add("pushToHive");
//    }
//
//    /**
//     * 释放掉资源 ...
//     */
//    public void close() {
//        methodCache.clear();
//        classMethodCache.clear();
//    }
//}
