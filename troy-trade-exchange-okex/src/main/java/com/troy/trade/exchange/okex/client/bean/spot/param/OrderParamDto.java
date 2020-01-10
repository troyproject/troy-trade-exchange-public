package com.troy.trade.exchange.okex.client.bean.spot.param;

import lombok.*;

import java.util.List;

/**
 * 撤单入参实体
 *
 * @author
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderParamDto {

    private String instrument_id;
    private List<String> order_ids;
}
