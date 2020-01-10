package com.troy.trade.exchange.api.model.dto.out.data;

import com.troy.commons.dto.out.ResData;

public class SyncTodamoonResDto extends ResData {

    /**
     * 返回结果
     */
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
