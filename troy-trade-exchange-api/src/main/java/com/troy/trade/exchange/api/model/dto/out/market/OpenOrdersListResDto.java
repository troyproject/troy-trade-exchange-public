package com.troy.trade.exchange.api.model.dto.out.market;

import com.troy.commons.dto.out.ResData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OpenOrdersListResDto extends ResData {

    private List<OpenOrdersResDto> myTradeResDtoList;

    public OpenOrdersListResDto() {
        super();
    }

    public OpenOrdersListResDto(List<OpenOrdersResDto> myTradeResDtoList) {
        this.myTradeResDtoList = myTradeResDtoList;
    }
}
