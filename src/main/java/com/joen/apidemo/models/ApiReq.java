package com.joen.apidemo.models;

import com.joen.apidemo.enums.HttpBodyType;
import org.springframework.http.HttpMethod;

import java.lang.invoke.MethodHandle;

/**
 * @Description
 * @Author Joen
 * @Date 2020-12-29 14:52:58
 */
public class ApiReq {
    String url;
    HttpMethod method;
    HttpBodyType bodyType;
    Class<?> result;
    MethodHandle methodHandle;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Class<?> getResult() {
        return result;
    }

    public void setResult(Class<?> result) {
        this.result = result;
    }

    public MethodHandle getMethodHandle() {
        return methodHandle;
    }

    public void setMethodHandle(MethodHandle methodHandle) {
        this.methodHandle = methodHandle;
    }

    public HttpBodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(HttpBodyType bodyType) {
        this.bodyType = bodyType;
    }
}
