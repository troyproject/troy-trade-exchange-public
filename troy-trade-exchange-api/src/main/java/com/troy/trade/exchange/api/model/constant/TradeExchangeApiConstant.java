package com.troy.trade.exchange.api.model.constant;

import com.troy.commons.enums.BaseEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 交易API Constant
 *
 * @author dp
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeExchangeApiConstant {

    /**
     * 订单状态是否为最终
     *
     * @param status
     * @return
     */
    public static boolean isFinalStatus(Integer status){
        return TradeExchangeApiConstant.OrderStatus.DEAL.code().equals(status)
                || TradeExchangeApiConstant.OrderStatus.CANCEL.code().equals(status);
    }

    /**
     * The enum Order status enum.
     */
    public enum OrderStatus implements BaseEnum<Integer> {

        /**
         * 初始订单状态--初始入库为此状态
         */
        INIT(0, "初始"),

        /**
         * 部分成交状态--订单未撤单，但是已成交部分的情况
         */
        PART(1, "部分成交"),

        /**
         * 已撤销状态--最终状态
         */
        CANCEL(2, "已撤销"),

        /**
         * 已成交--最终状态
         */
        DEAL(3, "已成交"),

        /**
         * 已提交状态--已提交至交易所，交易状态待同步
         */
        COMMIT(10, "已提交"),

        /**
         * 失败状态--订单提交至交易所，但交易所返回明确失败
         */
        FAIL(11, "失败"),

        /**
         * 状态未知--订单提交至交易所，但交易所超时未返回
         */
        UNKNOW(12, "状态未知"),
        /**
         * 部分成交撤销
         */
        PARTIAL_CANCELED(14, "部分成交撤销"),

        /**
         * 撤单按钮被按下的状态，该状态最后会进行与交易所状态同步
         */
        CANCEL_APPLY(13, "申请撤单");

        private String value;
        private int code;

        OrderStatus(int code, String value) {
            this.code = code;
            this.value = value;
        }

        /**
         * Code of order status enum.
         *
         * @param code the code
         * @return the order status enum
         */
        public static OrderStatus codeOf(int code) {
            OrderStatus result = null;
            for (OrderStatus paymentTypeEnum : values()) {
                if (paymentTypeEnum.code() == code) {
                    result = paymentTypeEnum;
                    break;
                }
            }
            return result;
        }

        /**
         * Gets value.
         *
         * @return the value
         */
        @Override
        public String desc() {
            return value;
        }

        /**
         * Gets code.
         *
         * @return the code
         */
        @Override
        public Integer code() {
            return code;
        }
    }

    /**
     * 成交角色
     *
     * @author sz
     */
    public enum OrderRole implements BaseEnum<Integer> {

        /**
         * BID (maker)
         */
        MAKER(1, "maker"),

        /**
         * ASK (taker)
         */
        TAKER(2, "taker"),
        ;

        private int code;
        private String value;

        OrderRole(int code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer code() {
            return code;
        }

        @Override
        public String desc() {
            return value;
        }
    }

    /**
     * 订单类型（限价 市价）
     *
     * @author dp
     */
    public enum OrderType implements BaseEnum<Integer> {

        /**
         * 限价交易
         */
        LIMIT(1, "limit"),

        /**
         * 市价交易
         */
        MARKET(2, "market"),
        ;

        private int code;
        private String value;

        OrderType(int code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer code() {
            return code;
        }

        @Override
        public String desc() {
            return value;
        }
    }

    /**
     * 交易类型
     */
    public interface Order {

    }

    /**
     * 市场行情类型
     */
    public interface Market {

        /**
         * 买卖挂单最大返回给前端条数
         */
        public final static int MAX_DEPTH_SIZE = 30;

    }

    /**
     * 1 未同步  2  已同步
     * 第三方平台手续费同步状态
     */
    public enum SynFeeStatus implements BaseEnum<Integer> {


        NOTSYN(1, "未同步"),


        SYN(2, "已同步"),
        ;

        private int code;
        private String value;

        SynFeeStatus(int code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer code() {
            return code;
        }

        @Override
        public String desc() {
            return value;
        }
    }
    /**
     * 1 未同步  2  已同步
     * troy系统手续费同步状态
     */
    public enum SynPlatFormFeeStatus implements BaseEnum<Integer> {


        NOTSYN(1, "未同步"),


        SYN(2, "已同步"),
        ;

        private int code;
        private String value;

        SynPlatFormFeeStatus(int code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer code() {
            return code;
        }

        @Override
        public String desc() {
            return value;
        }
    }
}
