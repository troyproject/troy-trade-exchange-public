package com.troy.trade.exchange.okex.client;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;
import com.troy.commons.utils.DateUtils;
import com.troy.trade.exchange.core.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 封装HTTP get post请求，简化发送http请求
 *
 * @author zhangchi
 */
@Slf4j
public class OkexHttpUtilManager {

    private static OkexHttpUtilManager instance = new OkexHttpUtilManager();
    private static CloseableHttpClient client;
    public static PoolingHttpClientConnectionManager cm;
    private static ConnectionKeepAliveStrategy keepAliveStrat;

    private OkexHttpUtilManager() {
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

    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(20000)
            .setConnectTimeout(10000)
            .setConnectionRequestTimeout(2000)
            .build();


    public static OkexHttpUtilManager getInstance() {
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

    public String requestHttpGet(String url_prex, String url, String param) throws HttpException, IOException {

        url = url_prex + url;
        if (StringUtils.isNotBlank(param)) {
            if (url.endsWith("?")) {
                url = url + param;
            } else {
                url = url + "?" + param;
            }
        }
        HttpRequestBase method = this.httpGetMethod(url);
        method.setConfig(requestConfig);
        HttpResponse response = client.execute(method);
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return "";
        }
        InputStream is = null;
        String responseData = "";
        try {
            is = entity.getContent();
            responseData = CharStreams.toString(new InputStreamReader(is, "UTF-8"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return responseData;
    }

    public String requestHttpV3(String url_prex, String url,
                                String methodType,String apiKey,
                                String secretKey, String passphrase,
                                Map<String,String> paramMap) throws Throwable  {

        String requestUrl = url;

        String signUrl = url;
        String methodTypeUpper = methodType.toUpperCase();

        String json = "";
        List<Header> headersList = new ArrayList<>();

        if(null != paramMap && !paramMap.isEmpty()){
            if(StringUtils.equals("GET",methodTypeUpper)){
                if(!requestUrl.endsWith("?")){
                    requestUrl += "?";
                }

                Set<String> keySet = paramMap.keySet();
                List<String> keyList = new ArrayList<>(keySet);
                String key = null;
                int size = keyList.size();
                for(int i=0;i<size;i++){
                    key = keyList.get(i);
                    if(i>0){
                        requestUrl += "&";
                    }
                    requestUrl += (key+"="+paramMap.get(key));
                }
                signUrl = requestUrl;
            }else{
                json = JSONObject.toJSONString(paramMap);
                signUrl += json;
            }
        }

        String timestamp = DateUtils.getISO8601UTCTime();
        String sign = "";
        if(StringUtils.isNotBlank(secretKey)){
            StringBuffer signSb = new StringBuffer("");
            signSb.append(timestamp);
            signSb.append(methodTypeUpper);
            signSb.append(signUrl);
            sign = SignUtils.base64(SignUtils.hmacSHA256(signSb.toString(),secretKey));
        }


        headersList.add(new BasicHeader("OK-ACCESS-KEY",apiKey));
        headersList.add(new BasicHeader("OK-ACCESS-SIGN",sign));
        headersList.add(new BasicHeader("OK-ACCESS-TIMESTAMP", timestamp));
        headersList.add(new BasicHeader("OK-ACCESS-PASSPHRASE", passphrase));
        headersList.add(new BasicHeader("Cookie", "locale=zh_CN"));//en_US
        headersList.add(new BasicHeader("Accept","application/json"));
        headersList.add(new BasicHeader("Content-Type","application/json; charset=UTF-8"));

        int size = headersList.size();
        Header[] headers =  new Header[size];
        for(int i=0;i<size;i++){
            headers[i] = headersList.get(i);
        }

        requestUrl = url_prex+requestUrl;

        CloseableHttpResponse response = null;

        if(StringUtils.equals("GET",methodTypeUpper)){//
            HttpGet get = new HttpGet(requestUrl);
            get.setHeaders(headers);
            get.setConfig(requestConfig);
            response = client.execute(get);
        }else{
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
            Header[] headersFrom = response.getHeaders("OK-BEFORE");
            if(null != headersFrom && headersFrom.length>0){
                JSONArray jsonArray = new JSONArray();
                JSONArray responseDataArray = JSONArray.parseArray(responseData);
                jsonArray.add(responseDataArray);
                JSONObject temp = new JSONObject();
                String fromObj = JSONObject.toJSONString(headersFrom[0]);
                JSONObject jsonObject1 = JSONObject.parseObject(fromObj);
                String before = jsonObject1.getString("value");
                temp.put("before",before);

                Header[] headersTo = response.getHeaders("OK-AFTER");
                String toObj = JSONObject.toJSONString(headersTo[0]);
                jsonObject1 = JSONObject.parseObject(toObj);
                String after = jsonObject1.getString("value");
                temp.put("after",after);
                jsonArray.add(temp);
                responseData = jsonArray.toJSONString();
            }
        } catch(Exception e){
            log.error("调用requestHttpV3异常，异常信息："+e.getLocalizedMessage(), e);
        } finally {
            if (is != null) {
                is.close();
            }
            if(response!=null){
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

