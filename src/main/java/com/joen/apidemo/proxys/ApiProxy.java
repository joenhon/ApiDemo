package com.joen.apidemo.proxys;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joen.apidemo.annotations.ApiService;
import com.joen.apidemo.core.ApiBean;
import com.joen.apidemo.models.ApiReq;
import com.joen.apidemo.models.ErrorDispose;
import com.joen.apidemo.service.TestService;
import com.joen.apidemo.utils.HttpUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;

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
    private MethodHandle before;
    private MethodHandle after;
    private Class<?> pClass;

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

        Method[] methods = aClass.getMethods();

        this.error = getDefMethod(methods, "error", proxy);
        this.after = getDefMethod(methods, "after", proxy);
        this.before = getDefMethod(methods, "before", proxy);

        this.pClass = getType(aClass);

        return proxy;
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
                apiReq.setUrl(getUrl(classAnnotation) + getUrl(methodAnnotation));
                apiReq.setMethod(methodAnnotation.method());
                apiReq.setBodyType(methodAnnotation.bodyType());
                apiReq.setResult(method.getReturnType());
                if (method.isDefault()) {
                    apiReq.setMethodHandle(this.lookup.unreflectSpecial(method, this.interfaceClass).bindTo(proxy));
                }
                map.put(method, apiReq);
            }
            /*调用之前 处理参数*/
            if (before != null) {
                Object object = before.invokeWithArguments(args);
                if (object != null) {
                    return object;
                }
            }

            String request = request(apiReq, args[0]);
            result = JSON.parseObject(request, apiReq.getResult());

            /*调用之后处理结果*/
            if (after != null) {
                Object object = after.invokeWithArguments(result);
                if (object != null) {
                    return object;
                }
            }

        } catch (Exception e) {

            if (this.error != null) {
                ErrorDispose dispose = new ErrorDispose(method.getName(), interfaceClass, args[0], e);
                this.error.invokeWithArguments(dispose);
            }

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

    /**
     * 请求
     *
     * @param req   请求配置
     * @param param 请求参数
     * @return
     */
    private String request(ApiReq req, Object param) throws Throwable {
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
     *
     * @param req
     * @param param
     * @return
     * @throws Throwable
     */
    private String post(ApiReq req, Object param) throws Throwable {
        String result = null;
        switch (req.getBodyType()) {
            case NONE:
                result = HttpUtil.jsonPost(req.getUrl());
                break;
            case RAW:
                result = HttpUtil.jsonPost(req.getUrl(), JSON.toJSONString(param));
                break;
            case FORM_DATA:
                if (param instanceof Map) {
                    result = HttpUtil.post(req.getUrl(), null, (Map<String, Object>) param, null);
                } else {
                    JSONObject object = JSON.parseObject(JSON.toJSONString(param));
                    result = HttpUtil.post(req.getUrl(), null, object.getInnerMap(), null);
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

    private String getUrl(ApiService service) {
        if (service == null) {
            return "";
        }
        String url = service.value();
        if (url.indexOf("${") == 0 && url.indexOf("}") == url.length() - 1) {
            url = ApiBean.getProperty(url.substring(2, url.length() - 1));
        }
        return url;
    }

    /**
     * 获取接口默认方法
     *
     * @param methods 方法列表
     * @param name    方法名
     * @param proxy   接口默认对象
     * @return
     */
    private MethodHandle getDefMethod(Method[] methods, String name, Object proxy) {
        try {
            List<Method> methods1 = new LinkedList<>();
            for (Method method : methods) {
                if (method.getName().equals(name)) {
                    if (method.getReturnType().getTypeName().equals("java.lang.Object")) {
                        methods1.add(method);
                    } else {
                        return this.lookup.unreflectSpecial(method, this.interfaceClass).bindTo(proxy);
                    }
                }
            }
            if (methods1.size() > 0) {
                return this.lookup.unreflectSpecial(methods1.get(0), this.interfaceClass).bindTo(proxy);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private static Class<?> getType(Class<?> clazz) {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            return (Class<?>) types[0];
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getType(TestService.class));
    }

}
