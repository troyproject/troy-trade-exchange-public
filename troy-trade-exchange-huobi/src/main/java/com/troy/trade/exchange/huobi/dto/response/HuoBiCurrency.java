package com.troy.trade.exchange.huobi.dto.response;

/**
 * 查询余额返回实体
 */
public class HuoBiCurrency {

    //"currency":"usdt","type":"trade","balance":"500009195917.4362872650"

    private String currency;

    private String type;

    private String balance;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
