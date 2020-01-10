package com.troy.trade.exchange.gateio.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResFactory;
import com.troy.commons.dto.out.ResList;
import com.troy.commons.exception.business.CallExchangeRemoteException;
import com.troy.commons.exception.enums.StateTypeSuper;
import com.troy.commons.exception.verification.VerificationException;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.OrderSideEnum;
import com.troy.commons.exchange.model.enums.TradeExchangeErrorCode;
import com.troy.commons.exchange.model.in.FullTickerReqDto;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.commons.exchange.model.out.*;
import com.troy.commons.utils.Assert;
import com.troy.commons.utils.DateUtils;
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import com.troy.trade.exchange.api.model.dto.in.account.*;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.CoinInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.*;
import com.troy.trade.exchange.api.model.dto.out.account.*;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.CoinInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.SymbolInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.SymbolInfoResDto;
import com.troy.trade.exchange.api.model.dto.out.market.MyTradeListResDto;
import com.troy.trade.exchange.api.model.dto.out.market.TickerPriceResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CancelOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CreateOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderDetailResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderListResData;
import com.troy.trade.exchange.api.util.SymbolUtil;
import com.troy.trade.exchange.core.constant.ExchangeConstant;
import com.troy.trade.exchange.core.service.IExchangeService;
import com.troy.trade.exchange.gateio.client.GateioConstant;
import com.troy.trade.exchange.gateio.client.stock.IGateioStockRestApi;
import com.troy.trade.exchange.gateio.client.stock.impl.GateioStockRestApi;
import com.troy.trade.exchange.gateio.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * gateio交易所服务
 *
 * @author dp
 */
@Slf4j
@Component
public class GateioExchangeServiceImpl implements IExchangeService {

    /**
     * gate 同时撤单异常
     */
    private final String MSG = "finished or cancelled";

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.GATEIO;
    }

    @Override
    public String toTradeSymbol(String symbol) {
        return SymbolUtil.ToTradeSymbol.underlineLowerCaseSymbol(symbol);
    }

    @Override
    public String symbol(String tradeSymbol) {
        if (StringUtils.isBlank(tradeSymbol)) {
            return null;
        }
        return tradeSymbol.replace("_", "/").toUpperCase();
    }

    @Override
    public Res<CreateOrderResDto> createOrder(Req<CreateOrderReqDto> createOrderReqDtoReq) {
        CreateOrderReqDto createOrderReqDto = createOrderReqDtoReq.getData();
        final String apiKey = createOrderReqDto.getApiKey();
        final String apiSecret = createOrderReqDto.getApiSecret();
        final String currencyPair = createOrderReqDto.getTradeSymbol();
        final String rate = createOrderReqDto.getPrice().toPlainString();
        final String amount = createOrderReqDto.getAmount().toPlainString();
        int direction = createOrderReqDto.getOrderSide().code();

        IGateioStockRestApi stockPost = new GateioStockRestApi();
        String returnStr;
        try {
            if (OrderSideEnum.BID.code().equals(direction)) {
                returnStr = stockPost.buy(currencyPair, rate, amount, apiKey, apiSecret);
            } else {
                returnStr = stockPost.sell(currencyPair, rate, amount, apiKey, apiSecret);
            }
        } catch (HttpException e) {
            log.error("调用gateio下单失败,返回数据为：", e);
            throw new CallExchangeRemoteException(e, e.getLocalizedMessage());
        } catch (Exception e) {
            log.error("调用gateio下单失败", e);
            throw new CallExchangeRemoteException(e, e.getLocalizedMessage());
        }
        log.debug("调用GateIoRemoteServiceImpl中trade方法，gateio第三方返回下单结果为：{}", returnStr);
        if (StringUtils.isBlank(returnStr)) {
            log.warn("调用gateio下单失败，返回数据为空");
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }
        GateIoTradeReturn gateIoTradeReturn = JSONObject.parseObject(returnStr, GateIoTradeReturn.class);
        if (gateIoTradeReturn != null) {
            if (!gateIoTradeReturn.isSuccess()) {
                log.warn("调用gateio下单失败，返回失败，gateio第三方返回:{}", returnStr);
                String msg = gateIoTradeReturn.getMessage();
                throw new CallExchangeRemoteException(msg);
            }
        } else {
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }
        return ResFactory.getInstance().success(new CreateOrderResDto(gateIoTradeReturn.getOrderNumber()));

    }

    @Override
    public Res<CancelOrderResDto> cancelOrder(Req<CancelOrderReqDto> cancelOrderReqDtoReq) {

        CancelOrderReqDto cancelOrderReqDto = cancelOrderReqDtoReq.getData();
        Assert.notNull(cancelOrderReqDto, TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_DATA, "撤单数据不能为空");
        final List<String> orderIds = cancelOrderReqDto.getOrderIds();
        log.info("调用Gateio撤销订单，开始，本次撤销订单ID列表大小为：{}", orderIds.size());

        Assert.notEmpty(orderIds, TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_LIST, "撤单列表不能为空");

        List<String> successOrderIds = Lists.newArrayList();
        List<String> failOrderIds = Lists.newArrayList();

        final String apiKey = cancelOrderReqDto.getApiKey();
        final String apiSecret = cancelOrderReqDto.getApiSecret();
        final String tradeSymbol = cancelOrderReqDto.getTradeSymbol();

        IGateioStockRestApi stockPost = new GateioStockRestApi();

        String cancelOrderReturn;

        try {
            if (orderIds.size() > 1) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;
                for (String orderId : orderIds) {
                    jsonObject = new JSONObject();
                    jsonObject.put("orderNumber", orderId);
                    jsonObject.put("currencyPair", tradeSymbol);
                    jsonArray.add(jsonObject);
                }
                cancelOrderReturn = stockPost.cancelOrders(jsonArray, apiKey, apiSecret);
            } else {
                // 单个撤单
                cancelOrderReturn = stockPost.cancelOrder(orderIds.get(0), tradeSymbol, apiKey, apiSecret);
            }
            log.info("撤单返回数据：" + cancelOrderReturn);
        } catch (HttpException e) {
            log.error("调用撤单HttpException异常", e);
            throw new CallExchangeRemoteException(e);
        } catch (IOException e) {
            log.error("调用撤单IOException异常", e);
            throw new CallExchangeRemoteException(e);
        }

        // 解析返回
        if (StringUtils.isNotBlank(cancelOrderReturn)) {
            JSONObject jsonObject = JSONObject.parseObject(cancelOrderReturn);
            boolean result = Boolean.valueOf(String.valueOf(jsonObject.get("result")));
            if (result) {
                successOrderIds.addAll(orderIds);
            } else {
                String message = jsonObject.getString("message");
                //同时撤单异常通过 其他异常认为失败
                if (StringUtils.isNotBlank(message) && message.contains(MSG)) {
                    successOrderIds.addAll(orderIds);
                } else {
                    failOrderIds.addAll(orderIds);
                }
            }
        }
        return ResFactory.getInstance().success(new CancelOrderResDto(successOrderIds, failOrderIds));
    }

    @Override
    public Res<OrderDetailResDto> orderDetail(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        final String appkey = orderDetailReqDto.getApiKey();
        final String appsecret = orderDetailReqDto.getApiSecret();
        final String orderId = orderDetailReqDto.getOrderId();
        final String tradeSymbol = toTradeSymbol(orderDetailReqDto.getSymbol());

        IGateioStockRestApi stockPost = new GateioStockRestApi();

        try {
            String gateioOrderDetail = stockPost.getOrder(orderId, tradeSymbol, appkey, appsecret);
            if (StringUtils.isBlank(gateioOrderDetail)) {
                log.error("调用查询订单{}失败，返回数据为空", orderId);
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用查询订单失败，返回数据为空");
            }
            GateIoOrderReturn gateIoOrderReturn = JSONObject.parseObject(gateioOrderDetail, GateIoOrderReturn.class);
            if (!gateIoOrderReturn.isResult()) {
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用查询订单失败：" + gateIoOrderReturn.getMessage());
            }
            return ResFactory.getInstance().success(transfer(gateIoOrderReturn.getOrder(), orderDetailReqDto.getSymbol()));

        } catch (HttpException e) {
            log.error("调用查询订单失败", e);
            throw new CallExchangeRemoteException(e, e.getLocalizedMessage());
        } catch (Throwable e) {
            log.error("调用查询订单失败", e);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, e.getLocalizedMessage());
        }
    }

    @Override
    public Res<OrderListResData> orderList(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        final String appkey = orderDetailReqDto.getApiKey();
        final String appsecret = orderDetailReqDto.getApiSecret();
        final String tradeSymbol = toTradeSymbol(orderDetailReqDto.getSymbol());

        IGateioStockRestApi stockPost = new GateioStockRestApi();

        try {
            String gateioOrderDetail = stockPost.myTradeHistory(tradeSymbol, null, appkey, appsecret);
            if (StringUtils.isBlank(gateioOrderDetail)) {
                log.error("调用查询订单列表{}失败，返回数据为空", tradeSymbol);
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用查询列表订单失败，返回数据为空");
            }
            GateioTradeHistoryReturn gateIoOrderReturn = JSONObject.parseObject(gateioOrderDetail, GateioTradeHistoryReturn.class);
            if (gateIoOrderReturn == null || CollectionUtils.isEmpty(gateIoOrderReturn.getGateioOpenOrders())) {
                return ResFactory.getInstance().success(new OrderListResData());
            } else {
                if (!StringUtils.equals(gateIoOrderReturn.getResult(), "true")) {
                    throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用查询订单列表失败：" + gateIoOrderReturn.getMessage());
                }
            }

            return ResFactory.getInstance().success(new OrderListResData(transferSucessOrderListReturn(gateIoOrderReturn.getGateioOpenOrders(), orderDetailReqDto.getSymbol())));

        } catch (HttpException e) {
            log.error("调用查询订单失败", e);
            throw new CallExchangeRemoteException(e, e.getLocalizedMessage());
        } catch (Throwable e) {
            log.error("调用查询订单失败", e);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, e.getLocalizedMessage());
        }

    }

    @Override
    public Res<OrderListResData> getOpenOrders(Req<OpenOrdersReqDto> ordersReqDtoReq) {
        OpenOrdersReqDto orderDetailReqDto = ordersReqDtoReq.getData();
        final String appkey = orderDetailReqDto.getApiKey();
        final String appsecret = orderDetailReqDto.getApiSecret();
        final String tradeSymbol = toTradeSymbol(orderDetailReqDto.getSymbol());

        IGateioStockRestApi stockPost = new GateioStockRestApi();

        try {
            String gateioOrderDetail = stockPost.openOrders(orderDetailReqDto.getTradeSymbol(), appkey, appsecret);
            if (StringUtils.isBlank(gateioOrderDetail)) {
                log.error("调用查询当前挂单列表{}失败，返回数据为空", tradeSymbol);
                throw new CallExchangeRemoteException("调用查询订单失败，返回数据为空");
            }
            GateioOpenOrderReturn gateIoOrderReturn = JSONObject.parseObject(gateioOrderDetail, GateioOpenOrderReturn.class);
            if (gateIoOrderReturn == null || CollectionUtils.isEmpty(gateIoOrderReturn.getGateioOpenOrders())) {
                return ResFactory.getInstance().success(new OrderListResData());
            } else {
                if (!StringUtils.equals(gateIoOrderReturn.getResult(), "true")) {
                    throw new CallExchangeRemoteException("调用当前挂单列表失败：" + gateIoOrderReturn.getMessage());
                }
            }

            return ResFactory.getInstance().success(new OrderListResData(transferOrderListReturn(gateIoOrderReturn.getGateioOpenOrders(), orderDetailReqDto.getSymbol())));

        } catch (HttpException e) {
            log.error("调用当前挂单列表失败", e);
            throw new CallExchangeRemoteException(e, e.getLocalizedMessage());
        } catch (Throwable e) {
            log.error("调用当前挂单列表失败", e);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, e.getLocalizedMessage());
        }

    }

    @Override
    public Res<ResList<ExchAcctDeptWdralResDto>> harkWithdrawal(Req<HarkWithdrawalReqDto> harkWithdrawalReqDtoReq) {

        if (null == harkWithdrawalReqDtoReq) {
            String temp = "调用 gateio 查询充提币记录失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }
        HarkWithdrawalReqDto harkWithdrawalReqDto = harkWithdrawalReqDtoReq.getData();

        IGateioStockRestApi stockPost = new GateioStockRestApi();

        String apiKey = harkWithdrawalReqDto.getApiKey();

        String apiSecret = harkWithdrawalReqDto.getApiSecret();

//            参数名	    参数类型	    必填	    描述
//            start	    String	    否	    起始UNIX时间(如 1469092370)
//            end	    String	    否	    终止UNIX时间(如 1469713981)

        //查询时间到秒
        String startTime = harkWithdrawalReqDto.getStartTime();
        String newStartTime = null;
        if (StringUtils.isNotBlank(startTime)) {
//               转换为到秒
            Long startTimeLong = Long.parseLong(startTime);
            startTimeLong = startTimeLong / 1000;
            newStartTime = String.valueOf(startTimeLong);
        }

        String endTime = harkWithdrawalReqDto.getEndTime();
        String newEndTime = null;
        if (StringUtils.isNotBlank(endTime)) {
            Long endTimeLong = Long.parseLong(endTime);
            endTimeLong = endTimeLong / 1000;
            newEndTime = String.valueOf(endTimeLong);
        }
        String result = null;
        try {
            result = stockPost.depositsWithdrawals(newStartTime, newEndTime, apiKey, apiSecret);
        } catch (Throwable e) {
            String temp = "调用gateio查询重提记录失败,异常信息：";
            log.error(temp, e);
            throw new CallExchangeRemoteException(e);
        }
//            成功：
//            {
//                "result": "true",
//                "deposits": [{
//                    "id": "d11228216",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.1",
//                    "txid": "0x348d1bff47583156c1cfbdbb82b3c68ab0ad5064d30fdbcffe2e82f2353c21ea",
//                    "timestamp": "1530337849",
//                    "status": "DONE"
//                }, {
//                    "id": "d11417802",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.7",
//                    "txid": "0x969e53135ca9acc72848d8fce8bbc8f913f5f0dba1424f85e80217de39aee9e6",
//                    "timestamp": "1530955123",
//                    "status": "DONE"
//                }, {
//                    "id": "d12239395",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.997",
//                    "txid": "0x0fa6a1936d02f4f60d2b57baed5a116661464946d8bbd69d86c87afd85a6e9ea",
//                    "timestamp": "1533886289",
//                    "status": "DONE"
//                }],
//                    "withdraws": [{
//                    "id": "w4085371",
//                    "currency": "ETH",
//                    "address": "0x6fa32086da0deef3e2125681a5bdfad607a82bbf",
//                    "amount": "0.9",
//                    "txid": "0x0f5b4f2f51097b46bf2c520c6d5c4633f8a3cca1792d1f2801fa802c261de442",
//                    "timestamp": "1541129846",
//                    "status": "DONE"
//                }],
//                "message": "Success",
//                "code": 0
//            }

        Res res = isOk(result, "充提");
        if (!res.isSuccess()) {
            return res;
        }

        List<ExchAcctDeptWdralResDto> exchAcctDeptWdralResDtos = transToHarkWithdrawalResDtos(result);
        if (null == exchAcctDeptWdralResDtos) {
            String temp = "调用gateio查询充提记录失败，gateio返回：" + result;
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }
        return ResFactory.getInstance().successList(exchAcctDeptWdralResDtos);

    }

    @Override
    public Res<WithdrawalResDto> withdraw(Req<WithdrawalReqDto> withdrawalReqDtoReq) {

            if (null == withdrawalReqDtoReq) {
                String temp = "调用 gateio 提现 失败，必填参数为空";
                log.warn(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            WithdrawalReqDto withdrawalReqDto = withdrawalReqDtoReq.getData();
            if (null == withdrawalReqDto) {
                String temp = "调用 gateio 提现 失败，必填参数为空";
                log.warn(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            String coinName = withdrawalReqDto.getCoinName();
            //提币地址
            String address = withdrawalReqDto.getAddress();
            BigDecimal amountBig = withdrawalReqDto.getTotalAmount();
            if (StringUtils.isBlank(coinName)
                    || StringUtils.isBlank(address)
                    || null == amountBig) {
                String temp = "调用 gateio 提现 失败，必填参数为空";
                log.warn(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

//            参数名	        参数类型	必填	描述
//            currency	    String	 是	提现币种(如:btc)
//            amount	    String	 是	提现数量
//            address	    String	 是	提现地址(如果需要转账memo等备注信息，可以放在收款地址后面，空格隔开)
        String result = "";
        try {
            IGateioStockRestApi gateioStockRestApi = new GateioStockRestApi();
            if(address.contains(":")){
                address = address.replace(":"," ");
            }
            result = gateioStockRestApi.withdraw(coinName.toLowerCase(),
                    amountBig.toPlainString(), address, withdrawalReqDto.getApiKey(),
                    withdrawalReqDto.getApiSecret());
//            成功：
//            { "result": "true",  "message": "Success"}
        } catch (Throwable throwable) {
            String temp = "调用 gateio 提现 异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }

        if (StringUtils.isBlank(result)) {
            String temp = "调用 gateio 提现 失败，交易所返回空";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }

        JSONObject resultJSON = JSONObject.parseObject(result);
        if (!resultJSON.containsKey("result")) {
            String temp = "调用 gateio 提现 失败，交易所返回:" + result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }

        Boolean resultBo = resultJSON.getBoolean("result");
        if (!resultBo) {
            String temp = "调用 gateio 提现 失败，交易所返回:" + result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }

        WithdrawalResDto withdrawalResDto = new WithdrawalResDto();
        return ResFactory.getInstance().success(withdrawalResDto);

    }

    /**
     * 转换成本地实体
     *
     * @param resultStr
     * @return
     */
    private List<ExchAcctDeptWdralResDto> transToHarkWithdrawalResDtos(String resultStr) {

//            {
//                "result": "true",
//                "deposits": [{
//                    "id": "d11228216",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.1",
//                    "txid": "0x348d1bff47583156c1cfbdbb82b3c68ab0ad5064d30fdbcffe2e82f2353c21ea",
//                    "timestamp": "1530337849",
//                    "status": "DONE"
//                }, {
//                    "id": "d11417802",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.7",
//                    "txid": "0x969e53135ca9acc72848d8fce8bbc8f913f5f0dba1424f85e80217de39aee9e6",
//                    "timestamp": "1530955123",
//                    "status": "DONE"
//                }, {
//                    "id": "d12239395",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.997",
//                    "txid": "0x0fa6a1936d02f4f60d2b57baed5a116661464946d8bbd69d86c87afd85a6e9ea",
//                    "timestamp": "1533886289",
//                    "status": "DONE"
//                }],
//                    "withdraws": [{
//                    "id": "w4085371",
//                    "currency": "ETH",
//                    "address": "0x6fa32086da0deef3e2125681a5bdfad607a82bbf",
//                    "amount": "0.9",
//                    "txid": "0x0f5b4f2f51097b46bf2c520c6d5c4633f8a3cca1792d1f2801fa802c261de442",
//                    "timestamp": "1541129846",
//                    "status": "DONE"
//                }],
//                "message": "Success",
//                "code": 0
//            }


        List<ExchAcctDeptWdralResDto> exchAcctDeptWdralResDtos = new ArrayList<>();

        JSONObject resultJson = JSONObject.parseObject(resultStr);

        JSONArray depositsJonArr = resultJson.getJSONArray("deposits");
        JSONArray withdrawJsonsArr = resultJson.getJSONArray("withdraws");
        if (null == depositsJonArr || depositsJonArr.size() == 0) {
            log.warn("调用gateio查询充提记录，充值列表为空");
        }

        if (null == withdrawJsonsArr || withdrawJsonsArr.size() == 0) {
            log.warn("调用gateio查询充提记录，提现列表为空");
        }

        JSONObject jsonObject = null;
        ExchAcctDeptWdralResDto exchAcctDeptWdralResDto = null;
        int depositsSize = depositsJonArr == null ? 0 : depositsJonArr.size();
        for (int i = 0; i < depositsSize; i++) {//充值记录
            jsonObject = depositsJonArr.getJSONObject(i);
            exchAcctDeptWdralResDto = new ExchAcctDeptWdralResDto();
            transToHarkWithdrawalResDto(jsonObject, exchAcctDeptWdralResDto, ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_DEPOSIT);
            exchAcctDeptWdralResDtos.add(exchAcctDeptWdralResDto);
        }

        int withdrawSize = withdrawJsonsArr == null ? 0 : withdrawJsonsArr.size();
        for (int i = 0; i < withdrawSize; i++) {//提现记录
            jsonObject = withdrawJsonsArr.getJSONObject(i);
            exchAcctDeptWdralResDto = new ExchAcctDeptWdralResDto();
            transToHarkWithdrawalResDto(jsonObject, exchAcctDeptWdralResDto, ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_WITHDRAWAL);
            exchAcctDeptWdralResDtos.add(exchAcctDeptWdralResDto);
        }

        return exchAcctDeptWdralResDtos;

//        {"result":"true","deposits":[],"withdraws":[],"message":"Success","code":0}

//        {
//            "result": "true",
//                "deposits": [{
//                    "id": "d11228216",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.1",
//                    "txid": "0x348d1bff47583156c1cfbdbb82b3c68ab0ad5064d30fdbcffe2e82f2353c21ea",
//                    "timestamp": "1530337849",
//                    "status": "DONE"
//        }, {
//                    "id": "d11417802",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.7",
//                    "txid": "0x969e53135ca9acc72848d8fce8bbc8f913f5f0dba1424f85e80217de39aee9e6",
//                    "timestamp": "1530955123",
//                    "status": "DONE"
//        }, {
//                    "id": "d12239395",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.997",
//                    "txid": "0x0fa6a1936d02f4f60d2b57baed5a116661464946d8bbd69d86c87afd85a6e9ea",
//                    "timestamp": "1533886289",
//                    "status": "DONE"
//        }],
//            "withdraws": [{
//                    "id": "w4085371",
//                    "currency": "ETH",
//                    "address": "0x6fa32086da0deef3e2125681a5bdfad607a82bbf",
//                    "amount": "0.9",
//                    "txid": "0x0f5b4f2f51097b46bf2c520c6d5c4633f8a3cca1792d1f2801fa802c261de442",
//                    "timestamp": "1541129846",
//                    "status": "DONE"
//        }],
//            "message": "Success",
//                "code": 0
//        }
    }

    /**
     * 将json转成HarkWithdrawalResDto
     *
     * @param targetJSON
     * @param exchAcctDeptWdralResDto
     * @param type
     */
    private void transToHarkWithdrawalResDto(JSONObject targetJSON, ExchAcctDeptWdralResDto exchAcctDeptWdralResDto, Integer type) {
//            {
//                    "id": "d11228216",
//                    "currency": "ETH",
//                    "address": false,
//                    "amount": "0.1",
//                    "txid": "0x348d1bff47583156c1cfbdbb82b3c68ab0ad5064d30fdbcffe2e82f2353c21ea",
//                    "timestamp": "1530337849",
//                    "status": "DONE"
//             }

        exchAcctDeptWdralResDto.setThirdId(targetJSON.getString("id"));
        exchAcctDeptWdralResDto.setCoinName(targetJSON.getString("currency").toUpperCase());

        String address = targetJSON.getString("address");
        if (!address.equals("false")) {
            exchAcctDeptWdralResDto.setAddress(address);
        }

        String amount = targetJSON.getString("amount");
        BigDecimal amountBig = null;
        if (StringUtils.isNotBlank(amount)) {
            amountBig = new BigDecimal(amount);
        }
        exchAcctDeptWdralResDto.setAmount(amountBig);

        exchAcctDeptWdralResDto.setTxId(targetJSON.getString("txid"));

        Long timeStamp = targetJSON.getLong("timestamp");
        if (null != timeStamp) {
            timeStamp = timeStamp * 1000;
            Date applyTime = DateUtils.parse(timeStamp);
            exchAcctDeptWdralResDto.setApplyTime(applyTime);
        }

        String thirdStatus = targetJSON.getString("status");//状态，1-申请中、2-已完成、3-已取消、4-失败
        Integer status = GateioHarkWithdrawalStatusEnum.getStatus(thirdStatus);
        exchAcctDeptWdralResDto.setStatus(status);
        exchAcctDeptWdralResDto.setDeptWdralType(type);
    }


    /**
     * 转换三方返回的成交的订单列表实体
     *
     * @param orders
     * @return
     */
    private List<OrderDetailResDto> transferSucessOrderListReturn(List<GateIoOrderHistory> orders, String symbol) {
        List<OrderDetailResDto> list = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (GateIoOrderHistory order : orders) {
                OrderDetailResDto dto = transferByList(order, symbol);
                dto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.DEAL);
                list.add(dto);
            }
            return list;
        }
        return list;
    }

    /**
     * 转换三方返回的历史挂单的订单列表实体
     *
     * @param orders
     * @return
     */
    private List<OrderDetailResDto> transferOrderListReturn(List<GateIoOrder> orders, String symbol) {
        List<OrderDetailResDto> list = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (GateIoOrder order : orders) {
                OrderDetailResDto dto = transfer(order, symbol);

                list.add(dto);
            }
            return list;
        }
        return list;
    }

    @Override
    public Res<OrderBookResDto> getOrderBook(Req<OrderBookReqDto> orderBookReqDtoReq) {
        try {
            OrderBookReqDto orderBookReqDto = orderBookReqDtoReq.getData();
            if (null == orderBookReqDto
                    || StringUtils.isBlank(orderBookReqDto.getSymbol())) {
                String temp = "调用 gateIo 查询买卖挂单记录失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            final String tradeSymbol = toTradeSymbol(orderBookReqDto.getSymbol());
            IGateioStockRestApi stockPost = new GateioStockRestApi();
            String orderBook = stockPost.orderBook(tradeSymbol);

            Res res = isOk(orderBook, "买卖挂单");
            if (!res.isSuccess()) {
                return res;
            }

            JSONObject resultJson = JSONObject.parseObject(orderBook);

//            成功：
//            {
//                "result": "true",
//                "asks": [[
//                    10402.8,
//                    0.0076
//                ]],
//                "bids": [[
//                    10335,
//                    0.151
//                ]]
//            }

            String price = null;
            String amount = null;


            JSONArray asksArr = resultJson.getJSONArray("asks");

            List<List<String>> asksList = new ArrayList<>();
            int asksSize = asksArr == null ? 0 : asksArr.size();
            JSONArray askJson = null;

            for (int i = 0; i < asksSize; i++) {
                askJson = asksArr.getJSONArray(i);
                price = askJson.getString(0);
                amount = askJson.getString(1);
                List<String> temp = new ArrayList<>();
                temp.add(price);
                temp.add(amount);
                asksList.add(temp);
            }

//            Collections.reverse(asksList);

            JSONArray bidsArr = resultJson.getJSONArray("bids");
            List<List<String>> bidsList = new ArrayList<>();
            int bidsSize = asksArr == null ? 0 : asksArr.size();
            JSONArray bidJson = null;

            for (int i = 0; i < bidsSize; i++) {
                bidJson = bidsArr.getJSONArray(i);
                price = bidJson.getString(0);
                amount = bidJson.getString(1);
                List<String> temp = new ArrayList<>();
                temp.add(price);
                temp.add(amount);
                bidsList.add(temp);
            }

            OrderBookResDto orderBookResDto = new OrderBookResDto(asksList, bidsList);
            log.debug("查找 gateio 查找买卖挂单 记录 返回结果：" + JSONObject.toJSONString(orderBookResDto));
            return ResFactory.getInstance().success(orderBookResDto);
        } catch (Throwable throwable) {
            String temp = "调用 gateio 查找买卖挂单信息异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }

    }

    @Override
    public Res<TradeHistoryListResDto> getTrades(Req<TradeHistoryReqDto> tradeHistoryReqDtoReq) {
        try {
            TradeHistoryReqDto tradeHistoryReqDto = tradeHistoryReqDtoReq.getData();
            if (null == tradeHistoryReqDto
                    || StringUtils.isBlank(tradeHistoryReqDto.getSymbol())) {
                String temp = "调用 gateIo 查询 历史成交 记录失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            String symbol = tradeHistoryReqDto.getSymbol();
            final String tradeSymbol = toTradeSymbol(symbol);
            IGateioStockRestApi stockPost = new GateioStockRestApi();
            String tradeHistory = stockPost.tradeHistory(tradeSymbol);


            Res res = isOk(tradeHistory, "历史成交");
            if (!res.isSuccess()) {
                return res;
            }

//            成功：
//            {
//                "elapsed": "1ms",
//                "result": "true",
//                "data": [
//                            {
//                                "tradeID": "189055524",
//                                "total": "0.0009284184",
//                                "date": "2019-08-28 00:41:18",
//                                "rate": "0.018312",
//                                "amount": "0.0507",
//                                "timestamp": "1566952878",
//                                "type": "sell"
//                            },
//                            {
//                                "tradeID": "189055525",
//                                "total": "0.023204263",
//                                "date": "2019-08-28 00:41:18",
//                                "rate": "0.01831",
//                                "amount": "1.2673",
//                                "timestamp": "1566952878",
//                                "type": "sell"
//                            }
//                ]
//            }
            JSONObject resultJson = JSONObject.parseObject(tradeHistory);

            JSONArray tradeHistoryArr = resultJson.getJSONArray("data");
            int tradeHistorySize = tradeHistoryArr == null ? 0 : tradeHistoryArr.size();

            List<TradeHistoryResDto> tradeHistoryResDtoList = new ArrayList<>();
            if (tradeHistorySize <= 0) {//交易所返回为空
                TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
                tradeHistoryListResDto.setTradeHistoryResDtoList(tradeHistoryResDtoList);
                return ResFactory.getInstance().success(tradeHistoryListResDto);
            }

            TradeHistoryResDto tempTradeHistoryResDto = null;
            OrderSideEnum orderSide = null;

            JSONObject jsonObject = null;
            for (int i = (tradeHistorySize - 1); i >= 0; i--) {
                jsonObject = tradeHistoryArr.getJSONObject(i);
                String type = jsonObject.getString("type");
                if (StringUtils.equals(type, GateioConstant.ORDER_TYPE_SELL)) {//卖出
                    orderSide = OrderSideEnum.ASK;
                } else {
                    orderSide = OrderSideEnum.BID;
                }

                String amount = jsonObject.getString("amount");
                String price = jsonObject.getString("rate");
                Long timestamp = jsonObject.getLong("timestamp");
                timestamp = timestamp * 1000;
                String tradeID = jsonObject.getString("tradeID");


//              TradeExchangeApiConstant.OrderSide orderSide, String amount, String symbol, String price, Long timestamp, String id) {
                tempTradeHistoryResDto = new TradeHistoryResDto(orderSide, amount,
                        symbol, price, timestamp, tradeID);
                tradeHistoryResDtoList.add(tempTradeHistoryResDto);
            }

            TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
            tradeHistoryListResDto.setTradeHistoryResDtoList(tradeHistoryResDtoList);
            return ResFactory.getInstance().success(tradeHistoryListResDto);

        } catch (Throwable throwable) {
            String temp = "调用 gateio 查找 历史成交 信息异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<MyTradeListResDto> getMyTrades(Req<MyTradeReqDto> myTradeReqDtoReq) {
        return null;
    }

    @Override
    public Res<SymbolInfoListResDto> getSymbolInfo(Req<SymbolInfoReqDto> symbolInfoReqDtoReq) {
        try {
            if (null == symbolInfoReqDtoReq
                    || null == symbolInfoReqDtoReq.getData()) {
                String temp = "调用 gateio 查询 交易对信息 失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            SymbolInfoReqDto symbolInfoReqDto = symbolInfoReqDtoReq.getData();

            IGateioStockRestApi stock = new GateioStockRestApi();
            String marketInfoResult = stock.marketinfo();//查找所有交易对市场信息

            Res res = isOk(marketInfoResult, "交易对信息");
            if (!res.isSuccess()) {
                return res;
            }

            JSONObject marketInfoJson = JSONObject.parseObject(marketInfoResult);

            List<SymbolInfoResDto> symbolInfoResDtoList = new ArrayList<>();
            SymbolInfoResDto symbolInfoResDto = null;

            JSONArray pairInfoArr = marketInfoJson.getJSONArray("pairs");
            JSONObject pairInfo = null;
            String tradeSymbol = null;
            String[] coinArr = null;
            Set<String> keySet = null;
            List<String> keyList = null;
            int pairsSize = pairInfoArr.size();
            for (int i = 0; i < pairsSize; i++) {//交易对信息
                JSONObject childJson = pairInfoArr.getJSONObject(i);
                keySet = childJson.keySet();
                keyList = new ArrayList<>(keySet);
                int keySize = keyList.size();
                for (int j = 0; j < keySize; j++) {
                    tradeSymbol = keyList.get(j);
                    pairInfo = childJson.getJSONObject(tradeSymbol);

                    Integer decimalPlaces = pairInfo.getInteger("decimal_places");//价格精度
                    BigDecimal minAmount = pairInfo.getBigDecimal("min_amount");//数量精度

                    Integer basePrecision = minAmount.scale();//货币精度
                    Integer quotePrecision = decimalPlaces;//钱币精度

                    String symbol = this.symbol(tradeSymbol);
                    coinArr = symbol.split("/");

                    String baseName = coinArr[0];
                    String quoteName = coinArr[1];
                    BigDecimal baseLeast = minAmount;

                    Integer status = ExchangeConstant.SYMBOL_STATUS_ON;
                    Integer trade_disabled = pairInfo.getInteger("trade_disabled");
                    if (trade_disabled == 1) {//已暂停交易
                        status = ExchangeConstant.SYMBOL_STATUS_OFF;
                    }

                    symbolInfoResDto = new SymbolInfoResDto();
                    symbolInfoResDto.setSymbol(symbol);
                    symbolInfoResDto.setStatus(status);
                    symbolInfoResDto.setQuoteName(quoteName);
                    symbolInfoResDto.setBaseName(baseName);
                    symbolInfoResDto.setBaseLeast(baseLeast);
                    symbolInfoResDto.setQuoteLeast(null);
                    symbolInfoResDto.setBasePrecision(basePrecision);
                    symbolInfoResDto.setQuotePrecision(quotePrecision);
                    symbolInfoResDtoList.add(symbolInfoResDto);
                }
            }

//            成功：
//                {
//                "result": "true",
//                "pairs": [{
//                        "btc_pax": {
//                            "decimal_places": 2,
//                            "min_amount": 0.0001,
//                            "min_amount_a": 0.0001,
//                            "min_amount_b": 0.0001,
//                            "fee": 0.2,
//                            "trade_disabled": 0
//                        }
//                    },
//                    {
//                        "btc_usdt": {
//                            "decimal_places": 2,
//                            "min_amount": 0.0001,
//                            "min_amount_a": 0.0001,
//                            "min_amount_b": 1,
//                            "fee": 0.2,
//                            "trade_disabled": 0
//                        }
//                    }]
//                }
            SymbolInfoListResDto symbolInfoListResDto = new SymbolInfoListResDto();
            symbolInfoListResDto.setExchangeCode(symbolInfoReqDto.getExchCode());
            symbolInfoListResDto.setSymbolInfoResDtoList(symbolInfoResDtoList);
            log.debug("调用 gateio 查找 交易对信息 记录 返回结果：" + JSONObject.toJSONString(symbolInfoListResDto));
            return ResFactory.getInstance().success(symbolInfoListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 gateio 查找 交易对信息 异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<CoinInfoListResDto> getCoinInfo(Req<CoinInfoReqDto> coinInfoReqDtoReq) {
        return null;
    }

    @Override
    public Res<FullTickerListResDto> fullTickers(Req<FullTickerReqDto> fullTickerReqDtoReq) {
        log.debug("调用 gateio 查询所有交易对tickers信息开始");
        try {
            IGateioStockRestApi stockPost = new GateioStockRestApi();
            String result = stockPost.tickers();
//            成功：
//            {
//                "usdt_cnyx": {
//                    "result": "true",
//                    "last": "6.977",
//                    "lowestAsk": "6.976",
//                    "highestBid": "6.973",
//                    "percentChange": "0.08",
//                    "baseVolume": "4088164.70005362",
//                    "quoteVolume": "586349.59876304",
//                    "high24hr": "6.98",
//                    "low24hr": "6.962"
//                },
//                "btc_cnyx": {
//                    "result": "true",
//                    "last": "64204.87",
//                    "lowestAsk": "64311.51",
//                    "highestBid": "64160.79",
//                    "percentChange": "2.29",
//                    "baseVolume": "912557.65235487",
//                    "quoteVolume": "14.33055657",
//                    "high24hr": "64428.42",
//                    "low24hr": "62093.85"
//            }}

            if (StringUtils.isBlank(result)) {
                String temp = "调用 gateio 查询所有交易对tickers信息失败，gateio返回为空。";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            List<FullTickerResDto> fullTickerResDtoList = new ArrayList<>();
            JSONObject jsonObject = JSONObject.parseObject(result);
            FullTickerResDto fullTickerResDto = null;
            String key = null;
            JSONObject symbolJSON = null;
            String symbol = null;
            BigDecimal last = null;
            BigDecimal quoteVolume = null;
            BigDecimal percentChange = null;
            BigDecimal lowPrice = null;
            BigDecimal highPrice = null;
            Set<String> keySet = jsonObject.keySet();
            List<String> keyList = new ArrayList<>(keySet);
            int size = keyList.size();
            for (int i = 0; i < size; i++) {
                fullTickerResDto = new FullTickerResDto();
                key = keyList.get(i);
                symbolJSON = jsonObject.getJSONObject(key);
                symbol = key.replace("_", "/").toUpperCase();
                last = symbolJSON.getBigDecimal("last");
                if (null != last) {
                    last = last.stripTrailingZeros();
                }

                percentChange = symbolJSON.getBigDecimal("percentChange");
                if (null != percentChange) {
                    percentChange = percentChange.setScale(2, BigDecimal.ROUND_FLOOR);
                }

                quoteVolume = symbolJSON.getBigDecimal("quoteVolume");
                if (null != quoteVolume) {
                    quoteVolume = quoteVolume.stripTrailingZeros();
                }

                lowPrice = symbolJSON.getBigDecimal("low24hr");
                if (null != lowPrice) {
                    lowPrice = lowPrice.stripTrailingZeros();
                }

                highPrice = symbolJSON.getBigDecimal("high24hr");
                if (null != highPrice) {
                    highPrice = highPrice.stripTrailingZeros();
                }


                fullTickerResDto.setSymbol(symbol);
                fullTickerResDto.setLast(last);
                fullTickerResDto.setPriceChangePercent(percentChange);
                fullTickerResDto.setVolume24h(quoteVolume);
                fullTickerResDto.setLowPrice(lowPrice);
                fullTickerResDto.setHighPrice(highPrice);
                fullTickerResDtoList.add(fullTickerResDto);
            }

            FullTickerListResDto fullTickerListResDto = new FullTickerListResDto();
            fullTickerListResDto.setFullTickerResDtoList(fullTickerResDtoList);
            log.debug("调用 gateio 查询全量ticker信息 返回结果：" + JSONObject.toJSONString(fullTickerListResDto));
            return ResFactory.getInstance().success(fullTickerListResDto);
        } catch (Throwable e) {
            String temp = "调用 gateio 查询全量ticker失败,异常信息：";
            log.error(temp, e);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<TickerPriceResDto> tickerPrice(Req<TickerPriceReqDto> tickerPriceReqDtoReq) {
        try {
            if (null == tickerPriceReqDtoReq
                    || null == tickerPriceReqDtoReq.getData()) {
                String temp = "调用 gateio 查询 交易对价格信息 失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            TickerPriceReqDto tickerPriceReqDto = tickerPriceReqDtoReq.getData();

            String symbol = tickerPriceReqDto.getSymbol();
            if (StringUtils.isBlank(symbol)) {
                String temp = "调用gateio查询价格信息失败,必填参数symbol为空";
                log.warn(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }


            IGateioStockRestApi stockPost = new GateioStockRestApi();
            String tradeSymbol = this.toTradeSymbol(symbol);
            String ticker = stockPost.ticker(tradeSymbol);

            Res res = isOk(ticker, "价格信息");
            if (!res.isSuccess()) {
                return res;
            }

            JSONObject tickerJson = JSONObject.parseObject(ticker);
            BigDecimal lastBig = new BigDecimal(tickerJson.getString("last"));
            TickerPriceResDto tickerPriceResDto = new TickerPriceResDto(symbol, lastBig.stripTrailingZeros());
            return ResFactory.getInstance().success(tickerPriceResDto);

//            {
//                "result": "true",
//                "last": "0.00031997",
//                "lowestAsk": "0.00031997",
//                "highestBid": "0.00031226",
//                "percentChange": "-2.56",
//                "baseVolume": "101268.82270183",
//                "quoteVolume": "318813306.68185854",
//                "high24hr": "0.00033347",
//                "low24hr": "0.000308"
//            }


//            {
//                "result": "false",
//                "code": 7,
//                "message": "Error: invalid currency pair"
//            }

        } catch (Throwable e) {
            String temp = "调用gateio查询ticker信息失败，异常信息：";
            log.error(temp, e);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    /**
     * 转换gateio返回对象为可解析的ordersDetail
     *
     * @param order
     * @return
     */
    public OrderDetailResDto transferByList(GateIoOrderHistory order, String symbol) {
        try {
            OrderDetailResDto result = new OrderDetailResDto();
            result.setOrderId(order.getOrderNumber());
            result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.DEAL);
            result.setFinishedAt(DateUtils.parse(Long.parseLong(order.getTime_unix()) * 1000));//订单变为最终状态的时间

            result.setSymbol(symbol);
            String type = order.getType();//买卖类型 sell卖出, buy买入
            if (StringUtils.equals(type, GateioConstant.ORDER_TYPE_BUY)) {
                result.setOrderSide(OrderSideEnum.BID);
            } else if (StringUtils.equals(type, GateioConstant.ORDER_TYPE_SELL)) {
                result.setOrderSide(OrderSideEnum.ASK);
            }

            result.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);

            result.setAmount(order.getAmount());//下单量
            result.setPrice(order.getRate());//已成交价格
            result.setFilledPrice(order.getRate());//已成交价格
            if (null == result.getFinishedAt()) {
                result.setFinishedAt(new Date());
            }
            return result;
        } catch (Exception e) {
            log.error("调用gateio查询第三方订单详情后后做实体转换异常，异常信息：", e);
            return null;
        }
    }

    /**
     * 转换gateio返回对象为可解析的ordersDetail
     *
     * @param order
     * @return
     */
    public OrderDetailResDto transfer(GateIoOrder order, String symbol) {
        try {
            String status = order.getStatus();

            OrderDetailResDto result = new OrderDetailResDto();
            result.setOrderId(order.getOrderNumber());
            BigDecimal filledAmount = order.getFilledAmount() == null ? BigDecimal.ZERO : order.getFilledAmount();
            result.setFilledAmount(filledAmount);//已成交数量
            if (StringUtils.equals(status, "cancelled")) {// 已取消
                result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.CANCEL);
                result.setFinishedAt(DateUtils.parse(Long.parseLong(order.getTimestamp()) * 1000));//订单变为最终状态的时间
            } else if (StringUtils.equals(status, "closed")) {// 已完成
                result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.DEAL);
                result.setFinishedAt(DateUtils.parse(Long.parseLong(order.getTimestamp()) * 1000));//订单变为最终状态的时间
            } else if (StringUtils.equals(status, "open")) {//已挂单
                result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.COMMIT);
                if (filledAmount.compareTo(BigDecimal.ZERO) > 0) {
                    result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.PART);
                }
            } else {
                result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.UNKNOW);
            }

            result.setSymbol(symbol);
            String type = order.getType();//买卖类型 sell卖出, buy买入
            if (StringUtils.equals(type, GateioConstant.ORDER_TYPE_BUY)) {
                result.setOrderSide(OrderSideEnum.BID);
            } else if (StringUtils.equals(type, GateioConstant.ORDER_TYPE_SELL)) {
                result.setOrderSide(OrderSideEnum.ASK);
            }

            result.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
            BigDecimal initialAmount = order.getInitialAmount() == null ? BigDecimal.ZERO : order.getInitialAmount();
            result.setAmount(initialAmount);//下单量
            result.setPrice(order.getInitialRate());//下单价格
            result.setFilledPrice(order.getFilledRate());//已成交价格

            result.setFilledCashAmount(order.getFilledRate().multiply(order.getFilledAmount()));//已成交总金额
            result.setFeeValue(order.getFeeValue());
            BigDecimal leftAmount = order.getLeft() == null ? (initialAmount.subtract(filledAmount)) : order.getLeft();
            result.setLeftAmount(leftAmount);//剩余数量
            if (null == result.getFinishedAt()) {
                result.setFinishedAt(new Date());
            }
            return result;
        } catch (Exception e) {
            log.error("调用gateio查询第三方订单详情后后做实体转换异常，异常信息：", e);
            return null;
        }
    }


    @Override
    public Res<AccountInfoResDto> getAccountInfo(Req<AccountInfoReqDto> accountInfoReqDtoReq) {
        return null;
    }

    @Override
    public Res<ResList<QueryBalanceResDto>> getBalance(QueryBalanceReqDto queryBalanceReqDto) {
        String result = null;
        try {
            IGateioStockRestApi stockPost = new GateioStockRestApi();
            result = stockPost.balance(queryBalanceReqDto.getApiKey(), queryBalanceReqDto.getApiSecret());
            if (StringUtils.isBlank(result)) {
                log.warn("调用gateio查询余额信息为空");
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用gateio查询余额信息为空");
            }

            Map<String, QueryBalanceResDto> resultMap = Maps.newHashMap();
            JSONObject resultJSON = JSON.parseObject(result);
            if (resultJSON.containsKey("result") && StringUtils.equals("true", resultJSON.getString("result"))) {

                String available = resultJSON.getString("available");
                if (StringUtils.isBlank(available) || StringUtils.startsWith(available, "[")) {
                    log.warn("调用gateio查询余额信息，返回available格式有误：", available);
                    throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用gateio查询余额信息，返回格式有误");
                }

                JSONObject availableJSON = resultJSON.getJSONObject("available");
                Set<String> set = availableJSON.keySet();
                Iterator<String> iterator = set.iterator();
                String key;
                for (; iterator.hasNext(); ) {
                    key = iterator.next().toUpperCase();
                    if (!resultMap.containsKey(key)) {
                        resultMap.put(key, new QueryBalanceResDto(key, BigDecimal.ZERO, new BigDecimal(availableJSON.getString(key))));
                    }
                }

                //初始化冻结金额
                JSONObject lockedJSON = resultJSON.getJSONObject("locked");
                Set<String> lockedKeySet = lockedJSON.keySet();
                Iterator<String> lockedIterator = lockedKeySet.iterator();
                String lockedKey;
                for (; lockedIterator.hasNext(); ) {
                    lockedKey = lockedIterator.next().toUpperCase();
                    if (!resultMap.containsKey(lockedKey)) {
                        resultMap.put(lockedKey, new QueryBalanceResDto(lockedKey, new BigDecimal(lockedJSON.getString(lockedKey)), BigDecimal.ZERO));
                    } else {
                        resultMap.get(lockedKey).setFrozen(new BigDecimal(lockedJSON.getString(lockedKey)));
                    }
                }
                return ResFactory.getInstance().successList(Lists.newArrayList(resultMap.values()));
            } else {
                log.warn("调用gateio查询余额信息，返回数据为失败：result = {}", resultJSON);
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用gateio查询余额信息，返回数据为失败");
            }
        } catch (Exception e) {
            log.error("调用gateio查询账户余额异常，第三方返回：" + result + ",异常信息：", e);
            throw new CallExchangeRemoteException(e, e.getMessage());
        }
    }

    @Override
    public Res<DepositAddressResDto> depositAddress(Req<DepositAddressReqDto> depositAddressReqDtoReq) {
        if (null == depositAddressReqDtoReq || null == depositAddressReqDtoReq.getData()) {
            String temp = "参数异常";
            log.error(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        DepositAddressReqDto depositAddressReqDto = depositAddressReqDtoReq.getData();
        String coinName = depositAddressReqDto.getCoinName();
        if (StringUtils.isBlank(coinName)) {
            String temp = "参数异常";
            log.error(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        IGateioStockRestApi restApi = new GateioStockRestApi();
        String result = null;
        try {
            result = restApi.depositAddress(depositAddressReqDto.getApiKey(), depositAddressReqDto.getApiSecret(), coinName.toUpperCase());

//            成功：
//            {
//                "result": "true",
//                "addr": "LPXtk1kWHioP62SzfqwKbYE3Z7Wt2ujYEc",
//                "message": "Sucess",
//                "code": 0
//            }

            if (StringUtils.isBlank(result)) {
                String temp = "调用 gateio 查询币种充值地址信息失败，交易所返回为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject resultJSON = JSONObject.parseObject(result);
            if (!resultJSON.containsKey("result")) {
                String temp = "调用 gateio 查询币种充值地址信息失败，交易所返回" + result;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            boolean resultBo = resultJSON.getBoolean("result");
            if (!resultBo) {
                String temp = "调用 gateio 查询币种充值地址信息失败，交易所返回" + result;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }
            String address = resultJSON.getString("addr");
            DepositAddressResDto depositAddressResDto = new DepositAddressResDto();
            depositAddressResDto.setAddress(address);
            depositAddressResDto.setCoinName(depositAddressReqDto.getCoinName());
            return ResFactory.getInstance().success(depositAddressResDto);
        } catch (Throwable throwable) {
            String temp = "调用 gateio 查询币种充值地址信息异常";
            log.error(temp + "，异常信息：", throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    /**
     * 验证返回结果是否调用成功
     *
     * @param result
     * @param desc
     * @return
     */
    private Res isOk(String result, String desc) {

        if (StringUtils.isBlank(result)) {
            String temp = "调用 gateio 查找 " + desc + " 记录失败,返回结果为空";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }

        JSONObject resultJson = JSONObject.parseObject(result);
        if (!resultJson.containsKey("result")
                || !resultJson.getBoolean("result")) {
            String temp = "调用 gateio 查找 " + desc + " 记录失败,返回结果为 " + result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
        }

        return ResFactory.getInstance().successList(null);
    }

    /**
     * @param sourceCoin 币名称，如：BTC/USDT的价格，此值为BTC
     * @param targetCoin 钱名称，如：BTC/USDT的价格，此值为USDT
     * @return 返回转换结果，true 转换成功、false 转换失败
     */
    private BigDecimal price(String sourceCoin, String targetCoin, Map<String, BigDecimal> priceMap) {
        try {
            String symbol = sourceCoin + "/" + targetCoin;
            if (priceMap.containsKey(symbol)) {
                return priceMap.get(symbol).stripTrailingZeros();
            }

            if (StringUtils.equals(sourceCoin, targetCoin)) {
                priceMap.put(symbol, new BigDecimal("1"));
                return new BigDecimal("1");
            } else {
                TickerPriceReqDto tickerPriceReqDto = new TickerPriceReqDto();
                tickerPriceReqDto.setExchCode(ExchangeCode.GATEIO);

                tickerPriceReqDto.setSymbol(symbol.toUpperCase());
                Req<TickerPriceReqDto> tickerPriceReqDtoReq = ReqFactory.getInstance().createReq(tickerPriceReqDto);
                Thread.sleep(1000L);
                Res<TickerPriceResDto> tickerPriceResDtoRes = this.tickerPrice(tickerPriceReqDtoReq);
                if (null != tickerPriceResDtoRes && tickerPriceResDtoRes.isSuccess()) {
                    TickerPriceResDto tickerPriceResDto = tickerPriceResDtoRes.getData();
                    if (null != tickerPriceResDto) {
                        priceMap.put(symbol, tickerPriceResDto.getPrice().stripTrailingZeros());
                        return tickerPriceResDto.getPrice().stripTrailingZeros();
                    }
                }
            }
        } catch (Throwable throwable) {
            //调用币种对USDT价格失败，查询对BTC价格
        }
        return null;
    }
}
