package com.troy.trade.exchange.api.model.dto.out.data;

import com.troy.commons.dto.out.ResData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 大额转移查询结果实体
 */
@Setter
@Getter
public class LargetransferListResDto extends ResData {

    /**
     * 币种名称
     */
    private List<LargetransferResDto> largetransferResDtoList;
}
