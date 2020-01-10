package com.troy.trade.exchange.api.model.dto.out.order;

import com.troy.commons.dto.out.ResData;
import com.troy.trade.exchange.api.model.dto.out.order.binance.BinanceResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 下单出参
 *
 * @author dp
 */
@Setter
@Getter
public class CreateOrderResDto extends ResData {

    /**
     * 交易所返回的订单ID
     */
    private String orderId;

    /**
     * 币安下单全量返回
     */
    private BinanceResponse binanceResponse;

    public CreateOrderResDto() {

    }

    public CreateOrderResDto(String orderId) {
        this.orderId = orderId;
    }

    public CreateOrderResDto(String orderId, BinanceResponse binanceResponse) {
        this.orderId = orderId;
        this.binanceResponse = binanceResponse;
    }
}
