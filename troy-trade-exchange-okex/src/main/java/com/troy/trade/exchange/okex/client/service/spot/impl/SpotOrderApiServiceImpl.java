package com.troy.trade.exchange.okex.client.service.spot.impl;

import com.troy.trade.exchange.okex.client.bean.spot.param.OrderParamDto;
import com.troy.trade.exchange.okex.client.bean.spot.param.PlaceOrderParam;
import com.troy.trade.exchange.okex.client.bean.spot.result.*;
import com.troy.trade.exchange.okex.client.client.APIClient;
import com.troy.trade.exchange.okex.client.config.APIConfiguration;
import com.troy.trade.exchange.okex.client.service.spot.SpotOrderAPIServive;

import java.util.List;
import java.util.Map;

/**
 * 币币订单相关接口
 **/
public class SpotOrderApiServiceImpl implements SpotOrderAPIServive {
    private final APIClient client;
    private final SpotOrderAPI spotOrderAPI;

    public SpotOrderApiServiceImpl(final APIConfiguration config) {
        this.client = new APIClient(config);
        this.spotOrderAPI = this.client.createService(SpotOrderAPI.class);
    }

    @Override
    public OrderResult addOrder(final PlaceOrderParam order) {
        return this.client.executeSync(this.spotOrderAPI.addOrder(order));
    }

    @Override
    public Map<String, List<OrderResult>> addOrders(final List<PlaceOrderParam> order) {
        return this.client.executeSync(this.spotOrderAPI.addOrders(order));
    }

    @Override
    public OrderResult cancelOrderByOrderId(final PlaceOrderParam order, final String orderId) {
        return this.client.executeSync(this.spotOrderAPI.cancleOrderByOrderId(orderId, order));
    }

    @Override
    public OrderResult cancelOrderByOrderIdPost(final PlaceOrderParam order, final String orderId) {
        return this.client.executeSync(this.spotOrderAPI.cancleOrderByOrderId_1(orderId, order));
    }

    @Override
    public Map<String, BatchOrdersResult> cancelOrders(final List<OrderParamDto> cancelOrders) {
        return this.client.executeSync(this.spotOrderAPI.batchCancleOrders(cancelOrders));
    }

    @Override
    public Map<String, List<BatchOrdersResult>> cancelOrdersPost(final List<OrderParamDto> cancelOrders) {
        return this.client.executeSync(this.spotOrderAPI.batchCancleOrders_1(cancelOrders));
    }

    @Override
    public OrderInfo getOrderByOrderId(final String product, final String orderId) {
        return this.client.executeSync(this.spotOrderAPI.getOrderByOrderId(orderId, product));
    }

    @Override
    public List<OrderInfo> getOrders(final String product, final String status, final String from, final String to, final String limit) {
        return this.client.executeSync(this.spotOrderAPI.getOrders(product, status, from, to, limit));
    }
    @Override
    public List<OrderInfo> getOrdersByPage(final String product, final String status, final String from, final String to, final String limit) {
        return this.client.executeSync(this.spotOrderAPI.getOrdersByPage(product, status, from, to, limit));
    }


    @Override
    public List<OrderInfo> getPendingOrders(final String from, final String to, final String limit, final String instrument_id) {
        return this.client.executeSync(this.spotOrderAPI.getPendingOrders(from, to, limit, instrument_id));
    }

    @Override
    public List<Fills> getFills(final String orderId, final String product, final String from, final String to, final String limit) {
        return this.client.executeSync(this.spotOrderAPI.getFills(orderId, product, from, to, limit));
    }

    @Override
    public String kline(final String product, final String start, final String end, final String granularity) {
        return this.client.executeSync(this.spotOrderAPI.kline( product, start, end, granularity));
    }
}
