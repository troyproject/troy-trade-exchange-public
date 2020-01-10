package com.troy.trade.exchange.huobi.dto.response;

import java.util.List;

public class Balance {
    /**
     * id : 100009
     * type : spot
     * state : working
     * list : [{"currency":"usdt","type":"trade","balance":"500009195917.4362872650"}]
     * user-id : 1000
     */

    private String id;
    private String type;
    private String state;
    private String userid;
    private List<BalanceBean> list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<BalanceBean> getList() {
        return list;
    }

    public void setList(List<BalanceBean> list) {
        this.list = list;
    }
}
