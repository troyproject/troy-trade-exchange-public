package com.troy.trade.exchange.huobi.client;

public enum HuobiDepositStatus {
    UNKNOWN(4,"失败","unknown","状态未知"),
    CONFIRMING(1,"申请中","confirming","确认中"),
    CONFIRMED(1,"申请中","confirmed","确认中"),
    SAFE(2,"已完成","safe","已完成"),
    ORPHAN(1,"申请中","orphan","待确认");

    private Integer parentsCode;

    private String desc;

    private String status;

    private String parentsDesc;

    HuobiDepositStatus(Integer parentsCode, String parentsDesc, String status, String desc) {
        this.parentsCode = parentsCode;
        this.parentsDesc = parentsDesc;
        this.desc = desc;
        this.status = status;

    }

    public String getParentsDesc() {
        return parentsDesc;
    }

    public void setParentsDesc(String parentsDesc) {
        this.parentsDesc = parentsDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getParentsCode() {
        return parentsCode;
    }

    public void setParentsCode(Integer parentsCode) {
        this.parentsCode = parentsCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Integer getStatusCode(String statusCode){
        for(HuobiDepositStatus status: HuobiDepositStatus.values()){
            if(status.getStatus().equals(statusCode)){
                return status.getParentsCode();
            }
        }
        return null;
    }
}
