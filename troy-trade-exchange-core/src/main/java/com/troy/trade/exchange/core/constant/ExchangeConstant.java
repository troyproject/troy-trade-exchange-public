package com.troy.trade.exchange.core.constant;

public class ExchangeConstant {

    /**
     * 交易对状态 停用
     */
    public final static Integer SYMBOL_STATUS_OFF = 0;

    /**
     * 交易对状态 启用
     */
    public final static Integer SYMBOL_STATUS_ON = 1;

    /**
     * 交易对状态 不可交易
     */
    public final static Integer SYMBOL_STATUS_UNTRADE = 2;

    /**
     * 币种状态，不可充提币
     */
    public static final Integer COINSTSTUS_DEPOSIT_WITHDRAW_CANNOT = 0;

    /**
     * 币种状态，可提币可充币
     */
    public static final Integer COINSTSTUS_DEPOSIT_WITHDRAW_CAN = 1;

    /**
     * 币种状态，只可提币
     */
    public static final Integer COINSTSTUS_WITHDRAW_CAN = 2;

    /**
     * 币种状态，只可充币
     */
    public static final Integer COINSTSTUS_DEPOSIT_CAN = 3;


    /**
     * 第三方账户充提币记录type -- 充值
     */
    public static final Integer DEPOSIT_WITHDRAWAL_TYPE_DEPOSIT = 1;

    /**
     * 第三方账户充提币记录type -- 提现
     */
    public static final Integer DEPOSIT_WITHDRAWAL_TYPE_WITHDRAWAL = 2;
}
