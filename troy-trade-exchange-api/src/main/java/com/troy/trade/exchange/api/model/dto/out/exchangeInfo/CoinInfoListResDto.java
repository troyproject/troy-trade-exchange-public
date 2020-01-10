package com.troy.trade.exchange.api.model.dto.out.exchangeInfo;

import com.troy.commons.dto.out.ResData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Setter
@Getter
public class CoinInfoListResDto extends ResData {

        /**
         * 币种名称
         */
        private List<CoinInfoResDto> coinInfoResDtoList;

}
