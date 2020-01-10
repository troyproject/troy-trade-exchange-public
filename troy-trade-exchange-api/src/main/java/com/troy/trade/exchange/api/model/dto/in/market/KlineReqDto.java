package com.troy.trade.exchange.api.model.dto.in.market;


import com.troy.commons.exchange.model.in.PublicMarketReqData;
import lombok.Getter;
import lombok.Setter;

/**
 * kline
 */
@Getter
@Setter
public class KlineReqDto extends PublicMarketReqData {

    private String period;

    private Integer size;


    private String interval;//时间粒度


    private Integer acctType;//账户类型

    /**
     * exch_acct中的账户ID
     */
    private String exchAcctId;


    private String startDate;//	开始时间，

    private String endDate;//结束时间，

}
