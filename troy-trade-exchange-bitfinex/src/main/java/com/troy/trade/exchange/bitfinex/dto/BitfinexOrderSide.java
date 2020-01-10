package com.troy.trade.exchange.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Buy/Sell order side.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public enum BitfinexOrderSide {
  BUY("buy"),
  SELL("sell");

  private String value;

  BitfinexOrderSide(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
