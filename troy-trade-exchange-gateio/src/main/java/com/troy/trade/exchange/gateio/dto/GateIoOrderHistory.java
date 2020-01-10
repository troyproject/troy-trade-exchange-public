package com.troy.trade.exchange.gateio.dto;



import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *
 */
@Setter
@Getter
public class GateIoOrderHistory {

    private String orderNumber;// 订单号
    private String status;// 订单状态 open已挂单 cancelled已取消 done已完成
    private String pair;// 交易对

    private String type;// 买卖类型 sell卖出, buy买入
    private BigDecimal rate;// 价格

    private BigDecimal amount;// 买卖数量
    private String date;// 下单价格
    private String time_unix;// 下单量
    private BigDecimal total;// 价格



}
