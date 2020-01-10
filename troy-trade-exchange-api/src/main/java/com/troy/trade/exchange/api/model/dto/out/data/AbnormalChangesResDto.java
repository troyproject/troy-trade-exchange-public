package com.troy.trade.exchange.api.model.dto.out.data;

import com.troy.commons.dto.out.ResData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 市场异动数据同步返回实体
 */
@Setter
@Getter
public class AbnormalChangesResDto extends ResData {

    private String symbol;//交易对名称

    private String dateTime;//异动时间

    private String changeType;//变化类型

    private String volumeChanges;//变化量/变化率（未做百分比处理，是原始数据如：0.01）

    private String changeDesc;//变化描述

    private String exchName;//交易所名称

    private String exchCode;//交易所code
}
