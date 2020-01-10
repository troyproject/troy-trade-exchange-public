package com.troy.trade.exchange.okex.client.bean.spot.result;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Fills {

    // 账单 id
    private Long ledger_id;
    // 币种 id
    private String instrument_id;
    private String product_id;
    // 价格
    private String price;
    // 数量
    private String size;
    // 订单 id
    private Long order_id;
    // 创建时间
    private String timestamp;
    private String created_at;
    // 流动方向
    private String liquidity;
    private String exec_type;
    // 手续费
    private BigDecimal fee;
    // buy、sell
    private String side;



}
