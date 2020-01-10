package com.troy.trade.exchange.okex.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.troy.commons.utils.CalculateUtil;
import com.troy.commons.utils.DateUtils;
import com.troy.trade.exchange.api.exception.TradeExchangeApiException;
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import com.troy.trade.exchange.api.model.dto.in.account.*;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.CoinInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.KlineReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.*;
import com.troy.trade.exchange.api.model.dto.out.account.*;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.CoinInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.CoinInfoResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.SymbolInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.SymbolInfoResDto;
import com.troy.trade.exchange.api.model.dto.out.market.KLineResDto;
import com.troy.trade.exchange.api.model.dto.out.market.MyTradeListResDto;
import com.troy.trade.exchange.api.model.dto.out.market.MyTradeResDto;
import com.troy.trade.exchange.api.model.dto.out.market.TickerPriceResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CancelOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CreateOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderDetailResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderListResData;
import com.troy.trade.exchange.api.util.SymbolUtil;
import com.troy.trade.exchange.core.constant.ExchangeConstant;
import com.troy.trade.exchange.core.service.IExchangeService;
import com.troy.trade.exchange.okex.client.bean.spot.param.OrderParamDto;
import com.troy.trade.exchange.okex.client.bean.spot.param.PlaceOrderParam;
import com.troy.trade.exchange.okex.client.bean.spot.result.*;
import com.troy.trade.exchange.okex.client.config.APIConfiguration;
import com.troy.trade.exchange.okex.client.constant.OKConstant;
import com.troy.trade.exchange.okex.client.enums.OKMarginConstant;
import com.troy.trade.exchange.okex.client.service.spot.impl.SpotAccountAPIServiceImpl;
import com.troy.trade.exchange.okex.client.service.spot.impl.SpotOrderApiServiceImpl;
import com.troy.trade.exchange.okex.client.stock.IOkexStockRestApi;
import com.troy.trade.exchange.okex.client.stock.dto.OkTrades;
import com.troy.trade.exchange.okex.client.stock.impl.OkexStockRestApi;
import com.troy.trade.exchange.okex.dto.OkexDepositStatusEnum;
import com.troy.trade.exchange.okex.dto.OkexWithDrawStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OKEX交易所服务
 *
 * @author dp
 */
@Slf4j
@Component
public class OkexExchangeServiceImpl implements IExchangeService {

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.OKEX;
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
        return tradeSymbol.replace("-", "/").toUpperCase();
    }

    @Override
    public Res<CreateOrderResDto> createOrder(Req<CreateOrderReqDto> createOrderReqDtoReq) {
        CreateOrderReqDto createOrderReqDto = createOrderReqDtoReq.getData();
        String createOrderReqDtoStr = createOrderReqDto == null ? null : JSONObject.toJSONString(createOrderReqDto);
        if (StringUtils.isBlank(createOrderReqDtoStr)) {
            throw new VerificationException(TradeExchangeErrorCode.FAIL_PARAMETER);
        }
        String appkey = createOrderReqDto.getApiKey();
        String appsecret = createOrderReqDto.getApiSecret();
        String passphrase = createOrderReqDto.getPassphrase();
        final String currencyPair = createOrderReqDto.getTradeSymbol();// 交易对
        APIConfiguration config = new APIConfiguration(appkey, appsecret, passphrase);
        SpotOrderApiServiceImpl spotOrderApiService = new SpotOrderApiServiceImpl(config);

        PlaceOrderParam orderParam = new PlaceOrderParam();
        orderParam.setInstrument_id(currencyPair);
        orderParam.setPrice(createOrderReqDto.getPrice().toPlainString());
        orderParam.setSide(OrderSideEnum.BID.code().equals(createOrderReqDto.getOrderSide().code()) ? OKConstant.TRADE_TYPE_BUY : OKConstant.TRADE_TYPE_SELL);
        orderParam.setSize(createOrderReqDto.getAmount().toPlainString());
        orderParam.setType("limit");
        String orderId;
        OrderResult tradeResult;
        try {
            tradeResult = spotOrderApiService.addOrder(orderParam);
        } catch (Throwable e) {
            log.error("调用okex下单失败：", e);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, e.getMessage());
        }
        if (tradeResult == null || !tradeResult.isResult()) {
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, JSON.toJSONString(tradeResult));
        }
        orderId = String.valueOf(tradeResult.getOrder_id());

        if (StringUtils.isBlank(orderId)) {
            log.warn("调用OKEX下单，返回订单号为空");
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
        }
        return ResFactory.getInstance().success(new CreateOrderResDto(orderId));
    }

    @Override
    public Res<CancelOrderResDto> cancelOrder(Req<CancelOrderReqDto> cancelOrderReqDtoReq) {

        CancelOrderReqDto cancelOrderReqDto = cancelOrderReqDtoReq.getData();
        Assert.notNull(cancelOrderReqDto, TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_DATA, "撤单数据不能为空");
        final List<String> orderIds = cancelOrderReqDto.getOrderIds();
        final String tradeSymbol = cancelOrderReqDto.getTradeSymbol();
        log.info("调用okex撤销订单，开始，本次撤销订单ID列表大小为：{}", orderIds.size());

        Assert.notEmpty(orderIds,  TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_LIST,"撤单列表不能为空");

        List<String> successOrderIds = Lists.newArrayList();
        List<String> failOrderIds = Lists.newArrayList();

        SpotOrderApiServiceImpl spotOrderApiService = new SpotOrderApiServiceImpl(
                new APIConfiguration(cancelOrderReqDto.getApiKey(), cancelOrderReqDto.getApiSecret(), cancelOrderReqDto.getPassphrase()));
        // 按照大小分隔需要撤单的列表
        List<List<String>> orderIdsList = Lists.partition(orderIds, OKConstant.BATCH_CANCEL_COUNT);
        for (List<String> splitOrderIds : orderIdsList) {
            Map<String, List<BatchOrdersResult>> batchOrdersResultMap = spotOrderApiService.cancelOrdersPost(Lists.newArrayList(new OrderParamDto(tradeSymbol, splitOrderIds)));
            Assert.notNull(batchOrdersResultMap, "撤销订单返回失败");
            List<BatchOrdersResult> batchOrdersResults = batchOrdersResultMap.get(tradeSymbol);
            batchOrdersResults.stream().forEach(batchOrdersResult -> {
                if (batchOrdersResult.isResult()) {
                    successOrderIds.add(batchOrdersResult.getOrder_id());
                } else {
                    failOrderIds.add(batchOrdersResult.getOrder_id());
                }
            });
        }
        return ResFactory.getInstance().success(new CancelOrderResDto(successOrderIds, failOrderIds));
    }

    @Override
    public Res<OrderDetailResDto> orderDetail(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        String symbol = orderDetailReqDto.getSymbol();
        APIConfiguration config = new APIConfiguration(orderDetailReqDto.getApiKey(), orderDetailReqDto.getApiSecret(), orderDetailReqDto.getPassphrase());
        SpotOrderApiServiceImpl spotOrderApiService = new SpotOrderApiServiceImpl(config);
        OrderInfo okOrderDetail;
        try {
            okOrderDetail = spotOrderApiService.getOrderByOrderId(toTradeSymbol(symbol), orderDetailReqDto.getOrderId());
        } catch (Throwable e) {
            log.error("远程调用OKEX查询订单失败：" + e.getMessage());
            throw new CallExchangeRemoteException("远程调用OKEX查询订单失败:" + orderDetailReqDto.getOrderId());
        }

        if (null == okOrderDetail || null == okOrderDetail.getOrder_id()) {
            log.error("远程调用OKEX查询订单失败：okOrderDetail为空");
            throw new CallExchangeRemoteException("远程调用OKEX查询订单失败:" + orderDetailReqDto.getOrderId());

        }
        return ResFactory.getInstance().success(transfer(okOrderDetail, symbol, orderDetailReqDto.getAmountDecimal(), spotOrderApiService));
    }

    @Override
    public Res<OrderListResData> orderList(Req<OrderDetailReqDto> orderDetailReqDtoReq) {

        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        String symbol = orderDetailReqDto.getSymbol();
        APIConfiguration config = new APIConfiguration(orderDetailReqDto.getApiKey(), orderDetailReqDto.getApiSecret(), orderDetailReqDto.getPassphrase());
        SpotOrderApiServiceImpl spotOrderApiService = new SpotOrderApiServiceImpl(config);
        List<OrderInfo> okOrderDetailList = null;
        try {
            //      System.out.println(spotOrderApiService.getFills(orderDetailReqDto.getOrderId(),toTradeSymbol(symbol),null,null,orderDetailReqDto.getLimit()+"").toString());
            okOrderDetailList = spotOrderApiService.getOrders(toTradeSymbol(symbol),  "7", null, null, "100");
            okOrderDetailList.addAll(spotOrderApiService.getOrders(toTradeSymbol(symbol),  "6", null, null, "100"));
        } catch (Exception e) {
            log.error("远程调用OKEX查询订单失败：", e);
            throw new CallExchangeRemoteException("远程调用OKEX查询订单失败:" + e.getMessage());
        }
        List<Long> ids = new ArrayList<>();//用来临时存储person的id
        List<OrderInfo> newList = okOrderDetailList.stream().filter(// 过滤去重
                v -> {
                    boolean flag = !ids.contains(v.getOrder_id());
                    ids.add(v.getOrder_id());
                    return flag;
                }
        ).collect(Collectors.toList());
        return ResFactory.getInstance().success(new OrderListResData(transferOrderListReturn(newList, symbol, orderDetailReqDto.getAmountDecimal(), spotOrderApiService)));
    }

    private String transferStatus(Integer status) {//ok没有部分撤销状态

        //查询状态范围，0-下单中、1-部分成交、2-已撤销、3-已成交、4-部分撤销、10-未成交、11-失败、13-申请撤单中、-1-所有
        if (status == TradeExchangeApiConstant.OrderStatus.INIT.code()) {//下单中
            return OKMarginConstant.OkStateEnum.ORDERING_STATUS.getType();
        } else if (status == TradeExchangeApiConstant.OrderStatus.PART.code()) {//部分成交
            return OKMarginConstant.OkStateEnum.PART_FILLED_STATUS.getType();
        } else if (status == TradeExchangeApiConstant.OrderStatus.CANCEL.code()) {//已撤销
            return OKMarginConstant.OkStateEnum.CANCELLED_STATUS.getType();
        } else if (status == TradeExchangeApiConstant.OrderStatus.DEAL.code()) {//已成交
            return OKMarginConstant.OkStateEnum.FILLED_STATUS.getType();
        } else if (status == TradeExchangeApiConstant.OrderStatus.COMMIT.code()) {//未成交
            return OKMarginConstant.OkStateEnum.OPEN_STATUS.getType();
        } else if (status == TradeExchangeApiConstant.OrderStatus.FAIL.code()) {//失败
            return OKMarginConstant.OkStateEnum.FAILURE_STATUS.getType();
        } else if (status == TradeExchangeApiConstant.OrderStatus.CANCEL_APPLY.code()) {//撤单申请中
            return OKMarginConstant.OkStateEnum.CANCELLING_STATUS.getType();
        } else {
            return "";
        }
    }

    @Override
    public Res<OrderListResData> orderListByPage(Req<OrderDetailReqDto> orderDetailReqDtoReq) {

        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        String symbol = orderDetailReqDto.getSymbol();
        APIConfiguration config = new APIConfiguration(orderDetailReqDto.getApiKey(), orderDetailReqDto.getApiSecret(), orderDetailReqDto.getPassphrase());
        SpotOrderApiServiceImpl spotOrderApiService = new SpotOrderApiServiceImpl(config);
        List<OrderInfo> okOrderDetailList = null;
        try {
            String startCondition = orderDetailReqDto.getStartCondition();
            String endCondition = orderDetailReqDto.getEndCondition();
            String size = "100";
            if (null != orderDetailReqDto.getLimit()) {
                 size = String.valueOf(orderDetailReqDto.getLimit());
            }
            if (orderDetailReqDto.getOrderStatus().code()==null) {
                String temp = "调用okV3,status=" + orderDetailReqDto.getOrderStatus().code() + ",返回信息为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM, temp);
            }
            String state = transferStatus(orderDetailReqDto.getOrderStatus().code());
            if (StringUtils.isBlank(state)) {
                String temp = "调用okV3,status=" + orderDetailReqDto.getOrderStatus().code() + ",返回信息为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM, temp);
            }

            okOrderDetailList = spotOrderApiService.getOrdersByPage(toTradeSymbol(symbol),  state, startCondition, endCondition, size);

        } catch (Exception e) {
            log.error("远程调用OKEX查询订单失败：", e);
            throw new CallExchangeRemoteException("远程调用OKEX查询订单失败:" + e.getMessage());
        }
        List<OrderDetailResDto> detailResDtos = transferOrderListReturn(okOrderDetailList, symbol, orderDetailReqDto.getAmountDecimal(), spotOrderApiService);


        int listSize = detailResDtos.size();
        if (listSize>0){
            String bigId=detailResDtos.get(0).getOrderId();
            String smallId=detailResDtos.get(listSize-1).getOrderId();
            return ResFactory.getInstance().success(new OrderListResData(detailResDtos,smallId,bigId));
        }

        return ResFactory.getInstance().success(new OrderListResData(detailResDtos,null,null));
    }
    /**
     * 获取最近的成交明细表。这个请求支持分页，并且按时间倒序排序和存储，最新的排在最前面。请参阅分页部分以获取第一页之后的其他记录。 本接口能查询最近3月的数据。
     *
     * @param myTradeReqDtoReq
     * @return
     */
    @Override
    public Res<MyTradeListResDto> getMyTrades(Req<MyTradeReqDto> myTradeReqDtoReq) {
        try {
            MyTradeReqDto myTradeReqDto = myTradeReqDtoReq.getData();

            String symbol = myTradeReqDto.getSymbol();
            if (StringUtils.isBlank(symbol)) {
                String temp = "调用 币安 查账户交易清单失败，失败原因：必填参数symbol为空";
                log.error(temp);
                throw new TradeExchangeApiException(temp);
            }

            APIConfiguration config = new APIConfiguration(myTradeReqDto.getApiKey(), myTradeReqDto.getApiSecret(), myTradeReqDto.getPassphrase());
            SpotOrderApiServiceImpl spotOrderApiService = new SpotOrderApiServiceImpl(config);
            List<Fills> fills = spotOrderApiService.getFills(myTradeReqDto.getOrderId(), toTradeSymbol(symbol), null, myTradeReqDto.getFromId() != null ? myTradeReqDto.getFromId() : null, myTradeReqDto.getLimit() + "");
            System.out.println(JSONObject.toJSONString(fills));
            int size = fills == null ? 0 : fills.size();

            MyTradeResDto tempTradeHistoryResDto = null;
            Fills fillItem = null;
            String buyerMaker = "";
            OrderSideEnum orderSide = null;
            TradeExchangeApiConstant.OrderRole orderRole = null;
            List<MyTradeResDto> trades = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                fillItem = fills.get(i);
                buyerMaker = fillItem.getSide();
                if ("sell".equals(buyerMaker)) {//卖
                    orderSide = OrderSideEnum.ASK;
                } else {//买
                    orderSide = OrderSideEnum.BID;
                }

                String role = fillItem.getExec_type();
                if ("T".equals(role)) {//卖
                    orderRole = TradeExchangeApiConstant.OrderRole.TAKER;
                } else {//买
                    orderRole = TradeExchangeApiConstant.OrderRole.MAKER;
                }
                tempTradeHistoryResDto = new MyTradeResDto(null, fillItem.getOrder_id().toString(), orderSide, new BigDecimal(fillItem.getSize()),
                        symbol, new BigDecimal(fillItem.getPrice()),
                        DateUtils.parse(getDateFormat(fillItem.getTimestamp())).getTime(),
                        String.valueOf(fillItem.getLedger_id()), fillItem.getFee(),orderRole);
                trades.add(tempTradeHistoryResDto);
            }
            MyTradeListResDto tradeHistoryListResDto = new MyTradeListResDto();
            tradeHistoryListResDto.setMyTradeResDtoList(trades);
            return ResFactory.getInstance().success(tradeHistoryListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 OKEX 查询账户交易清单异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, temp + throwable.getLocalizedMessage());

        }
    }

    public static String getDateFormat(String create_time) throws ParseException {

        String format = "";
        if (create_time != null && create_time != "NULL" && create_time != "") {
            //转换日期格式(将Mon Jun 18 2018 00:00:00 GMT+0800 (中国标准时间) 转换成yyyy-MM-dd)
            create_time = create_time.replace("Z", " UTC");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            Date d = sdf1.parse(create_time);//Mon Mar 06 00:00:00 CST 2017
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format = sdf.format(d);//2017-03-06
        }

        return format;
    }

    @Override
    public Res<OrderListResData> getOpenOrders(Req<OpenOrdersReqDto> ordersReqDtoReq) {
        OpenOrdersReqDto orderDetailReqDto = ordersReqDtoReq.getData();
        String symbol = orderDetailReqDto.getSymbol();
        APIConfiguration config = new APIConfiguration(orderDetailReqDto.getApiKey(), orderDetailReqDto.getApiSecret(), orderDetailReqDto.getPassphrase());
        SpotOrderApiServiceImpl spotOrderApiService = new SpotOrderApiServiceImpl(config);
        List<OrderInfo> okOrderDetailList = null;
        try {
            okOrderDetailList = spotOrderApiService.getPendingOrders(null, null, orderDetailReqDto.getLimit(), orderDetailReqDto.getTradeSymbol());
        } catch (Exception e) {
            log.error("远程调用OKEX获取所有未成交订单失败：", e);
            throw new CallExchangeRemoteException("远程调用OKEX获取所有未成交订单:" + e.getMessage());
        }
        return ResFactory.getInstance().success(new OrderListResData(transferOrderListReturn(okOrderDetailList, symbol, null, spotOrderApiService)));

    }

    @Override
    public Res<ResList<ExchAcctDeptWdralResDto>> harkWithdrawal(Req<HarkWithdrawalReqDto> harkWithdrawalReqDtoReq) {
        HarkWithdrawalReqDto harkWithdrawalReqDto = harkWithdrawalReqDtoReq.getData();
        Integer type = harkWithdrawalReqDto.getType();
        if(null == type){
            String temp = "调用 okex 查询充提币记录失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        Map<String,String> paramMap = new HashMap<>();
        String coinName = harkWithdrawalReqDto.getCoinName();
        if(StringUtils.isNotBlank(coinName)){
            paramMap.put("coinName",coinName.toLowerCase());
        }

        IOkexStockRestApi okexStockRestApi = new OkexStockRestApi(harkWithdrawalReqDto.getApiKey(), harkWithdrawalReqDto.getApiSecret(), harkWithdrawalReqDto.getPassphrase());
        if(type == ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_DEPOSIT){//充值
            String result = null;
            try{
                result = okexStockRestApi.depositHistory(paramMap);
            }catch (Throwable throwable){
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用充值历史查询异常");
            }

            Res res = isHarkWithdrawalOk(result,"查询充币记录");
            if(!res.isSuccess()){
                return res;
            }
            return turnDeposit(result);
        }else {//提现
            String result = null;
            try{
                result = okexStockRestApi.withdrawHistory(paramMap);
            }catch (Throwable throwable){
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用提现历史查询异常");
            }

            Res res = isHarkWithdrawalOk(result, "查询提币记录");
            if (!res.isSuccess()) {
                return res;
            }
            return turnWithdraw(result);
        }
    }

    @Override
    public Res<ResList<KLineResDto>> kline(Req<KlineReqDto> reqDtoReq) throws Throwable{
        KlineReqDto klineReqDto= reqDtoReq.getData();
        List<KLineResDto> resultList = new ArrayList<>();
        String instrument_id = klineReqDto.getSymbol();
        if(null == instrument_id){
            String temp = "调用 okex kline失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }
        String tradePeriod = null;
        String period= klineReqDto.getPeriod();
        if (StringUtils.isNotEmpty(period)){
            if ("1".equals(period)){
                tradePeriod="60";
            }else if ("3".equals(period)){
                tradePeriod="180";
            }else if ("5".equals(period)){
                tradePeriod="300";
            }else if ("15".equals(period)){
                tradePeriod="900";
            }else if ("30".equals(period)){
                tradePeriod="1800";
            }else if ("60".equals(period)){
                tradePeriod="3600";
            }else if ("120".equals(period)){
                tradePeriod="7200";
            }else if ("240".equals(period)){
                tradePeriod="14400";
            }else if ("360".equals(period)){
                tradePeriod="21600";
            }else if ("720".equals(period)){
                tradePeriod="43200";
            }else if ("1440".equals(period)){
                tradePeriod="86400";
            }else if ("10080".equals(period)){
                tradePeriod="604800";
            }else {
                String temp = "调用 okex kline失败，时间粒度不支持";
                log.warn(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }
        }

        APIConfiguration config = new APIConfiguration();
        SpotOrderApiServiceImpl spotOrderApiService = new SpotOrderApiServiceImpl(config);
        String result = spotOrderApiService.kline(instrument_id.replace("/", "-").toUpperCase(),klineReqDto.getStartDate(),klineReqDto.getEndDate(),tradePeriod);
        JSONArray resultJSONArr = JSONArray.parseArray(result );
        JSONArray childArr = null;
        if(resultJSONArr.size()>0){
            for(int i=0;i<resultJSONArr.size();i++){
                KLineResDto lineSwapResDto = new KLineResDto();
                lineSwapResDto.setSymbol(instrument_id);
                childArr = resultJSONArr.getJSONArray(i);
                lineSwapResDto.setTimestamp(DateUtils.parse(childArr.getString(0), DateUtils.FORMAT_DATE_TIME_ISO8601));
                BigDecimal open = childArr.getBigDecimal(1);
                if(null != open){
                    open = open.stripTrailingZeros();
                }
                BigDecimal high = childArr.getBigDecimal(2);
                if(null != high){
                    high = high.stripTrailingZeros();
                }
                BigDecimal low = childArr.getBigDecimal(3);
                if(null != low){
                    low = low.stripTrailingZeros();
                }
                BigDecimal close = childArr.getBigDecimal(4);
                if(null != close){
                    close = close.stripTrailingZeros();
                }
                BigDecimal volume = childArr.getBigDecimal(5);
                if(null != volume){
                    volume = volume.stripTrailingZeros();
                }

                lineSwapResDto.setClose(close);
                lineSwapResDto.setHighPrice(high);lineSwapResDto.setLowPrice(low);
                lineSwapResDto.setVolume(volume);lineSwapResDto.setOpen(open);
                resultList.add(lineSwapResDto);
            }
        }
        return ResFactory.getInstance().successList(resultList);
    }
    @Override
    public Res<WithdrawalResDto> withdraw(Req<WithdrawalReqDto> withdrawalReqDtoReq) {
        Res toTransferRes = null;
        WithdrawalReqDto withdrawalReqDto = null;
        BigDecimal outAmount = null;

        if(null == withdrawalReqDtoReq){
            String temp = "调用 okex 提现 失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        withdrawalReqDto = withdrawalReqDtoReq.getData();
        if(null == withdrawalReqDto){
            String temp = "调用 okex 提现 失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        String coinName = withdrawalReqDto.getCoinName();
        //提币地址
        String address = withdrawalReqDto.getAddress();
        BigDecimal amountBig = withdrawalReqDto.getReceivedAmount();
        String tradePwd = withdrawalReqDto.getTradePwd();
        if(StringUtils.isBlank(coinName)
                ||StringUtils.isBlank(address)
                ||null == amountBig
                || StringUtils.isBlank(tradePwd)){
            String temp = "调用 okex 提现 失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }


//            参数名	参数类型	是否必须	描述
//            currency	String	是	币种
//            amount	String	是	数量
//            destination	String	是	提币到
//                                    2:OKCoin国际; 3:OKEx; 4:数字货币地址; 68:币全CoinAll; 69:OKGAEX;
//                                    70:2100BIT; 71:OCNex; 72:咖啡交易所; 73:OKTop; 75:BBang;
//                                    76:TokenClub; 79:VREX; 80:币窗; 81:PAICLUB; 82:淘币网;
//                                    83: GoTop; 84:ABull; 85:LETDAX; 86:爵爷; 87:币牛牛交易所;
//                                    88:YAOEOS; 89:TOKR; 90:4A交易平台; 91:币六六; 92:Vipexc;
//                                    93:imex; 95:DEX.HK; 96:BigEx; 97:StarEX; 98:比特上海;
//                                    99:LBL.market; 100:CoinGod; 101:WOOT; 102:币小龙Coinxiaolong; 103:VBEX;
//                                    104:MY1EX; 105:Bitfinance; 106:DBEX 迪交所; 107:LOVEUEX; 108:99EX-久币网;
//                                    109:Futures Crypto; 110:Exx BK; 111:MyCoin; 112:ROCKEX; 113:以太坊交易所;
//                                    114:COINZOZ; 115:ECGEX; 116:雷爵爾交易所; 117:EXVALUE; 118:TradeDee;
//                                    119:HHEX; 120:ERCSTO; 121:Tenspace; 122:APNEX; 123:HelloExc;
//                                    124:A9CPS; 125:PICKCOIN; 126:WeBit; 127:abv138; 128:币商所;
//                                    129:66交易所; 130:CBK; 131:Coinpop99; 132:Chain Bay; 133:Longwinex 长胜网;
//                                    134:BE.TOP; 135:CRYPTEX GLOBAL; 136:FT交易所; 137:牛币网; 138:ybitex;
//                                    139:大圣交易所; 140:EXFINEX; 141:aiEx; 142:EXIPFS; 143:盘行;
//                                    144:PeBank; 145:YES交易所; 146:币栈; 147:VitBlock; 148:CPC;
//                                    149:KGcoin; 150:京东交易所; 151:Korbot Exchange; 152:xFutures; 153:Float SV;
//                                    154:Y83; 155:雾交所; 156:KoKoEx; 157:MerXader; 158:理想国;
//                                    159:TEX IO; 160:贝壳国际交易所; 161:币爱交易所; 162:COEX; 163:BOOMEX;
//                                    164:艾普; 165:币火严选; 166:Token Coin; 167:实通所; 168:诺贝尔;
//                                    169:Oakiss; 170:魅幻城; 171:ETBBpro; 172:币可富; 173:VVCOIN)
//            to_address	String	是	认证过的数字货币地址、邮箱或手机号。某些数字货币地址格式为:地址+标签，例：ARDOR-7JF3-8F2E-QUWZ-CAN7F:123456
//            trade_pwd	String	是	交易密码
//            fee	String	是	网络手续费≥0。提币到OKCoin国际或OKEx免手续费，请设置为0。提币到数字货币地址所需网络手续费可通过提币手续费接口查询

        IOkexStockRestApi okexStockRestApi = new OkexStockRestApi(withdrawalReqDto.getApiKey(), withdrawalReqDto.getApiSecret(), withdrawalReqDto.getPassphrase());

        String coinNameUpper = coinName.toUpperCase();
        /**
         * 调用币种查询接口查找提现手续费
         */
        Map<String, JSONObject> withdrawalFeeMap = this.withdrawalFee(okexStockRestApi);
        JSONObject childFeeJson = withdrawalFeeMap.get(coinNameUpper);
        //手续费JSONObject
        BigDecimal withdrawsFee = null;
        if (null != childFeeJson) {
            //最小提币手续费
            withdrawsFee = childFeeJson.getBigDecimal("min_fee");
            if (null != withdrawsFee) {
                withdrawsFee = withdrawsFee.stripTrailingZeros();
            }
        }

        if(null == withdrawsFee){
            String temp = "调用 okex 提现失败，手续费查询为空";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
        }

        /**
         * 做资金划转
         */
        outAmount = withdrawsFee.add(amountBig);
        toTransferRes = toTransfer(withdrawalReqDto.getApiKey(),withdrawalReqDto.getApiSecret(),
                withdrawalReqDto.getPassphrase(),withdrawalReqDto.getCoinName(),
                outAmount,"1","6");
        if(!toTransferRes.isSuccess()){
            String temp = "调用 okex 提现失败，做资金划转失败";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,"做资金划转失败");
        }

        boolean toReTransfer = false;

        /**
         * 做提现处理
         */
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("currency",coinName.toLowerCase());
        paramMap.put("amount",amountBig.toPlainString());
        paramMap.put("destination","4");
        paramMap.put("to_address",address);
        paramMap.put("trade_pwd",tradePwd);
        paramMap.put("fee",withdrawsFee.toPlainString());

        String result = "";
        try {
            result = okexStockRestApi.withdraw(paramMap);

//            成功：
//            {
//                "amount":"0.1",
//                "withdrawal_id":"67485",
//                "currency":"btc",
//                "result":true
//            }
            if(StringUtils.isBlank(result)){
                String temp = "调用 okex 提现 失败，交易所返回空";
                log.warn(temp);
                if(!toReTransfer && null != toTransferRes && toTransferRes.isSuccess()){
                    reToTrans(withdrawalReqDto.getApiKey(),withdrawalReqDto.getApiSecret(),
                            withdrawalReqDto.getPassphrase(),withdrawalReqDto.getCoinName(),
                            outAmount);
                    toReTransfer = true;
                }
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject resultJSON = JSONObject.parseObject(result);
            if(!resultJSON.containsKey("withdrawal_id")){
                String temp = "调用 okex 提现 失败，交易所返回:"+result;
                log.warn(temp);
                if(!toReTransfer && null != toTransferRes && toTransferRes.isSuccess()){
                    reToTrans(withdrawalReqDto.getApiKey(),withdrawalReqDto.getApiSecret(),
                            withdrawalReqDto.getPassphrase(),withdrawalReqDto.getCoinName(),
                            outAmount);
                    toReTransfer = true;
                }
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
            }

            String withdrawalId = resultJSON.getString("withdrawal_id");
            if(StringUtils.isBlank(withdrawalId)){
                String temp = "调用 okex 提现 失败，交易所返回:"+result;
                log.warn(temp);
                if(!toReTransfer && null != toTransferRes && toTransferRes.isSuccess()){
                    reToTrans(withdrawalReqDto.getApiKey(),withdrawalReqDto.getApiSecret(),
                            withdrawalReqDto.getPassphrase(),withdrawalReqDto.getCoinName(),
                            outAmount);
                    toReTransfer = true;
                }
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
            }

            WithdrawalResDto withdrawalResDto = new WithdrawalResDto();
            withdrawalResDto.setThirdId(withdrawalId);
            return ResFactory.getInstance().success(withdrawalResDto);
        } catch (Throwable throwable) {
            String temp = "调用 okex 提现 异常，异常信息：";
            log.error(temp, throwable);
            if(!toReTransfer && null != toTransferRes && toTransferRes.isSuccess()){
                reToTrans(withdrawalReqDto.getApiKey(),withdrawalReqDto.getApiSecret(),
                        withdrawalReqDto.getPassphrase(),withdrawalReqDto.getCoinName(),
                        outAmount);
                toReTransfer = true;
            }
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }
    }

    /**
     * 做资金划转的反向操作
     * @param apiKey
     * @param apiSecret
     * @param passphrase
     * @param coinName
     * @param amount
     */
    private void reToTrans(String apiKey,String apiSecret,
                           String passphrase,String coinName,
                           BigDecimal amount){
        toTransfer(apiKey,apiSecret,
                passphrase,coinName,
                amount,"6","1");
    }

    /**
     * 做资金划转
     * @param apiKey
     * @param apiSecret
     * @param passphrase
     * @param coinName
     * @param amount
     * @return
     */
    private Res toTransfer(String apiKey,String apiSecret,
                           String passphrase,String coinName,
                           BigDecimal amount,String from,String to){
        /**
         * 做资金划转，将币币账户的余额划转到资金账户
         */
        TransferReqDto transferReqDto = new TransferReqDto();
        transferReqDto.setApiKey(apiKey);
        transferReqDto.setApiSecret(apiSecret);
        transferReqDto.setPassphrase(passphrase);
        transferReqDto.setCoinName(coinName);
        transferReqDto.setFrom(from);
        transferReqDto.setTo(to);
        transferReqDto.setAmount(amount);
        Req<TransferReqDto> transferReqDtoReq = ReqFactory.getInstance().createReq(transferReqDto);
        Res<TransferResDto> transferResDtoRes = this.transfer(transferReqDtoReq);
        return transferResDtoRes;
    }

    private Res isHarkWithdrawalOk(String result,String desc){
        log.info(result);
        if(StringUtils.isBlank(result)){
            String temp = "调用okex"+desc+"失败：返回结果为空";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }

        if(!result.startsWith("[")){
            String temp = "调用okex"+desc+"失败，返回信息:"+result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
        }

        return ResFactory.getInstance().success(null);
    }


    /**
     * 将查询结果转换
     * @param result
     * @return
     */
    private Res<ResList<ExchAcctDeptWdralResDto>> turnWithdraw(String result){
//        成功：
//        [{
//            "amount": "0.01000000",
//            "withdrawal_id": "4884381",
//            "fee": "0.01000000eth",
//            "txid": "0x7a07a5cbfdbf855a962a3bd435905d1a1b88df1fff2461e9869855498b70b67d",
//            "currency": "ETH",
//            "from": "woaiblock888@163.com",
//            "to": "0xad4e56ae3a16a7453416f73478b64a24aa5437df",
//            "timestamp": "2019-10-26T07:54:31.000Z",
//            "status": "2"
//        }]

//        amount	    String	数量
//        timestamp	    String	提币申请时间
//        from	        String	提币地址(如果收币地址是OKEx平台地址，则此处将显示用户账户)
//        to	        String	收币地址
//        tag	        String	部分币种提币需要标签，若不需要则不返回此字段
//        payment_id	String	部分币种提币需要此字段，若不需要则不返回此字段
//        txid	        String	提币哈希记录(内部转账将不返回此字段)
//        fee	        String	提币手续费和对应币种，如0.00000009btc
//        status	    String	提现状态（-3:撤销中;-2:已撤销;-1:失败;0:等待提现;1:提现中;2:已汇出;3:邮箱确认;4:人工审核中5:等待身份认证）
//        withdraw_id	String	提币申请ID

        try{

            List<ExchAcctDeptWdralResDto> resultList = new ArrayList<>();
            ExchAcctDeptWdralResDto exchAcctDeptWdralResDto = null;

            String coinName = null;

            BigDecimal amount = null;//数量

            String txId = null;//提币哈希记录

            String address = null;//地址

            Date applyTime = null;//提申请时间

            Integer status = null;//状态，1-申请中、2-已完成、3-已取消、4-失败


            String dataTimestamp = null;
            Integer dataStatus = null;
            String thirdId = null;


            JSONArray jsonArray = JSONArray.parseArray(result);
            JSONObject dataJSON = null;
            int size = jsonArray == null?0:jsonArray.size();
            for(int i=0;i<size;i++){
                dataJSON = jsonArray.getJSONObject(i);
                dataTimestamp = dataJSON.getString("timestamp");
                amount = dataJSON.getBigDecimal("amount");
                txId = dataJSON.getString("txid");
                address = dataJSON.getString("to");
                thirdId = dataJSON.getString("withdrawal_id");

                applyTime = null;
                if(StringUtils.isNotBlank(dataTimestamp)){
                    applyTime = DateUtils.parse(dataTimestamp, DateUtils.FORMAT_DATE_TIME_ISO8601);
                }

                dataStatus = dataJSON.getInteger("status");
                status = null;
                if(null != dataStatus){
                    status = OkexWithDrawStatusEnum.getStatus(dataStatus);
                }

//                String thirdId, String coinName,
//                BigDecimal amount, String txId, String address,
//                Integer deptWdralType, BigDecimal fee, Date applyTime,
//                Integer status
                exchAcctDeptWdralResDto = ExchAcctDeptWdralResDto.getInstance(thirdId,coinName,amount,txId,address,
                        ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_WITHDRAWAL,null,applyTime, status,null);

                resultList.add(exchAcctDeptWdralResDto);
            }

            return ResFactory.getInstance().successList(resultList);
        }catch (Throwable throwable){
            String temp = "查找 okex 查找 提币 记录异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, result);
        }

    }

    /**
     * 将查询结果转换
     * @param result
     * @return
     */
    private Res<ResList<ExchAcctDeptWdralResDto>> turnDeposit(String result){

//        成功：
//        [{
//            "amount": "0.20000000",
//            "txid": "0x11a1f15b2150c9ff8af2bf840a91de78966f4897716c0438699a0312727bd620",
//            "currency": "ETH",
//            "from": "",
//            "to": "0x26e82fff44f6a6aa9e6ac7bf8194d5e542186780",
//            "timestamp": "2019-10-14T10:31:30.000Z",
//            "status": "2"
//        }, {
//            "amount": "0.20000000",
//            "txid": "0x1334fc6d4d2301e98f0bd0aeda7cd6eb841f3ef5a62969ff21af5a97112f6ab8",
//            "currency": "ETH",
//            "from": "",
//            "to": "0x26e82fff44f6a6aa9e6ac7bf8194d5e542186780",
//            "timestamp": "2019-10-14T10:31:30.000Z",
//            "status": "2"
//        }]
        try {
            List<ExchAcctDeptWdralResDto> resultList = new ArrayList<>();
            ExchAcctDeptWdralResDto exchAcctDeptWdralResDto = null;

            String coinName = null;

            BigDecimal amount = null;//数量

            String txId = null;//提币哈希记录

            String address = null;//地址

            Date applyTime = null;//提申请时间

            Integer status = null;//状态，1-申请中、2-已完成、3-已取消、4-失败


            String dataFrom = null;
            String dataTo = null;
            String dataTimestamp = null;
            Integer dataStatus = null;

            JSONArray resultArr = JSONArray.parseArray(result);
            JSONObject dataJSON = null;
            int size = resultArr == null?0:resultArr.size();
            for(int i=0;i<size;i++){
                dataJSON = resultArr.getJSONObject(i);
                amount = dataJSON.getBigDecimal("amount");
                txId = dataJSON.getString("txid");
                coinName = dataJSON.getString("currency");

                dataFrom = dataJSON.getString("from");
                if (StringUtils.isNotBlank(dataFrom)) {
                    address = dataFrom;
                }

                dataTo = dataJSON.getString("to");
                if (StringUtils.isNotBlank(dataTo)) {
                    address = dataTo;
                }
                dataTimestamp = dataJSON.getString("timestamp");
                applyTime = null;
                if(StringUtils.isNotBlank(dataTimestamp)){
                    applyTime = DateUtils.parse(dataTimestamp, DateUtils.FORMAT_DATE_TIME_ISO8601);
                }

                dataStatus = dataJSON.getInteger("status");
                status = null;
                if(null != dataStatus){
                    status = OkexDepositStatusEnum.getStatus(dataStatus);
                }

//                String thirdId, String coinName,
//                BigDecimal amount, String txId, String address,
//                Integer deptWdralType, BigDecimal fee, Date applyTime,
//                Integer status
                exchAcctDeptWdralResDto = ExchAcctDeptWdralResDto.getInstance(null,coinName,amount,txId,address,
                ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_DEPOSIT,null,applyTime, status,null);

                resultList.add(exchAcctDeptWdralResDto);
            }
            return ResFactory.getInstance().successList(resultList);
        }catch (Throwable throwable){
            String temp = "查找 okex 查找 充币 记录异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, result);
        }
    }

    @Override
    public Res<SymbolInfoListResDto> getSymbolInfo(Req<SymbolInfoReqDto> symbolInfoReqDtoReq) {
        try {
            if (null == symbolInfoReqDtoReq
                    || null == symbolInfoReqDtoReq.getData()) {
                String temp = "调用 okex 查询 币对 信息失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            SymbolInfoReqDto symbolInfoReqDto = symbolInfoReqDtoReq.getData();

            IOkexStockRestApi restApi = new OkexStockRestApi();
            String instruments = restApi.instruments();

            if (StringUtils.isBlank(instruments)) {
                String temp = "查找 okex 查找 币对信息 失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            if (!instruments.startsWith("[")) {
                String temp = "查找 okex 查找 币对信息 记录失败,第三方返回:" + instruments;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            List<SymbolInfoResDto> symbolInfoResDtoList = new ArrayList<>();
            SymbolInfoResDto symbolInfoResDto = null;

            String symbol = null;
            JSONObject pairInfo = null;
            JSONArray resultJSONArray = JSONArray.parseArray(instruments);
            int size = resultJSONArray == null ? 0 : resultJSONArray.size();
            for (int i = 0; i < size; i++) {
                pairInfo = resultJSONArray.getJSONObject(i);
                String baseName = pairInfo.getString("base_currency");
                String quoteName = pairInfo.getString("quote_currency");
                symbol = baseName + "/" + quoteName;

                BigDecimal size_increment = pairInfo.getBigDecimal("size_increment");
                BigDecimal tick_size = pairInfo.getBigDecimal("tick_size");
                BigDecimal min_size = pairInfo.getBigDecimal("min_size");

                Integer basePrecision = size_increment.stripTrailingZeros().scale();//货币精度
                Integer quotePrecision = tick_size.stripTrailingZeros().scale();//钱币精度
                BigDecimal baseLeast = min_size.stripTrailingZeros();

                symbolInfoResDto = new SymbolInfoResDto();
                symbolInfoResDto.setSymbol(symbol);
                symbolInfoResDto.setStatus(ExchangeConstant.SYMBOL_STATUS_ON);
                symbolInfoResDto.setQuoteName(quoteName);
                symbolInfoResDto.setBaseName(baseName);
                symbolInfoResDto.setBaseLeast(baseLeast);
                symbolInfoResDto.setQuoteLeast(null);
                symbolInfoResDto.setBasePrecision(basePrecision);
                symbolInfoResDto.setQuotePrecision(quotePrecision);
                symbolInfoResDtoList.add(symbolInfoResDto);
            }


//            成功：
//            [
//                {
//                    "base_currency": "BCH",
//                    "instrument_id": "BCH-BTC",
//                    "min_size": "0.01",
//                    "quote_currency": "BTC",
//                    "size_increment": "0.0001",
//                    "tick_size": "0.00001"
//                },
//                {
//                    "base_currency": "BSV",
//                    "instrument_id": "BSV-BTC",
//                    "min_size": "0.01",
//                    "quote_currency": "BTC",
//                    "size_increment": "0.0001",
//                    "tick_size": "0.000001"
//                },
//                {
//                    "base_currency": "DASH",
//                    "instrument_id": "DASH-BTC",
//                    "min_size": "0.001",
//                    "quote_currency": "BTC",
//                    "size_increment": "0.000001",
//                    "tick_size": "0.00001"
//                }
//            ]

            SymbolInfoListResDto symbolInfoListResDto = new SymbolInfoListResDto();
            symbolInfoListResDto.setExchangeCode(symbolInfoReqDto.getExchCode());
            symbolInfoListResDto.setSymbolInfoResDtoList(symbolInfoResDtoList);
            log.debug("调用 okex 查找 交易对信息 记录 返回结果：" + JSONObject.toJSONString(symbolInfoListResDto));
            return ResFactory.getInstance().success(symbolInfoListResDto);
        } catch (Throwable throwable) {
            String temp = "查找 okex 查找 交易对信息 记录异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    private Map<String, JSONObject> withdrawalFee(IOkexStockRestApi restApi){
        Map<String, JSONObject> withdrawalFeeMap = new HashMap<>();
        try {
            String withdrawalFee = restApi.withdrawalFee(null);
//            成功：
//            [{
//                "min_fee": "0.00050000",
//                "currency": "BTC",
//                "max_fee": "0.00100000"
//            }, {
//                "min_fee": "0.00100000",
//                "currency": "LTC",
//                "max_fee": "0.00200000"
//            }]

            if (StringUtils.isBlank(withdrawalFee)) {
                String temp = "查找 okex 查找 币种提现手续费 信息失败,返回结果为空";
                log.warn(temp);
                return withdrawalFeeMap;
//                throw new TradeExchangeApiException(temp);
            }

            if (!withdrawalFee.startsWith("[")) {
                String temp = "查找 okex 查找 币种提现手续费 信息记录失败,第三方返回:" + withdrawalFee;
                log.warn(temp);
                return withdrawalFeeMap;
//                throw new TradeExchangeApiException(temp);
            }

            String temp1 = "查找 okex 查找 币种提现手续费 信息记录,第三方返回:" + withdrawalFee;
            log.warn(temp1);

            JSONArray withdrawalFeeArr = JSONArray.parseArray(withdrawalFee);
            //将手续费数据放入MAP中
            JSONObject childFeeJson = null;
            String currency = null;
            int withdrawalFeeSize = withdrawalFeeArr == null ? 0 : withdrawalFeeArr.size();
            for (int i = 0; i < withdrawalFeeSize; i++) {
                childFeeJson = withdrawalFeeArr.getJSONObject(i);
                currency = childFeeJson.getString("currency");
                withdrawalFeeMap.put(currency, childFeeJson);
            }

        }catch (Throwable throwable){
            log.error("调用手续费查询接口异常，异常信息：",throwable);
        }
        return withdrawalFeeMap;
    }

    @Override
    public Res<CoinInfoListResDto> getCoinInfo(Req<CoinInfoReqDto> coinInfoReqDtoReq) {
        try {
            if (null == coinInfoReqDtoReq
                    || null == coinInfoReqDtoReq.getData()) {
                String temp = "调用 okex 查询 币种 信息失败，失败原因：必填参数为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_PARAMETER);
            }

            CoinInfoReqDto coinInfoReqDto = coinInfoReqDtoReq.getData();
            IOkexStockRestApi restApi = new OkexStockRestApi(coinInfoReqDto.getApiKey(), coinInfoReqDto.getApiSecret(), coinInfoReqDto.getPassphrase());

            //查询所有币种列表信息
            String currencies = restApi.currencies();

//            成功：
//            [{
//                "name": "",
//                "currency": "BTC",
//                "can_withdraw": "1",
//                "can_deposit": "1",
//                "min_withdrawal": "0.0100000000000000"
//            }, {
//                "name": "",
//                "currency": "LTC",
//                "can_withdraw": "1",
//                "can_deposit": "1",
//                "min_withdrawal": "0.1000000000000000"
//            }]

            if (StringUtils.isBlank(currencies)) {
                String temp = "查找 okex 查找 币种列表 信息失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            if (!currencies.startsWith("[")) {
                String temp = "查找 okex 查找 币种列表 信息记录失败,第三方返回:" + currencies;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            Map<String, JSONObject> withdrawalFeeMap = this.withdrawalFee(restApi);

            List<CoinInfoResDto> coinInfoResDtoList = new ArrayList<>();
            CoinInfoResDto coinInfoResDto = null;
            JSONArray currenciesArr = JSONArray.parseArray(currencies);
            String currency = null;
            JSONObject childFeeJson = null;

            JSONObject childCurrenciesJson = null;
            String aliasName = null;
            BigDecimal withdrawsLeast = null;
            BigDecimal withdrawsFee = null;
            Integer status = null;

            Integer canDeposit = null;
            Integer canWithdraw = null;
            int currenciesSize = currenciesArr == null ? 0 : currenciesArr.size();
            for (int i = 0; i < currenciesSize; i++) {
                childCurrenciesJson = currenciesArr.getJSONObject(i);
                currency = childCurrenciesJson.getString("currency");
                withdrawsFee = null;
                aliasName = childCurrenciesJson.getString("name");
                withdrawsLeast = childCurrenciesJson.getBigDecimal("min_withdrawal");
                if (withdrawsLeast != null) {
                    withdrawsLeast = withdrawsLeast.stripTrailingZeros();
                }

                canDeposit = childCurrenciesJson.getInteger("can_deposit");
                canWithdraw = childCurrenciesJson.getInteger("can_withdraw");

                status = ExchangeConstant.COINSTSTUS_DEPOSIT_WITHDRAW_CAN;
                //该币不可充提
                if (canDeposit != OKConstant.STSTUS_DEPOSIT_WITHDRAW_CAN
                        && canWithdraw != OKConstant.STSTUS_DEPOSIT_WITHDRAW_CAN) {
                    status = ExchangeConstant.COINSTSTUS_DEPOSIT_WITHDRAW_CANNOT;
                } else if (canDeposit != OKConstant.STSTUS_DEPOSIT_WITHDRAW_CAN) {
                    //不可充值，只可提币
                    status = ExchangeConstant.COINSTSTUS_WITHDRAW_CAN;
                } else if (canWithdraw != OKConstant.STSTUS_DEPOSIT_WITHDRAW_CAN) {
                    //不可提币，只可充币
                    status = ExchangeConstant.COINSTSTUS_DEPOSIT_CAN;
                }

                //手续费实体
                childFeeJson = withdrawalFeeMap.get(currency);
                //手续费JSONObject
                if (null != childFeeJson) {
                    //最小提币手续费
                    withdrawsFee = childFeeJson.getBigDecimal("min_fee");
                    if (null != withdrawsFee) {
                        withdrawsFee = withdrawsFee.stripTrailingZeros();
                    }
                }


//                参数名	         参数类型	    描述
//                currency	      String	币种名称，如btc
//                name	          String	币种中文名称，不显示则无对应名称
//                can_deposit	  String	是否可充值，0表示不可充值，1表示可以充值
//                can_withdraw	  String	是否可提币，0表示不可提币，1表示可以提币
//                min_withdrawal  String	币种最小提币量

                coinInfoResDto = new CoinInfoResDto();
                coinInfoResDto.setCoinName(currency);
                coinInfoResDto.setAliasName(aliasName);
                coinInfoResDto.setWithdrawsLeast(withdrawsLeast);
                coinInfoResDto.setWithdrawsFee(withdrawsFee);
                coinInfoResDto.setWithdrawsFeeType(1);
                coinInfoResDto.setOperationStatus(status);
                coinInfoResDtoList.add(coinInfoResDto);
            }

            CoinInfoListResDto coinInfoListResDto = new CoinInfoListResDto();
            coinInfoListResDto.setCoinInfoResDtoList(coinInfoResDtoList);
            log.debug("调用 okex 查找 币种信息 记录 返回结果：" + JSONObject.toJSONString(coinInfoListResDto));
            return ResFactory.getInstance().success(coinInfoListResDto);
        } catch (Throwable throwable) {
            String temp = "查找 okex 查找 币种信息 记录异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<FullTickerListResDto> fullTickers(Req<FullTickerReqDto> fullTickerReqDtoReq) {
        try {
            if(null == fullTickerReqDtoReq
                    || null == fullTickerReqDtoReq.getData()){
                String temp = "调用 okex 查询 全部ticker 信息失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            IOkexStockRestApi restApi = new OkexStockRestApi(null,null,null);
            String allTickers = restApi.allTickers();

//            成功：
//            [{
//                "best_ask": "0.006686",
//                "best_bid": "0.006685",
//                "instrument_id": "LTC-BTC",
//                "product_id": "LTC-BTC",
//                "last": "0.006683",
//                "ask": "0.006686",
//                "bid": "0.006685",
//                "open_24h": "0.006892",
//                "high_24h": "0.006905",
//                "low_24h": "0.006594",
//                "base_volume_24h": "257488.593601",
//                "timestamp": "2019-08-30T11:28:06.564Z",
//                "quote_volume_24h": "1732.36244"
//            },
//            {
//                "best_ask": "0.01777",
//                "best_bid": "0.01776",
//                "instrument_id": "ETH-BTC",
//                "product_id": "ETH-BTC",
//                "last": "0.01777",
//                "ask": "0.01777",
//                "bid": "0.01776",
//                "open_24h": "0.01785",
//                "high_24h": "0.01797",
//                "low_24h": "0.01737",
//                "base_volume_24h": "85843.19609",
//                "timestamp": "2019-08-30T11:28:19.948Z",
//                "quote_volume_24h": "1518.83263"
//            }]


            if(StringUtils.isBlank(allTickers)){
                String temp = "查找 okex 查找 全部交易对ticker 信息失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            if(!allTickers.startsWith("[")){
                String temp = "查找 okex 查找 全部交易对ticker 信息记录失败,第三方返回:"+allTickers;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }



            JSONArray allTickersArr = JSONArray.parseArray(allTickers);

            List<FullTickerResDto> fullTickerResDtoList = new ArrayList<>();
            FullTickerResDto fullTickerResDto = null;


            //将手续费数据放入MAP中
            JSONObject childTickerJson = null;

            BigDecimal priceChangePercent = null;
            BigDecimal lastPrice = null;
            BigDecimal volume = null;//24小时成交量

//            "best_ask": "0.01777",
//                "best_bid": "0.01776",
//                "instrument_id": "ETH-BTC",
//                "product_id": "ETH-BTC",
//                "last": "0.01777",
//                "ask": "0.01777",
//                "bid": "0.01776",
//                "open_24h": "0.01785",
//                "high_24h": "0.01797",
//                "low_24h": "0.01737",
//                "base_volume_24h": "85843.19609",
//                "timestamp": "2019-08-30T11:28:19.948Z",
//                "quote_volume_24h": "1518.83263"

            String tradeSymbol = null;
            BigDecimal openBig = null;
            BigDecimal dividend = null;
            BigDecimal lowPrice = null;
            BigDecimal highPrice = null;

            int allTickersSize = allTickersArr == null?0:allTickersArr.size();
            for(int i=0;i<allTickersSize;i++){
                childTickerJson = allTickersArr.getJSONObject(i);
                fullTickerResDto = new FullTickerResDto();
                openBig = childTickerJson.getBigDecimal("open_24h");
                lastPrice = childTickerJson.getBigDecimal("last");
                if(null != lastPrice){
                    lastPrice = lastPrice.stripTrailingZeros();
                }
                tradeSymbol = childTickerJson.getString("instrument_id");

                //日涨跌幅乘100
                if(null != openBig && null != lastPrice && CalculateUtil.compareTo(openBig,BigDecimal.ZERO)>0){
                    dividend = (lastPrice.subtract(openBig)).multiply(new BigDecimal(100L));
                    priceChangePercent = CalculateUtil.divide(dividend, openBig, 2, BigDecimal.ROUND_FLOOR);
                }else{
                    priceChangePercent = BigDecimal.ZERO;
                }

                lowPrice = childTickerJson.getBigDecimal("low_24h");
                if(null != lowPrice){
                    lowPrice = lowPrice.stripTrailingZeros();
                }

                highPrice = childTickerJson.getBigDecimal("high_24h");
                if(null != highPrice){
                    highPrice = highPrice.stripTrailingZeros();
                }

                String symbol = this.symbol(tradeSymbol);

                volume = childTickerJson.getBigDecimal("base_volume_24h");
                fullTickerResDto.setLast(lastPrice);
                fullTickerResDto.setPriceChangePercent(priceChangePercent);
                fullTickerResDto.setVolume24h(volume);
                fullTickerResDto.setSymbol(symbol);
                fullTickerResDto.setLowPrice(lowPrice);
                fullTickerResDto.setHighPrice(highPrice);
                fullTickerResDtoList.add(fullTickerResDto);

                if(symbol.endsWith("/USDK")
                        && (symbol.startsWith("BTC/")
                            || symbol.startsWith("USDT/")
                            || symbol.startsWith("ETH/"))){
                    FullTickerResDto tempFullTickerResDto = new FullTickerResDto();
                    String[] symbolArr = symbol.split("/");
                    String newSymbol = symbolArr[1]+"/"+symbolArr[0];
                    tempFullTickerResDto.setLast(CalculateUtil.divide(new BigDecimal("1"),lastPrice,18));
                    tempFullTickerResDto.setSymbol(newSymbol);
                    fullTickerResDtoList.add(tempFullTickerResDto);
                }

            }

            FullTickerResDto tempFullTickerResDto = new FullTickerResDto();
            String newSymbol ="OKDK/BTC";
            tempFullTickerResDto.setLast(BigDecimal.ZERO);
            tempFullTickerResDto.setSymbol(newSymbol);
            fullTickerResDtoList.add(tempFullTickerResDto);

            tempFullTickerResDto = new FullTickerResDto();
            newSymbol ="OKDK/USDT";
            tempFullTickerResDto.setLast(BigDecimal.ZERO);
            tempFullTickerResDto.setSymbol(newSymbol);
            fullTickerResDtoList.add(tempFullTickerResDto);

            tempFullTickerResDto = new FullTickerResDto();
            newSymbol ="OKDK/ETH";
            tempFullTickerResDto.setLast(BigDecimal.ZERO);
            tempFullTickerResDto.setSymbol(newSymbol);
            fullTickerResDtoList.add(tempFullTickerResDto);

            FullTickerListResDto fullTickerListResDto = new FullTickerListResDto();
            fullTickerListResDto.setFullTickerResDtoList(fullTickerResDtoList);

            log.debug("调用 okex 查找 全部交易对ticker 记录 返回结果："+ JSONObject.toJSONString(fullTickerListResDto));
            return ResFactory.getInstance().success(fullTickerListResDto);
        }catch (Throwable throwable){
            String temp = "查找 okex 查找 全部交易对ticker 记录异常,异常信息：";
            log.error(temp,throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<TickerPriceResDto> tickerPrice(Req<TickerPriceReqDto> tickerPriceReqDtoReq) {
        String symbol = null;
        try {
            if(null == tickerPriceReqDtoReq
                    || null == tickerPriceReqDtoReq.getData()){
                String temp = "调用 okex 查询 价格信息 失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            TickerPriceReqDto tickerPriceReqDto = tickerPriceReqDtoReq.getData();

            symbol = tickerPriceReqDto.getSymbol();
            if(StringUtils.isBlank(symbol)){
                String temp = "调用okex查询价格信息失败,必填参数symbol为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            if(symbol.startsWith("OKDK/")){//包含OKDK的币对，价格直接返回0
                TickerPriceResDto tickerPriceResDto = new TickerPriceResDto(symbol,BigDecimal.ZERO);
                return ResFactory.getInstance().success(tickerPriceResDto);
            }

            boolean doDiv = false;
            String symbolNew = symbol;
            if(symbol.startsWith("USDK/")){
                String[] tempArr = symbol.split("/");
                symbolNew = tempArr[1]+"-"+tempArr[0];
                doDiv = true;
            }

            String tradeSymbol = this.toTradeSymbol(symbolNew);
            IOkexStockRestApi restApi = new OkexStockRestApi(null,null,null);
            String ticker = restApi.ticker(tradeSymbol);

            if(StringUtils.isBlank(ticker)){
                String temp = "调用okex查询价格信息失败,交易对名称symbol="+symbol+",返回信息为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject jsonObject = JSONObject.parseObject(ticker);
            if(jsonObject.containsKey("last")){
                String last = jsonObject.getString("last");
                BigDecimal lastBig = new BigDecimal(last);

                if(doDiv){//做除法转换
                    lastBig = CalculateUtil.divide(new BigDecimal("1"),lastBig,18,BigDecimal.ROUND_FLOOR);
                }

                TickerPriceResDto tickerPriceResDto = new TickerPriceResDto(symbol,lastBig.stripTrailingZeros());
                return ResFactory.getInstance().success(tickerPriceResDto);
            }else{
                String temp = "调用okex查询价格信息失败,交易对名称symbol="+symbol+",返回信息:"+ticker;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }
//            {
//                    "best_ask": "3796.5817",
//                    "best_bid": "3792.9649",
//                    "instrument_id": "BTC-USDT",
//                    "product_id": "BTC-USDT",
//                    "last": "3797.2576",
//                    "ask": "3796.5817",
//                    "bid": "3792.9649",
//                    "open_24h": "4020.842",
//                    "high_24h": "4087.9311",
//                    "low_24h": "3697.3002",
//                    "base_volume_24h": "64230",
//                    "timestamp": "2018-11-27T08:45:38.503Z",
//                    "quote_volume_24h": ""
//            }
        } catch (Throwable e) {
            String temp = "调用okex查询价格信息失败,交易对名称symbol="+symbol+",异常信息：";
            log.error(temp,e);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }


    /**
     * 转换三方返回的订单列表实体
     *
     * @param orders
     * @return
     */
    private List<OrderDetailResDto> transferOrderListReturn(List<OrderInfo> orders, String symbol, Integer amountDecimal, SpotOrderApiServiceImpl spotOrderApiService) {
        List<OrderDetailResDto> list = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (OrderInfo order : orders) {
                list.add(transfer(order, symbol, null, spotOrderApiService));
            }
            return list;
        }
        return list;
    }

    @Override
    public Res<OrderBookResDto> getOrderBook(Req<OrderBookReqDto> orderBookReqDtoReq) {
        try {
            if (null == orderBookReqDtoReq
                    || null == orderBookReqDtoReq.getData()
                    || StringUtils.isBlank(orderBookReqDtoReq.getData().getSymbol())) {
                String temp = "调用 okex 查询买卖挂单信息失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            OrderBookReqDto orderBookReqDto = orderBookReqDtoReq.getData();
            String tradeSymbol = this.toTradeSymbol(orderBookReqDto.getSymbol());

            IOkexStockRestApi restApi = new OkexStockRestApi();
            String depth = restApi.depth(tradeSymbol, orderBookReqDto.getLimit());

            if (StringUtils.isBlank(depth)) {
                String temp = "查找 okex 查找 orderbook 记录失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject jsonObject = JSONObject.parseObject(depth);

            if (!jsonObject.containsKey("asks")
                    && !jsonObject.containsKey("bids")) {
                String temp = "查找 okex 查找 orderbook 记录失败,第三方返回:" + depth;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            JSONArray asksArray = jsonObject.getJSONArray("asks");
            JSONArray bidsArray = jsonObject.getJSONArray("bids");

            List<List<String>> asksList = new ArrayList<>();
            int askSize = asksArray == null ? 0 : asksArray.size();
            if (askSize > 0) {
                asksArray.stream().forEach(asks -> {
                    JSONArray array = JSONArray.parseArray(asks.toString());
                    List<String> temp = new ArrayList<>();
                    temp.add(array.getBigDecimal(0).toPlainString());
                    temp.add(array.getBigDecimal(1).toPlainString());
                    asksList.add(temp);
                });
                Collections.reverse(asksList);
            }

            List<List<String>> bidsList = new ArrayList<>();
            int bidSize = bidsArray == null ? 0 : bidsArray.size();
            if (bidSize > 0) {
                bidsArray.stream().forEach(bids -> {
                    JSONArray array = JSONArray.parseArray(bids.toString());
                    List<String> temp = new ArrayList<>();
                    temp.add(array.getBigDecimal(0).toPlainString());
                    temp.add(array.getBigDecimal(1).toPlainString());
                    bidsList.add(temp);
                });
            }
            OrderBookResDto orderBookResDto = new OrderBookResDto(asksList, bidsList);
            log.debug("查找okex orderbook 记录 返回结果：" + JSONObject.toJSONString(orderBookResDto));
            return ResFactory.getInstance().success(orderBookResDto);
        } catch (Throwable throwable) {
            String temp = "查找 okex 查找 orderbook 记录异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<TradeHistoryListResDto> getTrades(Req<TradeHistoryReqDto> tradeHistoryReqDtoReq) {
        try {
            TradeHistoryReqDto tradeHistoryReqDto = tradeHistoryReqDtoReq.getData();

            IOkexStockRestApi stockGet = new OkexStockRestApi();
            String historyTrades = null;

            String symbol = tradeHistoryReqDto.getSymbol();
            if (StringUtils.isBlank(symbol)) {
                String temp = "调用 okex 查询最新成交记录失败，失败原因：必填参数symbol为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            String tradeSymbol = this.toTradeSymbol(symbol);
            historyTrades = stockGet.trades(tradeSymbol, 30);

            if (StringUtils.isBlank(historyTrades)) {
                String temp = "调用 okex 查询最新成交记录失败，失败原因：交易所返回为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            //        成功：
            //        [{
            //            "time": "2019-08-19T10:17:34.958Z",
            //            "timestamp": "2019-08-19T10:17:34.958Z",
            //            "trade_id": "1567283159",
            //            "price": "77.44",
            //            "size": "1.0916",
            //            "side": "buy"
            //        }]

            JSONArray tradesJson = JSONArray.parseArray(historyTrades);
            int tradeJsonSize = tradesJson.size();
            if (tradeJsonSize <= 0) {//
                log.warn("调用okex查询最新成交，交易所返回：" + historyTrades);
                TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
                List<TradeHistoryResDto> tradeHistoryResDtoList = new ArrayList<>();
                tradeHistoryListResDto.setTradeHistoryResDtoList(tradeHistoryResDtoList);
                return ResFactory.getInstance().success(tradeHistoryListResDto);
            }


            List<OkTrades> jsonTradesList = JSONObject.parseArray(historyTrades, OkTrades.class);
            int jsonTradesListSize = jsonTradesList.size();
            if (jsonTradesListSize <= 0) {
                log.warn("调用okex查询最新成交，交易所返回：" + historyTrades);
                TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
                List<TradeHistoryResDto> tradeHistoryResDtoList = new ArrayList<>();
                tradeHistoryListResDto.setTradeHistoryResDtoList(tradeHistoryResDtoList);
                return ResFactory.getInstance().success(tradeHistoryListResDto);
            }


            if (jsonTradesListSize > TradeExchangeApiConstant.Market.MAX_DEPTH_SIZE) {
                jsonTradesList = jsonTradesList.subList(jsonTradesList.size() - TradeExchangeApiConstant.Market.MAX_DEPTH_SIZE - 1, jsonTradesList.size() - 1);
            }

            //前端展示30条
            List<TradeHistoryResDto> tradeHistoryResDtoList = new ArrayList<>();
            jsonTradesList.forEach(trade -> {
                try {
                    Date time = DateUtils.parse(trade.getTimestamp(), DateUtils.FORMAT_DATE_TIME_ISO8601);
                    //时间加8小时
                    time = DateUtils.addHours(time,8);
                    OrderSideEnum orderSide = null;
                    if (trade.getSide().equals("sell")) {//卖
                        orderSide = OrderSideEnum.ASK;
                    } else {//买
                        orderSide = OrderSideEnum.BID;
                    }

                    //TradeExchangeApiConstant.OrderSide orderSide, String amount, String symbol, String price, Long timestamp, String id) {
                    TradeHistoryResDto tempTradeHistoryResDto = new TradeHistoryResDto(orderSide, new BigDecimal(trade.getSize()).toPlainString(),
                            symbol, new BigDecimal(trade.getPrice()).toPlainString(),
                            time.getTime(),
                            trade.getTrade_id());
                    tradeHistoryResDtoList.add(tempTradeHistoryResDto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
            tradeHistoryListResDto.setTradeHistoryResDtoList(tradeHistoryResDtoList);
            return ResFactory.getInstance().success(tradeHistoryListResDto);
        } catch (Throwable throwable) {
            String temp = "查找 okex 查找 最新成交 记录异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    /**
     * 转换三方返回的订单详情实体
     *
     * @param okOrderDetail
     * @return
     */
    private OrderDetailResDto transfer(OrderInfo okOrderDetail, String symbol, Integer amountDecimal, SpotOrderApiServiceImpl spotOrderApiService) {
        OrderDetailResDto orderDetailDto = new OrderDetailResDto();
        orderDetailDto.setOrderId(okOrderDetail.getOrder_id() + "");
        orderDetailDto.setSymbol(symbol);
        if (okOrderDetail.getType().equals(TradeExchangeApiConstant.OrderType.MARKET.desc())){
            orderDetailDto.setOrderType(TradeExchangeApiConstant.OrderType.MARKET);
        }else {
            orderDetailDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        }
        switch (okOrderDetail.getSide()) {
            case OKConstant.TRADE_TYPE_BUY:
                orderDetailDto.setOrderSide(OrderSideEnum.BID);
                break;
            case OKConstant.TRADE_TYPE_SELL:
                orderDetailDto.setOrderSide(OrderSideEnum.ASK);
                break;
            case OKConstant.TRADE_TYPE_BUY_MARKET:
                orderDetailDto.setOrderSide(OrderSideEnum.BID);
                break;
            case OKConstant.TRADE_TYPE_SELL_MARKET:
                orderDetailDto.setOrderSide(OrderSideEnum.ASK);
                break;
        }
        switch (okOrderDetail.getStatus()) {
            case OKConstant.STATUS_CANCELD:
                orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.CANCEL);
                break;
            case OKConstant.STATUS_PENDING:
                orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.COMMIT);
                break;
            case OKConstant.STATUS_PART_FILLED:
                orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.PART);
                break;
            case OKConstant.STATUS_FILLED:
                orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.DEAL);
                break;
            case OKConstant.STATUS_CANCEL_APPLY:
                orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.CANCEL_APPLY);
                break;
        }

        if (StringUtils.isNotEmpty(okOrderDetail.getCreated_at())){
            try {

                orderDetailDto.setThirdCreateTime(DateUtils.addHours(DateUtils.parse(okOrderDetail.getCreated_at(),DateUtils.FORMAT_DATE_TIME_ISO8601), 8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotEmpty(okOrderDetail.getTimestamp())){
            try {
                orderDetailDto.setFinishedAt(DateUtils.parse(okOrderDetail.getTimestamp(),DateUtils.FORMAT_DATE_TIME_ISO8601));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        BigDecimal amount = new BigDecimal(okOrderDetail.getSize());
        BigDecimal dealAmount = new BigDecimal(okOrderDetail.getFilled_size());
        BigDecimal filledNotional = new BigDecimal(okOrderDetail.getFilled_notional());
    //    BigDecimal price = new BigDecimal(okOrderDetail.getPrice());

        if (StringUtils.isBlank(okOrderDetail.getPrice())){
            orderDetailDto.setPrice(BigDecimal.ZERO);
        }else {
            orderDetailDto.setPrice(new BigDecimal(okOrderDetail.getPrice()));
        }

        orderDetailDto.setAmount(amount);
        if (filledNotional.compareTo(BigDecimal.ZERO) != 0 && dealAmount.compareTo(BigDecimal.ZERO) != 0) {
            orderDetailDto.setFilledPrice(filledNotional.divide(dealAmount, 18, BigDecimal.ROUND_FLOOR));
        }
        orderDetailDto.setFilledAmount(dealAmount);
        orderDetailDto.setLeftAmount(amount.subtract(dealAmount));
        orderDetailDto.setFilledCashAmount(filledNotional);
        orderDetailDto.setTradeSymbol(toTradeSymbol(symbol));
        //获取手续费
       /* if (TradeExchangeApiConstant.OrderStatus.DEAL.code().equals(orderDetailDto.getOrderStatus().code())) {
            try {
                List<Fills> fillsList = spotOrderApiService.getFills(orderDetailDto.getOrderId(), okOrderDetail.getInstrument_id(), null, null, null);
                BigDecimal bigDecimal = new BigDecimal("0");
                for (Fills fill : fillsList) {
                    bigDecimal = bigDecimal.add(fill.getFee());
                }
                orderDetailDto.setFeeValue(bigDecimal);
            } catch (Throwable e) {
                log.error("获取OKEX查询订单明细异常：" + e.getMessage());
            }
        }*/
//        orderDetailDto.setFeeValue();
//        orderDetailDto.setFinishedAt();
        return orderDetailDto;
    }

    @Override
    public Res<AccountInfoResDto> getAccountInfo(Req<AccountInfoReqDto> accountInfoReqDtoReq) {
        return null;
    }

    @Override
    public Res<ResList<QueryBalanceResDto>> getBalance(QueryBalanceReqDto queryBalanceReqDto) {
        Map<String, QueryBalanceResDto> resultMap = Maps.newHashMap();
        try {
            APIConfiguration config = new APIConfiguration(queryBalanceReqDto.getApiKey(), queryBalanceReqDto.getApiSecret(), queryBalanceReqDto.getPassphrase());
            SpotAccountAPIServiceImpl spotAccountAPIService = new SpotAccountAPIServiceImpl(config);
            List<Account> accounts = spotAccountAPIService.getAccounts();

            if (accounts.isEmpty()) {
                return ResFactory.getInstance().successList(new ArrayList<>());
            }
            Account account;
            String currency;
            int resultSize = accounts == null ? 0 : accounts.size();
            for (int j = 0; j < resultSize; j++) {
                account = accounts.get(j);
                currency = account.getCurrency();
                if (!resultMap.containsKey(currency)) {
                    resultMap.put(currency, new QueryBalanceResDto(currency, BigDecimal.ZERO, BigDecimal.ZERO));
                }
                resultMap.get(currency).setUsable(new BigDecimal(account.getAvailable()));
                resultMap.get(currency).setFrozen(new BigDecimal(account.getHold()));
            }
            return ResFactory.getInstance().successList(Lists.newArrayList(resultMap.values()));
        } catch (Exception e) {
            log.error("调用okex查询账户余额异常，异常信息：", e);
            throw new CallExchangeRemoteException(e, e.getMessage());
        }
    }

    @Override
    public Res<TransferResDto> transfer(Req<TransferReqDto> transferReqDtoReq) {

        try {
            Thread.sleep(2000L);
        }catch (Throwable throwable){
            log.error("做资金反划转等待异常:",throwable);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,"请稍后再试");
        }

        String coinName = null;
        if(null == transferReqDtoReq
                || null == transferReqDtoReq.getData()){
            String temp = "调用 okex 做资金划转 失败，失败原因：必填参数为空";
            log.error(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        TransferReqDto transferReqDto = transferReqDtoReq.getData();

        coinName = transferReqDto.getCoinName();
        BigDecimal amountBig = transferReqDto.getAmount();
        String from = transferReqDto.getFrom();
        String to = transferReqDto.getTo();
        if(StringUtils.isBlank(coinName)
                || null == amountBig
                || StringUtils.isBlank(from)
                || StringUtils.isBlank(to)){
            String temp = "调用 okex 做资金划转 失败,必填参数为空";
            log.error(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        IOkexStockRestApi restApi = new OkexStockRestApi(transferReqDto.getApiKey(),transferReqDto.getApiSecret(),transferReqDto.getPassphrase());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("currency",coinName);
        paramMap.put("amount",amountBig.toPlainString());
        paramMap.put("from",from);
        paramMap.put("to",to);

        String subAccount = transferReqDto.getSubAccount();
        if(StringUtils.isNotBlank(subAccount)){
            paramMap.put("sub_account",subAccount);
        }

        String instrumentId = transferReqDto.getInstrumentId();
        if(StringUtils.isNotBlank(instrumentId)){
            paramMap.put("instrument_id",instrumentId);
        }

        String toInstrumentId = transferReqDto.getToInstrumentId();
        if(StringUtils.isNotBlank(toInstrumentId)){
            paramMap.put("to_instrument_id",toInstrumentId);
        }

        String transfer = null;
        try {
            transfer = restApi.transfer(paramMap);
        }catch (Throwable throwable){
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,"资金划转异常");
        }

//            成功：
//            {
//                "transfer_id": "754147",
//                "currency”："ETC”
//                "from": "6",
//                "amount": "0.1",
//                "to”: "1",
//                "result": true
//            }

        if(StringUtils.isBlank(transfer)){
            String temp = "调用okex资金划转失败,币种名称coinName="+coinName+",返回信息为空";
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,"返回信息为空");
        }
        if(!transfer.startsWith("{")){
            String temp = "调用okex资金划转失败,币种名称coinName="+coinName+",返回信息："+transfer;
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,transfer);
        }

        JSONObject jsonObject = JSONObject.parseObject(transfer);

        if(jsonObject.containsKey("result")){
            Boolean resultBo = jsonObject.getBoolean("result");
            if(resultBo){
                String transferId = jsonObject.getString("transfer_id");

                TransferResDto transferResDto = new TransferResDto();
                transferResDto.setTransferId(transferId);
                return ResFactory.getInstance().success(transferResDto);
            }else{
                String temp = "调用okex资金划转失败,币种名称coinName="+coinName+",返回信息:"+transfer;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,transfer);
            }
        }else{
            String temp = "调用okex资金划转失败,币种名称coinName="+coinName+",返回信息:"+transfer;
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,transfer);
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
        if(StringUtils.isBlank(coinName)){
            String temp = "参数异常";
            log.error(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        IOkexStockRestApi restApi = new OkexStockRestApi(depositAddressReqDto.getApiKey(),depositAddressReqDto.getApiSecret(),depositAddressReqDto.getPassphrase());
        String result = null;
        try {
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("currency",depositAddressReqDto.getCoinName().toUpperCase());
            result = restApi.depositAddress(paramMap);

//            成功：
//            [{
//                "address": "okbtothemoon",
//                "memo": "971668",
//                "currency": "eos",
//                "to": 6
//            }]

            if (StringUtils.isBlank(result)) {
                String temp = "调用 okex 查询币种充值地址信息失败，交易所返回为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }
            if (!result.startsWith("[")) {
                String temp = "调用 okex 查询币种充值地址信息失败，交易所返回"+result;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONArray jsonArray = JSONArray.parseArray(result);
            int size = jsonArray.size();
            if(size<=0){
                String temp = "调用 okex 查询币种充值地址信息失败，交易所返回"+result;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String address = jsonObject.getString("address");
            DepositAddressResDto depositAddressResDto = new DepositAddressResDto();
            depositAddressResDto.setAddress(address);
            depositAddressResDto.setCoinName(depositAddressReqDto.getCoinName());
            return ResFactory.getInstance().success(depositAddressResDto);
        } catch (Throwable throwable) {
            String temp = "调用 okex 查询币种充值地址信息异常";
            log.error(temp+"，异常信息：", throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    /**
     *
     * @param sourceCoin 币名称，如：BTC/USDT的价格，此值为BTC
     * @param targetCoin 钱名称，如：BTC/USDT的价格，此值为USDT
     * @return 返回转换结果，true 转换成功、false 转换失败
     */
    private BigDecimal price(String sourceCoin,String targetCoin,Map<String,BigDecimal> priceMap){
        try {
            String symbol = sourceCoin + "/"+targetCoin;
            if(priceMap.containsKey(symbol)){
                return priceMap.get(symbol).stripTrailingZeros();
            }

            if(StringUtils.equals(sourceCoin,targetCoin)){
                priceMap.put(symbol,new BigDecimal("1"));
                return new BigDecimal("1");
            }else{
                TickerPriceReqDto tickerPriceReqDto = new TickerPriceReqDto();
                tickerPriceReqDto.setExchCode(ExchangeCode.OKEX);

                tickerPriceReqDto.setSymbol(symbol.toUpperCase());
                Req<TickerPriceReqDto> tickerPriceReqDtoReq = ReqFactory.getInstance().createReq(tickerPriceReqDto);
                Thread.sleep(1000L);
                Res<TickerPriceResDto> tickerPriceResDtoRes = this.tickerPrice(tickerPriceReqDtoReq);
                if(null != tickerPriceResDtoRes && tickerPriceResDtoRes.isSuccess()){
                    TickerPriceResDto tickerPriceResDto = tickerPriceResDtoRes.getData();
                    if(null != tickerPriceResDto){
                        priceMap.put(symbol,tickerPriceResDto.getPrice().stripTrailingZeros());
                        return tickerPriceResDto.getPrice().stripTrailingZeros();
                    }
                }
            }
        }catch (Throwable throwable){
            //调用币种对USDT价格失败，查询对BTC价格
        }
        return null;
    }



}
