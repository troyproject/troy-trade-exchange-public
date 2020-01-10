package com.troy.trade.exchange.api.model.dto.in.order;

import com.troy.commons.exchange.model.enums.OrderSideEnum;
import com.troy.commons.exchange.model.in.PrivateTradeReqData;
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 查询订单详情DTO
 *
 * @author dp
 */
@Setter
@Getter
public class OrderDetailReqDto extends PrivateTradeReqData {

    /**
     * 三方订单号
     */
    private String orderId;

    /**
     * 数量精度
     */
    private Integer numDecimal;

    /**
     * 价格精度
     */
    private Integer amountDecimal;

    /**
     * 查询数量
     */
    private Integer limit;

    /**
     * 批量订单
     */
    private List<OrderIdsDto> orderIdsDtoList  ;

    /**
     * 交易类型
     */
    private TradeExchangeApiConstant.OrderType orderType;

    /**
     * 交易方向
     */
    private OrderSideEnum orderSide;
    /**
     * 交易状态
     */
    private TradeExchangeApiConstant.OrderStatus orderStatus;

    private String startCondition;//开始条件，如：时间毫秒值、起始订单ID  	请求此id之后（更新的数据）的分页内容，传的值为对应接口的order_id

    private String endCondition;//结束条件，如：结束毫秒值、结束订单ID


}
