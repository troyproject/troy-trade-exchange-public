package com.troy.trade.exchange.bitfinex.client;


import com.troy.trade.exchange.bitfinex.dto.BitfinexCreateOrderRequest;
import org.apache.http.HttpException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IBitfinexStockRestApi {

    String ticker(String symbol) throws HttpException, IOException;

    String tradeHistory(String symbol, int limit) throws HttpException, IOException;

    /**
     * 下单
     *
     * @param createOrderRequest
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String createOrder(BitfinexCreateOrderRequest createOrderRequest) throws Exception;

    /**
     * 取消下单
     *
     * @param orderNumber
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String cancelOrder(String orderNumber) throws Exception;

    /**
     * 批量取消挂单
     *
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String cancelMultiOrders(List<String> orderIds) throws Exception;

    /**
     * 余额查询
     *
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String balance() throws Exception;


    /**
     * 获取下单状态
     *
     * @param orderNumber
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String getOrder(String orderNumber) throws Exception;

    /**
     * 查询交易对信息，精度查询
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String symbolDetail() throws Exception;

    /**
     * 获取盘口信息
     * @param tradeSymbol
     * @param limit_bids
     * @param limit_asks
     * @return
     * @throws Exception
     */
    String getOrderBook(String tradeSymbol,Integer limit_bids,Integer limit_asks) throws Exception;

    String tradeHistoryV2(String symbol, int limit) throws HttpException, IOException;

    /**
     * 调用V2 接口查找ticker信息 -- 可以批量查找
     * 当symbol为 ALL时返回所有交易对的ticker信息
     * @param symbol
     * @return
     * @throws Throwable
     */
    String V2_tickers(String symbol) throws Throwable;

    /**
     * 查询账号历史充提币信息列表
     * @param paramMap
     * @author yanping
     * @return
     * @throws Throwable
     */
    String harkWithdrawalHistory(Map<String,Object> paramMap) throws Throwable;

    /**
     * 提现
     * @param paramMap
     *  wallet* String Select an origin wallet for your withdrawl ("trading" = margin, "exchange" = exchange, "deposit" = funding)
        method* String Method of withdrawal (methods accepted: “bitcoin”, “litecoin”, “ethereum”, “tetheruso", “tetherusl", “tetherusx", “tetheruss", "ethereumc", "zcash", "monero", "iota"). For an up-to-date listing of supported currencies see: https://dddddd.bitfinex.com/v2/conf/pub:map:currency:label
        amount* String Amount of Withdrawal
        address* String Destination address
     *
     * @return
     * @throws Throwable
     */
    String V2_withdraw(Map<String,Object> paramMap) throws Throwable;

    /**
     * 币种简称查询
     *
     * @return
     * @throws Throwable
     */
    String V2_currency_label() throws Throwable;
}
