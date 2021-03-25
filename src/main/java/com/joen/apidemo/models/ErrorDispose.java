package com.joen.apidemo.models;

import org.apache.commons.lang3.RandomUtils;

import java.util.Date;

/**
 * @Description 请求异常处理实体
 * @Author Joen
 * @Date 2021-03-22 16:22:00
 */
public class ErrorDispose {

    public ErrorDispose() {
    }

    public ErrorDispose(String methodName, Class<?> aClass, Object param, Throwable throwable) {
        this.methodName = methodName;
        this.aClass = aClass;
        this.param = param;
        this.throwable = throwable;
        this.errorId = Long.toString(System.currentTimeMillis()) + RandomUtils.nextInt(100000,999999);
    }

    /**
     * 错误Id 时间搓+6位Random
     */
    private String errorId;
    /**
     * 调用方法
     */
    private String methodName;
    /**
     * 调用Class
     */
    private Class<?> aClass;
    /**
     * 调用参数
     */
    private Object param;
    /**
     * 异常信息
     */
    private Throwable throwable;

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
