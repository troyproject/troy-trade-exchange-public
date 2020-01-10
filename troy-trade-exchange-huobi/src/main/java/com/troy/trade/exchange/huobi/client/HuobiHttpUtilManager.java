package com.troy.trade.exchange.huobi.client;

import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HuobiHttpUtilManager {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static HuobiHttpUtilManager instance = new HuobiHttpUtilManager();
    private static CloseableHttpClient client;

    public static PoolingHttpClientConnectionManager cm;
    private static ConnectionKeepAliveStrategy keepAliveStrat;

    private HuobiHttpUtilManager() {
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

    public static HuobiHttpUtilManager getInstance() {
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

    public String requestHttp(String url_prex, String path,
                              String methodType,String apiKey,
                              String secretKey,
                              Map<String,String> paramMap) throws Throwable  {


        String methodTypeUpper = methodType.toUpperCase();

        if(null == paramMap){
            paramMap = new HashMap<>();
        }

        if(StringUtils.isNotBlank(apiKey)){
            this.createSignature(apiKey,secretKey,methodType,HuobiConstant.HOST,path,paramMap);
        }

        CloseableHttpResponse response = null;
        if(StringUtils.equals(HuobiConstant.METHOD_GET,methodTypeUpper)){//
            String url = url_prex+path+ "?" + toQueryString(paramMap);
            HttpGet get = new HttpGet(url);
            get.setConfig(requestConfig);
            response = client.execute(get);
        }else{
            String url = url_prex+path+ "?" + toQueryString(paramMap);
            HttpPost post = new HttpPost(url);

            Header[] headers = new Header[1];
            headers[0] = new BasicHeader(HuobiConstant.HTTP_CONTENT_TYPE,HuobiConstant.HTTP_CONTENT_TYPE_JSON);
            post.setHeaders(headers);
            if(null != paramMap && !paramMap.isEmpty()){
                StringEntity requestEntity = new StringEntity(JSONObject.toJSONString(paramMap), "utf-8");
                requestEntity.setContentEncoding("UTF-8");
                post.setEntity(requestEntity);
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


    static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
    static final ZoneId ZONE_GMT = ZoneId.of("Z");

    /**
     * 创建一个有效的签名。该方法为客户端调用，将在传入的params中添加AccessKeyId、Timestamp、SignatureVersion、SignatureMethod、Signature参数。
     *
     * @param apiKey       AppKeyId.
     * @param apiSecretKey AppKeySecret.
     * @param method       请求方法，"GET"或"POST"
     * @param host         请求域名，例如"be.huobi.com"
     * @param uri          请求路径，注意不含?以及后的参数，例如"/v1/dddddd/info"
     * @param params       原始请求参数，以Key-Value存储，注意Value不要编码
     */
    private void createSignature(String apiKey, String apiSecretKey, String method, String host,
                                String uri, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(method.toUpperCase()).append('\n') // GET
                .append(host.toLowerCase()).append('\n') // Host
                .append(uri).append('\n'); // /path
        params.remove("Signature");
        params.put("AccessKeyId", apiKey);
        params.put("SignatureVersion", "2");
        params.put("SignatureMethod", "HmacSHA256");
        params.put("Timestamp", gmtNow());
        // build signature:
        SortedMap<String, String> map = new TreeMap<>(params);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            sb.append(key).append('=').append(urlEncode(value)).append('&');
        }
        // remove last '&':
        sb.deleteCharAt(sb.length() - 1);
        // sign:
        Mac hmacSha256 = null;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secKey =
                    new SecretKeySpec(apiSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm: " + e.getMessage());
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key: " + e.getMessage());
        }
        String payload = sb.toString();
        byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        String actualSign = Base64.getEncoder().encodeToString(hash);
        params.put("Signature", actualSign);
    }

    /**
     * 使用标准URL Encode编码。注意和JDK默认的不同，空格被编码为%20而不是+。
     *
     * @param s String字符串
     * @return URL编码后的字符串
     */
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 encoding not supported!");
        }
    }

    /**
     * Return epoch seconds
     */
    long epochNow() {
        return Instant.now().getEpochSecond();
    }

    String gmtNow() {
        return Instant.ofEpochSecond(epochNow()).atZone(ZONE_GMT).format(DT_FORMAT);
    }

    // Encode as "a=1&b=%20&c=&d=AAA"
    String toQueryString(Map<String, String> params) {
        return String.join("&", params.entrySet().stream().map((entry) -> {
            return entry.getKey() + "=" + this.urlEncode(entry.getValue());
        }).collect(Collectors.toList()));
    }
}

