package com.troy.trade.exchange.okex.client.stock.dto;

import com.troy.commons.dto.in.ReqData;

import java.math.BigDecimal;

/**
 * OKEX订单详情
 * @author dp
 */
public class OkOrderDetail extends ReqData {

    /**
     * 委托数量
     */
    private BigDecimal amount;

    /**
     * 委托时间
     */
    private Long create_date;

    /**
     * 平均成交价
     */
    private BigDecimal avg_price;

    /**
     * 成交数量
     */
    private BigDecimal deal_amount;

    /**
     * 委托价格
     */
    private BigDecimal price;

    /**
     * 订单ID
     */
    private int order_id;

    /**
     * 状态 -1:已撤销  0:未成交  1:部分成交  2:完全成交 3:撤单处理中
     */
    private int status;

    /**
     * 购买类型 buy_market:市价买入 / sell_market:市价卖出 / buy:限价买入  / sell:限价卖出
     */
    private String type;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Long create_date) {
        this.create_date = create_date;
    }

    public BigDecimal getAvg_price() {
        return avg_price;
    }

    public void setAvg_price(BigDecimal avg_price) {
        this.avg_price = avg_price;
    }

    public BigDecimal getDeal_amount() {
        return deal_amount;
    }

    public void setDeal_amount(BigDecimal deal_amount) {
        this.deal_amount = deal_amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
