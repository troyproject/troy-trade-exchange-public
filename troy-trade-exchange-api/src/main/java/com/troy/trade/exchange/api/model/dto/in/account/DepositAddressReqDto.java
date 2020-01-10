package com.troy.trade.exchange.api.model.dto.in.account;

import com.troy.commons.exchange.model.in.PrivateAccountReqData;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 充币地址查询实体
 */
@Setter
@Getter
public class DepositAddressReqDto extends PrivateAccountReqData {

    /**
     * 币种名称
     */
    @NotBlank(message = "币种名称不能为空")
    private String coinName;

}
