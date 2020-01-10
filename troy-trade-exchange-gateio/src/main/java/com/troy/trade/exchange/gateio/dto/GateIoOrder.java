package com.troy.trade.exchange.gateio.dto;



import java.math.BigDecimal;

/**
 *
 */
public class GateIoOrder {

    private String orderNumber;// 订单号
    private String status;// 订单状态 open已挂单 cancelled已取消 done已完成
    private String currencyPair;// 交易对
    private String type;// 买卖类型 sell卖出, buy买入
    private BigDecimal rate;// 价格
    private BigDecimal left;
    private BigDecimal amount;// 买卖数量
    private BigDecimal initialRate;// 下单价格
    private BigDecimal initialAmount;// 下单量
    private BigDecimal filledAmount;// 实际成交数量
    private BigDecimal filledRate;// 实际成交价格
    private BigDecimal feePercentage;// 手续费百分比
    private BigDecimal feeValue;// 手续费
    private String feeCurrency;// 手续费币种
    private String fee;// 手续费
    private String timestamp;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getLeft() {
        return left;
    }

    public void setLeft(BigDecimal left) {
        this.left = left;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInitialRate() {
        return initialRate;
    }

    public void setInitialRate(BigDecimal initialRate) {
        this.initialRate = initialRate;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public BigDecimal getFilledAmount() {
        return filledAmount;
    }

    public void setFilledAmount(BigDecimal filledAmount) {
        this.filledAmount = filledAmount;
    }

    public BigDecimal getFilledRate() {
        return filledRate;
    }

    public void setFilledRate(BigDecimal filledRate) {
        this.filledRate = filledRate;
    }

    public BigDecimal getFeePercentage() {
        return feePercentage;
    }

    public void setFeePercentage(BigDecimal feePercentage) {
        this.feePercentage = feePercentage;
    }

    public BigDecimal getFeeValue() {
        return feeValue;
    }

    public void setFeeValue(BigDecimal feeValue) {
        this.feeValue = feeValue;
    }

    public String getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
