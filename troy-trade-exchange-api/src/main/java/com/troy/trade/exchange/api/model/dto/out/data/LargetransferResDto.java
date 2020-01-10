package com.troy.trade.exchange.api.model.dto.out.data;

import com.troy.commons.dto.out.ResData;

/**
 * 大额转移查询结果实体
 */
public class LargetransferResDto extends ResData {

    /**
     * 币种名称
     */
    private String coin;

    /**
     * 币种名称
     */
    private String amount;

    /**
     * 分页码
     */
    private String blockTime;

    /**
     * 转账发起方
     */
    private String fromSource;

    /**
     * 转账发起方地址
     */
    private String fromAddress;

    /**
     * 转账接收方
     */
    private String toSource;

    /**
     * 转账接收方地址
     */
    private String toAddress;


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(String blockTime) {
        this.blockTime = blockTime;
    }

    public String getFromSource() {
        return fromSource;
    }

    public void setFromSource(String fromSource) {
        this.fromSource = fromSource;
    }

    public String getToSource() {
        return toSource;
    }

    public void setToSource(String toSource) {
        this.toSource = toSource;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
}
