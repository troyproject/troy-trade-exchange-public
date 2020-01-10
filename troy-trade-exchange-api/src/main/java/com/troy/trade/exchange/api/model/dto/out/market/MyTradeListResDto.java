package com.troy.trade.exchange.api.model.dto.out.market;

import com.troy.commons.dto.out.ResData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MyTradeListResDto extends ResData {

    private List<MyTradeResDto> myTradeResDtoList;

    public MyTradeListResDto() {
        super();
    }

    public MyTradeListResDto(List<MyTradeResDto> myTradeResDtoList) {
        this.myTradeResDtoList = myTradeResDtoList;
    }
}
