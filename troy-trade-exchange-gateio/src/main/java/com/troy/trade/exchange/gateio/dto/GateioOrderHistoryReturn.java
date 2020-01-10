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
public class GateioOrderHistoryReturn {
    private String result;
    private String message;

    private List<GateIoOrderReturn> gateioOpenOrders;

    public GateioOrderHistoryReturn(@JsonProperty("result") String result, @JsonProperty("message") String message,
                                    @JsonProperty("orders") List<GateIoOrderReturn> gateioOpenOrders) {

        this.result = result;
        this.message = message;

        this.gateioOpenOrders = gateioOpenOrders;
    }


    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }



    public List<GateIoOrderReturn> getGateioOpenOrders() {
        return gateioOpenOrders;
    }
}
