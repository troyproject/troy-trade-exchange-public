package com.troy.trade.exchange.api.model.dto.in.account;


import com.troy.commons.exchange.model.in.PrivateTradeReqData;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询余额公共入参
 *
 * @author dp
 */
@Setter
@Getter
public class QueryBalanceReqDto extends PrivateTradeReqData {

    public QueryBalanceReqDto() {

    }

    public QueryBalanceReqDto(PrivateTradeReqData privateTradeReqData) {
        this.symbol = privateTradeReqData.getSymbol();
        this.apiKey = privateTradeReqData.getApiKey();
        this.apiSecret = privateTradeReqData.getApiSecret();
        this.exchCode = privateTradeReqData.getExchCode();
        this.passphrase = privateTradeReqData.getPassphrase();
        this.thirdAcctId = privateTradeReqData.getThirdAcctId();
        this.tradeSymbol = privateTradeReqData.getTradeSymbol();
    }
}
