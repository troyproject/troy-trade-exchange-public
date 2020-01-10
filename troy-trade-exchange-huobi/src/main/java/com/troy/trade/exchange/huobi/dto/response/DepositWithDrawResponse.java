package com.troy.trade.exchange.huobi.dto.response;

import java.util.List;

public class DepositWithDrawResponse<T> {

    /**
     * status : ok
     * data : [{"id":5511038,"type":"deposit","currency":"eth","chain":"eth","tx-hash":"02096b28f89fad7d51162a87c511c1571ea5a23a71b43e4acd812d094af5f73c","amount":0.500000000000000000,"address":"2d533337487fe775cc002d54faae14c30ca95aaf","address-tag":"","fee":0,"state":"safe","created-at":1530951611148,"updated-at":1530951716703}]
     */

    private String status;
    private String errCode;
    private String errMsg;
    private List<T> data;

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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
