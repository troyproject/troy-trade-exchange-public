package com.troy.trade.exchange.api.model.dto.in.account;

import com.troy.commons.dto.in.ReqData;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawalReqDto extends ReqData {

    /**
     * apikey
     */
    @NotBlank(message = "apikey不能为空")
    protected String apiKey;

    /**
     * 私钥
     */
    @NotBlank(message = "apiSecret不能为空")
    protected String apiSecret;

    /**
     * 三方授权apikey的密码
     */
    protected String passphrase;

    /**
     * 交易所名称
     */
    @NotNull(message = "交易所名称不能为空")
    protected ExchangeCode exchangeCode;

    /**
     * 账户ID
     */
    private String accountId;

    /**
     * 币种名称
     */
    @NotBlank(message = "coinName不能为空")
    private String coinName;

    /**
     * 提现地址
     */
    @NotBlank(message = "address不能为空")
    private String address;

    /**
     * 提现总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实际到账金额
     */
    private BigDecimal receivedAmount;

    /**
     * OKEX必填参数
     * OKEX描述：交易密码
     */
    private String tradePwd;

}
