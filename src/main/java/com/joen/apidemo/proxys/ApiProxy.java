package com.joen.apidemo.proxys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joen.apidemo.annotations.ApiService;
import com.joen.apidemo.models.ApiReq;
import com.joen.apidemo.utils.HttpUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
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
    private MethodHandle error;

    public Object bind(Class<?> aClass) {
        this.interfaceClass = aClass;
        Object proxy = Proxy.newProxyInstance(aClass.getClassLoader(), new Class[]{interfaceClass}, this);
        try {
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);

            int allModes = MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE;
            this.lookup = constructor.newInstance(this.interfaceClass, allModes);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        try {
            Method error = aClass.getDeclaredMethod("error", Exception.class);
            this.error = this.lookup.unreflectSpecial(error, this.interfaceClass).bindTo(proxy);
        }catch (Exception e){
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Date date = new Date();
        logger.debug("调用动态代理方法-----开始");
        Object result;
        ApiReq apiReq = map.get(method);
        try {
            if (true){
                throw new RuntimeException("测试");
            }
            if (apiReq == null) {
                ApiService methodAnnotation = method.getAnnotation(ApiService.class);
                ApiService classAnnotation = interfaceClass.getAnnotation(ApiService.class);
                apiReq = new ApiReq();
                apiReq.setUrl((classAnnotation != null ? classAnnotation.value() : "") + methodAnnotation.value());
                apiReq.setMethod(methodAnnotation.method());
                apiReq.setBodyType(methodAnnotation.bodyType());
                apiReq.setResult(method.getReturnType());
                if (method.isDefault()){
                    apiReq.setMethodHandle(this.lookup.unreflectSpecial(method, this.interfaceClass).bindTo(proxy));
                }
                map.put(method, apiReq);
            }
            result = JSON.parseObject(request(apiReq, args[0]),apiReq.getResult());
        } catch (Exception e) {
            if (method.isDefault()) {
                assert apiReq != null;
                result = apiReq.getMethodHandle().invokeWithArguments(args);
            } else {
                if (this.error != null){
                    this.error.invokeWithArguments(e);
                }
                throw e;
            }
        }
        logger.debug("调用动态代理方法-----结束，使用时间：{}", new Date().getTime() - date.getTime());
        return result;
    }

    /**
     * 请求
     * @param req 请求配置
     * @param param 请求参数
     * @return
     */
    private String request(ApiReq req,Object param) throws Throwable{
        String result = null;
        switch (req.getMethod()) {
            case GET:
                if (param instanceof Map) {
                    result = HttpUtil.jsonGet(req.getUrl(), (Map<String, Object>) param);
                } else {
                    JSONObject object = JSON.parseObject(JSON.toJSONString(param));
                    result = HttpUtil.jsonGet(req.getUrl(), object.getInnerMap());
                }
                break;
            case POST:
                result = post(req, param);
                break;
            default:
                if (req.getMethodHandle() != null) {
                    result = JSON.toJSONString(req.getMethodHandle().invokeWithArguments(param));
                } else {
                    throw new NullPointerException("不支持的请求方法协议");
                }
                break;
        }
        return result;
    }

    /**
     * POST 请求
     * @param req
     * @param param
     * @return
     * @throws Throwable
     */
    private String post(ApiReq req,Object param) throws Throwable{
        String result = null;
        switch (req.getBodyType()){
            case NONE:
                result = HttpUtil.jsonPost(req.getUrl());
                break;
            case RAW:
                result = HttpUtil.jsonPost(req.getUrl(),JSON.toJSONString(param));
                break;
            case FORM_DATA:
                if (param instanceof Map){
                    result = HttpUtil.jsonPost(req.getUrl(), (Map<String, Object>) param);
                }else {
                    JSONObject object = JSON.parseObject(JSON.toJSONString(param));
                    result = HttpUtil.jsonPost(req.getUrl(),object.getInnerMap());
                }
                break;
            default:
                if (req.getMethodHandle() != null) {
                    result = JSON.toJSONString(req.getMethodHandle().invokeWithArguments(param));
                } else {
                    throw new NullPointerException("不支持的请求参数类型");
                }
                break;
        }
        return result;
    }

}
