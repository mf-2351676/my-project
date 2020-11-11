package com.utils;

import com.bean.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 * @Author: menfeng
 * @Date: 2019/12/20 11:20
 * @Version 1.0
 */
public class HttpClientUtils {
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    /**
     * 不带参数的get请求
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpResult doGet(String url) throws Exception {
        HttpResult httpResult = doGet(url, null);
        return httpResult;
    }

    /**
     * 带参数的get请求
     *
     * @param url
     * @param map
     * @return
     * @throws Exception
     */
    public static HttpResult doGet(String url, Map<String, Object> map) throws Exception {

        // 声明URIBuilder
        URIBuilder uriBuilder = new URIBuilder(url);

        // 判断参数map是否为非空
        if (map != null) {
            // 遍历参数
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                // 设置参数
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        // 2 创建httpGet对象，相当于设置url请求地址
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        // 3 使用HttpClient执行httpGet，相当于按回车，发起请求
        CloseableHttpResponse response = httpClient.execute(httpGet);

        HttpResult httpResult = null;
        // 解析数据封装HttpResult
        if (response.getEntity() != null) {
            httpResult = new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(response.getEntity(), "UTF-8"));
        } else {
            httpResult = new HttpResult(response.getStatusLine().getStatusCode(), "");
        }

        // 返回
        return httpResult;
    }
}
