package com.troy.trade.exchange.huobi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 18:50
 */
@Setter
@Getter
public class MatchresultsOrdersDetail {
    /**
     * id : 29553
     * order-id : 59378
     * match-id : 59335
     * symbol : ethusdt
     * type : buy-limit
     * source : api
     * price : 100.1000000000
     * filled-amount : 9.1155000000
     * filled-fees : 0.0182310000
     * created-at : 1494901400435
     */

    private long id;
    @JsonProperty("order-id")
    private long orderid;
    @com.google.gson.annotations.SerializedName("match-id")
    private long matchid;
    private String symbol;
    private String type;
    // 成交角色
    private String role;
    private String source;
    private String price;
    @com.google.gson.annotations.SerializedName("filled-amount")
    private String filledamount;
    @JsonProperty("filled-fees")
    private String filledfees;
    @com.google.gson.annotations.SerializedName("created-at")
    private long createdat;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderid() {
        return orderid;
    }

    public void setOrderid(long orderid) {
        this.orderid = orderid;
    }

    public long getMatchid() {
        return matchid;
    }

    public void setMatchid(long matchid) {
        this.matchid = matchid;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFilledamount() {
        return filledamount;
    }

    public void setFilledamount(String filledamount) {
        this.filledamount = filledamount;
    }

    public String getFilledfees() {
        return filledfees;
    }

    public void setFilledfees(String filledfees) {
        this.filledfees = filledfees;
    }

    public long getCreatedat() {
        return createdat;
    }

    public void setCreatedat(long createdat) {
        this.createdat = createdat;
    }
}
