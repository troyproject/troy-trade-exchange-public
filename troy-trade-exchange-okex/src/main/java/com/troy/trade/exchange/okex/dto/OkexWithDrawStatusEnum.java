package com.troy.trade.exchange.okex.dto;

public enum OkexWithDrawStatusEnum {

//    提现状态（-3:撤销中;-2:已撤销;-1:失败;0:等待提现;1:提现中;2:已汇出;3:邮箱确认;4:人工审核中5:等待身份认证）
    CANCELING(1,"申请中",-3,"撤销中"),
    CANCELLED(3,"取消",-2,"已撤销"),
    FAILURE(4,"失败",-1,"失败"),
    WAITING(1,"申请中",0,"等待提现"),
    WITHDRAWALING(1,"申请中",1,"提现中"),
    COMPLETED(2,"成功",2,"已汇出"),
    EMAILCONFIRM(1,"申请中",3,"邮箱确认"),
    INREVIEW(1,"申请中",4,"人工审核中"),
    WAITINGIDENTIFI(1,"申请中",5,"等待身份认证"),
    ;

    private int parentCode;

    private String parentDesc;

    private int code;

    private String desc;

    OkexWithDrawStatusEnum(int parentCode, String parentDesc, int code, String desc) {
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
        for (OkexWithDrawStatusEnum deposit: OkexWithDrawStatusEnum.values()){
            if(deposit.getCode() == code){
                return deposit.getParentCode();
            }
        }
        return null;
    }
}
