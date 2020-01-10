package com.troy.trade.exchange.data.client;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.utils.ApplicationContextUtil;
import com.troy.trade.exchange.core.utils.SignUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DataHttpUtilManager
 *
 * @author yanping
 */
public class DataHttpUtilManager {
    public static PoolingHttpClientConnectionManager cm;
    private static DataHttpUtilManager instance = new DataHttpUtilManager();
    private static CloseableHttpClient client;
    private static ConnectionKeepAliveStrategy keepAliveStrat;
    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(15000)//socket读数据超时时间：从服务器获取响应数据的超时时间
            .setConnectTimeout(1000)//与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间
            .setConnectionRequestTimeout(1000)//从连接池中获取连接的超时时间
            .build();
    Logger logger = LoggerFactory.getLogger(getClass());


    private DataHttpUtilManager() {
        keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {

            @Override
            public long getKeepAliveDuration(
                    HttpResponse response,
                    HttpContext context) {
                long keepAlive = super.getKeepAliveDuration(response, context);

                if (keepAlive == -1) {
                    keepAlive = 20000;
                }
                return keepAlive;
            }

        };
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(500); //设置整个连接池最大连接数 根据自己的场景决定
        cm.setDefaultMaxPerRoute(100);
        client = HttpClients.custom()
                .setConnectionManager(cm)
                .setConnectionManagerShared(true)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy(keepAliveStrat)
                .setRetryHandler(// 不进行重试
                        new DefaultHttpRequestRetryHandler(0, false)
                ).build();
    }

    public static DataHttpUtilManager getInstance() {
        return instance;
    }

    public HttpClient getHttpClient() {
        return client;
    }

    private HttpPost httpPostMethod(String url) {
        return new HttpPost(url);
    }

    private HttpRequestBase httpGetMethod(String url) {
        return new HttpGet(url);
    }

    public String requestHttpGet(String url, String param) throws Throwable {

        if (StringUtils.isNotBlank(param)) {
            if (url.endsWith("?")) {
                url = url + param;
            } else {
                url = url + "?" + param;
            }
        }

        HttpRequestBase method = this.httpGetMethod(url);
        method.setConfig(requestConfig);
        CloseableHttpResponse response = client.execute(method);
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return "";
        }
        InputStream is = null;
        String responseData = "";
        try {
            is = entity.getContent();
            responseData = IOUtils.toString(is, "UTF-8");
            responseData = SignUtils.unicodeToString(responseData);
        } catch (Exception e) {
            logger.error("调用requestHttpGet异常，异常信息：" + e.getLocalizedMessage(), e);
        } finally {
            if (is != null) {
                is.close();
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseData;
    }

    public String requestHttp(String url_prex, String url,
                              String methodType, String apiKey,
                              String secretKey, Map<String, String> paramMap) throws Throwable {

        String requestUrl = url;

        String methodTypeUpper = methodType.toUpperCase();
        String json = "";
        List<Header> headersList = new ArrayList<>();
        if (null != paramMap) {
            if (StringUtils.equals("GET", methodTypeUpper)) {
                if (!requestUrl.endsWith("?")) {
                    requestUrl += "?";
                }

                Set<String> keySet = paramMap.keySet();
                List<String> keyList = new ArrayList<>(keySet);
                String key = null;
                int size = keyList.size();
                for (int i = 0; i < size; i++) {
                    key = keyList.get(i);
                    if (i > 0) {
                        requestUrl += "&";
                    }
                    requestUrl += (key + "=" + paramMap.get(key));
                }
            } else {
                json = JSONObject.toJSONString(paramMap);
            }
        }

        if (StringUtils.isNotBlank(secretKey)) {
            paramMap.put("key",apiKey);
            paramMap.put("timeStap",String.valueOf(System.currentTimeMillis()));

            List<String> ignore = new ArrayList<String>();
            ignore.add("sign");
            String signStr = ApplicationContextUtil.getBean(SignUtils.class).signJson(paramMap, ignore, secretKey);
            paramMap.put("sign",signStr);
            json = JSONObject.toJSONString(paramMap);
        }


        headersList.add(new BasicHeader("Content-Type", "application/json; charset=UTF-8"));

        int size = headersList.size();
        Header[] headers = new Header[size];
        for (int i = 0; i < size; i++) {
            headers[i] = headersList.get(i);
        }

        requestUrl = url_prex + requestUrl;

        CloseableHttpResponse response = null;

        if (StringUtils.equals("GET", methodTypeUpper)) {//
            HttpGet get = new HttpGet(requestUrl);
            get.setHeaders(headers);
            get.setConfig(requestConfig);
            response = client.execute(get);
        } else {
            HttpPost post = new HttpPost(requestUrl);
            StringEntity requestEntity = new StringEntity(json, "utf-8");
            requestEntity.setContentEncoding("UTF-8");
            post.setHeaders(headers);
            post.setEntity(requestEntity);
            post.setConfig(requestConfig);
            response = client.execute(post);
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return "";
        }

        InputStream is = null;
        String responseData = null;
        try {
            is = entity.getContent();
            responseData = IOUtils.toString(is, "UTF-8");
        } catch (Exception e) {
            logger.error("调用requestHttp异常，异常信息：" + e.getLocalizedMessage(), e);
        } finally {
            if (is != null) {
                is.close();
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseData;
    }
}

