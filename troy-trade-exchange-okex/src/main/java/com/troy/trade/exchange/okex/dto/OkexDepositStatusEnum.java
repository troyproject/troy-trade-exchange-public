package com.troy.trade.exchange.okex.dto;

public enum OkexDepositStatusEnum {

//    充值状态（0:等待确认;1:确认到账;2:充值成功；）
    WAITINGCONFIRMATION(1,"申请中",0,"等待确认"),
    CONFIRM(1,"申请中",1,"确认到账"),
    COMPLETED(2,"成功",2,"充值成功");

    private int parentCode;

    private String parentDesc;

    private int code;

    private String desc;

    OkexDepositStatusEnum(int parentCode, String parentDesc, int code, String desc) {
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
        for (OkexDepositStatusEnum deposit:OkexDepositStatusEnum.values()){
            if(deposit.getCode() == code){
                return deposit.getParentCode();
            }
        }
        return null;
    }
}
