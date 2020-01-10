package com.troy.trade.exchange.huobi.dto.request;

/**
 *
 */
public class IntrustOrdersDetailRequest {

    public interface OrderType {
        /**
         * 限价买入
         */
        String BUY_LIMIT = "buy-limit";
        /**
         * 限价卖出
         */
        String SELL_LIMIT = "sell-limit";
        /**
         * 市价买入
         */
        String BUY_MARKET = "buy-market";
        /**
         * 市价卖出
         */
        String SELL_MARKET = "sell-market";
    }

    public interface OrderStates {
        /**
         * pre-submitted 准备提交
         */
        String PRE_SUBMITTED = "pre-submitted";
        /**
         * submitted 已提交
         */
        String SUBMITTED = "submitted";
        /**
         * partial-filled 部分成交
         */
        String PARTIAL_FILLED = "partial-filled";
        /**
         * partial-canceled 部分成交撤销
         */
        String PARTIAL_CANCELED = "partial-canceled";

        /**
         * filled 完全成交
         */
        String FILLED = "filled";
        /**
         * canceled 已撤销
         */
        String CANCELED = "canceled";
    }

    public String symbol;       //true	string	交易对		btcusdt, bccbtc, rcneth ...
    public String types;       //false	string	查询的订单类型组合，使用','分割		buy-market：市价买, sell-market：市价卖, buy-limit：限价买, sell-limit：限价卖
    public String startDate;   //false	string	查询开始日期, 日期格式yyyy-mm-dd
    public String endDate;       //false	string	查询结束日期, 日期格式yyyy-mm-dd
    public String states;       //true	string	查询的订单状态组合，使用','分割		pre-submitted 准备提交, submitted 已提交, partial-filled 部分成交,
    // partial-canceled 部分成交撤销, filled 完全成交, canceled 已撤销
    public String from;           //false	string	查询起始 ID
    public String direct;       //false	string	查询方向		prev 向前，next 向后
    public String size;           //false	string	查询记录大小
}
