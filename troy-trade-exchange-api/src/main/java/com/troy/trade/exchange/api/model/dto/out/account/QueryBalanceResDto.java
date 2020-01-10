package com.troy.trade.exchange.api.model.dto.out.account;

import com.troy.commons.dto.out.ResData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 查询余额返回
 *
 * @author dp
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QueryBalanceResDto extends ResData {

    /**
     * 币种
     */
    private String currency;

    /**
     * 冻结金额
     */
    private BigDecimal frozen;

    /**
     * 可用余额
     */
    private BigDecimal usable;


}
