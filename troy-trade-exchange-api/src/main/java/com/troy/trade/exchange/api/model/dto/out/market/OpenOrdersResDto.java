package com.troy.trade.exchange.api.model.dto.out.market;

import com.troy.commons.dto.out.ResData;
import com.troy.commons.exchange.model.enums.OrderSideEnum;
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OpenOrdersResDto extends ResData {

    /**
     * 交易方向（1-买 2-卖）
     */
    private OrderSideEnum orderSide;
    /**
     * Amount that was traded
     */
    private BigDecimal amount;

    /**
     * The currency pair
     */
    private String symbol;

    /**
     * The price
     */
    private BigDecimal price;

    /**
     * The timestamp of the trade according to the exchange's server, null if not provided
     */
    private Long timestamp;

    /**
     * The trade id
     */
    private String id;
    /**
     * 手续费
     **/
    private BigDecimal commission;

    /**
     * 手续费单位
     **/
    private String commissionAsset;
    private String orderId;


    public OpenOrdersResDto() {
        super();
    }

    public OpenOrdersResDto(String commissionAsset, String orderId, OrderSideEnum orderSide, BigDecimal amount, String symbol, BigDecimal price, Long timestamp, String id, BigDecimal commission) {
        this.commissionAsset = commissionAsset;
        this.orderId = orderId;
        this.orderSide = orderSide;
        this.amount = amount;
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
        this.id = id;
        this.commission = commission;
    }

}
