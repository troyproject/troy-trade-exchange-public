package com.troy.trade.exchange.okex.client.stock.dto;

public class OkTicker {

    /**
     * 漲跌幅
     */
    private String changePercentage;

    /**
     * 最新价格
     */
    private String last;

    /**
     * 24小時成交量
     */
    private String volume;

    /**
     * 24小時成交额

     */
    private String coinVolume;

    /**
     * 24小時最高
     */
    private String high;

    /**
     * 24小時最低
     */
    private String low;

    public String getCoinVolume() {
        return coinVolume;
    }

    public void setCoinVolume(String coinVolume) {
        this.coinVolume = coinVolume;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(String changePercentage) {
        this.changePercentage = changePercentage;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
