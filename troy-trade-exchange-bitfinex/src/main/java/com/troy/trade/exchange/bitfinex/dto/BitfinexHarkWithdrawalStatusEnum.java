package com.troy.trade.exchange.bitfinex.dto;

import org.apache.commons.lang3.StringUtils;

public enum BitfinexHarkWithdrawalStatusEnum {

    SENDING(1,"申请中","Sending","Sending"),
    PROCESSING(1,"申请中","Processing","Processing"),
    PENDING(1,"申请中","Pending","Pending"),
    POSTPENDING(1,"申请中","Postpending","Postpending"),
    COMPLETED(2,"成功","Completed","Completed"),
    USEREMAILED(1,"申请中","User Emailed","User Emailed"),
    APPROVED(1,"申请中","Approved","Approved"),
    USERAPPROVED(1,"申请中","User Approved","User Approved"),
    CANCELED(3,"取消","Canceled","Canceled"),
    PENDINGCANCELED(1,"申请中","Pending Cancellation","Pending Cancellation"),
    UNCONFIRMED(1,"申请中","Unconfirmed","Unconfirmed"),
    ;

    private int parentCode;

    private String parentDesc;

    private String code;

    private String desc;

    BitfinexHarkWithdrawalStatusEnum(int parentCode, String parentDesc, String code, String desc) {
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
        for (BitfinexHarkWithdrawalStatusEnum deposit: BitfinexHarkWithdrawalStatusEnum.values()){
            if(StringUtils.equals(deposit.getCode(),code)){
                return deposit.getParentCode();
            }
        }
        return null;
    }
}
