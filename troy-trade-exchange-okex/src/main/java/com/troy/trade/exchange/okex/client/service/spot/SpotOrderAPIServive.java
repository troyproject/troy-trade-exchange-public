package com.troy.trade.exchange.okex.client.service.spot;

import com.troy.trade.exchange.okex.client.bean.spot.param.OrderParamDto;
import com.troy.trade.exchange.okex.client.bean.spot.param.PlaceOrderParam;
import com.troy.trade.exchange.okex.client.bean.spot.result.*;


import java.util.List;
import java.util.Map;

public interface SpotOrderAPIServive {
    /**
     * 添加订单
     *
     * @param order
     * @return
     */
    OrderResult addOrder(PlaceOrderParam order);

    /**
     * 批量下单
     *
     * @param order
     * @return
     */
    Map<String, List<OrderResult>> addOrders(List<PlaceOrderParam> order);

    /**
     * 取消单个订单 delete协议
     *
     *  @param order
     * @param orderId
     */
    OrderResult cancelOrderByOrderId(final PlaceOrderParam order, String orderId);

    /**
     * 取消单个订单 post协议
     *
     * @param order
     * @param orderId
     */
    OrderResult cancelOrderByOrderIdPost(final PlaceOrderParam order, String orderId);

    /**
     * 批量取消订单 delete协议
     *
     * @param cancleOrders
     * @return
     */
    Map<String, BatchOrdersResult> cancelOrders(final List<OrderParamDto> cancleOrders);

    /**
     * 批量取消订单 post协议
     *
     * @param cancleOrders
     * @return
     */
    Map<String, List<BatchOrdersResult>> cancelOrdersPost(final List<OrderParamDto> cancleOrders);

    /**
     * 单个订单
     * @param product
     * @param orderId
     * @return
     */
    OrderInfo getOrderByOrderId(String product, String orderId);

    /**
     * 订单列表
     *
     * @param product
     * @param status
     * @param from
     * @param to
     * @param limit
     * @return
     */
    List<OrderInfo> getOrders(String product, String status, String from, String to, String limit);

    List<OrderInfo> getOrdersByPage(final String product, final String status, final String from, final String to, final String limit);
    /**
     * 订单列表
     *
     * @param from
     * @param to
     * @param limit
     * @return
     */
    List<OrderInfo> getPendingOrders(String from, String to, String limit, String instrument_id);

    /**
     * 账单列表
     *
     * @param orderId
     * @param product
     * @param from
     * @param to
     * @param limit
     * @return
     */
    List<Fills> getFills(String orderId, String product, String from, String to, String limit);


    /**
     * kLine
     * @param product
     * @param start
     * @param end
     * @param granularity
     * @return
     */
    String kline(final String product, final String start, final String end, final String granularity);
}
