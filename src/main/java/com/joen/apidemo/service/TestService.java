package com.joen.apidemo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joen.apidemo.annotations.ApiService;
import com.joen.apidemo.enums.HttpBodyType;
import com.joen.apidemo.models.ErrorDispose;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpMethod;

@ApiService
public interface TestService extends SuperService<JSONObject, JSONObject> {

    @ApiService("https://v0.yiketianqi.com/api")
    default JSONObject test(JSONObject object) {
        //throw new NullPointerException("调用失败");
        JSONObject object1 = new JSONObject();
        object1.put("code", -1);
        object1.put("msg", "请求失败");
        return object1;
    }

    @ApiService(value = "http://192.168.3.215/Amaidan-Users-Web/user/baseUserCard/page", method = HttpMethod.POST, bodyType = HttpBodyType.FORM_DATA)
    default JSONObject jsonTest(JSONObject object) {
        return null;
    }

    @ApiService(value = "http://192.168.3.77:8080/test1", method = HttpMethod.POST, bodyType = HttpBodyType.FORM_DATA)
    default JSONObject test1(JSONObject object) {
        JSONObject object1 = new JSONObject();
        object1.put("code", -1);
        object1.put("msg", "请求失败");
        return object1;
    }

    @Override
    default void error(ErrorDispose dispose) {
        logger.error(JSON.toJSONString(dispose));
        logger.error(",{}", ExceptionUtils.getStackTrace(dispose.getThrowable()));
    }

    @Override
    default JSONObject before(JSONObject object) {
        if (object != null){
            String token = object.getString("token");
            if (StringUtils.isEmpty(token)){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg","Token无效");
                return jsonObject;
            }
        }
        return null;
    }

    @Override
    default JSONObject after(JSONObject object) {
        if (object != null){
            Boolean success = object.getBoolean("success");
            if (success){
                return object;
            }
        }
        return null;
    }

}
