package com.troy.trade.exchange.okex.client.bean.spot.result;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderInfo {

    /**
     * 订单id
     */
    private Long order_id;
    /**
     * limit 订单类型的价格信息
     */
    private String price;
    /**
     * market 订单类型的价格信息
     */
    private String notional;
    /**
     * 委托数量
     */
    private String size;
    /**
     * 平均成交价
     */
    private String avg_price;
    /**
     * 委托时间
     */
    private String timestamp;
    private String created_at;
    /**
     * 成交数量
     */
    private String filled_size;
    /**
     * 订单状态 -1 已撤销 0 未成交
     */
    private String status;
    /**
     * 订单买卖类型 buy/sell
     */
    private String side;
    /**
     * 订单类型 limit/xxxxx
     */
    private String type;
    /**
     * 币对信息
     */
    private String instrument_id;



    /**
     * 计价成交量
     */
    private String filled_notional;


}
