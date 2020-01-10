package com.troy.trade.exchange.gateio.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.cert.ocsp.RespData;

import java.util.List;

/**
 * GateioOpenOrderReturn
 * 当前挂单
 *
 * @author liuxiaocheng
 * @date 2018/7/19
 */
public class GateioOpenOrderReturn  {
    private String result;
    private String message;
    private String code;
    private String elapsed;
    private List<GateIoOrder> gateioOpenOrders;

    public GateioOpenOrderReturn(@JsonProperty("result") String result, @JsonProperty("message") String message,
                                 @JsonProperty("code") String code, @JsonProperty("elapsed") String elapsed,
                                 @JsonProperty("orders") List<GateIoOrder> gateioOpenOrders) {

        this.result = result;
        this.message = message;
        this.code = code;
        this.elapsed = elapsed;
        this.gateioOpenOrders = gateioOpenOrders;
    }


    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public String getElapsed() {
        return elapsed;
    }

    public List<GateIoOrder> getGateioOpenOrders() {
        return gateioOpenOrders;
    }
}
