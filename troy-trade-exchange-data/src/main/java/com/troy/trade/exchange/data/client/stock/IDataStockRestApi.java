package com.troy.trade.exchange.data.client.stock;

import java.util.Map;

public interface IDataStockRestApi {

    /**
     * 查询指定ID的index信息
     *
     * @param paramMap 参数名	参数类型	是否必须	描述
     *                 coin  	String	币名称，如BTC
     *                 page     String  页码，从1开始
     * @return
     * @throws Throwable
     */
    String largetransfer(Map<String, String> paramMap) throws Throwable;

    /**
     * 获取usd对cny的值
     * @return
     * @throws Throwable
     */
    String usdExchangeCny() throws Throwable;

    /**
     * 根据参数查找 todamoon的折线数据
     * @param paramMap type - 同步类型、timeGranularity - 时间粒度，如："minute","hour","day",biefinex持仓数据同步会用到
     * @return
     * @throws Throwable
     */
    String todamoonSourceData(Map<String,String> paramMap) throws Throwable;
}
