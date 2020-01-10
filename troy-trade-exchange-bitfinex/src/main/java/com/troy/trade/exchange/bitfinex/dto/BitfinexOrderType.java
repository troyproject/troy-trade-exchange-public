package com.troy.trade.exchange.bitfinex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Type of order to submit to the system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public enum BitfinexOrderType {
  LIMIT("exchange limit"),
  MARKET("exchange market"),
  STOP("exchange stop"),
  REAILING_STOP("exchange trailing-stop"),
  FILL_OR_KILL("exchange fill-or-kill");

  private String value;

  BitfinexOrderType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
