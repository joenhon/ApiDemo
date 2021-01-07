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
        //object.put("token","2cf8972cad81458db852c906dbe5a117");
        JSONObject object1 = service.test1(object);
        return object1;
    }

    @RequestMapping("test1")
    public Object test1(String test,String test1){
        JSONObject object = new JSONObject();
        JSONObject object1 = new JSONObject();
        object1.put("msg","测试");
        object.put("test",object1);
        object.put("test1",test1);
        return object;
    }
}
