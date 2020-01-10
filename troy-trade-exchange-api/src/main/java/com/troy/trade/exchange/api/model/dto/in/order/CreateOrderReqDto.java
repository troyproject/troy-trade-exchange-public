package com.troy.trade.exchange.api.model.dto.in.order;

import com.troy.commons.exchange.model.enums.OrderSideEnum;
import com.troy.commons.exchange.model.in.PrivateTradeReqData;
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 下单入参
 *
 * @author dp
 */
@Setter
@Getter
public class CreateOrderReqDto extends PrivateTradeReqData {

    /**
     * 下单数量
     */
    private BigDecimal amount;

    /**
     * 下单金额
     */
    private BigDecimal price;

    /**
     * 市价下单总金额
     */
    private BigDecimal marketCashAmount;

    /**
     * 下单方向
     */
    @NotNull(message = "下单方向不能为空")
    private OrderSideEnum orderSide;

    /**
     * 下单类型
     */
    @NotNull(message = "下单类型不能为空")
    private TradeExchangeApiConstant.OrderType orderType;

    /**
     * trade系统订单记录ID
     */
    private String transId;

    @Override
    public String toString() {
        return super.toString();
    }
}