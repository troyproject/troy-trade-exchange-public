package com.troy.trade.exchange.gateio.dto;


import com.troy.commons.dto.out.ResData;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class GateIoTradeReturn extends ResData {

    private boolean result;
    private String message;
    private int code;
    private String orderNumber;
    private BigDecimal rate;
    private BigDecimal leftAmount;
    private BigDecimal filledAmount;
    private BigDecimal filledRate;

    public boolean isSuccess(){
        return result;
    }
}
