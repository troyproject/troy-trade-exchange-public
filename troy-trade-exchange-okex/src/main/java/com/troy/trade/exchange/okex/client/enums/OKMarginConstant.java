package com.troy.trade.exchange.okex.client.enums;

/**
 * OK常量
 *
 * @author dp
 */
public class OKMarginConstant {
    /**
     * 市价买入
     */
    public static final String TRADE_TYPE_BUY_MARKET = "buy_market";

    /**
     * 市价卖出
     */
    public static final String TRADE_TYPE_SELL_MARKET = "sell_market";

    /**
     * 限价买入
     */
    public static final String TRADE_TYPE_BUY = "buy";

    /**
     * 限价卖出
     */
    public static final String TRADE_TYPE_SELL = "sell";

    /**
     * 订单状态已撤销
     */
    public static final int STATUS_CANCELD = -1;

    /**
     * 未成交
     */
    public static final int STATUS_PENDING = 0;

    /**
     * 部分成交
     */
    public static final int STATUS_PART_FILLED = 1;

    /**
     * 完全成交
     */
    public static final int STATUS_FILLED = 2;

    /**
     * 撤单处理中
     */
    public static final int STATUS_CANCEL_APPLY = 3;


    /**
     * 订单类型 - 限价交易
     */
    public static final String ORDER_TYPE_LIMIT = "limit";

    /**
     * 订单类型 - 市价交易
     */
    public static final String ORDER_TYPE_MARKET = "market";

    /**
     * ok版本号 - v3
     */
    public static final String VERSION_V3 = "3";

    /**
     * 订单类型枚举
     *
     * @author dp
     */
    public enum OkStateEnum {
//        订单状态("-2":失败,"-1":撤单成功,"0":等待成交 ,"1":部分成交, "2":完全成交,"3":下单中,"4":撤单中 ）

        FAILURE_STATUS("-2", "失败"),
        CANCELLED_STATUS("-1", "撤单成功"),
        OPEN_STATUS("0", "等待成交"),
        PART_FILLED_STATUS("1", "部分成交"),
        FILLED_STATUS("2", "完全成交"),
        ORDERING_STATUS("3", "下单中"),
        CANCELLING_STATUS("4", "撤单中");


        private String type;//类型
        private String description;//描述

        OkStateEnum(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }


}
