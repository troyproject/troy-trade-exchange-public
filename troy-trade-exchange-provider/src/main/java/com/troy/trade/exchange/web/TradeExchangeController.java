package com.troy.trade.exchange.web;

import cn.hutool.core.lang.Assert;
import com.troy.commons.BaseController;
import com.troy.commons.constraints.Log;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.PrivateTradeReqData;
import com.troy.trade.exchange.api.model.dto.in.order.*;
import com.troy.trade.exchange.api.model.dto.out.market.MyTradeListResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CancelOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CreateOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderDetailResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderListResData;
import com.troy.trade.exchange.api.service.TradeExchangeApi;
import com.troy.trade.exchange.core.service.IExchangeService;
import com.troy.trade.exchange.service.TroyExchangeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易所服务
 *
 * @author dp
 */
@RefreshScope
@RestController
public class TradeExchangeController extends BaseController implements TradeExchangeApi {

    @Autowired
    private TroyExchangeFactory troyExchangeFactory;

    /**
     * 调用交易所下单
     *
     * @param createOrderReqDtoReq
     * @return
     */
    @Log("createOrder")
    @Override
    public Res<CreateOrderResDto> createOrder(@RequestBody Req<CreateOrderReqDto> createOrderReqDtoReq) {
        return getExchangService(createOrderReqDtoReq.getData()).createOrder(createOrderReqDtoReq);
    }

    /**
     * 调用交易所撤单（支持批量模式）
     *
     * @param cancelOrderReqDtoReq
     * @return
     */
    @Log("cancelOrder")
    @Override
    public Res<CancelOrderResDto> cancelOrder(@RequestBody Req<CancelOrderReqDto> cancelOrderReqDtoReq) {
        return getExchangService(cancelOrderReqDtoReq.getData()).cancelOrder(cancelOrderReqDtoReq);
    }

    /**
     * 根据三方订单号查询订单详情
     *
     * @param orderDetailReqDtoReq
     * @return
     */
    @Log("orderDetail")
    @Override
    public Res<OrderDetailResDto> orderDetail(@RequestBody Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        return getExchangService(orderDetailReqDtoReq.getData()).orderDetail(orderDetailReqDtoReq);
    }

    /**
     * 根据条件查询订单列表
     *
     * @param orderDetailReqDtoReq
     * @return
     */
    @Log(value = "orderList", outputPrint = false)
    @Override
    public Res<OrderListResData> orderList(@RequestBody Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        return getExchangService(orderDetailReqDtoReq.getData()).orderList(orderDetailReqDtoReq);
    }

    /**
     * 根据条件获取成交明细
     *
     * @param myTradeReqDtoReq
     * @return
     */
    @Log(value = "getMyTrades", outputPrint = false)
    @Override
    public Res<MyTradeListResDto> getMyTrades(@RequestBody Req<MyTradeReqDto> myTradeReqDtoReq) {
        return getExchangService(myTradeReqDtoReq.getData()).getMyTrades(myTradeReqDtoReq);

    }
    /**
     * 查看账户当前挂单
     *
     * @param ordersReqDtoReq
     * @return
     */
    @Log(value = "getOpenOrders", outputPrint = false)
    @Override
    public Res<OrderListResData> getOpenOrders(@RequestBody Req<OpenOrdersReqDto> ordersReqDtoReq){
        return getExchangService(ordersReqDtoReq.getData()).getOpenOrders(ordersReqDtoReq);
    }

    /**
     * 获取交易所服务对象
     *
     * @param privateTradeReqData
     * @return
     */
    public IExchangeService getExchangService(PrivateTradeReqData privateTradeReqData) {
        ExchangeCode exchCode = privateTradeReqData.getExchCode();
        Assert.notNull(exchCode, "交易所Code不能为空");

        IExchangeService exchange = troyExchangeFactory.getExchangeService(exchCode);
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        privateTradeReqData.setTradeSymbol(exchange.toTradeSymbol(privateTradeReqData.getSymbol()));
        return exchange;
    }

    /**
     * 根据条件查询订单列表
     *
     * @param orderDetailReqDtoReq
     * @return
     */
    @Log(value = "orderListByPage", outputPrint = false)
    @Override
    public Res<OrderListResData> orderListByPage(@RequestBody Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        return getExchangService(orderDetailReqDtoReq.getData()).orderListByPage(orderDetailReqDtoReq);
    }
}
