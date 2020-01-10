package com.troy.trade.exchange.api.model.dto.out.order;

import com.troy.commons.dto.out.ResData;
import com.troy.commons.exchange.model.enums.OrderSideEnum;
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单返回数据实体
 *
 * @author dp
 */
@Setter
@Getter
public class OrderDetailResDto extends ResData {

    /**
     * 第三方订单号
     */
    private String orderId;

    /**
     * 交易状态
     */
    private TradeExchangeApiConstant.OrderStatus orderStatus;

    /**
     * 交易方向（1-买 2-卖）
     */
    private OrderSideEnum orderSide;

    /**
     * 交易类型（1-限价交易 2-市价交易）
     */
    private TradeExchangeApiConstant.OrderType orderType;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 下单数量
     */
    private BigDecimal amount;

    /**
     * 下单单价
     */
    private BigDecimal price;

    /**
     * 实际成交价格
     */
    private BigDecimal filledPrice;

    /**
     * 实际成交数量
     */
    private BigDecimal filledAmount;

    /**
     * 已成交总金额
     */
    private BigDecimal filledCashAmount;

    /**
     * 已成交手续费（买入为币，卖出为钱）
     */
    private BigDecimal feeValue;

    /**
     * 剩余数量
     */
    private BigDecimal leftAmount;

    /**
     * 订单变为终结态的时间
     */
    private Date finishedAt;

    private String thirdAccountId;

    /**
     * 订单在交易所的创建时间
     */
    private Date thirdCreateTime;

    private String tradeSymbol;//第三方交易对ID

}
