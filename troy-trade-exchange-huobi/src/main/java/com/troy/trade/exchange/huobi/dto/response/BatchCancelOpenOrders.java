package com.troy.trade.exchange.huobi.dto.response;


import com.google.gson.annotations.SerializedName;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 17:51
 */

public class BatchCancelOpenOrders {


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

    @SerializedName("success-count")
    private int successCount;

    @SerializedName("failed-count")
    private int failedCount;

    @SerializedName("next-id")
    private Long nextId;

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public Long getNextId() {
        return nextId;
    }

    public void setNextId(Long nextId) {
        this.nextId = nextId;
    }
}
