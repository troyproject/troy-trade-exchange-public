package com.troy.trade.exchange.okex.client.stock.dto;


import com.troy.commons.dto.in.ReqData;

import java.util.List;

/**
 * 查询OKEX订单返回结果
 * @author dp
 */
public class OKOrderDetailReturn extends ReqData {

    /**
     * 返回成功失败标志
     */
    private boolean result;

    /**
     * 订单列表
     */
    private List<OkOrderDetail> orders;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<OkOrderDetail> getOrders() {
        return orders;
    }

    public void setOrders(List<OkOrderDetail> orders) {
        this.orders = orders;
    }
}
