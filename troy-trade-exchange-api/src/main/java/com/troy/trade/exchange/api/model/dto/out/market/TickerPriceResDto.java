package com.troy.trade.exchange.api.model.dto.out.market;

import com.troy.commons.dto.out.ResData;

import java.math.BigDecimal;

/**
 * ticker交易所信息返回实体
 */
public class TickerPriceResDto extends ResData {

    /**
     * 交易对
     */
    private String symbol;

    /**
     *
     */
    private BigDecimal price;

    public TickerPriceResDto() {
        super();
    }

    public TickerPriceResDto(String symbol, BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
