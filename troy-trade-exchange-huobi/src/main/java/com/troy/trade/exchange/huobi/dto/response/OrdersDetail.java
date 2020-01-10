package com.troy.trade.exchange.huobi.dto.response;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 18:22
 */

public class OrdersDetail {

    /**
     * id : 59378 symbol : ethusdt account-id : 100009 amount : 10.1000000000 price : 100.1000000000
     * created-at : 1494901162595 type : buy-limit field-amount : 10.1000000000
     * field-cash-amount : 1011.0100000000 field-fees : 0.0202000000
     * finished-at : 1494901400468 user-id : 1000 source : api
     * state : filled canceled-at : 0 exchange : huobi batch :
     */

    /**
     * 订单ID
     */
    private String id;

    /**
     * 交易对 btcusdt
     */
    private String symbol;

    /**
     * 账户 ID
     */
    private String accountId;

    /**
     * 订单数量
     */
    private BigDecimal amount;

    /**
     * 订单价格
     */
    private BigDecimal price;

    /**
     * 订单创建时间
     */
    private Date createdAt;

    /**
     * 订单变为终结态的时间，不是成交时间，包含“已撤单”状态
     */
    private Date finishedAt;

    /**
     * 订单类型:buy-market：市价买, sell-market：市价卖, buy-limit：限价买, sell-limit：限价卖, buy-ioc：IOC买单, sell-ioc：IOC卖单
     */
    private String type;

    /**
     * 已成交数量
     */
    private BigDecimal fieldAmount;

    /**
     * 已成交单价
     */
    private BigDecimal fieldPrice;

    /**
     * 已成交总金额
     */
    private BigDecimal fieldCashAmount;

    /**
     * 已成交手续费（买入为币，卖出为钱）
     */
    private BigDecimal fieldFees;

    /**
     * 订单来源
     */
    private String source;

    /**
     * submitting , submitted 已提交, partial-filled 部分成交, partial-canceled 部分成交撤销, filled 完全成交, canceled 已撤销
     */
    private String state;

    /**
     * 订单撤销时间
     */
    private long canceledAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getFieldAmount() {
        return fieldAmount;
    }

    public void setFieldAmount(BigDecimal fieldAmount) {
        this.fieldAmount = fieldAmount;
    }

    public BigDecimal getFieldCashAmount() {
        return fieldCashAmount;
    }

    public void setFieldCashAmount(BigDecimal fieldCashAmount) {
        this.fieldCashAmount = fieldCashAmount;
    }

    public BigDecimal getFieldFees() {
        return fieldFees;
    }

    public void setFieldFees(BigDecimal fieldFees) {
        this.fieldFees = fieldFees;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(long canceledAt) {
        this.canceledAt = canceledAt;
    }

    public BigDecimal getFieldPrice() {
        return fieldPrice;
    }

    public void setFieldPrice(BigDecimal fieldPrice) {
        this.fieldPrice = fieldPrice;
    }
}
