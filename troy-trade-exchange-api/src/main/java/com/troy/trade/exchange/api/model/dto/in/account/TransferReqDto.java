package com.troy.trade.exchange.api.model.dto.in.account;

import com.troy.commons.exchange.model.in.PrivateAccountReqData;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * 价格信息查询实体
 */
@Setter
@Getter
public class TransferReqDto extends PrivateAccountReqData {

    /**
     * 币种名称
     */
    @NotBlank(message = "币种名称不能为空")
    private String coinName;

    /**
     * 划转数量
     */
    private BigDecimal amount;

    /**
     * 转出账户
     * okex:
     *   0:子账户
         1:币币
         3:合约
         4:C2C
         5:币币杠杆
         6:资金账户
         8:余币宝
         9:永续合约
     */
    private String from;

    /**
     * 转入账户
     * okex:
     *   0:子账户
         1:币币
         3:合约
         4:C2C
         5:币币杠杆
         6:资金账户
         8:余币宝
         9:永续合约
     */
    private String to;

    /**
     * okex:
     * 子账号登录名,from或to指定为0时,sub_account为必填项，
     */
    private String subAccount;

    /**
     * 杠杆转出币对，如：eos-usdt，仅限已开通杠杆的币对
     */
    private String instrumentId;

    /**
     * 杠杆转入币对，如：eos-btc，仅限已开通杠杆的币对，仅币币杠杆内转账时用到此参数
     */
    private String toInstrumentId;

}
