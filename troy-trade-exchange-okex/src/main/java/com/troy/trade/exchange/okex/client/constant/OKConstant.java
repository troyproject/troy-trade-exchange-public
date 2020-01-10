package com.troy.trade.exchange.okex.client.constant;

/**
 * OK常量
 * @author dp
 */
public class OKConstant {

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
    public static final String STATUS_CANCELD = "cancelled";

    /**
     * 未成交
     */
    public static final String STATUS_PENDING = "open";

    /**
     * 部分成交
     */
    public static final String STATUS_PART_FILLED = "part_filled";

    /**
     * 完全成交
     */
    public static final String STATUS_FILLED = "filled";

    /**
     * 撤单处理中
     */
    public static final String STATUS_CANCEL_APPLY = "canceling";

    /**
     * 币种状态，是否可充提币--否
     */
    public static final Integer STSTUS_DEPOSIT_WITHDRAW_CANNOT = 0;

    /**
     * 币种状态，是否可充提币--是
     */
    public static final Integer STSTUS_DEPOSIT_WITHDRAW_CAN = 1;

    /**
     * 批量撤单最大条数
     */
    public static final Integer BATCH_CANCEL_COUNT = 10;

    public static final Integer MAX_DEPTH_SIZE = 1;

}
