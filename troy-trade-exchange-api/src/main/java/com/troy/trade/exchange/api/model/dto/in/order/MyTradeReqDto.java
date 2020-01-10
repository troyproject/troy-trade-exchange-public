package com.troy.trade.exchange.api.model.dto.in.order;

import com.troy.commons.exchange.model.in.PrivateTradeReqData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MyTradeReqDto extends PrivateTradeReqData {
    private Integer limit;
    private String fromId;
    private Long recvWindow;
    private Long timestamp;
    private Long startTime;
    private Long endTime;
    private String orderId;
}
