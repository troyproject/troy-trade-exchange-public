package com.troy.trade.exchange.api.model.dto.in.account;

import com.troy.commons.dto.in.ReqData;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class HarkWithdrawalReqDto extends ReqData {

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
     * 1充值 2提现
     */
    private Integer type;

    /**
     * 三方apiKey密码
     */
    private String passphrase;

    /**
     * 开始时间,单位：毫秒
     * 币安、biefinex会用到
     */
    private String startTime;

    /**
     * 结束时间,单位：毫秒
     */
    private String endTime;

    /**
     * 币种名称
     */
    private String coinName;

    /**
     * 起始ID,火币
     */
    private String from;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 页码
     */
    private Integer page;
}
