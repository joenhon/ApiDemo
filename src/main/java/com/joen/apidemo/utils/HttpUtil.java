package com.joen.apidemo.utils;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author Joen
 * @Date 2021-01-04 14:48:14
 */
public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static final String CHARSET = "UTF-8";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_GET = "GET";
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .build();

    /*post start*/


    public static String jsonPost(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return post(url, null, null, headers);
    }

    public static String jsonPost(String url, String data) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return post(url, data, null, headers);
    }

    public static String jsonPost(String url, String data, Map<String, String> headers) {
        headers.put("Content-Type", "application/json");
        return post(url, data, null, headers);
    }

    public static String jsonPost(String url, Map<String, Object> params) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return post(url, null, params, headers);
    }

    public static String jsonPost(String url, Map<String, Object> params, Map<String, String> headers) {
        headers.put("Content-Type", "application/json");
        return post(url, null, params, headers);
    }

    public static String post(String url, String data, Map<String, Object> params, Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }

        HttpPost post = new HttpPost(url);

        if (params != null) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            params.forEach((key, value) -> {
                pairs.add(new BasicNameValuePair(key, String.valueOf(value)));
            });
            post.setEntity(new UrlEncodedFormEntity(pairs, Consts.UTF_8));
        }

        if (data != null) {
            InputStream stream = new ByteArrayInputStream(data.getBytes(Consts.UTF_8));
            BasicHttpEntity entity = new BasicHttpEntity();
            entity.setContent(stream);
            post.setEntity(entity);
        }

        headers.forEach(post::addHeader);


        post.setConfig(requestConfig);

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpResponse response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity resEntity = response.getEntity();
                return EntityUtils.toString(resEntity, "utf-8");
            } else {
                if (data != null) {
                    logger.error("请求失败，请求URL：{}，参数：{}，请求头：{}。", url, data, headers);
                } else if (params != null) {
                    logger.error("请求失败，请求URL：{}，参数：{}，请求头：{}。", url, params, headers);
                } else {
                    logger.error("请求失败，请求URL：{}，请求头：{}。", url, headers);
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /*post end*/

    /*get start*/

    public static String jsonGet(String url, Map<String, Object> params){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return get(url,params,headers);
    }

    public static String jsonGet(String url, Map<String, Object> params, Map<String, String> headers){
        headers.put("Content-Type", "application/json");
        return get(url,params,headers);
    }

    public static String get(String url, Map<String, Object> params, Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }

        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null){
                params.forEach((s, o) -> {
                    uriBuilder.addParameter(s, String.valueOf(o));
                });
            }
            HttpGet get = new HttpGet(uriBuilder.build());
            headers.forEach(get::addHeader);
            HttpClient httpClient = HttpClients.createDefault();
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity resEntity = response.getEntity();
                return EntityUtils.toString(resEntity, "utf-8");
            } else {
                if (params != null) {
                    logger.error("请求失败，请求URL：{}，参数：{}，请求头：{}。", url, params, headers);
                } else {
                    logger.error("请求失败，请求URL：{}，请求头：{}。", url, headers);
                }
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

}
