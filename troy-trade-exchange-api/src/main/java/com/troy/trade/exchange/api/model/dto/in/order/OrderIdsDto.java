package com.troy.trade.exchange.api.model.dto.in.order;

import com.troy.commons.dto.in.ReqData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderIdsDto extends ReqData {
    private String orderId;
    private String symbol;
}
