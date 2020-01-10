package com.troy.trade.exchange.okex.client.stock.dto;

public class OkTrades {

    /**
     * 成交时间
     */
    private String timestamp;
    /**
     * 成交数量
     */
    private String size;
    /**
     * 成交价格
     */
    private String price;

    /**
     * 订单id
     */
    private String trade_id;

    /**
     * 成交类型sell/buy
     */
    private String side;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }
}
