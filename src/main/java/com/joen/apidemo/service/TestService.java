package com.joen.apidemo.service;

import com.alibaba.fastjson.JSONObject;
import com.joen.apidemo.annotations.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

/**
 * @Description
 * @Author Joen
 * @Date 2020-12-29 10:00:37
 */
@ApiService
public interface TestService {
    Logger logger = LoggerFactory.getLogger(TestService.class);

    @ApiService("https://v0.yiketianqi.com/api")
    default JSONObject test(JSONObject object) {
        //throw new NullPointerException("调用失败");
        JSONObject object1 = new JSONObject();
        object1.put("code", -1);
        object1.put("msg", "请求失败");
        return object1;
    }

    @ApiService(value = "http://192.168.3.215/Amaidan-Users-Web/user/baseUserCard/page", method = HttpMethod.POST)
    default JSONObject jsonTest(JSONObject object) {
        return object;
    }
}
