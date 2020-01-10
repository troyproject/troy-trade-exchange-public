package com.troy.trade.exchange.bitfinex.dto;

public class BitfinexCreateOrderRequest {

    /**
     * 交易对，必填，例如："ethcny"，
     */
    private String symbol;

    /**
     * 交易量
     */
    private String amount;

    /**
     * 交易价格
     */
    private String price;

    /**
     * 买卖方向，buy-买、sell-卖
     */
    private BitfinexOrderSide side;

    /**
     * 交易类型
     */
    private BitfinexOrderType type;

    /**
     *
     */
    private boolean ocoorder;

    /**
     *
     */
    private String buy_price_oco;

    /**
     *
     */
    private String sell_price_oco;


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public BitfinexOrderSide getSide() {
        return side;
    }

    public void setSide(BitfinexOrderSide side) {
        this.side = side;
    }

    public BitfinexOrderType getType() {
        return type;
    }

    public void setType(BitfinexOrderType type) {
        this.type = type;
    }

    public boolean isOcoorder() {
        return ocoorder;
    }

    public void setOcoorder(boolean ocoorder) {
        this.ocoorder = ocoorder;
    }

    public String getBuy_price_oco() {
        return buy_price_oco;
    }

    public void setBuy_price_oco(String buy_price_oco) {
        this.buy_price_oco = buy_price_oco;
    }

    public String getSell_price_oco() {
        return sell_price_oco;
    }

    public void setSell_price_oco(String sell_price_oco) {
        this.sell_price_oco = sell_price_oco;
    }
}
