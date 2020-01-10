package com.troy.trade.exchange.gateio.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.troy.commons.dto.out.ResData;

import java.math.BigDecimal;

/**
 * GateioOpenOrder
 * 当前挂单
 *
 * @author liuxiaocheng
 * @date 2018/7/19
 */

public class GateioOpenOrder extends ResData {
    //订单总数量 剩余未成交数量
    private BigDecimal amount;
    //订单交易对
    private String currencyPair;
    //已成交量
    private BigDecimal filledAmount;
    //成交价格
    private BigDecimal filledRate;
    //下单量
    private BigDecimal initialAmount;
    //下单价格
    private BigDecimal initialRate;
    //订单号
    private String orderNumber;
    //交易单价
    private BigDecimal rate;
    //订单状态
    private String status;
    //时间戳
    private long timestamp;
    //总计
    private BigDecimal total;
    //买卖类型 buy:买入;sell:卖出
    private String type;

    public GateioOpenOrder(@JsonProperty("amount") BigDecimal amount, @JsonProperty("currencyPair") String currencyPair, @JsonProperty("filledAmount") BigDecimal filledAmount,
                           @JsonProperty("filledRate") BigDecimal filledRate, @JsonProperty("initialAmount") BigDecimal initialAmount, @JsonProperty("initialRate") BigDecimal initialRate,
                           @JsonProperty("orderNumber") String orderNumber, @JsonProperty("rate") BigDecimal rate, @JsonProperty("status") String status,
                           @JsonProperty("timestamp") long timestamp, @JsonProperty("total") BigDecimal total, @JsonProperty("type") String type) {
        this.amount = amount;
        this.currencyPair = currencyPair;
        this.filledAmount = filledAmount;
        this.filledRate = filledRate;
        this.initialAmount = initialAmount;
        this.initialRate = initialRate;
        this.orderNumber = orderNumber;
        this.rate = rate;
        this.status = status;
        this.timestamp = timestamp;
        this.total = total;
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public BigDecimal getFilledAmount() {
        return filledAmount;
    }

    public BigDecimal getFilledRate() {
        return filledRate;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public BigDecimal getInitialRate() {
        return initialRate;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getType() {
        return type;
    }
}
