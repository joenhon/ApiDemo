package com.joen.apidemo.annotations;

import com.joen.apidemo.enums.HttpBodyType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ApiService {
    /**
     * 请求链接
     * @return
     */
    String value() default "";

    /**
     * 请求协议
     * @return
     */
    HttpMethod method() default HttpMethod.GET;

    /**
     * 请求参数类型
     * @return
     */
    HttpBodyType bodyType() default HttpBodyType.RAW;

}
