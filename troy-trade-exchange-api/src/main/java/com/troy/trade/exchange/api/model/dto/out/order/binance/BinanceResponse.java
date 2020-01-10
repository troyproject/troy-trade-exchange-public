package com.troy.trade.exchange.api.model.dto.out.order.binance;

import com.troy.trade.exchange.api.model.dto.out.order.OrderDetailResDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 币安下单全量返回
 *
 * @author dp
 */
@Setter
@Getter
public class BinanceResponse extends OrderDetailResDto implements Serializable {

    private Long spotTransId;

}