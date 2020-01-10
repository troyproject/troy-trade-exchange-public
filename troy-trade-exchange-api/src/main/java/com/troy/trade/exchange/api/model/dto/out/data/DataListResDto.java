package com.troy.trade.exchange.api.model.dto.out.data;

import com.troy.commons.dto.out.ResData;
import lombok.Getter;
import lombok.Setter;

/**
 * data数据返回实体
 */
@Getter
@Setter
public class DataListResDto extends ResData {

    private Object x;//x列数据

    private Object y;//x列数据
}
