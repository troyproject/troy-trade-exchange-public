package com.troy.trade.exchange.gateio.dto;


import com.troy.commons.dto.out.ResData;
import org.bouncycastle.cert.ocsp.RespData;

/**
 *
 */
public class GateIoOrderReturn  {

    /*
		{
			"result":"true",
			"order":{
				"orderNumber":"977675590",
				"status":"closed",
				"currencyPair":"hsc_usdt",
				"type":"buy",
				"rate":"0.03875",
				"left":0,
				"amount":"0.00000000",
				"initialRate":"0.03875",
				"initialAmount":"26",
				"filledAmount":"26",
				"filledRate":0.03859,
				"feePercentage":0.2,
				"feeValue":"0.052",
				"feeCurrency":"HSC",
				"fee":"0.052 HSC",
				"timestamp":1530698521
			},
			"message":"Success",
			"code":0,
			"elapsed":"6.50597ms"
		}
		 */
    private boolean result;

    private String message;// success

    private int code;// 0

    private String elapsed;// 6.50597ms

    private GateIoOrder order;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getElapsed() {
        return elapsed;
    }

    public void setElapsed(String elapsed) {
        this.elapsed = elapsed;
    }

    public GateIoOrder getOrder() {
        return order;
    }

    public void setOrder(GateIoOrder order) {
        this.order = order;
    }
}
