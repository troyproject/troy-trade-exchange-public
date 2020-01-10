package com.troy.trade.exchange.binance.dto.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Rate limit intervals.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public enum RateLimitInterval {
  SECOND,
  MINUTE,
  DAY
}