package com.troy.trade.exchange.api.model.dto.out.account;

import com.troy.commons.dto.out.ResData;
import lombok.Getter;
import lombok.Setter;

/**
 * 价格信息查询实体
 */
@Setter
@Getter
public class TransferResDto extends ResData {

    /**
     * 划转ID
     */
    private String transferId;

}
