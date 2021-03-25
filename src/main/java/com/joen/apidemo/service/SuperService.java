package com.joen.apidemo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joen.apidemo.annotations.ApiService;
import com.joen.apidemo.enums.HttpBodyType;
import com.joen.apidemo.models.ErrorDispose;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

/**
 * @Description
 * @Author Joen
 * @Date 2020-12-29 10:00:37
 */
public interface SuperService<P,R> {
    Logger logger = LoggerFactory.getLogger(SuperService.class);

    /**
     * 异常是调用
     * @param dispose
     */
    default void error(ErrorDispose dispose){
        logger.error(JSON.toJSONString(dispose));
        logger.error(",{}",ExceptionUtils.getStackTrace(dispose.getThrowable()));
    }

    /**
     * 调用之前
     * @param object
     * @return
     */
    default P before(P object) {
        return null;
    }

    /**
     * 调用之后
     * @param object
     * @return
     */
    default R after(R object) {
        return null;
    }
}
