package com.troy.trade.exchange.api.model.dto.out.exchangeInfo;

import com.troy.commons.dto.out.ResData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Getter
public class CoinInfoResDto extends ResData {

        /**
         * 币种名称
         */
        private String coinName;

        /**
         * 币种简称
         */
        private String aliasName;

        /**
         * 最小提币数量
         */
        private BigDecimal withdrawsLeast;

        /**
         * 提币手续费收取方式：1-按量、2-按比例、3-按区间
         */
        private Integer withdrawsFeeType;

        /**
         * 提币手续费
         */
        private BigDecimal withdrawsFee;

        /**
         * 币种操作状态，0-不可提币不可充币、1-可提币可充币、2-只可提币、3-只可充币
         */
        private Integer operationStatus;

        /**
         * 入账确认数
         */
        private Integer bookedConfirmNum;

        /**
         * 最终确认数
         */
        private Integer finalConfirmNum;

        /**
         * 充提币精度
         */
        private Integer operationPrecision;

}
