package com.joen.apidemo.proxys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joen.apidemo.annotations.ApiService;
import com.joen.apidemo.models.ApiReq;
import com.joen.apidemo.utils.HttpUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author Joen
 * @Date 2020-12-29 14:50:28
 */
public class ApiProxy implements InvocationHandler {
    private static Logger logger = LoggerFactory.getLogger(ApiProxy.class);
    private static Map<Method, ApiReq> map = new HashMap<>();

    private Class<?> interfaceClass;
    private MethodHandles.Lookup lookup;

    public Object bind(Class<?> aClass) {
        this.interfaceClass = aClass;
        try {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);

            int allModes = MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE;
            this.lookup = constructor.newInstance(this.interfaceClass, allModes);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return Proxy.newProxyInstance(aClass.getClassLoader(), new Class[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Date date = new Date();
        logger.debug("调用动态代理方法-----开始");
        Object result;
        ApiReq apiReq = map.get(method);
        try {
            if (apiReq == null) {
                ApiService methodAnnotation = method.getAnnotation(ApiService.class);
                ApiService classAnnotation = interfaceClass.getAnnotation(ApiService.class);
                apiReq = new ApiReq();
                apiReq.setUrl((classAnnotation != null ? classAnnotation.value() : "") + methodAnnotation.value());
                apiReq.setMethod(methodAnnotation.method());
                apiReq.setResult(method.getReturnType());
                if (method.isDefault()){
                    apiReq.setMethodHandle(this.lookup.unreflectSpecial(method, this.interfaceClass).bindTo(proxy));
                }
                map.put(method, apiReq);
            }
            switch (apiReq.getMethod()) {
                case GET:
                    if (args[0] instanceof Map) {
                        String get = HttpUtil.get(apiReq.getUrl(), (Map<String, Object>) args[0]);
                        result = JSON.parseObject(get, apiReq.getResult());
                    } else {
                        JSONObject object = JSON.parseObject(JSON.toJSONString(args[0]));
                        String get = HttpUtil.get(apiReq.getUrl(), object.getInnerMap());
                        result = JSON.parseObject(get, apiReq.getResult());
                    }
                    break;
                case POST:
                    String post = HttpUtil.jsonPost(apiReq.getUrl(), JSON.toJSONString(args[0]));
                    result = JSON.parseObject(post, apiReq.getResult());
                    break;
                default:
                    if (method.isDefault()) {
                        result = apiReq.getMethodHandle().invokeWithArguments(args);
                    } else {
                        throw new NullPointerException("不支持的请求方法协议");
                    }

            }
        } catch (Exception e) {
            if (method.isDefault()) {
                assert apiReq != null;
                result = apiReq.getMethodHandle().invokeWithArguments(args);
            } else {
                throw e;
            }
        }
        logger.debug("调用动态代理方法-----结束，使用时间：{}", new Date().getTime() - date.getTime());
        return result;
    }

}
