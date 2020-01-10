package com.troy.trade.exchange.binance.dto;

public enum DepositStatusEnum {

    PENDING(1,"申请中",0,"PENDING"),
    SUCCESS(2,"成功",1,"SUCCESS");

    private int parentCode;

    private String parentDesc;

    private int code;

    private String desc;

    DepositStatusEnum(int parentCode,String parentDesc,int code, String desc) {
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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Integer getStatus(int code){
        for (DepositStatusEnum deposit:DepositStatusEnum.values()){
            if(deposit.getCode() == code){
                return deposit.getParentCode();
            }
        }
        return null;
    }
}
