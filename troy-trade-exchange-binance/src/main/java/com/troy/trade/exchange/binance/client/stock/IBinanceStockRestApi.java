package com.troy.trade.exchange.binance.client.stock;

import org.apache.http.HttpException;

import java.util.Map;

public interface IBinanceStockRestApi {

    /**
     * 盘口信息查询
     * @param tradeSymbol
     * @param limit
     * @return
     * @throws Throwable
     */
    String orderBook(String tradeSymbol, Integer limit) throws Throwable;

    /**
     * 最新成交信息查询
     * @param tradeSymbol
     * @param limit
     * @return
     * @throws Throwable
     */
    String getTrades(String tradeSymbol, Integer limit) throws Throwable;

    /**
     * 查询所有交易对信息
     * @return
     * @throws Throwable
     */
    String exchangeInfo() throws Throwable;

    /**
     * 获取所有交易对的24小时ticker数据变动
     *
     * @return
     * @throws Throwable
     */
    String tickers24() throws Throwable;

    /**
     * 根据交易对名称获取最新的价格信息
     * @param tradeSymbol
     * @return
     * @throws Throwable
     */
    String getLatestPrice(String tradeSymbol) throws Throwable;

    /****** 数据同步相关 ********************************/
    /**
     * 查询异常变动信息
     *
     * @return
     * @throws Throwable
     */
    String abnormalChanges() throws Throwable;

    /**
     * 历史充值记录查询
     * @param paramMap
     * @return
     * @throws Throwable
     */
    String depositHistory(Map<String,String> paramMap) throws Throwable;

    /**
     * 历史提现记录查询
     * @param paramMap
     * @return
     * @throws Throwable
     */
    String withdrawHistory(Map<String,String> paramMap) throws Throwable;

    /**
     * 充币地址查询
     * @param paramMap
     * @return
     * @throws Throwable
     */
    String depositAddress(Map<String,String> paramMap) throws Throwable;

    /**
     * 提现
     * @param paramMap
     * @return
     * @throws Throwable
     */
    String withdraw(Map<String,String> paramMap) throws Throwable;

    /**
     * 获取账户所有币种信息
     * @param paramMap
     * @return
     * @throws Throwable
     */
    String capitalConfigGetall(Map<String,String> paramMap) throws Throwable;

}
