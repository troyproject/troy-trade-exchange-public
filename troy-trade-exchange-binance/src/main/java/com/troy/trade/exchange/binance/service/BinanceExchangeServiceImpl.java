package com.troy.trade.exchange.binance.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
import com.troy.trade.exchange.api.model.dto.in.data.AbnormalChangesReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.CoinInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.*;
import com.troy.trade.exchange.api.model.dto.out.account.*;
import com.troy.trade.exchange.api.model.dto.out.data.AbnormalChangesResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.CoinInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.SymbolInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.SymbolInfoResDto;
import com.troy.trade.exchange.api.model.dto.out.market.MyTradeListResDto;
import com.troy.trade.exchange.api.model.dto.out.market.MyTradeResDto;
import com.troy.trade.exchange.api.model.dto.out.market.TickerPriceResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CancelOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CreateOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderDetailResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderListResData;
import com.troy.trade.exchange.api.model.dto.out.order.binance.BinanceResponse;
import com.troy.trade.exchange.api.util.SymbolUtil;
import com.troy.trade.exchange.binance.client.BinanceApiClientFactory;
import com.troy.trade.exchange.binance.client.BinanceApiRestClient;
import com.troy.trade.exchange.binance.client.impl.BinanceApiRestClientImpl;
import com.troy.trade.exchange.binance.client.stock.IBinanceStockRestApi;
import com.troy.trade.exchange.binance.client.stock.impl.BinanceStockRestApi;
import com.troy.trade.exchange.binance.dto.*;
import com.troy.trade.exchange.binance.dto.account.*;
import com.troy.trade.exchange.binance.dto.account.request.AllOrdersRequest;
import com.troy.trade.exchange.binance.dto.account.request.CancelOrderRequest;
import com.troy.trade.exchange.binance.dto.account.request.OrderRequest;
import com.troy.trade.exchange.binance.dto.account.request.OrderStatusRequest;
import com.troy.trade.exchange.binance.dto.exception.BinanceApiException;
import com.troy.trade.exchange.binance.dto.general.FilterType;
import com.troy.trade.exchange.core.constant.ExchangeConstant;
import com.troy.trade.exchange.core.service.IBinanceExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Binance交易所服务实现
 *
 * @author dp
 */
@Slf4j
@Component
public class BinanceExchangeServiceImpl implements IBinanceExchangeService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.BINANCE;
    }

    @Override
    public String toTradeSymbol(String symbol) {
        return SymbolUtil.ToTradeSymbol.upperCaseSymbol(symbol);
    }

    @Override
    public String symbol(String tradeSymbol) {
        // BTCUSDT
        if (StringUtils.isBlank(tradeSymbol)) {
            return null;
        }
        String coinName;
        String moneyName;
        // BNB、BTC、ETH、XRP、PAX、USDT、TUSD、USDC、USDS、BUSD、NGN、TRX
        if (tradeSymbol.endsWith("BNB")
                || tradeSymbol.endsWith("BTC")
                || tradeSymbol.endsWith("ETH")
                || tradeSymbol.endsWith("XRP")
                || tradeSymbol.endsWith("PAX")
                || tradeSymbol.endsWith("NGN")
                || tradeSymbol.endsWith("TRX")) {
            coinName = tradeSymbol.substring(0, tradeSymbol.length() - 3);
            moneyName = tradeSymbol.substring(tradeSymbol.length() - 3);
        } else {
            coinName = tradeSymbol.substring(0, tradeSymbol.length() - 4);
            moneyName = tradeSymbol.substring(tradeSymbol.length() - 4);
        }
        return new StringBuffer(coinName).append("/").append(moneyName).toString();
    }

    @Override
    public Res<CreateOrderResDto> createOrder(Req<CreateOrderReqDto> createOrderReqDtoReq) {
        CreateOrderReqDto createOrderReqDto = createOrderReqDtoReq.getData();
        final String apiKey = createOrderReqDto.getApiKey();
        final String apiSecret = createOrderReqDto.getApiSecret();
        // 交易对
        final String currencyPair = createOrderReqDto.getTradeSymbol();
        String amount;
        String price = null;
        OrderSide binanceOrderSide;
        OrderType binanceOrderType;

        BinanceApiRestClient binanceApiRestClient = new BinanceApiRestClientImpl(apiKey, apiSecret);

        // 挂单方向
        if (OrderSideEnum.BID.equals(createOrderReqDto.getOrderSide())) {
            binanceOrderSide = OrderSide.BUY;
        } else {
            binanceOrderSide = OrderSide.SELL;
        }
        //交易类型，限价交易
        if (TradeExchangeApiConstant.OrderType.LIMIT.equals(createOrderReqDto.getOrderType())) {
            amount = createOrderReqDto.getAmount().toPlainString();
            price = createOrderReqDto.getPrice().toPlainString();
            binanceOrderType = OrderType.LIMIT;
        } else {//交易类型，市价交易
            amount = String.valueOf(createOrderReqDto.getAmount());
            binanceOrderType = OrderType.MARKET;
        }

        NewOrderResponse newOrderResponse;
        try {
            // 下单
            NewOrder order = new NewOrder(currencyPair, binanceOrderSide, binanceOrderType, TimeInForce.GTC, amount, price);
            order = order.newClientOrderId(createOrderReqDto.getTransId());
            newOrderResponse = binanceApiRestClient.newOrder(order);
            BinanceResponse binanceResponse = new BinanceResponse();
            convert(newOrderResponse, binanceResponse);
            return ResFactory.getInstance().success(new CreateOrderResDto(binanceResponse.getOrderId(), binanceResponse));
        } catch (Exception e) {
            log.error("调用币安下单失败：", e);
            throw new CallExchangeRemoteException(e, e.getMessage());
        }
    }

    @Override
    public Res<CancelOrderResDto> cancelOrder(Req<CancelOrderReqDto> cancelOrderReqDtoReq) {
        CancelOrderReqDto cancelOrderReqDto = cancelOrderReqDtoReq.getData();
        Assert.notNull(cancelOrderReqDto, TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_DATA, "撤单数据不能为空");
        final List<String> orderIds = cancelOrderReqDto.getOrderIds();

        Assert.notEmpty(orderIds, TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_LIST, "撤销列表不能为空");

        log.info("调用币安撤销订单，开始，本次撤销订单ID列表大小为：" + orderIds.size());

        BinanceApiRestClientImpl binanceApiRestClient = new BinanceApiRestClientImpl(cancelOrderReqDto.getApiKey(), cancelOrderReqDto.getApiSecret());
        List<String> successOrderIds = Lists.newArrayList();
        List<String> failOrderIds = Lists.newArrayList();
        for (String orderId : orderIds) {
            try {
                binanceApiRestClient.cancelOrder(new CancelOrderRequest(cancelOrderReqDto.getTradeSymbol(), Long.valueOf(orderId)));
                successOrderIds.add(orderId);
            } catch (BinanceApiException e) {
                log.warn("调用撤单失败 {}", e.getError());
                // -2011为重复撤单的错误，出现该错误也算成功
                if(e.getError().getCode() != -2011){
                    failOrderIds.add(orderId);
                }
                // 出现错误继续将本轮订单撤销完成
            }
        }
        return ResFactory.getInstance().success(new CancelOrderResDto(successOrderIds, failOrderIds));
    }

    @Override
    public Res<OrderDetailResDto> orderDetail(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        log.debug("查询币安订单信息 start");
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        long orderId = Long.parseLong(orderDetailReqDto.getOrderId());
        BinanceApiRestClientImpl binanceApiRestClient = new BinanceApiRestClientImpl(orderDetailReqDto.getApiKey(), orderDetailReqDto.getApiSecret());

        // 组装参数调用币安查询订单信息
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest(toTradeSymbol(orderDetailReqDto.getSymbol()), orderId);
        try {
            return ResFactory.getInstance().success(transferOrderReturn(binanceApiRestClient.getOrderStatus(orderStatusRequest), orderDetailReqDto.getSymbol(), orderDetailReqDto.getAmountDecimal()));
        } catch (BinanceApiException e) {
            log.error("调用币安查询[" + orderId + "]订单信息接口失败：", e);
            throw new CallExchangeRemoteException("调用币安查询订单信息接口失败：" + e.getLocalizedMessage());
        }
    }

    @Override
    public Res<OrderListResData> orderList(Req<OrderDetailReqDto> orderDetailReqDtoReq) {

        log.debug("查询币安订单信息列表 start");
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        final String symbol = orderDetailReqDto.getSymbol();
        final String appkey = orderDetailReqDto.getApiKey();
        final String appsecret = orderDetailReqDto.getApiSecret();
        BinanceApiRestClientImpl binanceApiRestClient = new BinanceApiRestClientImpl(appkey, appsecret);

        List<Order> order;
        try {
            AllOrdersRequest orderRequest = new AllOrdersRequest(toTradeSymbol(symbol));
            order = binanceApiRestClient.getAllOrders(orderRequest);
            List<Order> openOrders = binanceApiRestClient.getOpenOrders(orderRequest);

            if (openOrders!=null && openOrders.size()>0 &&  order!=null && order.size()>0){
                order.addAll(openOrders);
            }else if(order==null || order.size()<1){
                order = openOrders;
            }
        } catch (BinanceApiException e) {
            log.error("调用币安查询订单信息列表接口失败：", e);
            throw new CallExchangeRemoteException("调用币安查询订单信息列表接口失败：" + e.getLocalizedMessage());
        }
        if (order == null) {
            return ResFactory.getInstance().success(new OrderListResData());
        }
        List<Long> ids = new ArrayList<>();//用来临时存储person的id
        List<Order> newList = order.stream().filter(// 过滤去重
                v -> {
                    boolean flag = !ids.contains(v.getOrderId());
                    ids.add(v.getOrderId());
                    return flag;
                }
        ).collect(Collectors.toList());
        return ResFactory.getInstance().success(new OrderListResData(transferOrderListReturn(newList, orderDetailReqDto.getSymbol(), orderDetailReqDto.getAmountDecimal())));

    }

    @Override
    public Res<OrderBookResDto> getOrderBook(Req<OrderBookReqDto> orderBookReqDtoReq) {
        try {
            if (null == orderBookReqDtoReq
                    || null == orderBookReqDtoReq.getData()
                    || StringUtils.isBlank(orderBookReqDtoReq.getData().getSymbol())) {
                String temp = "调用 币安 查询买卖挂单信息失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            OrderBookReqDto orderBookReqDto = orderBookReqDtoReq.getData();

            Integer limit = orderBookReqDto.getLimit();
            if (null == limit) {
                limit = 50;
            }

            String tradeSymbol = this.toTradeSymbol(orderBookReqDto.getSymbol());
            IBinanceStockRestApi binanceStockRestApi = new BinanceStockRestApi();
            String result = binanceStockRestApi.orderBook(tradeSymbol, limit);
            if (StringUtils.isBlank(result)) {
                String temp = "查找 币安 查找 orderbook 记录失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject resultJson = JSONObject.parseObject(result);
            if (resultJson.containsKey("code")) {
                String temp = "查找 币安 查找 orderbook 记录失败,返回结果" + result;
                log.warn(temp);
                String msg = resultJson.getString("msg");
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,msg);
            }

//            失败：
//            {
//                "code": -1121,
//                "msg": "Invalid symbol."
//            }

//            成功：
//            {
//                "lastUpdateId": 1009607226,
//                "bids": [
//                    [
//                        "10163.26000000",
//                        "0.01967500"
//                    ]
//                ],
//                "asks": [
//                    [
//                        "10165.06000000",
//                        "0.01856200"
//                    ]
//                ]
//            }

            BigDecimal priceBig = null;
            BigDecimal amountBig = null;


            List<List<String>> asksList = new ArrayList<>();
            JSONArray asksArr = resultJson.getJSONArray("asks");
            JSONArray childAskArr = null;
            int askSize = asksArr == null ? 0 : asksArr.size();
            for (int i = 0; i < askSize; i++) {
                childAskArr = asksArr.getJSONArray(i);
                priceBig = childAskArr.getBigDecimal(0);
                amountBig = childAskArr.getBigDecimal(1);
                List<String> temp = new ArrayList<>();
                temp.add(priceBig.stripTrailingZeros().toPlainString());
                temp.add(amountBig.stripTrailingZeros().toPlainString());
                asksList.add(temp);
            }

            Collections.reverse(asksList);

            List<List<String>> bidsList = new ArrayList<>();
            JSONArray bidsArr = resultJson.getJSONArray("bids");
            JSONArray childBidArr = null;
            int bidSize = bidsArr == null ? 0 : bidsArr.size();
            for (int i = 0; i < bidSize; i++) {
                childBidArr = bidsArr.getJSONArray(i);
                priceBig = childBidArr.getBigDecimal(0);
                amountBig = childBidArr.getBigDecimal(1);
                List<String> temp = new ArrayList<>();
                temp.add(priceBig.stripTrailingZeros().toPlainString());
                temp.add(amountBig.stripTrailingZeros().toPlainString());
                bidsList.add(temp);
            }

            OrderBookResDto orderBookResDto = new OrderBookResDto(asksList, bidsList);

            log.debug("查找 币安 查找orderbook 记录 返回结果：" + JSONObject.toJSONString(orderBookResDto));
            return ResFactory.getInstance().success(orderBookResDto);
        } catch (Throwable throwable) {
            String temp = "调用 币安 查找orderBook信息异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<TradeHistoryListResDto> getTrades(Req<TradeHistoryReqDto> tradeHistoryReqDtoReq) {
        try {
            TradeHistoryReqDto tradeHistoryReqDto = tradeHistoryReqDtoReq.getData();

            String symbol = tradeHistoryReqDto.getSymbol();
            if (StringUtils.isBlank(symbol)) {
                String temp = "调用 币安 查询历史成交记录失败，失败原因：必填参数symbol为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            String tradeSymbol = this.toTradeSymbol(tradeHistoryReqDto.getSymbol());
//            BinanceApiRestClient binanceApiRestClient = new BinanceApiRestClientImpl(null, null);
//            List<TradeHistoryItem> tradeHistoryItems = binanceApiRestClient.getTrades(tradeSymbol, 30);
            IBinanceStockRestApi binanceStockRestApi = new BinanceStockRestApi();
            String result = binanceStockRestApi.getTrades(tradeSymbol, 30);

            if (StringUtils.isBlank(result)) {
                String temp = "查找 币安 查找 最新成交 记录失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            if (result.contains("code")) {
                JSONObject resultJson = JSONObject.parseObject(result);
                String temp = "查找 币安 查找 最新成交 记录失败,返回结果" + result;
                log.warn(temp);
                String msg = resultJson.getString("msg");
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,msg);
            }

//            失败：
//            {
//                "code": -1121,
//                "msg": "Invalid symbol."
//            }

//            成功：
//            [
//                {
//                    "id": 28457,
//                    "price": "4.00000100",
//                    "qty": "12.00000000",
//                    "time": 1499865549590,
//                    "isBuyerMaker": true,
//                    "isBestMatch": true
//                }
//            ]

            List<TradeHistoryItem> tradeHistoryItems = JSONArray.parseArray(result, TradeHistoryItem.class);
            int size = tradeHistoryItems == null ? 0 : tradeHistoryItems.size();

            TradeHistoryResDto tempTradeHistoryResDto = null;
            TradeHistoryItem tradeHistoryItem = null;
            boolean buyerMaker = false;
            OrderSideEnum orderSide = null;
            List<TradeHistoryResDto> trades = new ArrayList<>();
            for (int i = (size-1); i >= 0; i--) {
                tradeHistoryItem = tradeHistoryItems.get(i);
                buyerMaker = tradeHistoryItem.isBuyerMaker();
                if (buyerMaker) {//卖
                    orderSide = OrderSideEnum.ASK;
                } else {//买
                    orderSide = OrderSideEnum.BID;
                }

//                TradeExchangeApiConstant.OrderSide orderSide, String amount, String symbol, String price, Long timestamp, String id) {
                tempTradeHistoryResDto = new TradeHistoryResDto(orderSide, new BigDecimal(tradeHistoryItem.getQty()).toPlainString(),
                        symbol, new BigDecimal(tradeHistoryItem.getPrice()).toPlainString(),
                        DateUtils.parse(tradeHistoryItem.getTime()).getTime(),
                        String.valueOf(tradeHistoryItem.getId()));
                trades.add(tempTradeHistoryResDto);
            }

            TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
            tradeHistoryListResDto.setTradeHistoryResDtoList(trades);
            return ResFactory.getInstance().success(tradeHistoryListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 币安 查询历史成交记录异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

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

            String tradeSymbol = this.toTradeSymbol(myTradeReqDto.getSymbol());

            BinanceApiRestClient binanceApiRestClient = new BinanceApiRestClientImpl(myTradeReqDto.getApiKey(), myTradeReqDto.getApiSecret());

            List<Trade> tradeHistoryItems = binanceApiRestClient.getMyTrades(tradeSymbol, myTradeReqDto.getLimit(), myTradeReqDto.getFromId() != null ? Long.valueOf(myTradeReqDto.getFromId()) : null, BinanceApiConstants.DEFAULT_RECEIVING_WINDOW, System.currentTimeMillis(),
                    myTradeReqDto.getStartTime() != null ? myTradeReqDto.getStartTime() : null, myTradeReqDto.getEndTime() != null ? myTradeReqDto.getEndTime() : null);
            int size = tradeHistoryItems == null ? 0 : tradeHistoryItems.size();

            MyTradeResDto tempTradeHistoryResDto = null;
            Trade tradeHistoryItem = null;
            boolean buyerMaker = false;
            TradeExchangeApiConstant.OrderRole orderRole = null;
            OrderSideEnum orderSide = null;
            List<MyTradeResDto> trades = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                tradeHistoryItem = tradeHistoryItems.get(i);
                buyerMaker = tradeHistoryItem.isBuyer();
                if (buyerMaker) {//卖
                    orderSide = OrderSideEnum.ASK;
                } else {//买
                    orderSide = OrderSideEnum.BID;
                }

                if (tradeHistoryItem.isMaker()) {//卖
                    orderRole = TradeExchangeApiConstant.OrderRole.MAKER;
                } else {//买
                    orderRole = TradeExchangeApiConstant.OrderRole.TAKER;
                }
                tempTradeHistoryResDto = new MyTradeResDto(tradeHistoryItem.getCommissionAsset(), tradeHistoryItem.getOrderId(), orderSide, new BigDecimal(tradeHistoryItem.getQty()),
                        symbol, new BigDecimal(tradeHistoryItem.getPrice()),
                        DateUtils.parse(tradeHistoryItem.getTime()).getTime(),
                        String.valueOf(tradeHistoryItem.getId()), tradeHistoryItem.getCommission(),orderRole);
                trades.add(tempTradeHistoryResDto);
            }
            MyTradeListResDto tradeHistoryListResDto = new MyTradeListResDto();
            tradeHistoryListResDto.setMyTradeResDtoList(trades);
            return ResFactory.getInstance().success(tradeHistoryListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 币安 查询账户交易清单异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, temp + throwable.getLocalizedMessage());

        }
    }

    @Override
    public Res<OrderListResData> getOpenOrders(Req<OpenOrdersReqDto> ordersReqDtoReq) {
        try {
            OpenOrdersReqDto openOrdersReqDto = ordersReqDtoReq.getData();

            String symbol = openOrdersReqDto.getSymbol();
            if (StringUtils.isBlank(symbol)) {
                String temp = "调用 币安 查查看账户当前挂单失败，失败原因：必填参数symbol为空";
                throw new TradeExchangeApiException(temp);
            }

            BinanceApiRestClient binanceApiRestClient = new BinanceApiRestClientImpl(openOrdersReqDto.getApiKey(), openOrdersReqDto.getApiSecret());

            List<Order> orders = binanceApiRestClient.getOpenOrders(new OrderRequest(openOrdersReqDto.getTradeSymbol()));
            if (orders == null) {
                return ResFactory.getInstance().success(new OrderListResData());
            }
            return ResFactory.getInstance().success(new OrderListResData(transferOrderListReturn(orders, openOrdersReqDto.getSymbol(), null)));

        } catch (Throwable throwable) {
            String temp = "调用 币安 查询账户交易清单异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, temp + throwable.getLocalizedMessage());

        }
    }

    @Override
    public Res<ResList<ExchAcctDeptWdralResDto>> harkWithdrawal(Req<HarkWithdrawalReqDto> harkWithdrawalReqDtoReq) {

        HarkWithdrawalReqDto harkWithdrawalReqDto = harkWithdrawalReqDtoReq.getData();
        Integer type = harkWithdrawalReqDto.getType();
        if(null == type){
            String temp = "调用 binance 查询充提币记录失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        Map<String,String> paramMap = new HashMap<>();
        String coinName = harkWithdrawalReqDto.getCoinName();
        if(StringUtils.isNotBlank(coinName)){
            paramMap.put("asset",coinName);
        }

        String startTime = harkWithdrawalReqDto.getStartTime();
        if(StringUtils.isNotBlank(startTime)){//时间查询单位：毫秒
            paramMap.put("startTime",startTime);
        }

        String endTime = harkWithdrawalReqDto.getEndTime();
        if(StringUtils.isNotBlank(endTime)){
            paramMap.put("endTime",endTime);
        }

        IBinanceStockRestApi binanceStockRestApi = new BinanceStockRestApi(harkWithdrawalReqDto.getApiKey(),harkWithdrawalReqDto.getApiSecret());
        if(type == ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_DEPOSIT){//充值
            String result = null;
            try {
                result = binanceStockRestApi.depositHistory(paramMap);
            } catch (Throwable throwable) {
                String temp = "调用 binance 查询充币记录异常，异常信息：";
                log.error(temp, throwable);
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
            }
            log.warn("调用 binance 查询充币记录,返回：{}",result);
            Res res = isHarkWithdrawalOk(result,"查询充币记录");
            if(!res.isSuccess()){
                return res;
            }
            return turnDeposit(result);
        }else {//提现
            String result = null;
            try {
                result = binanceStockRestApi.withdrawHistory(paramMap);
                log.warn("调用 binance 查询提币记录,返回：{}",result);
            } catch (Throwable throwable) {
                String temp = "调用 binance 查询提币记录异常，异常信息：";
                log.error(temp, throwable);
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
            }
            log.warn(result);
            Res res = isHarkWithdrawalOk(result, "查询提币记录");
            if (!res.isSuccess()) {
                return res;
            }
            return turnWithdraw(result);
        }

    }

    @Override
    public Res<WithdrawalResDto> withdraw(Req<WithdrawalReqDto> withdrawalReqDtoReq) {

        if(null == withdrawalReqDtoReq){
            String temp = "调用 binance 提现失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        WithdrawalReqDto withdrawalReqDto = withdrawalReqDtoReq.getData();

        if(null == withdrawalReqDto){
            String temp = "调用 binance 提现失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        String coinName = withdrawalReqDto.getCoinName();
        //提币地址
        String address = withdrawalReqDto.getAddress();
        BigDecimal amountBig = withdrawalReqDto.getTotalAmount();
        if(StringUtils.isBlank(coinName)
            ||StringUtils.isBlank(address)
            ||null == amountBig){
            String temp = "调用 binance 提现失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        String result = "";
        try {

            String addrTag = null;
            if(address.contains(":")){
                String[] addressArr = address.split(":");
                address = addressArr[0];
                int length = addressArr.length;
                if(length>1){
                    addrTag = addressArr[1];
                }
            }

            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("asset",coinName.toUpperCase());
            paramMap.put("address",address);
            paramMap.put("amount",amountBig.toPlainString());
            if(StringUtils.isNotBlank(addrTag)){
                paramMap.put("addressTag", addrTag);
            }

            IBinanceStockRestApi binanceStockRestApi = new BinanceStockRestApi(withdrawalReqDto.getApiKey(),withdrawalReqDto.getApiSecret());

            try {
                log.warn("调用 binance 提现,调用交易所接口,入参：{}", objectMapper.writeValueAsString(paramMap));
            }catch (Throwable throwable){

            }

            result = binanceStockRestApi.withdraw(paramMap);
        } catch (Throwable throwable) {
            String temp = "调用 binance 提现异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }
//            成功：
//            {
//                "msg": "success",
//                "success": true,
//                "id":"7213fea8e94b4a5593d507237e5a555b"
//            }

//            {"msg":"Invalid operation","success":false}
        if(StringUtils.isBlank(result)){
            String temp = "调用 binance 提现失败，交易所返回为空";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }

        JSONObject resultJSON = JSONObject.parseObject(result);
        if(!resultJSON.containsKey("success")){
            String temp = "调用 binance 提现失败 失败，返回信息:"+result;
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }

        Boolean success = resultJSON.getBoolean("success");
        if(!success) {
            String temp = "调用 binance 提现失败 失败，返回信息:"+result;
            log.error(temp);
//            {"msg":"API key does not exist","success":false}
            String msg = resultJSON.getString("msg");
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,msg);
        }

        WithdrawalResDto withdrawalResDto = new WithdrawalResDto();
        withdrawalResDto.setThirdId(resultJSON.getString("id"));
        return ResFactory.getInstance().success(withdrawalResDto);
    }

    /**
     * 将查询结果转换
     * @param result
     * @return
     */
    private Res<ResList<ExchAcctDeptWdralResDto>> turnWithdraw(String result){
        List<ExchAcctDeptWdralResDto> resultList = new ArrayList<>();
        ExchAcctDeptWdralResDto exchAcctDeptWdralResDto = null;

        String thirdId = null;

        Long applyTimeLong = null;

        String coinName = null;

        BigDecimal amount = null;//数量

        String txId = null;//提币哈希记录

        String address = null;//地址

        Date applyTime = null;//提申请时间

        Integer status = null;//状态，1-申请中、2-已完成、3-已取消、4-失败

        String addressTag = null;


        JSONObject resultJSON = JSONObject.parseObject(result);
        JSONArray withdrawJSONArr = resultJSON.getJSONArray("withdrawList");

        JSONObject depositJSON = null;
        int depositSize = withdrawJSONArr == null?0:withdrawJSONArr.size();
        for(int i=0;i<depositSize;i++){
            depositJSON = withdrawJSONArr.getJSONObject(i);

            thirdId = depositJSON.getString("id");
            applyTimeLong = depositJSON.getLong("applyTime");
            amount = depositJSON.getBigDecimal("amount");
            coinName = depositJSON.getString("asset");
            address = depositJSON.getString("address");
            txId = depositJSON.getString("txId");
            status = depositJSON.getInteger("status");
            addressTag = depositJSON.getString("addressTag");
            applyTime = null;
            if(null != applyTimeLong){
                applyTime = DateUtils.parse(applyTimeLong);
            }

//            String thirdId, String coinName,
//            BigDecimal amount, String txId, String address,
//            Integer deptWdralType, BigDecimal fee, Date applyTime,
//            Integer status
            exchAcctDeptWdralResDto = ExchAcctDeptWdralResDto.getInstance(thirdId,coinName,amount,txId,address,
                    ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_WITHDRAWAL,null,applyTime,WithDrawStatusEnum.getStatus(status),addressTag);
            resultList.add(exchAcctDeptWdralResDto);
        }

        return ResFactory.getInstance().successList(resultList);

//                成功：
//                {
//                    "withdrawList": [{
//                        "id":"7213fea8e94b4a5593d507237e5a555b",
//                        "amount": 1,
//                        "address": "0x6915f16f8791d0a1cc2bf47c13a6b2a92000504b",
//                        "asset": "ETH",
//                        "txId": "0xdf33b22bdb2b28b1f75ccd201a4a4m6e7g83jy5fc5d5a9d1340961598cfcb0a1",
//                        "applyTime": 1508198532000,
//                        "status": 4
//                    },
//                    {
//                        "id":"7213fea8e94b4a5534ggsd237e5a555b",
//                        "amount": 1000,
//                        "address": "463tWEBn5XZJSxLU34r6g7h8jtxuNcDbjLSjkn3XAXHCbLrTTErJrBWYgHJQyrCwkNgYvyV3z8zctJLPCZy24jvb3NiTcTJ",
//                        "addressTag": "342341222",
//                        "txId": "b3c6219639c8ae3f9cf010cdc24fw7f7yt8j1e063f9b4bd1a05cb44c4b6e2509",
//                        "asset": "XMR",
//                        "applyTime": 1508198532000,
//                        "status": 4
//                    }],
//                    "success": true
//                }
    }

    /**
     * 将查询结果转换
     * @param result
     * @return
     */
    private Res<ResList<ExchAcctDeptWdralResDto>> turnDeposit(String result){
        List<ExchAcctDeptWdralResDto> resultList = new ArrayList<>();
        ExchAcctDeptWdralResDto exchAcctDeptWdralResDto = null;

        Long applyTimeLong = null;

        String coinName = null;

        BigDecimal amount = null;//数量

        String txId = null;//提币哈希记录

        String address = null;//地址

        Date applyTime = null;//提申请时间

        Integer status = null;//状态，1-申请中、2-已完成、3-已取消、4-失败

        String addressTag = null;


        JSONObject resultJSON = JSONObject.parseObject(result);
        JSONArray depositJSONArr = resultJSON.getJSONArray("depositList");

        JSONObject depositJSON = null;
        int depositSize = depositJSONArr == null?0:depositJSONArr.size();
        for(int i=0;i<depositSize;i++){
            depositJSON = depositJSONArr.getJSONObject(i);

            applyTimeLong = depositJSON.getLong("insertTime");
            amount = depositJSON.getBigDecimal("amount");
            coinName = depositJSON.getString("asset");
            address = depositJSON.getString("address");
            txId = depositJSON.getString("txId");
            status = depositJSON.getInteger("status");
            addressTag = depositJSON.getString("addressTag");
            applyTime = null;
            if(null != applyTimeLong){
                applyTime = DateUtils.parse(applyTimeLong);
            }

//            String thirdId, String coinName,
//            BigDecimal amount, String txId, String address,
//            Integer deptWdralType, BigDecimal fee, Date applyTime,
//            Integer status
            exchAcctDeptWdralResDto = ExchAcctDeptWdralResDto.getInstance(null,coinName,amount,txId,address,
                    ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_DEPOSIT,null,applyTime,DepositStatusEnum.getStatus(status),addressTag);
            resultList.add(exchAcctDeptWdralResDto);
        }

        return ResFactory.getInstance().successList(resultList);
//                成功：
//                {
//                    "depositList": [{
//                        "insertTime": 1508198532000,
//                        "amount": 0.04670582,
//                        "asset": "ETH",
//                        "address": "0x6915f16f8791d0a1cc2bf47c13a6b2a92000504b",
//                        "txId": "0xdf33b22bdb2b28b1f75ccd201a4a4m6e7g83jy5fc5d5a9d1340961598cfcb0a1",
//                        "status": 1
//                    },
//                    {
//                        "insertTime": 1508298532000,
//                        "amount": 1000,
//                        "asset": "XMR",
//                        "address": "463tWEBn5XZJSxLU34r6g7h8jtxuNcDbjLSjkn3XAXHCbLrTTErJrBWYgHJQyrCwkNgYvyV3z8zctJLPCZy24jvb3NiTcTJ",
//                        "addressTag": "342341222",
//                        "txId": "b3c6219639c8ae3f9cf010cdc24fw7f7yt8j1e063f9b4bd1a05cb44c4b6e2509",
//                        "status": 1
//                    }],
//                    "success": true
//                }
    }

    @Override
    public Res<SymbolInfoListResDto> getSymbolInfo(Req<SymbolInfoReqDto> symbolInfoReqDtoReq) {
        try {
            if (null == symbolInfoReqDtoReq
                    || null == symbolInfoReqDtoReq.getData()) {
                String temp = "调用 币安 查询 交易对信息 失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            SymbolInfoReqDto symbolInfoReqDto = symbolInfoReqDtoReq.getData();

            IBinanceStockRestApi binanceStockRestApi = new BinanceStockRestApi();
            String result = binanceStockRestApi.exchangeInfo();

            if (StringUtils.isBlank(result)) {
                String temp = "调用 币安 查找 交易对信息 记录失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            if (result.contains("code")) {
                String temp = "调用 币安 查找 交易对信息 记录失败,返回结果" + result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            //返回结果
            JSONObject resultJSON = JSONObject.parseObject(result);
            //成功：
//            {
//                "timezone": "UTC",
//                "serverTime": 1566888315021,
//                "rateLimits": [{
//                        "rateLimitType": "REQUEST_WEIGHT",
//                            "interval": "MINUTE",
//                            "intervalNum": 1,
//                            "limit": 1200
//                    },
//                    {
//                        "rateLimitType": "ORDERS",
//                            "interval": "SECOND",
//                            "intervalNum": 1,
//                            "limit": 10
//                    },
//                    {
//                        "rateLimitType": "ORDERS",
//                            "interval": "DAY",
//                            "intervalNum": 1,
//                            "limit": 100000
//                    }],
//                "exchangeFilters": [],
//                "symbols": [{
//                            "symbol": "ETHBTC",
//                            "status": "TRADING",
//                            "baseAsset": "ETH",
//                            "baseAssetPrecision": 8,
//                            "quoteAsset": "BTC",
//                            "quotePrecision": 8,
//                            "orderTypes": [
//                                "LIMIT",
//                                "LIMIT_MAKER",
//                                "MARKET",
//                                "STOP_LOSS",
//                                "STOP_LOSS_LIMIT",
//                                "TAKE_PROFIT",
//                                "TAKE_PROFIT_LIMIT"
//                                ],
//                            "icebergAllowed": true,
//                            "ocoAllowed": true,
//                            "isSpotTradingAllowed": true,
//                            "isMarginTradingAllowed": true,
//                            "filters": [
//                            {
//                                "filterType": "PRICE_FILTER",
//                                "minPrice": "0.00000100",
//                                "maxPrice": "100000.00000000",
//                                "tickSize": "0.00000100"
//                            },
//                            {
//                                "filterType": "PERCENT_PRICE",
//                                "multiplierUp": "5",
//                                "multiplierDown": "0.2",
//                                "avgPriceMins": 5
//                            },
//                            {
//                                "filterType": "LOT_SIZE",
//                                "minQty": "0.00100000",
//                                "maxQty": "100000.00000000",
//                                "stepSize": "0.00100000"
//                            },
//                            {
//                                "filterType": "MIN_NOTIONAL",
//                                "minNotional": "0.00010000",
//                                "applyToMarket": true,
//                                "avgPriceMins": 5
//                            },
//                            {
//                                "filterType": "ICEBERG_PARTS",
//                                "limit": 10
//                            },
//                            {
//                                "filterType": "MARKET_LOT_SIZE",
//                                "minQty": "0.00000000",
//                                "maxQty": "63100.00000000",
//                                "stepSize": "0.00000000"
//                            },
//                            {
//                                "filterType": "MAX_NUM_ALGO_ORDERS",
//                                "maxNumAlgoOrders": 5
//                            }]
//                        }
//                    ]
//                }


            List<SymbolInfoResDto> symbolInfoResDtoList = new ArrayList<>();
            SymbolInfoResDto symbolInfoResDto = null;
            JSONObject childSymbolJSON = null;

            JSONArray symbolsArr = resultJSON.getJSONArray("symbols");
            int size = symbolsArr == null ? 0 : symbolsArr.size();
            for (int i = 0; i < size; i++) {
                childSymbolJSON = symbolsArr.getJSONObject(i);
                String status = childSymbolJSON.getString("status");
                Integer statusInt = ExchangeConstant.SYMBOL_STATUS_OFF;
                if(StringUtils.equals("TRADING",status)){//是否处于 交易状态
                    statusInt = ExchangeConstant.SYMBOL_STATUS_ON;
                }

                String baseName = childSymbolJSON.getString("baseAsset").toUpperCase();//货币名称
                String quoteName = childSymbolJSON.getString("quoteAsset").toUpperCase();//钱币名称
                String symbol = baseName + "/" + quoteName;

                BigDecimal tickSize = null;
                BigDecimal stepSize = null;
                BigDecimal quoteLeast = null;//最小成交额
                BigDecimal baseLeast = null;//最小成交量
                JSONArray filtersArr = childSymbolJSON.getJSONArray("filters");//过滤器
                int filterSize = filtersArr == null ? 0 : filtersArr.size();
                for (int j = 0; j < filterSize; j++) {
                    JSONObject filterJSON = filtersArr.getJSONObject(j);
                    String filterType = filterJSON.getString("filterType");
                    if (StringUtils.equals(filterType, FilterType.PRICE_FILTER.toString())) {//价格过滤器
                        tickSize = new BigDecimal(filterJSON.getString("tickSize")).stripTrailingZeros();
                    } else if (StringUtils.equals(filterType, FilterType.LOT_SIZE.toString())) {//数量过滤器
                        stepSize = new BigDecimal(filterJSON.getString("stepSize")).stripTrailingZeros();
                        baseLeast = new BigDecimal(filterJSON.getString("minQty")).stripTrailingZeros();//最小下单量
                    } else if (StringUtils.equals(filterType, FilterType.MIN_NOTIONAL.toString())) {//最小下单额
                        quoteLeast = new BigDecimal(filterJSON.getString("minNotional")).stripTrailingZeros();
                    }
                }

                Integer basePrecision = stepSize.scale();//货币精度
                Integer quotePrecision = tickSize.scale();//钱币精度

                symbolInfoResDto = new SymbolInfoResDto();
                symbolInfoResDto.setSymbol(symbol);
                symbolInfoResDto.setStatus(statusInt);
                symbolInfoResDto.setQuoteName(quoteName);
                symbolInfoResDto.setBaseName(baseName);
                symbolInfoResDto.setBaseLeast(baseLeast);
                symbolInfoResDto.setQuoteLeast(quoteLeast);
                symbolInfoResDto.setBasePrecision(basePrecision);
                symbolInfoResDto.setQuotePrecision(quotePrecision);
                symbolInfoResDtoList.add(symbolInfoResDto);
            }

            SymbolInfoListResDto symbolInfoListResDto = new SymbolInfoListResDto();
            symbolInfoListResDto.setExchangeCode(symbolInfoReqDto.getExchCode());
            symbolInfoListResDto.setSymbolInfoResDtoList(symbolInfoResDtoList);
            log.debug("调用 币安 查找 交易对信息 记录 返回结果：" + JSONObject.toJSONString(symbolInfoListResDto));
            return ResFactory.getInstance().success(symbolInfoListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 币安 查找 交易对信息信息异常,异常信息：";
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
        log.debug("调用binance查询所有交易对tickers信息开始");
        IBinanceStockRestApi binanceStockRestApi = new BinanceStockRestApi();
        String result = null;
        try {
            result = binanceStockRestApi.tickers24();
//            成功：
//            [{
//                "symbol": "ETHBTC",
//                "priceChange": "-0.00008700",
//                "priceChangePercent": "-0.446",
//                "weightedAvgPrice": "0.01927463",
//                "prevClosePrice": "0.01950800",
//                "lastPrice": "0.01941900",
//                "lastQty": "0.00700000",
//                "bidPrice": "0.01941600",
//                "bidQty": "0.10200000",
//                "askPrice": "0.01941800",
//                "askQty": "2.48000000",
//                "openPrice": "0.01950600",
//                "highPrice": "0.01955700",
//                "lowPrice": "0.01897500",
//                "volume": "238968.47200000",
//                "quoteVolume": "4606.02997508",
//                "openTime": 1572160323079,
//                "closeTime": 1572246723079,
//                "firstId": 148347827,
//                "lastId": 148531579,
//                "count": 183753
//            }, {
//                "symbol": "LTCBTC",
//                "priceChange": "0.00007400",
//                "priceChangePercent": "1.203",
//                "weightedAvgPrice": "0.00623538",
//                "prevClosePrice": "0.00615300",
//                "lastPrice": "0.00622400",
//                "lastQty": "3.65000000",
//                "bidPrice": "0.00622200",
//                "bidQty": "30.00000000",
//                "askPrice": "0.00622400",
//                "askQty": "4.51000000",
//                "openPrice": "0.00615000",
//                "highPrice": "0.00644500",
//                "lowPrice": "0.00599300",
//                "volume": "227644.69000000",
//                "quoteVolume": "1419.45107421",
//                "openTime": 1572160323113,
//                "closeTime": 1572246723113,
//                "firstId": 35359080,
//                "lastId": 35402880,
//                "count": 43801
//            }]
            if (StringUtils.isBlank(result)) {
                String temp = "调用 binance 查询全量ticker信息失败，交易所返回为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            if (!result.startsWith("[")) {
                String temp = "调用 binance 查询全量ticker信息失败，交易所返回:" + result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            JSONArray resultJSONArr = JSONArray.parseArray(result);
            JSONObject jsonObject = null;
            List<FullTickerResDto> fullTickerResDtoList = new ArrayList<>();
            FullTickerResDto fullTickerResDto = null;
            BigDecimal priceChangePercent = null;
            BigDecimal lastPrice = null;
            BigDecimal volume = null;//24小时成交量
            BigDecimal lowPrice = null;
            BigDecimal highPrice = null;
            String tradeSymbol = null;
            int size = resultJSONArr.size();
            for (int i = 0; i < size; i++) {
                fullTickerResDto = new FullTickerResDto();
                jsonObject = resultJSONArr.getJSONObject(i);
                lastPrice = jsonObject.getBigDecimal("lastPrice");
                if(null != lastPrice){
                    lastPrice = lastPrice.stripTrailingZeros();
                }
                volume = jsonObject.getBigDecimal("volume");
                tradeSymbol = jsonObject.getString("symbol");
                priceChangePercent = jsonObject.getBigDecimal("priceChangePercent");
                if(null != priceChangePercent){
                    priceChangePercent = priceChangePercent.setScale(2, BigDecimal.ROUND_FLOOR);
                }

                lowPrice = jsonObject.getBigDecimal("lowPrice");
                if(null != lowPrice){
                    lowPrice = lowPrice.stripTrailingZeros();
                }
                highPrice = jsonObject.getBigDecimal("highPrice");
                if(null != highPrice){
                    highPrice = highPrice.stripTrailingZeros();
                }

                fullTickerResDto.setLast(lastPrice);
                fullTickerResDto.setPriceChangePercent(priceChangePercent);
                fullTickerResDto.setVolume24h(volume);
                fullTickerResDto.setSymbol(this.symbol(tradeSymbol));
                fullTickerResDto.setLowPrice(lowPrice);
                fullTickerResDto.setHighPrice(highPrice);
                fullTickerResDtoList.add(fullTickerResDto);
            }

            FullTickerListResDto fullTickerListResDto = new FullTickerListResDto();
            fullTickerListResDto.setFullTickerResDtoList(fullTickerResDtoList);
            log.debug("调用 币安 查询全量ticker信息 返回结果：" + JSONObject.toJSONString(fullTickerListResDto));
            return ResFactory.getInstance().success(fullTickerListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 binance 查询全量ticker信息异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<TickerPriceResDto> tickerPrice(Req<TickerPriceReqDto> tickerPriceReqDtoReq) {
        String symbol = null;
        try {
            if (null == tickerPriceReqDtoReq
                    || null == tickerPriceReqDtoReq.getData()) {
                String temp = "调用 币安 查询 交易对价格信息 失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            TickerPriceReqDto tickerPriceReqDto = tickerPriceReqDtoReq.getData();
            symbol = tickerPriceReqDto.getSymbol();
            if(StringUtils.isBlank(symbol)){
                String temp = "调用币安查询价格信息失败,必填参数symbol为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }
            String tradeSymbol = this.toTradeSymbol(symbol);
            IBinanceStockRestApi binanceStockRestApi = new BinanceStockRestApi();
            String lastPrice = binanceStockRestApi.getLatestPrice(tradeSymbol);
            if (StringUtils.isBlank(lastPrice)) {
                String temp = "调用 binance 查询价格信息失败，交易所返回为空";
                log.warn(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

//            成功：
//            {
//                "symbol": "LTCBTC",
//                "price": "0.00671100"
//            }

            if(!lastPrice.startsWith("{")){
                String temp = "调用 binance 查询价格信息失败，交易所返回"+lastPrice;
                log.warn(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            JSONObject priceJSON = JSONObject.parseObject(lastPrice);
            BigDecimal priceBig = priceJSON.getBigDecimal("price");
            if(null == priceBig){
                String temp = "调用 binance 查询价格信息失败，交易所返回"+lastPrice;
                log.warn(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            TickerPriceResDto tickerPriceResDto = new TickerPriceResDto(symbol,priceBig.stripTrailingZeros());
            return ResFactory.getInstance().success(tickerPriceResDto);
        } catch (Throwable e) {
            String temp = "调用币安查询价格信息失败,交易对名称symbol="+symbol+",异常信息：";
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
    private List<OrderDetailResDto> transferOrderListReturn(List<Order> orders, String symbol, Integer amountDecimal) {
        List<OrderDetailResDto> list = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (Order order : orders) {
                list.add(transferOrderReturn(order, symbol, null));
            }
            return list;
        }
        return list;
    }

    /**
     * 转换三方返回的订单详情实体
     *
     * @param order
     * @return
     */
    private OrderDetailResDto transferOrderReturn(Order order, String symbol, Integer amountDecimal) {
        OrderDetailResDto orderDetailDto = new OrderDetailResDto();
        orderDetailDto.setOrderId(String.valueOf(order.getOrderId()));
        orderDetailDto.setSymbol(symbol);
        if (order.getSide() == OrderSide.BUY) {
            orderDetailDto.setOrderSide(OrderSideEnum.BID);
        } else {
            orderDetailDto.setOrderSide(OrderSideEnum.ASK);
        }
        // TODO 更多交易类型支持
        if (order.getType() == OrderType.LIMIT) {
            orderDetailDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        } else if (order.getType() == OrderType.MARKET) {
            orderDetailDto.setOrderType(TradeExchangeApiConstant.OrderType.MARKET);
        }

        orderDetailDto.setAmount(new BigDecimal(order.getOrigQty()));
        BigDecimal price = new BigDecimal(order.getPrice());
        orderDetailDto.setPrice(price);
        BigDecimal filedAmount = new BigDecimal(order.getExecutedQty());
        orderDetailDto.setFilledAmount(filedAmount);
        BigDecimal filledCashAmount = BigDecimal.ZERO;
        if (null != filedAmount && null != price) {
            if (amountDecimal == null) {
                filledCashAmount = CalculateUtil.multiply(filedAmount, price).setScale(filedAmount.scale(), BigDecimal.ROUND_HALF_UP);
            } else {
                filledCashAmount = CalculateUtil.multiply(filedAmount, price).setScale(amountDecimal, BigDecimal.ROUND_HALF_UP);
            }

        }

        if (order.getCummulativeQuoteQty()!=null){
            orderDetailDto.setFilledCashAmount(order.getCummulativeQuoteQty());
        }else {
            orderDetailDto.setFilledCashAmount(filledCashAmount);
        }
        BigDecimal fieldPrice = BigDecimal.ZERO;
//        orderDetailDto.setFeeValue();
        Integer scale = amountDecimal == null ? filedAmount.scale() : amountDecimal;
        orderDetailDto.setLeftAmount(new BigDecimal(order.getOrigQty()).subtract(new BigDecimal(order.getExecutedQty())));
        if (order.getStatus() == OrderStatus.NEW) {
            orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.COMMIT);
        } else if (order.getStatus() == OrderStatus.PARTIALLY_FILLED) {
            orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.PART);
            fieldPrice = CalculateUtil.divide(order.getCummulativeQuoteQty(), filedAmount, scale);

            if (fieldPrice!=null){
                orderDetailDto.setFilledPrice(fieldPrice);
            }else {
                orderDetailDto.setFilledPrice(new BigDecimal(order.getPrice()));
            }
        } else if (order.getStatus() == OrderStatus.FILLED) {
            log.debug("-=-=-=-=-= {}", order);
            orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.DEAL);
            fieldPrice = CalculateUtil.divide(order.getCummulativeQuoteQty(), filedAmount, scale);
            if (fieldPrice!=null){
                orderDetailDto.setFilledPrice(fieldPrice);
            }else {
                orderDetailDto.setFilledPrice(new BigDecimal(order.getPrice()));
            }
        } else if (order.getStatus() == OrderStatus.CANCELED) {
            orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.CANCEL);
            if (filedAmount.compareTo(BigDecimal.ZERO) == 0) {
                fieldPrice = price;
            } else {
                fieldPrice = CalculateUtil.divide(order.getCummulativeQuoteQty(), filedAmount, scale);
            }
            if (fieldPrice!=null){
                orderDetailDto.setFilledPrice(fieldPrice);
            }else {
                orderDetailDto.setFilledPrice(new BigDecimal(order.getPrice()));
            }
        } else if (order.getStatus() == OrderStatus.PENDING_CANCEL) {
            orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.COMMIT);
        } else if (order.getStatus() == OrderStatus.REJECTED) {
            orderDetailDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.FAIL);
            fieldPrice = CalculateUtil.divide(order.getCummulativeQuoteQty(), filedAmount, scale);

            if (fieldPrice!=null){
                orderDetailDto.setFilledPrice(fieldPrice);
            }else {
                orderDetailDto.setFilledPrice(new BigDecimal(order.getPrice()));
            }
        }
        orderDetailDto.setFinishedAt(order.getUpdateTime() != 0 ? DateUtils.parse(order.getUpdateTime()) : new Date());
        return orderDetailDto;
    }

    @Override
    public Res<AccountInfoResDto> getAccountInfo(Req<AccountInfoReqDto> accountInfoReqDtoReq) {
        return null;
    }

    @Override
    public Res<ResList<AbnormalChangesResDto>> abnormalChanges(Req<AbnormalChangesReqDto> abnormalChangesReqDtoReq) {

        if (null == abnormalChangesReqDtoReq || null == abnormalChangesReqDtoReq.getData()) {
            String temp = "参数异常";
            log.error(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        List<AbnormalChangesResDto> abnormalChangesResDtoList = new ArrayList<>();
        IBinanceStockRestApi binanceStockRestApi = new BinanceStockRestApi();
        String result = null;
        try {
            result = binanceStockRestApi.abnormalChanges();
            if (StringUtils.isBlank(result)) {
                String temp = "调用 binance 查询异动信息失败，交易所返回为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            JSONObject jsonObject = JSONObject.parseObject(result);
            String code = jsonObject.getString("code");
            if (StringUtils.isBlank(code)
                    || !StringUtils.equals(code, "000000")) {
                String temp = "调用 binance 查询异动信息失败，交易所返回:" + result;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            String data = jsonObject.getString("data");
            if (StringUtils.isBlank(data)) {
                String temp = "调用 binance 查询异动信息，交易所返回:" + result;
                log.info(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            String[] dataArr = data.split(";");
            int length = dataArr.length;
            String abnormalChangeStr = null;
            String[] abnormalChangeArr = null;

            String symbol = null;
            String suffix = null;
            AbnormalChangesResDto abnormalChangesResDto = null;

            Integer symbolLength = null;
            Integer suffixLength = null;
            String start = null;
            for (int i = 0; i < length; i++) {
                abnormalChangeStr = dataArr[i];
                abnormalChangeArr = abnormalChangeStr.split(",");
                abnormalChangesResDto = new AbnormalChangesResDto();
                symbol = abnormalChangeArr[0];
                suffix = abnormalChangeArr[1];
                symbolLength = symbol.length();
                suffixLength = suffix.length();
                start = symbol.substring(0, symbolLength - suffixLength);
                symbol = start + "/" + suffix;
                abnormalChangesResDto.setSymbol(symbol.toUpperCase());
                abnormalChangesResDto.setDateTime(abnormalChangeArr[2]);
                abnormalChangesResDto.setChangeType(abnormalChangeArr[3]);
                abnormalChangesResDto.setVolumeChanges(abnormalChangeArr[4]);
                abnormalChangesResDto.setChangeDesc(abnormalChangeArr[5]);
                abnormalChangesResDto.setExchName("币安");
                abnormalChangesResDto.setExchCode("Binance");
                abnormalChangesResDtoList.add(abnormalChangesResDto);
            }

//            成功：
//
//            {"code":"000000","message":null,"messageDetail":null,"data":"
//
//                MCOBTC,BTC,1560583027000,PriceBreakthroughs,-0.07389,今日新低,false,-1;
//                CMTBTC,BTC,1560582510000,AbnormalPrice,0.032626427,拉升,false,1;
//                EDOBTC,BTC,1560582247000,PriceBreakthroughs,-0.09603,今日新低,false,-1;
//                SCBTC,BTC,1560582074000,PriceBreakthroughs,,本周新低,false,-2;
//                GNTBTC,BTC,1560581828000,PriceBreakthroughs,-0.07362,今日新低,false,-1;
//                BRDBTC,BTC,1560581707000,PriceBreakthroughs,-0.05681,今日新低,false,-1;
//                BTCUSDT,USDT,1560581700000,PriceBreakthroughs,,本周新高,false,2;
//                XMRUSDT,USDT,1560581700000,PriceBreakthroughs,0.05392,今日新高,false,3;
//                BTCUSDT,USDT,1560581640000,PriceBreakthroughs,0.06149,今日新高,false,3;
//                XMRUSDT,USDT,1560581341000,PriceBreakthroughs,,本周新高,false,2;
//                EVXBTC,BTC,1560581250000,AbnormalPrice,0.039179104,拉升,false,1;
//                CMTBTC,BTC,1560581160000,AbnormalPrice,0.036363636,拉升,false,1;
//                BRDBTC,BTC,1560580807000,PriceBreakthroughs,-0.05437,今日新低,false,-1;
//                XMRUSDT,USDT,1560580561000,PriceBreakthroughs,0.05814,今日新高,false,3;
//                STORMBTC,BTC,1560580512000,PriceBreakthroughs,,本周新低,false,-2;
//                OAXBTC,BTC,1560580321000,AbnormalPrice,-0.053292600,跳水,false,-1;
//                CMTBTC,BTC,1560580260000,AbnormalPrice,0.033388982,拉升,false,1;
//                OAXBTC,BTC,1560579990000,AbnormalPrice,0.031294452,拉升,false,1;
//                BRDBTC,BTC,1560579910000,PriceBreakthroughs,,本周新低,false,-2;
//                MFTBTC,BTC,1560579910000,PriceBreakthroughs,,本周新低,false,-2;
//
//
//                ","success":true}

            return ResFactory.getInstance().successList(abnormalChangesResDtoList);
        } catch (Throwable throwable) {
            String temp = "调用 binance 查询异动信息异常";
            log.error(temp+"，异常信息：", throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<ResList<QueryBalanceResDto>> getBalance(QueryBalanceReqDto queryBalanceReqDto) {
        BinanceApiClientFactory binanceApiClientFactory = BinanceApiClientFactory.newInstance(queryBalanceReqDto.getApiKey(), queryBalanceReqDto.getApiSecret());
        BinanceApiRestClient binanceApiRestClient = binanceApiClientFactory.newRestClient();
        Account account;
        try {
            account = binanceApiRestClient.getAccount();
        } catch (BinanceApiException e) {
            log.error("调用币安查询余额异常", e);
            throw new CallExchangeRemoteException(e, e.getMessage());
        }
        List<AssetBalance> resultList = account.getBalances();

        if(CollectionUtils.isEmpty(resultList)){
            log.warn("调用币安查询余额返回为空");
            return ResFactory.getInstance().successList(null);
        }

        List<QueryBalanceResDto> queryBalanceResDtos = Lists.newArrayList();

        resultList.stream().forEach(assetBalance -> queryBalanceResDtos.add(new QueryBalanceResDto(assetBalance.getAsset().toUpperCase(), new BigDecimal(assetBalance.getLocked()), new BigDecimal(assetBalance.getFree()))));

        return ResFactory.getInstance().successList(queryBalanceResDtos);
    }

    /**
     * 转换订单方向
     *
     * @param orderSide
     * @return
     */
    private OrderSideEnum convertOrderSide(OrderSide orderSide){
        if (orderSide == OrderSide.BUY) {
            return OrderSideEnum.BID;
        } else {
            return OrderSideEnum.ASK;
        }
    }

    /**
     * 转换订单方向
     *
     * @param orderType
     * @return
     */
    private TradeExchangeApiConstant.OrderType convertOrderType(OrderType orderType){
        if (orderType == OrderType.LIMIT) {
            return TradeExchangeApiConstant.OrderType.LIMIT;
        } else if (orderType == OrderType.MARKET) {
            return TradeExchangeApiConstant.OrderType.MARKET;
        }
        return TradeExchangeApiConstant.OrderType.LIMIT;
    }

    /**
     * 转换订单状态
     *
     * @param orderStatus
     * @return
     */
    private TradeExchangeApiConstant.OrderStatus convertOrderStatus(OrderStatus orderStatus){
        if (orderStatus == OrderStatus.NEW) {
            return TradeExchangeApiConstant.OrderStatus.COMMIT;
        } else if (orderStatus == OrderStatus.PARTIALLY_FILLED) {
            return TradeExchangeApiConstant.OrderStatus.PART;
        } else if (orderStatus == OrderStatus.FILLED) {
            return TradeExchangeApiConstant.OrderStatus.DEAL;
        } else if (orderStatus == OrderStatus.CANCELED) {
            return TradeExchangeApiConstant.OrderStatus.CANCEL;
        } else if (orderStatus == OrderStatus.PENDING_CANCEL) {
            return TradeExchangeApiConstant.OrderStatus.COMMIT;
        } else if (orderStatus == OrderStatus.REJECTED) {
            return TradeExchangeApiConstant.OrderStatus.FAIL;
        }
        return TradeExchangeApiConstant.OrderStatus.UNKNOW;
    }

    /**
     * 转化订单
     *
     * @param newOrderResponse
     * @param binanceResponse
     */
    private void convert(NewOrderResponse newOrderResponse, BinanceResponse binanceResponse){
        BigDecimal amount = StringUtils.isNotBlank(newOrderResponse.getOrigQty()) ? new BigDecimal(newOrderResponse.getOrigQty()) : BigDecimal.ZERO;
        BigDecimal filledAmount = StringUtils.isNotBlank(newOrderResponse.getExecutedQty()) ? new BigDecimal(newOrderResponse.getExecutedQty()) : BigDecimal.ZERO;
        BigDecimal price = StringUtils.isNotBlank(newOrderResponse.getPrice()) ? new BigDecimal(newOrderResponse.getPrice()) : BigDecimal.ZERO;
        // 已成交的金额
        BigDecimal filledCashAmount = StringUtils.isNotBlank(newOrderResponse.getCummulativeQuoteQty()) ? new BigDecimal(newOrderResponse.getCummulativeQuoteQty()) : BigDecimal.ZERO;
        BigDecimal filledPrice = BigDecimal.ZERO;
        if(filledAmount.compareTo(BigDecimal.ZERO) == 1){
            filledPrice = filledCashAmount.divide(filledAmount, filledCashAmount.scale(), BigDecimal.ROUND_HALF_UP);
        }

        binanceResponse.setAmount(amount);
        binanceResponse.setFilledAmount(filledAmount);
        binanceResponse.setOrderId(String.valueOf(newOrderResponse.getOrderId()));
        binanceResponse.setOrderSide(convertOrderSide(newOrderResponse.getSide()));
        binanceResponse.setOrderType(convertOrderType(newOrderResponse.getType()));
        binanceResponse.setPrice(price);
        binanceResponse.setSpotTransId(Long.valueOf(newOrderResponse.getClientOrderId()));
        binanceResponse.setOrderStatus(convertOrderStatus(newOrderResponse.getStatus()));
        binanceResponse.setSymbol(symbol(newOrderResponse.getSymbol()));
        binanceResponse.setFinishedAt(new Date(newOrderResponse.getTransactTime()));
        binanceResponse.setFilledCashAmount(filledCashAmount);
        binanceResponse.setFilledPrice(filledPrice);
        binanceResponse.setLeftAmount(amount.subtract(filledAmount));
    }

    private Res isHarkWithdrawalOk(String result,String desc){
        log.info(result);
        if(StringUtils.isBlank(result)){
            String temp = "调用币安"+desc+"失败：返回结果为空";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }

        JSONObject resultJson = JSONObject.parseObject(result);

        if(!resultJson.containsKey("success")){
            String temp = "调用币安"+desc+"失败，返回信息:"+result;
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
        }

        Boolean success = resultJson.getBoolean("success");
        if(!success) {
            String temp = "调用币安"+desc+"失败，返回信息:"+result;
            log.error(temp);
//            {"msg":"API key does not exist","success":false}
            String msg = resultJson.getString("msg");
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,msg);
        }

        return ResFactory.getInstance().success(null);

    }

    @Override
    public Res<DepositAddressResDto> depositAddress(Req<DepositAddressReqDto> depositAddressReqDtoReq) {
        if (null == depositAddressReqDtoReq || null == depositAddressReqDtoReq.getData()) {
            String temp = "必填参数为空";
            log.error(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        DepositAddressReqDto depositAddressReqDto = depositAddressReqDtoReq.getData();
        String coinName = depositAddressReqDto.getCoinName();
        if(StringUtils.isBlank(coinName)){
            String temp = "必填参数为空";
            log.error(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        IBinanceStockRestApi binanceStockRestApi = new BinanceStockRestApi(depositAddressReqDto.getApiKey(),depositAddressReqDto.getApiSecret());
        String result = null;
        try {
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("coin",coinName.toUpperCase());
            if(StringUtils.equals(coinName.toUpperCase(),"USDT")){
                paramMap.put("network","OMNI");
            }
            result = binanceStockRestApi.depositAddress(paramMap);

//            成功：
//            {
//                "address": "1HPn8Rx2y6nNSfagQBKy27GB99Vbzg89wv",
//                "coin": "BTC",
//                "tag": "",
//                "url": "https://btc.com/1HPn8Rx2y6nNSfagQBKy27GB99Vbzg89wv"
//            }

            if (StringUtils.isBlank(result)) {
                String temp = "调用 binance 查询币种充值地址信息失败，交易所返回为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject jsonObject = JSONObject.parseObject(result);
            String address = jsonObject.getString("address");
            if (StringUtils.isBlank(address)) {
                String temp = "调用 binance 查询币种充值地址信息失败，币种名称："+depositAddressReqDto.getCoinName()+"交易所返回:" + result;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            DepositAddressResDto depositAddressResDto = new DepositAddressResDto();
            depositAddressResDto.setAddress(address);
            depositAddressResDto.setCoinName(depositAddressReqDto.getCoinName());
            return ResFactory.getInstance().success(depositAddressResDto);
        } catch (Throwable throwable) {
            String temp = "调用 binance 查询币种充值地址信息异常";
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
                tickerPriceReqDto.setExchCode(ExchangeCode.BINANCE);

                tickerPriceReqDto.setSymbol(symbol.toUpperCase());
                Req<TickerPriceReqDto> tickerPriceReqDtoReq = ReqFactory.getInstance().createReq(tickerPriceReqDto);
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
