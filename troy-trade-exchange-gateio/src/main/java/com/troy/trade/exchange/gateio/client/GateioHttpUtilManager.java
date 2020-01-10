package com.troy.trade.exchange.gateio.client;

import com.google.common.io.CharStreams;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * gateio demo HttpUtilManager
 *
 * @author GateIo
 */
public class GateioHttpUtilManager {
    Logger logger = LoggerFactory.getLogger(GateioHttpUtilManager.class);

    private static GateioHttpUtilManager instance = new GateioHttpUtilManager();
    private static CloseableHttpClient client;

    public static PoolingHttpClientConnectionManager cm;
    private static ConnectionKeepAliveStrategy keepAliveStrat;

    private GateioHttpUtilManager() {
        keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(
                    HttpResponse response,
                    HttpContext context) {
                long keepAlive = super.getKeepAliveDuration(response, context);

                if (keepAlive == -1) {
                    keepAlive = 60000;
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

    public static GateioHttpUtilManager getInstance() {
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

    public String doRequest(String api, String requestType, String url, Map<String, String> arguments, String appkey, String appsecret) throws HttpException, IOException {

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        Mac mac = null;
        SecretKeySpec key = null;

        String postData = "";

        for (Iterator<Entry<String, String>> argumentIterator = arguments.entrySet().iterator(); argumentIterator.hasNext(); ) {

            Entry<String, String> argument = argumentIterator.next();
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(argument.getValue())) {
                urlParameters.add(new BasicNameValuePair(argument.getKey().toString(), argument.getValue().toString()));
            }

            if (postData.length() > 0) {
                postData += "&";
            }

            postData += argument.getKey() + "=" + argument.getValue();

        }

        // Create a new secret key
        try {
            key = new SecretKeySpec(appsecret.getBytes("UTF-8"), "HmacSHA512");
        } catch (UnsupportedEncodingException uee) {
            logger.error("Unsupported encoding exception: " + uee.toString());
        }

        try {
            mac = Mac.getInstance("HmacSHA512");
        } catch (NoSuchAlgorithmException nsae) {
            logger.error("No such algorithm exception: " + nsae.toString());
        }

        try {
            mac.init(key);
        } catch (InvalidKeyException ike) {
            logger.error("Invalid key exception: " + ike.toString());
        }

        // add header
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader("Key", appkey);
        headers[1] = new BasicHeader("Sign", Hex.encodeHexString(mac.doFinal(postData.getBytes("UTF-8"))));


        HttpPost post = null;
        HttpGet get = null;
        CloseableHttpResponse response = null;

        if (StringUtils.equals(requestType, "post")) {
            post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
            post.setHeaders(headers);
            post.setConfig(requestConfig);
            response = client.execute(post);
        } else if (StringUtils.equals(requestType, "get")) {
            get = new HttpGet(url);
            get.setHeaders(headers);
            get.setConfig(requestConfig);
            response = client.execute(get);
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
     * 获取忽略证书的httpclient
     *
     * @return
     * @throws Exception
     */
    public CloseableHttpClient getIgnoeSSLClient() throws Exception {
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        }).build();

//        return HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).setConnectionManager(cm).setConnectionManagerShared(true).setDefaultRequestConfig(requestConfig).setKeepAliveStrategy(keepAliveStrat).build();
        return HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).setConnectionManager(cm).setConnectionManagerShared(true).setDefaultRequestConfig(requestConfig).setKeepAliveStrategy(keepAliveStrat).build();
    }

}

