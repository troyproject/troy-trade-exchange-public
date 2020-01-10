package com.troy.trade.exchange.gateio.dto;

import org.apache.commons.lang3.StringUtils;

public enum GateioHarkWithdrawalStatusEnum {

    REQUEST(1,"申请中","REQUEST","请求中"),
    DONE(2,"成功","DONE","完成"),
    CANCEL(3,"取消","CANCEL","取消"),
    ;

    private int parentCode;

    private String parentDesc;

    private String code;

    private String desc;

    GateioHarkWithdrawalStatusEnum(int parentCode, String parentDesc, String code, String desc) {
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
        for (GateioHarkWithdrawalStatusEnum deposit: GateioHarkWithdrawalStatusEnum.values()){
            if(StringUtils.equals(deposit.getCode(),code)){
                return deposit.getParentCode();
            }
        }
        return null;
    }
}
