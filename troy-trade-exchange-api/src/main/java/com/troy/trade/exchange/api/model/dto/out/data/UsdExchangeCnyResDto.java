package com.troy.trade.exchange.api.model.dto.out.data;

import com.troy.commons.dto.out.ResData;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * usd转人民币价格
 */
@Getter
@Setter
public class UsdExchangeCnyResDto extends ResData {

    /**
     * 价格
     */
    private BigDecimal price;
}
