package com.troy.trade.exchange.api.model.dto.out.exchangeInfo;

import com.troy.commons.dto.out.ResData;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SymbolInfoListResDto extends ResData {

    /**
     * 交易所code
     */
    private ExchangeCode exchangeCode;

    /**
     * 交易对信息列表
     */
    private List<SymbolInfoResDto> symbolInfoResDtoList;
}
