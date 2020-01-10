package com.troy.trade.exchange.huobi.dto;

import org.apache.commons.lang3.StringUtils;

public enum HuobiHarkWithdrawalStatusEnum {

    CONFIRMING(1,"申请中","confirming","confirming"),
    CONFIRMED(2,"申请中","confirmed","confirmed"),
    COMPLETED(2,"成功","safe","safe"),
    ORPHAN(1,"申请中","orphan","orphan"),
    UNKNOWN(4,"失败","unknown","unknown"),
    WALLETTRANSFER(1,"钱包转移","wallet-transfer","wallet-transfer"),
    ;

    private int parentCode;

    private String parentDesc;

    private String code;

    private String desc;

    HuobiHarkWithdrawalStatusEnum(int parentCode, String parentDesc, String code, String desc) {
        this.code = code;
        this.desc = desc;
        this.parentCode = parentCode;
        this.parentDesc = parentDesc;
    }

    public int getParentCode() {
        return parentCode;
    }

    public void setParentCode(int parentCode) {
        this.parentCode = parentCode;
    }

    public String getParentDesc() {
        return parentDesc;
    }

    public void setParentDesc(String parentDesc) {
        this.parentDesc = parentDesc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Integer getStatus(String code){
        for (HuobiHarkWithdrawalStatusEnum deposit: HuobiHarkWithdrawalStatusEnum.values()){
            if(StringUtils.equals(deposit.getCode(),code)){
                return deposit.getParentCode();
            }
        }
        return null;
    }
}
