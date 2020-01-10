package com.troy.trade.exchange.binance.client;

import com.google.common.io.CharStreams;
import com.troy.trade.exchange.binance.client.constant.BinanceConstant;
import com.troy.trade.exchange.binance.dto.BinanceApiConstants;
import com.troy.trade.exchange.core.utils.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class BinanceHttpUtilManager {
    Logger logger = LoggerFactory.getLogger(BinanceHttpUtilManager.class);

    private static BinanceHttpUtilManager instance = new BinanceHttpUtilManager();
    private static CloseableHttpClient client;

    public static PoolingHttpClientConnectionManager cm;
    private static ConnectionKeepAliveStrategy keepAliveStrat;

    private BinanceHttpUtilManager() {
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
            .setSocketTimeout(20000)//socket读数据超时时间：从服务器获取响应数据的超时时间
            .setConnectTimeout(10000)//与服务器连接超时时间：httpclient会创建一个异步线程用以创建socket连接，此处设置该socket的连接超时时间
            .setConnectionRequestTimeout(2000)//从连接池中获取连接的超时时间
            .build();

    public static BinanceHttpUtilManager getInstance() {
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
        if (param != null && !param.equals("")) {
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
            responseData = CharStreams.toString(new InputStreamReader(is, "UTF-8"));
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
                              String methodType,String apiKey,
                              String secretKey,
                              Map<String,String> paramMap) throws Throwable  {

        String requestUrl = url;

        String signUrl = "";
        String methodTypeUpper = methodType.toUpperCase();

        Long timestamp = new Date().getTime();
        Long recvWindow = BinanceApiConstants.DEFAULT_RECEIVING_WINDOW;
        if(null == paramMap){
            paramMap = new HashMap<>();
        }
        paramMap.put("timestamp",String.valueOf(timestamp));
        paramMap.put("recvWindow",String.valueOf(recvWindow));


        if(null != paramMap){
            Set<String> keySet = paramMap.keySet();
            List<String> keyList = new ArrayList<>(keySet);
            String key = null;
            int size = keyList.size();
            for(int i=0;i<size;i++){
                key = keyList.get(i);
                if(i>0){
                    signUrl += "&";
                }
                signUrl += (key+"="+paramMap.get(key));
            }

            requestUrl += "?";
            requestUrl += signUrl;
        }


        String sign = "";
        if(StringUtils.isNotBlank(secretKey)){
            sign = SignUtils.hmacSHA256Hex(signUrl,secretKey);
            if(requestUrl.contains("?")){
                requestUrl += "&";
            }else{
                requestUrl += "?";
            }
            requestUrl += "signature=";
            requestUrl += sign;
        }

        requestUrl = url_prex+requestUrl;

        CloseableHttpResponse response = null;
        if(StringUtils.equals(BinanceConstant.METHOD_GET,methodTypeUpper)){//
            HttpGet get = new HttpGet(requestUrl);
            if(StringUtils.isNotBlank(apiKey)){
                Header[] headers = new Header[1];
                headers[0] = new BasicHeader("X-MBX-APIKEY",apiKey);
                get.setHeaders(headers);
            }
            get.setConfig(requestConfig);
            response = client.execute(get);
        }else{
            HttpPost post = new HttpPost(requestUrl);
            if(StringUtils.isNotBlank(apiKey)){
                Header[] headers = new Header[1];
                headers[0] = new BasicHeader("X-MBX-APIKEY",apiKey);
                post.setHeaders(headers);
            }
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
            responseData = CharStreams.toString(new InputStreamReader(is, "UTF-8"));
        } catch(Exception e){
            logger.error("调用requestHttp异常，异常信息："+e.getLocalizedMessage(), e);
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

