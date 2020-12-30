package com.joen.apidemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.joen.apidemo.service.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description
 * @Author Joen
 * @Date 2020-12-29 10:00:21
 */
@RestController
public class TestController {
    @Resource
    private TestService service;

    @RequestMapping("test")
    public Object test(){
        JSONObject object = new JSONObject();
        return service.jsonTest(object);
    }
}
