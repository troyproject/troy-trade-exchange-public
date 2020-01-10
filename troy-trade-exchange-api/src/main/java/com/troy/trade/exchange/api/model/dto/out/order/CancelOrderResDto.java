package com.troy.trade.exchange.api.model.dto.out.order;

import com.troy.commons.dto.out.ResData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 撤销订单返回
 *
 * @author dp
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelOrderResDto extends ResData {

    /**
     * 申请撤单成功的订单号
     */
    private List<String> successOrderIds;

    /**
     * 申请撤单失败的订单号
     */
    private List<String> failOrderIds;

    /**
     * 撤单是否完全成功
     *
     * @return
     */
    public boolean isAllSuccess(){
        return failOrderIds == null || failOrderIds.size() == 0;
    }
}
