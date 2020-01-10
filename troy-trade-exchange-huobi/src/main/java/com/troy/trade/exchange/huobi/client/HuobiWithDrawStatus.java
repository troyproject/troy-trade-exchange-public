package com.troy.trade.exchange.huobi.client;

public enum HuobiWithDrawStatus {
    submitted(1,"申请中","submitted","已提交"),
    reexamine(1,"申请中","reexamine","审核中"),
    canceled(3,"已取消","canceled","已撤销"),
    pass(1,"申请中","pass","审批通过"),
    reject(4,"失败","reject","审批拒绝"),
    preTransfer(1,"申请中","pre-transfer","处理中"),
    walletTransfer(2,"已完成","wallet-transfer","已汇出"),
    walletReject(4,"失败","wallet-reject","钱包拒绝"),
    confirmed(2,"申请中","confirmed","区块已确认"),
    confirmError(4,"失败","confirm-error","区块确认错误"),
    repealed(3,"已取消","repealed","已撤销");

    private Integer parentsCode;

    private String desc;

    private String status;

    private String parentsDesc;

    HuobiWithDrawStatus(Integer parentsCode,String parentsDesc,String status,String desc) {
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
        for(HuobiWithDrawStatus status: HuobiWithDrawStatus.values()){
            if(status.getStatus().equals(statusCode)){
                return status.getParentsCode();
            }
        }
        return null;
    }
}
