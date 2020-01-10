package com.troy.trade.exchange.huobi.dto.response;

public class AggregateBalance {

    /**
     * 币种名称
     */
    private String currency;

    /**
     * 币种余额
     */
    private String balance;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
