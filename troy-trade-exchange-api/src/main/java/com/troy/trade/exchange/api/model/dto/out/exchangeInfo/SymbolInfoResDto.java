package com.troy.trade.exchange.api.model.dto.out.exchangeInfo;

import com.troy.commons.dto.out.ResData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 交易对查询返回实体
 */
@Setter
@Getter
public class SymbolInfoResDto extends ResData {

    /**
     * 钱币种名称
     */
    private String quoteName;

    /**
     * 货币种名称
     */
    private String baseName;

    /**
     * 交易对名称，如：BTC/USDT
     */
    private String symbol;

    /**
     * 状态 0-禁用、1-启用、2-不可交易
     */
    private Integer status;

    /**
     * 最小成交量
     */
    private BigDecimal baseLeast;

    /**
     * 最小成交额
     */
    private BigDecimal quoteLeast;

    /**
     * 数量精度,如1
     */
    private Integer basePrecision;

    /**
     * 价格精度,如1
     */
    private Integer quotePrecision;


}
