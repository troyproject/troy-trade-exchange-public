package com.troy.trade.exchange.api.model.dto.out.order;

import com.troy.commons.dto.out.ResData;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Han
 */
@Setter
@Getter
public class OrderListResData extends ResData {

    private static final long serialVersionUID = 299624018144201161L;

    private List<OrderDetailResDto> list; //火币会取最下面的减1
    private String smallId; // 最下面的id
    private String bigId;// 最上面的id
    public OrderListResData() {

    }

    public OrderListResData(List<OrderDetailResDto> list) {
        this.list = list;
    }
    public OrderListResData(List<OrderDetailResDto> list,String smallId,String bigId) {
        this.smallId=smallId;
        this.bigId=bigId;
        this.list = list;
    }

}
