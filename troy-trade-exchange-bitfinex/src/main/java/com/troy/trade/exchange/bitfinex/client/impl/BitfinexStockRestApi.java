package com.troy.trade.exchange.bitfinex.client.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.troy.trade.exchange.bitfinex.client.BitfinexConstant;
import com.troy.trade.exchange.bitfinex.client.BitfinexHttpUtilManager;
import com.troy.trade.exchange.bitfinex.client.IBitfinexStockRestApi;
import com.troy.trade.exchange.bitfinex.dto.BitfinexCreateOrderRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class BitfinexStockRestApi implements IBitfinexStockRestApi {

    Logger logger = LoggerFactory.getLogger(getClass());

    private String appkey;

    private String appsecret;

    private static String url_prex = "https://dddddd.bitfinex.com";

    private static String pub_url_prex = "https://dddddd-pub.bitfinex.com";

    private static String TICKER_URL = "/v2/ticker";

    private static String TRADEHISTORY_URL = "/auth/r/xxxxxs/t{0}/hist";

    private static String TRADEHISTORY_URL1  ="/v2/auth/r/orders/Symbol/hist";

    private static String CREATEORDER_URL = "/v1/order/new";//创建订单

    private static String CANCELORDER_URL = "/v1/order/cancel";//取消订单

    private static String CANCELMULTIORDER_URL = "/v1/order/cancel/multi";//批量撤销

    private static String BALANCE_URL = "/v1/balances";//查询余额

    private static String ORDERSTATUS_URL = "/v1/order/status";//查询订单状态

    private static String SYMBOL_DETAIL_URL = "/v1/symbols_details";//交易对详情，用来查精度

    private static String ORDERBOOK_URL = "/v1/book/{tradeSymbol}";

    private static String V2_TRADEHISTORY_URL = "/v2/xxxxxs/t{0}/hist";

    private static String V2_TICKERS_URL = "/v2/tickers";

    //历史充值记录查询
    public static String HARKWITHDRAWAL_HISTORY_URL = "/v1/history/movements";

    /**
     * 提现
     */
    public static String V2_WITHDRAW_URL = "/v2/auth/w/withdraw";

    /**
     * 币种简称
     */
    public static String V2_CURRENCY_LABEL_URL = "/v2/conf/pub:map:currency:label";


    public BitfinexStockRestApi() {
        super();
    }

    public BitfinexStockRestApi(String appkey, String appsecret) {
        this.appkey = appkey;
        this.appsecret = appsecret;
    }

    @Override
    public String ticker(String symbol) throws HttpException, IOException {
        BitfinexHttpUtilManager httpUtil = BitfinexHttpUtilManager.getInstance();
        String param = "";

        param += "/" + symbol;

        String result = httpUtil.requestHttpGet(this.pub_url_prex, this.TICKER_URL + param, "");
        return result;
    }

	@Override
	public String tradeHistory(String symbol,int limit) throws HttpException, IOException {
        BitfinexHttpUtilManager httpUtil = BitfinexHttpUtilManager.getInstance();
		String param = "";
		String url = this.TRADEHISTORY_URL;
		if(!StringUtils.isEmpty(symbol )) {
            url = MessageFormat.format(url,symbol);
		}
		if(limit != 0){
		    param += "limit="+limit;
        }
		String result = httpUtil.requestHttpGet(this.pub_url_prex, url, param);
		return result;
	}

    @Override
    public String createOrder(BitfinexCreateOrderRequest createOrderRequest) throws Exception {
        JSONObject params = getCreateOrderParam(createOrderRequest);
        return doPost(this.CREATEORDER_URL,params);
    }

    /**
     * 将创建订单参数转为Map
     * @param createOrderRequest
     * @return
     */
    private JSONObject getCreateOrderParam(BitfinexCreateOrderRequest createOrderRequest){
        if(null == createOrderRequest){
            return null;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("symbol", createOrderRequest.getSymbol());
            json.put("amount", createOrderRequest.getAmount());
            json.put("price", createOrderRequest.getPrice());
            json.put("side", createOrderRequest.getSide().getValue());
            json.put("type", createOrderRequest.getType().getValue());
            json.put("ocoorder",createOrderRequest.isOcoorder());
            json.put("buy_price_oco",createOrderRequest.getBuy_price_oco());
            json.put("sell_price_oco",createOrderRequest.getSell_price_oco());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public String cancelOrder(String orderNumber) throws Exception {
        JSONObject json = new JSONObject();
        json.put("order_id", Long.parseLong(orderNumber));
        return doPost(this.CANCELORDER_URL,json);
    }

    @Override
    public String cancelMultiOrders(List<String> orderIds) throws Exception {
        JSONObject json = new JSONObject();
        json.put("order_ids",JSONArray.toJSONString(orderIds));
        return doPost(this.CANCELMULTIORDER_URL,json);
    }

    @Override
    public String balance() throws Exception {
        return doPost(this.BALANCE_URL,null);
    }

    @Override
    public String getOrder(String orderNumber) throws Exception {
        JSONObject json = new JSONObject();
        json.put("order_id",Long.parseLong(orderNumber));
        return doPost(this.ORDERSTATUS_URL,json);
    }

    @Override
    public String symbolDetail() throws Exception {
        BitfinexHttpUtilManager httpUtil = BitfinexHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpGet(this.url_prex, this.SYMBOL_DETAIL_URL, null);
        return result;
    }

    @Override
    public String getOrderBook(String tradeSymbol, Integer limit_bids, Integer limit_asks) throws Exception {
        BitfinexHttpUtilManager httpUtil = BitfinexHttpUtilManager.getInstance();
        String url = this.ORDERBOOK_URL;
        url = url.replace("{tradeSymbol}",tradeSymbol);
        String params = "";
        if(null != limit_bids){
            params = "limit_bids="+limit_bids;
        }

        if(null != limit_asks){
            if(StringUtils.isNotBlank(params)){
                params+="&";
            }
            params += "limit_asks="+limit_asks;
        }

        String result = httpUtil.requestHttpGet(this.url_prex, url, params);
        return result;
    }

    @Override
    public String tradeHistoryV2(String symbol, int limit) throws HttpException, IOException {
        BitfinexHttpUtilManager httpUtil = BitfinexHttpUtilManager.getInstance();
        String param = "";
        String url = this.V2_TRADEHISTORY_URL;
        if(!StringUtils.isEmpty(symbol)) {
            url = MessageFormat.format(url,symbol);
        }
        if(limit != 0){
            param += "limit="+limit;
        }
        String result = httpUtil.requestHttpGet(this.pub_url_prex, url, param);
        return result;
    }

    @Override
    public String V2_tickers(String symbol) throws Throwable {
        BitfinexHttpUtilManager httpUtil = BitfinexHttpUtilManager.getInstance();
        String param = "";
        String url = this.V2_TICKERS_URL;
        if(!StringUtils.isEmpty(symbol )) {
            param = "symbols=";
            param += symbol;
        }
        String result = httpUtil.requestHttpGet(this.pub_url_prex, url, param);
        return result;
    }

    @Override
    public String harkWithdrawalHistory(Map<String, Object> paramMap) throws Throwable {
        if(null == paramMap || paramMap.isEmpty()){
            logger.info("调用bitfinex的harkWithdrawalHistory接口，入参为空");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(paramMap));
        return doPost(this.HARKWITHDRAWAL_HISTORY_URL,jsonObject);
    }

    @Override
    public String V2_withdraw(Map<String, Object> paramMap) throws Throwable {
        if(null == paramMap || paramMap.isEmpty()){
            logger.info("调用bitfinex的withdraw接口，入参为空");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(paramMap));
        return doPost(this.V2_WITHDRAW_URL,jsonObject);
    }

    @Override
    public String V2_currency_label() throws Throwable {
        BitfinexHttpUtilManager httpUtil = BitfinexHttpUtilManager.getInstance();
        String param = "";
        String url = this.V2_CURRENCY_LABEL_URL;
        String result = httpUtil.requestHttpGet(this.url_prex, url, param);
        return result;
    }

    private String doPost(String path, JSONObject params) throws Exception {
        if(StringUtils.isBlank(appkey)
                ||StringUtils.isBlank(appsecret)){
            logger.info("调用bitfinex的post接口，appkey或appsecret为空");
            return null;
        }

        BitfinexHttpUtilManager httpUtil = BitfinexHttpUtilManager.getInstance();
        String result = httpUtil.doRequest( "post", this.url_prex, path, params, appkey, appsecret);
        return result;
    }

}
