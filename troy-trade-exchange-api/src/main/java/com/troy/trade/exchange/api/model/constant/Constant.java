package com.troy.trade.exchange.api.model.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Trade常量
 */
public class Constant {


    public static final List<String> MONEY_COIN_LIST = new ArrayList<>();

    static {
        MONEY_COIN_LIST.add("ETH");
        MONEY_COIN_LIST.add("BTC");
        MONEY_COIN_LIST.add("USDT");
        MONEY_COIN_LIST.add("USD");
    }

    /**
     * 订单类型枚举
     *
     * @author dp
     */
    public enum TransFlowStatusEnum {

        INIT_STATUS(0, "初始"),
        PART_STATUS(1, "部分成交"),
        /**
         * 已撤销、已成交、部分撤销 三种是最终状态
         */
        CANCEL_STATUS(2, "已撤销"),
        DEAL_STATUS(3, "已成交"),
        PART_CANCEL_STATUS(4, "部分撤销"),

        COMMIT_STATUS(10, "已提交"),
        FAIL_STATUS(11, "失败"),
        UNKNOW_STATUS(12, "状态未知"),
        CANCEL_APPLY_STATUS(13, "申请撤单");

        private int type;//类型
        private String description;//描述

        TransFlowStatusEnum(int type, String description) {
            this.type = type;
            this.description = description;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
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
