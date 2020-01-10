package com.troy.trade.exchange.api.model.dto.in.order;

import com.troy.commons.exchange.model.in.PrivateTradeReqData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OpenOrdersReqDto extends PrivateTradeReqData {

    private  String symbol;

    private  String limit;

    private Long recvWindow;

    private Long timestamp;
    public OpenOrdersReqDto(){}

    public OpenOrdersReqDto(String symbol) {
        this.symbol = symbol;
        this.timestamp = System.currentTimeMillis();
    }
}
