package com.troy.trade.exchange.binance.dto;

public enum WithDrawStatusEnum {

    EMAILSENT(1,"申请中",0,"Email Sent"),
    CANCELLED(3,"取消",1,"Cancelled"),
    AWAITINGAPPROVAL(1,"申请中",2,"Awaiting Approval"),
    REJECTED(4,"失败",3,"Rejected"),
    PROCESSING (1,"申请中",4,"Processing"),
    FAILURE(4,"失败",5,"Failure"),
    COMPLETED(2,"成功",6,"Completed");

    private int parentCode;

    private String parentDesc;

    private int code;

    private String desc;

    WithDrawStatusEnum(int parentCode,String parentDesc,int code, String desc) {
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
        for (WithDrawStatusEnum deposit: WithDrawStatusEnum.values()){
            if(deposit.getCode() == code){
                return deposit.getParentCode();
            }
        }
        return null;
    }
}
