package com.troy.trade.exchange.api.model.dto.in.account;

import com.troy.commons.dto.in.ReqData;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 账号信息请求入参
 *
 * @author dp
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoReqDto extends ReqData {

    /**
     * apikey
     */
    private String apiKey;

    /**
     * 私钥
     */
    private String apiSecret;

    /**
     * 交易所代码
     */
    private ExchangeCode exchCode;
}
