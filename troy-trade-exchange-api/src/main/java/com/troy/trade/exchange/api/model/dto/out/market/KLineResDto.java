package com.troy.trade.exchange.api.model.dto.out.market;


import com.troy.commons.dto.out.ResData;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 持仓结果实体
 */
@Setter
@Getter
public class KLineResDto extends ResData {


    private Date timestamp;
    private String symbol;
    private long id;
    private BigDecimal open;
    private BigDecimal close;//日涨幅，乘过100之后的值
    private BigDecimal volume;//24小时成交量--基础币种
    private BigDecimal amount;//价格转人民币后的金额
    private BigDecimal highPrice;//最高价
    private BigDecimal lowPrice;//最低价

   /* 数组中的数据顺序分别是：时间戳，开盘价格，最高价格，最低价格，收盘价格，交易量（张），交易量（币）

    即分别为[timestamp,open,high,low,close,volume,currency_volume]*/
}
