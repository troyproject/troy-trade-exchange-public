package com.troy.trade.exchange.huobi.client;

/**
 * 火币常量
 */
public class HuobiConstant {


    public static final String HOST = "api.huobi.pro";

    /**
     * 请求方式 -- GET
     */
    public final static String HTTP_CONTENT_TYPE = "Content-type";

    /**
     * 请求方式 -- GET
     */
    public final static String HTTP_CONTENT_TYPE_JSON = "application/json";

    /**
     * 请求方式 -- GET
     */
    public final static String METHOD_GET = "GET";

    /**
     * 请求方式 -- POST
     */
    public final static String METHOD_POST = "POST";

    /**
     * 订单方向-买入
     */
    public static final String ORDER_TYPE_BUY = "buy";

    /**
     * 订单方向-买入
     */
    public static final String ORDER_TYPE_SELL = "sell";

    /**
     * 接口返回状态
     */
    public static final String RESPONSE_STATUS_OK = "ok";

    /**
     * 交易对状态 - 已上线
     */
    public static final String SYMBOL_STATE_ONLINE = "online";

    /**
     * 交易对状态 - 不可交易
     */
    public static final String SYMBOL_STATE_SUSPEND = "suspend";


    /**
     * 提交中
     */
    public static final String ORDER_STATUS_SUBMITTING= "submitting";

    /**
     * 已提交
     */
    public static final String ORDER_STATUS_SUBMITTED = "submitted";

    /**
     * 部分成功
     */
    public static final String ORDER_STATUS_PARTIAL_FILLED = "partial-filled";

    /**
     * 部分撤销
     */
    public static final String ORDER_STATUS_PARTIAL_CANCELED = "partial-canceled";

    /**
     * 完全成交
     */
    public static final String ORDER_STATUS_FILLED = "filled";


    /**
     * 已撤销
     */
    public static final String ORDER_STATUS_CANCELED = "canceled";

    /**
     * 币种余额类型--trade
     */
    public static final String CURRENCY_TYPE_TRADE = "trade";

    /**
     * 币种余额类型--frozen
     */
    public static final String CURRENCY_TYPE_FROZEN = "frozen";

    /**
     * 交易对类型，pro
     */
    public static final String EXCH_ACCT_SYMBOL_TYPE_PRO = "pro";

    /**
     * 交易对类型，hadax
     */
    public static final String EXCH_ACCT_SYMBOL_TYPE_HADAX = "hadax";

    /**
     * 火币批量撤单最大条数
     */
    public static final Integer MAX_CANCEL_BATCH_SIZE = 50;

    /**
     * 充值提现类型 -- 充值
     */
    public static final String DEPT_WIAL_TYPE_DEPOSIT = "deposit";

    /**
     * 充值提现类型 -- 提现
     */
    public static final String DEPT_WIAL_TYPE_WIAL = "withdraw";

    /**
     * 充提币状态 - 允许
     */
    public static final String DEPT_WIAL_STATUS_ALLOWED = "allowed";

    /**
     * 充提币状态 - 禁止
     */
    public static final String DEPT_WIAL_STATUS_PROHIBITED = "prohibited";


    /**
     * 提币手续费类型 - 按量
     */
    public static final String WITHDRAWFEE_TYPE_FIXED = "fixed";

    /**
     * 提币手续费类型 - 按区间
     */
    public static final String WITHDRAWFEE_TYPE_CIRCULATED = "circulated";

    /**
     * 提币手续费类型 - 按比例
     */
    public static final String WITHDRAWFEE_TYPE_RATIO = "ratio";

}
