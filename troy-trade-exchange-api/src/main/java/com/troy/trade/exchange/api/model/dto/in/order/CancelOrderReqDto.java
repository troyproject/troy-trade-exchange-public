package com.troy.trade.exchange.api.model.dto.in.order;

import com.troy.commons.exchange.model.in.PrivateTradeReqData;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * 撤单请求DTO
 *
 * @author dp
 */
@Setter
@Getter
public class CancelOrderReqDto extends PrivateTradeReqData {

    /**
     * 批量撤销orderId列表
     */
    @NotEmpty
    private List<String> orderIds;

    private List<String> spotTransIds;

}
