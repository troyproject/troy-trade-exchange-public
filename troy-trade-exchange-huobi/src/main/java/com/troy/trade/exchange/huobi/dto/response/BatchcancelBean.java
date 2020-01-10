package com.troy.trade.exchange.huobi.dto.response;

public class BatchcancelBean {
    /**
     * err-msg : 记录无效 order-id : 2 err-code : base-record-invalid
     */
    private String errMsg;

    private String orderId;
    private String errCode;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
	}
}