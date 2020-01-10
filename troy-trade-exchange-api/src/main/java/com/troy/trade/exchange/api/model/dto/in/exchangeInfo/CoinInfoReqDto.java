package com.troy.trade.exchange.api.model.dto.in.exchangeInfo;

import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.PublicMarketReqData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CoinInfoReqDto extends PublicMarketReqData {

    /**
     * 币种名称
     */
    private String coinName;

    /**
     * 交易所code信息
     */
    private ExchangeCode exchCode;

}
