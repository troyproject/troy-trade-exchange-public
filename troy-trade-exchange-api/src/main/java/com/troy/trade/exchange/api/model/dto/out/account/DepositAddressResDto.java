package com.troy.trade.exchange.api.model.dto.out.account;

import com.troy.commons.dto.out.ResData;
import lombok.Getter;
import lombok.Setter;

/**
 * 充币地址查询实体
 */
@Setter
@Getter
public class DepositAddressResDto extends ResData {

    /**
     * 币种名称
     */
    private String coinName;

    /**
     * 充币地址
     */
    private String address;


}
