package com.troy.trade.exchange.huobi.dto.response;

public class BalanceResponse {


    /**
     * status : ok
     * data : {"id":"100009","type":"spot","state":"working","list":[{"currency":"usdt","type":"trade","balance":"500009195917.4362872650"}],"user-id":"1000"}
     */

    private String status;
    public String errCode;
    public String errMsg;

    private Balance data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Balance getData() {
        return data;
    }

    public void setData(Balance data) {
        this.data = data;
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
