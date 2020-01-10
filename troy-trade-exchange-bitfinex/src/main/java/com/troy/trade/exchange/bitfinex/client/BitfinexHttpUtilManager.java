package com.troy.trade.exchange.bitfinex.client;


import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
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
import org.apache.http.protocol.HttpContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

/**
 * BitFinex demo BitFinexHttpUtilManager
 *
 * @author yanping
 */
public class BitfinexHttpUtilManager {
    Logger logger = LoggerFactory.getLogger(BitfinexHttpUtilManager.class);

    private static final String TAG = BitfinexHttpUtilManager.class.getSimpleName();

    private static final String ALGORITHM_HMACSHA384 = "HmacSHA384";

    private static long nonce = System.currentTimeMillis();

    private static BitfinexHttpUtilManager instance = new BitfinexHttpUtilManager();
    private static CloseableHttpClient client;

    public static PoolingHttpClientConnectionManager cm;
    private static ConnectionKeepAliveStrategy keepAliveStrat;

    private BitfinexHttpUtilManager() {
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

    public static BitfinexHttpUtilManager getInstance() {
        return instance;
    }

    public HttpClient getHttpClient() {
        return client;
    }

    private HttpPost httpPostMethod(String url) {
        return new HttpPost(url);
    }

    private HttpGet httpGetMethod(String url) {
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

    public String doRequest(String requestType, String url_prex, String path, JSONObject body, String appkey, String appsecret) throws Exception {

        if (null == body) {
            body = new JSONObject();
        }

        body.put("request", path);
      /*  RedisUtil redisUtil = ApplicationContextUtil.getBean(RedisUtil.class);
        if (!redisUtil.exists(Constant.HTTP_NONCE_BITFINEX)) {
            redisUtil.set(Constant.HTTP_NONCE_BITFINEX, System.currentTimeMillis());
        }*/
        body.put("nonce", String.valueOf(System.currentTimeMillis()));
        Header[] headers = getAuthenticationHeaders(requestType, appkey, appsecret, body);

        CloseableHttpResponse response = null;
        String url = url_prex + path;
        if (StringUtils.equals(requestType, "post")) {
            HttpPost post = this.httpPostMethod(url);
            post.setHeaders(headers);
            post.setConfig(requestConfig);
            response = client.execute(post);
        } else if (StringUtils.equals(requestType, "get")) {
            if (null != body) {
                url += "?";
                StringBuffer param = new StringBuffer("");
                Iterator<String> it = body.keySet().iterator();
                String key = null;
                for (; it.hasNext(); ) {
                    key = it.next();
                    if (StringUtils.isNotBlank(param)) {
                        param.append("&");
                    }
                    param.append(key);
                    param.append("=");
                    param.append(body.get(key));
                }

                url += param.toString();
            }

            HttpGet method = this.httpGetMethod(url);
            method.setConfig(requestConfig);
            method.setHeaders(headers);
            response = client.execute(method);
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
        } catch (Exception e) {
            logger.error("调用doRequest异常，异常信息：" + e.getLocalizedMessage(), e);
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



    /**
     * Get authentication headers.
     *
     * @param body The request body.
     * @return An unmodifiable collection of HTTP headers.
     */
    protected Header[] getAuthenticationHeaders(String requestType, String apiKey, String apiKeySecret, JSONObject body) {

        Header[] headers = null;
        if ("post".equals(requestType)) {
            headers = new Header[5];
            // API v1
            String payload = body.toString();
            // this is usage for Base64 Implementation in Android. For pure java you can use java.util.Base64.Encoder
            // Base64.NO_WRAP: Base64-string have to be as one line string
            String payload_base64 = Base64.encode(payload.getBytes());
            //TODO
            String payload_sha384hmac = hmacDigest(payload_base64, apiKeySecret, ALGORITHM_HMACSHA384);
            headers[0] = new BasicHeader("Accept", "application/json");
            headers[1] = new BasicHeader("X-BFX-APIKEY", apiKey);
            headers[2] = new BasicHeader("X-BFX-SIGNATURE", payload_sha384hmac);
            headers[4] = new BasicHeader("X-BFX-PAYLOAD", payload_base64);
            headers[3] = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        }
        return headers;
    }

    private String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception: " + e.getMessage());
        } catch (InvalidKeyException e) {
            logger.error("Exception: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Exception: " + e.getMessage());
        }
        return digest;
    }
}

