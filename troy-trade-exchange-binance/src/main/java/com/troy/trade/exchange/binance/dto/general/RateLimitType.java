package com.troy.trade.exchange.binance.dto.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Rate limiters.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public enum RateLimitType {
  REQUEST_WEIGHT,
  ORDERS
}
