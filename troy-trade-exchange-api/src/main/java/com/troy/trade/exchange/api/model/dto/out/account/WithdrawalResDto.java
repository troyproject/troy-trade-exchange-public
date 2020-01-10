package com.troy.trade.exchange.api.model.dto.out.account;

import com.troy.commons.dto.out.ResData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawalResDto extends ResData {

    /**
     * 第三方返回ID
     */
    private String thirdId;


}
