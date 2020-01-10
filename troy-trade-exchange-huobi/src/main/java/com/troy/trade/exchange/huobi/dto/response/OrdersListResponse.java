package com.troy.trade.exchange.huobi.dto.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 18:21
 */

public class OrdersListResponse<T> {

    /**
     * {
     *     "status": "ok",
     *     "data": [
     *         {
     *             "id": 31215214553,
     *             "symbol": "btcusdt",
     *             "account-id": 4717043,
     *             "amount": "1.000000000000000000",
     *             "price": "1.000000000000000000",
     *             "created-at": 1556533539282,
     *             "type": "buy-limit",
     *             "field-amount": "0.0",
     *             "field-cash-amount": "0.0",
     *             "field-fees": "0.0",
     *             "finished-at": 1556533568953,
     *             "source": "web",
     *             "state": "canceled",
     *             "canceled-at": 1556533568911
     *         }
     *     ]
     * }
     */

    private String status;
    public String errCode;
    public String errMsg;
    private T data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static class DataBean {

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
}
