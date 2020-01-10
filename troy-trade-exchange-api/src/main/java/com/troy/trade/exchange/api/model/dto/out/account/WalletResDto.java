package com.troy.trade.exchange.api.model.dto.out.account;

import com.troy.commons.dto.out.ResData;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 资金账户信息
 */
@Setter
@Getter
public class WalletResDto extends ResData {

    private String currency;//交易对名称

    private BigDecimal balance;//

    private BigDecimal hold;//变化类型

    private BigDecimal available;//变化量/变化率（未做百分比处理，是原始数据如：0.01）


}
