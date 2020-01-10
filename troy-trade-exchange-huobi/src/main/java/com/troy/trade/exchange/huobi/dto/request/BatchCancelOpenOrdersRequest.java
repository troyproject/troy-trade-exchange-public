package com.troy.trade.exchange.huobi.dto.request;

import com.google.gson.annotations.SerializedName;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 14:39
 */
public class BatchCancelOpenOrdersRequest {

    //账户ID
    @SerializedName("account-id")
    private String accountId;

    //交易对		单个交易对字符串，缺省将返回所有符合条件尚未成交订单
    private String symbol;

    //主动交易方向		“buy”或“sell”，缺省将返回所有符合条件尚未成交订单
    private String side;

    //所需返回记录数	100	[0,100]
    private int size;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
