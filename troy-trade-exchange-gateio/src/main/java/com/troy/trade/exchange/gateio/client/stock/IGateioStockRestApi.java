package com.troy.trade.exchange.gateio.client.stock;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpException;

import java.io.IOException;

public interface IGateioStockRestApi {

    //    String pairs() throws HttpException, IOException;

    String marketinfo() throws HttpException, IOException;

//    String marketlist() throws HttpException, IOException;

    String tickers() throws Throwable;

    String ticker(String symbol) throws HttpException, IOException;

    String orderBook(String symbol) throws Throwable;

    String tradeHistory(String symbol) throws Throwable;

    String balance(String appkey, String appsecret) throws HttpException, IOException;
//
//    String depositAddress(String symbol) throws HttpException, IOException;

    String depositsWithdrawals(String startTime, String endTime, String appkey, String appsecret) throws HttpException, IOException;

    /**
     * 买
     *
     * @param currencyPair
     * @param rate
     * @param amount
     * @param appkey
     * @param appsecret
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String buy(String currencyPair, String rate, String amount, String appkey, String appsecret) throws HttpException, IOException;

    /**
     * 卖
     *
     * @param currencyPair
     * @param rate
     * @param amount
     * @param appkey
     * @param appsecret
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String sell(String currencyPair, String rate, String amount, String appkey, String appsecret) throws HttpException, IOException;

    /**
     * 取消下单
     *
     * @param orderNumber
     * @param currencyPair
     * @param appkey
     * @param appsecret
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String cancelOrder(String orderNumber, String currencyPair, String appkey, String appsecret) throws HttpException, IOException;

    /**
     * 取消多个下单
     *
     * @param array
     * @param appkey
     * @param appsecret
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String cancelOrders(JSONArray array, String appkey, String appsecret) throws HttpException, IOException;

    /**
     * 取消所有挂单
     *
     * @param type
     * @param currencyPair
     * @param appkey
     * @param appsecret
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String cancelAllOrders(String type, String currencyPair, String appkey, String appsecret) throws HttpException, IOException;

    /**
     * 获取下单状态
     *
     * @param orderNumber
     * @param currencyPair
     * @param appkey
     * @param appsecret
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String getOrder(String orderNumber, String currencyPair, String appkey, String appsecret) throws HttpException, IOException;

    /**
     * 获取K线
     *
     * @param currencyPair
     * @param groupSec
     * @param rangeHour
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String getKline(String currencyPair, String groupSec, String rangeHour) throws HttpException, IOException;

    /**
     * 获取当前挂单
     *
     * @param currencyPair 交易对
     * @param appkey
     * @param appsecret
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String openOrders(String currencyPair, String appkey, String appsecret) throws HttpException, IOException;

    /**
     * 获取我的24小时内成交记录API
     *
     * API URL: https://dddddd.gateio.co/dddddd2/1/private/xxxxxHistory
     * @param currencyPair
     * @param orderNumber
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String myTradeHistory(String currencyPair, String orderNumber, String appkey, String appsecret) throws HttpException, IOException;

    /**
     * 提现
     * @param currency
     * @param amount
     * @param address
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String withdraw(String currency, String amount, String address, String apiKey, String apiSecret) throws HttpException, IOException;

    /**
     * 充币地址查询
     * @param apiKey apiSecret currency
     * @return
     * @throws Throwable
     */
    String depositAddress(String apiKey,String apiSecret,String currency) throws Throwable;
}
