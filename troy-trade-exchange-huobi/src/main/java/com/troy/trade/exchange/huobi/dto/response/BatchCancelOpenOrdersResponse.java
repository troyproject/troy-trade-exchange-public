package com.troy.trade.exchange.huobi.dto.response;


/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 17:51
 */

public class BatchCancelOpenOrdersResponse<T> {


    /**
     * GET /v1/order/batchCancelOpenOrders
     * {
     * "status": "ok",
     * "data": {
     * "success-count": 2,
     * "failed-count": 0,
     * "next-id": 5454600
     * }
     * }
     */

    private String status;

    //{"status":"error",
    // "err-code":"api-signature-not-valid",
    // "err-msg":"Signature not valid: Verification failure [校验失败]",
    // "data":null}

    private String errCode;

    private String errMsg;

    private T data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BatchCancelOpenOrdersResponse{" +
                "status='" + status + '\'' +
                ", errCode='" + errCode + '\'' +
                ", errMsg='" + errMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
