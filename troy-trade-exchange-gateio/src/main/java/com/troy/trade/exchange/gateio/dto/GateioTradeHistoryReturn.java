package com.troy.trade.exchange.gateio.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * GateioOpenOrderReturn
 * 当前挂单
 *
 * @author liuxiaocheng
 * @date 2018/7/19
 */
public class GateioTradeHistoryReturn {
    private String result;
    private String message;

    private List<GateIoOrderHistory> trades;

    public GateioTradeHistoryReturn(@JsonProperty("result") String result, @JsonProperty("message") String message,
                                    @JsonProperty("orders") List<GateIoOrderHistory> trades) {

        this.result = result;
        this.message = message;

        this.trades = trades;
    }


    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }



    public List<GateIoOrderHistory> getGateioOpenOrders() {
        return trades;
    }
}
