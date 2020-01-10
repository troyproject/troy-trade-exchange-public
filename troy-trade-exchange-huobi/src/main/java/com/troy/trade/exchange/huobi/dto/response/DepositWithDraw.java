package com.troy.trade.exchange.huobi.dto.response;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class DepositWithDraw {

    /**
     *{"status":"ok","data":[{"id":5511038,"type":"deposit","currency":"eth","chain":"eth",
     "tx-hash":"02096b28f89fad7d51162a87c511c1571ea5a23a71b43e4acd812d094af5f73c","amount":0.500000000000000000,
     "address":"2d533337487fe775cc002d54faae14c30ca95aaf","address-tag":"","fee":0,"state":"safe",
     "created-at":1530951611148,"updated-at":1530951716703}]}
     */

    @SerializedName("id")
    private long id;

    @SerializedName("type")
    private String type;

    @SerializedName("currency")
    private String currency;

    @SerializedName("chain")
    private String chain;

    @SerializedName("tx-hash")
    private String txHash;

    @SerializedName("amount")
    private String amount;

    @SerializedName("address")
    private String address;

    @SerializedName("address-tag")
    private String addressTag;

    @SerializedName("fee")
    private BigDecimal fee;

    @SerializedName("state")
    private String state;

    @SerializedName("created-at")
    private Long createdAt;

    @SerializedName("updated-at")
    private Long updatedAt;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressTag() {
        return addressTag;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
