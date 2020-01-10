package com.troy.trade.exchange.api.model.dto.out.account;

import com.troy.commons.dto.out.ResData;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充提记录返回实体
 */
@Setter
@Getter
public class ExchAcctDeptWdralResDto extends ResData {

    private String thirdId;//第三方返回ID

    private String coinName;

    private BigDecimal amount;//数量

    private String txId;//提币哈希记录

    private String address;//地址

    private Integer deptWdralType;//充提类型，1-充值、2-提现

    private BigDecimal fee;//手续费

    private Date applyTime;//提申请时间

    private Integer status;//状态，1-申请中、2-已完成、3-已取消、4-失败

    private String addrTag;//地址标签

    public ExchAcctDeptWdralResDto() {
        super();
    }

    public ExchAcctDeptWdralResDto(String thirdId, String coinName,
                                   BigDecimal amount, String txId, String address,
                                   Integer deptWdralType, BigDecimal fee, Date applyTime,
                                   Integer status,String addrTag) {
        this.thirdId = thirdId;
        this.coinName = coinName;
        this.amount = amount;
        this.txId = txId;
        this.address = address;
        this.deptWdralType = deptWdralType;
        this.fee = fee;
        this.applyTime = applyTime;
        this.status = status;
        this.addrTag = addrTag;
    }

    public static ExchAcctDeptWdralResDto getInstance(String thirdId, String coinName,
                                                      BigDecimal amount, String txId, String address,
                                                      Integer deptWdralType, BigDecimal fee, Date applyTime,
                                                      Integer status,String addrTag) {
        ExchAcctDeptWdralResDto exchAcctDeptWdralResDto = new ExchAcctDeptWdralResDto(thirdId, coinName, amount, txId, address,
                deptWdralType, fee, applyTime, status,addrTag);
        return exchAcctDeptWdralResDto;
    }
}
