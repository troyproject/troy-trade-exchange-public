package com.troy.trade.exchange.bitfinex.service;

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
import com.troy.commons.utils.CalculateUtil;
import com.troy.commons.utils.DateUtils;
import com.troy.trade.exchange.api.exception.TradeExchangeApiException;
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import com.troy.trade.exchange.api.model.dto.in.account.AccountInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.account.HarkWithdrawalReqDto;
import com.troy.trade.exchange.api.model.dto.in.account.QueryBalanceReqDto;
import com.troy.trade.exchange.api.model.dto.in.account.WithdrawalReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.CoinInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.*;
import com.troy.trade.exchange.api.model.dto.out.account.AccountInfoResDto;
import com.troy.trade.exchange.api.model.dto.out.account.ExchAcctDeptWdralResDto;
import com.troy.trade.exchange.api.model.dto.out.account.QueryBalanceResDto;
import com.troy.trade.exchange.api.model.dto.out.account.WithdrawalResDto;
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
import com.troy.trade.exchange.bitfinex.client.BitfinexConstant;
import com.troy.trade.exchange.bitfinex.client.IBitfinexStockRestApi;
import com.troy.trade.exchange.bitfinex.client.impl.BitfinexStockRestApi;
import com.troy.trade.exchange.bitfinex.dto.BitfinexCreateOrderRequest;
import com.troy.trade.exchange.bitfinex.dto.BitfinexHarkWithdrawalStatusEnum;
import com.troy.trade.exchange.bitfinex.dto.BitfinexOrderSide;
import com.troy.trade.exchange.bitfinex.dto.BitfinexOrderType;
import com.troy.trade.exchange.core.constant.ExchangeConstant;
import com.troy.trade.exchange.core.service.IExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * bitfinex交易所服务实现
 *
 * @author dp
 */
@Slf4j
@Component
public class BitfinexExchangeServiceImpl implements IExchangeService {

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.BITFINEX;
    }

    @Override
    public String toTradeSymbol(String symbol) {
        return SymbolUtil.ToTradeSymbol.lowerCaseSymbol(symbol);
    }

    @Override
    public String symbol(String tradeSymbol) {
        // btcusd
        if (StringUtils.isBlank(tradeSymbol)) {
            return null;
        }
        // usd、eur、gbp、jpy、btc、eth、eos、xlm、dai、ust、xch
        String coinName = tradeSymbol.substring(0, tradeSymbol.length() - 3);
        String moneyName = tradeSymbol.substring(tradeSymbol.length() - 3);
        return new StringBuffer(coinName).append("/").append(moneyName).toString().toUpperCase();
    }

    private String V2_symbol(String tradeSymbol) {
        // btcusd
        if (StringUtils.isBlank(tradeSymbol)) {
            return null;
        }
        if(tradeSymbol.startsWith("t")){
            tradeSymbol = tradeSymbol.substring(1);
        }
        // usd、eur、gbp、jpy、btc、eth、eos、xlm、dai、ust、xch
        String coinName = tradeSymbol.substring(0, tradeSymbol.length() - 3);
        String moneyName = tradeSymbol.substring(tradeSymbol.length() - 3);
        return new StringBuffer(coinName).append("/").append(moneyName).toString().toUpperCase();
    }


    private String V2_tradeSymbol(String symbol) {
        if (StringUtils.isBlank(symbol)) {
            return null;
        }
        String tradeSymbol = symbol.replace("/","").toUpperCase();
        tradeSymbol = "t"+tradeSymbol;
        return tradeSymbol;
    }

    @Override
    public Res<CreateOrderResDto> createOrder(Req<CreateOrderReqDto> createOrderReqDtoReq) {
        CreateOrderReqDto createOrderReqDto = createOrderReqDtoReq.getData();
        final String appkey = createOrderReqDto.getApiKey();
        final String appsecret = createOrderReqDto.getApiSecret();
        final String currencyPair = createOrderReqDto.getTradeSymbol();// 交易对
        int direction = createOrderReqDto.getOrderSide().code();// 购买方向（1-买 2-卖）
        Integer transType = createOrderReqDto.getOrderType().code();//交易类型（1-限价交易 2-市价交易）

        IBitfinexStockRestApi bitfinexStockRestApi = new BitfinexStockRestApi(appkey, appsecret);
        BitfinexCreateOrderRequest createOrderRequest = new BitfinexCreateOrderRequest();
        createOrderRequest.setSymbol(currencyPair);
        createOrderRequest.setAmount(createOrderReqDto.getAmount().toPlainString());

        if (OrderSideEnum.BID.code().equals(direction)) {//限价买入
            createOrderRequest.setSide(BitfinexOrderSide.BUY);
        } else {//限价卖出
            createOrderRequest.setSide(BitfinexOrderSide.SELL);
        }

        if (TradeExchangeApiConstant.OrderType.LIMIT.code().equals(transType)) {//交易类型，限价交易
            createOrderRequest.setPrice(createOrderReqDto.getPrice().toPlainString());
            createOrderRequest.setType(BitfinexOrderType.LIMIT);
        } else {//交易类型，市价交易
            BigDecimal min = new BigDecimal(0.00000001);
            BigDecimal max = new BigDecimal(0.00000090);
            BigDecimal random = generateRandomBigDecimalFromRange(min, max, 8);
            createOrderRequest.setPrice(random.toPlainString());
            createOrderRequest.setType(BitfinexOrderType.MARKET);
        }

        String result;
        String orderId;
        try {
            result = bitfinexStockRestApi.createOrder(createOrderRequest);
            if (StringUtils.isBlank(result)) {
                log.warn("调用bitfinex下单失败，第三方返回:[{}]", result);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);

            }
            JSONObject json = JSONObject.parseObject(result);
            if (!json.containsKey("order_id")) {
                log.warn("调用bitfinex下单失败，第三方返回:[{}]", result);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT, result);

            }
            orderId = json.getString("order_id");
        } catch (Exception e) {
            log.error("调用bitfinex下单失败：", e);
            throw new CallExchangeRemoteException(e, e.getLocalizedMessage());
        }
        return ResFactory.getInstance().success(new CreateOrderResDto(orderId));

    }

    @Override
    public Res<CancelOrderResDto> cancelOrder(Req<CancelOrderReqDto> cancelOrderReqDtoReq) {
        CancelOrderReqDto cancelOrderReqDto = cancelOrderReqDtoReq.getData();
        Assert.notNull(cancelOrderReqDto, TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_DATA, "撤单数据不能为空");
        final List<String> orderIds = cancelOrderReqDto.getOrderIds();
        log.info("调用bitfinex撤销订单，开始，本次撤销订单ID列表大小为：{}", orderIds.size());
        Assert.notEmpty(orderIds, TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_LIST, "撤单列表不能为空");

        List<String> successOrderIds = Lists.newArrayList();
        List<String> failOrderIds = Lists.newArrayList();

        IBitfinexStockRestApi stock = new BitfinexStockRestApi(cancelOrderReqDto.getApiKey(), cancelOrderReqDto.getApiSecret());

        try {
            String cancelOrderReturn;
            if (orderIds.size() > 1) {
                // 调用全部撤单
                // 下单类型(0:卖出,1:买入,-1:不限制)
                cancelOrderReturn = stock.cancelMultiOrders(orderIds);
                if (StringUtils.isNotBlank(cancelOrderReturn)) {
                    JSONObject jsonObject = JSONObject.parseObject(cancelOrderReturn);
                    String resultStr = jsonObject.getString("result");
                    if (StringUtils.isNotBlank(resultStr)) {
                        successOrderIds.addAll(orderIds);
                    } else {
                        failOrderIds.addAll(orderIds);
                    }
                }
            } else {
                // 单个撤单
                String orderNumber = orderIds.get(0);
                cancelOrderReturn = stock.cancelOrder(orderNumber);
                if (StringUtils.isNotBlank(cancelOrderReturn)) {
                    JSONObject jsonObject = JSONObject.parseObject(cancelOrderReturn);
                    String id = jsonObject.getString("id");
                    if (StringUtils.isNotBlank(id)) {
                        successOrderIds.add(orderNumber);
                    } else {
                        failOrderIds.add(orderNumber);
                    }
                }
            }
            log.debug("调用bitfinex撤单返回数据：{}", cancelOrderReturn);
        } catch (Exception e) {
            log.error("调用bitfinex撤单Exception异常", e);
            throw new CallExchangeRemoteException(e, e.getLocalizedMessage());
        }
        return ResFactory.getInstance().success(new CancelOrderResDto(successOrderIds, failOrderIds));
    }

    @Override
    public Res<OrderDetailResDto> orderDetail(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        final String appkey = orderDetailReqDto.getApiKey();
        final String appsecret = orderDetailReqDto.getApiSecret();
        final String orderId = orderDetailReqDto.getOrderId();
        final String symbol = orderDetailReqDto.getSymbol();
        IBitfinexStockRestApi bitfinexStockRestApi = new BitfinexStockRestApi(appkey, appsecret);

        String result = null;
        try {
            result = bitfinexStockRestApi.getOrder(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(result)) {
            log.warn("查询订单信息[{}]--bitfinex返回为空", result);
            throw new CallExchangeRemoteException("查询订单信息[" + orderId + "]--bitfinex返回为空");
        }

        JSONObject json = JSONObject.parseObject(result);
        if (!json.containsKey("symbol")) {
            log.warn("查询订单信息[{}]--bitfinex失败", result);
            throw new CallExchangeRemoteException("调用bitfinex查询订单信息失败，第三方返回:" + result);
        }


        JSONObject resultJSON = JSONObject.parseObject(result);
        OrderDetailResDto orderDetailDto = transfer(resultJSON);
        orderDetailDto.setSymbol(toTradeSymbol(symbol));
        return ResFactory.getInstance().success(orderDetailDto);

    }

    /**
     * 转换bitfinex返回对象为可解析的ordersDetail
     *
     * @param resultJSON
     * @return
     */
    public OrderDetailResDto transfer(JSONObject resultJSON) {
        OrderDetailResDto orderDetailDto = new OrderDetailResDto();
        orderDetailDto.setOrderId(String.valueOf(resultJSON.getInteger("id")));

        BigDecimal avgPrice = new BigDecimal(resultJSON.getString("avg_execution_price"));

        String type = resultJSON.getString("type");
        if (type.equals(BitfinexOrderType.LIMIT.getValue())) {//限价
            orderDetailDto.setPrice(new BigDecimal(resultJSON.getString("price")));
            orderDetailDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        } else if (type.equals(BitfinexOrderType.MARKET.getValue())) {//市价
            orderDetailDto.setPrice(avgPrice);
            orderDetailDto.setOrderType(TradeExchangeApiConstant.OrderType.MARKET);
        }

        BigDecimal original_amount = new BigDecimal(resultJSON.getString("original_amount"));
        orderDetailDto.setAmount(original_amount);

        BigDecimal remaining_amount = new BigDecimal(resultJSON.getString("remaining_amount"));
        orderDetailDto.setLeftAmount(remaining_amount);

        BigDecimal executed_amount = new BigDecimal(resultJSON.getString("executed_amount"));
        orderDetailDto.setFilledAmount(executed_amount);

        orderDetailDto.setFilledPrice(avgPrice);

        BigDecimal filledCashAmount = executed_amount.multiply(avgPrice);//已成交总金额
        orderDetailDto.setFilledCashAmount(filledCashAmount);

        String side = resultJSON.getString("side");
        if (side.equals(BitfinexOrderSide.BUY.getValue())) {//买入
            orderDetailDto.setOrderSide(OrderSideEnum.BID);
        } else {//卖出
            orderDetailDto.setOrderSide(OrderSideEnum.ASK);
        }

        boolean is_live = resultJSON.getBoolean("is_live");//是否还有货
        boolean is_cancelled = resultJSON.getBoolean("is_cancelled");//是否已取消
        Integer executedCompareTo = CalculateUtil.compareTo(executed_amount, BigDecimal.ZERO);//0-没有成交数量、1-有成交数量
        TradeExchangeApiConstant.OrderStatus status = null;
        if (is_live) {//有余货
            if (executedCompareTo == 0) {//没有成交数量
                status = TradeExchangeApiConstant.OrderStatus.COMMIT;//已提交
            } else if (executedCompareTo == 1) {//有成交数量
                status = TradeExchangeApiConstant.OrderStatus.PART;//部分成交
            }
        } else {
            status = TradeExchangeApiConstant.OrderStatus.DEAL;//已成交
        }

        if (is_cancelled) {//已取消
            status = TradeExchangeApiConstant.OrderStatus.CANCEL;//已取消
        }
        orderDetailDto.setOrderStatus(status);//交易状态（0-初始；1-部分成交；2-已撤销；3-已成交；10-已提交；11-失败；12-状态未知）

        orderDetailDto.setFinishedAt(new Date());
        return orderDetailDto;
    }

    /**
     * 生成指定范围内随机的BigDecimal
     *
     * @param min
     * @param max
     * @param scale
     * @return
     */
    public static BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max, int scale) {
        BigDecimal randomBigDecimal = (min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)))).setScale(scale, BigDecimal.ROUND_HALF_UP);
        return randomBigDecimal;
    }

    @Override
    public Res<OrderListResData> orderList(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        final String appkey = orderDetailReqDto.getApiKey();
        final String appsecret = orderDetailReqDto.getApiSecret();
        final String orderId = orderDetailReqDto.getOrderId();
        final String symbol = orderDetailReqDto.getSymbol();
        IBitfinexStockRestApi bitfinexStockRestApi = new BitfinexStockRestApi(appkey, appsecret);

        String result = null;
        try {
            result = bitfinexStockRestApi.tradeHistory(toTradeSymbol(symbol), orderDetailReqDto.getLimit());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(result)) {
            log.warn("查询订单信息列表[{}]--bitfinex返回为空", result);
            throw new CallExchangeRemoteException("查询订单信息列表[" + orderId + "]--bitfinex返回为空");
        }
        if (result.contains("200")) {
            JSONArray resultJSON = JSONArray.parseArray(result);
            List<OrderDetailResDto> orderDetailDto = transferOrderListReturn(resultJSON);
            return ResFactory.getInstance().success(new OrderListResData(orderDetailDto));
        }
        return ResFactory.getInstance().success(new OrderListResData());
    }

    @Override
    public Res<OrderListResData> getOpenOrders(Req<OpenOrdersReqDto> ordersReqDtoReq) {

        return ResFactory.getInstance().success(new OrderListResData());
    }

    @Override
    public Res<ResList<ExchAcctDeptWdralResDto>> harkWithdrawal(Req<HarkWithdrawalReqDto> harkWithdrawalReqDtoReq) {
       HarkWithdrawalReqDto harkWithdrawalReqDto = harkWithdrawalReqDtoReq.getData();
       String coinName = harkWithdrawalReqDto.getCoinName();
       if(StringUtils.isBlank(coinName)){
           String temp = "调用 bitfinex 查找充提币记录信息失败：必填参数为空";
           log.warn(temp);
           throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
       }

       final String apikey = harkWithdrawalReqDto.getApiKey();
       final String apisecret = harkWithdrawalReqDto.getApiSecret();
       IBitfinexStockRestApi bitfinexStockRestApi = new BitfinexStockRestApi(apikey, apisecret);

//           currency The currency to look for. For an up-to-date listing of supported currencies see: https://dddddd.bitfinex.com/v2/conf/pub:map:currency:label
//           method The method of the deposit/withdrawal (can be “bitcoin”, “litecoin”, “darkcoin”, “wire”).
//           since Return only the history after this timestamp.-- timestamp_created
//           until Return only the history before this timestamp. timestamp_created
//           limit Limit the number of entries to return.


       Map<String, Object> paramMap = new HashMap<>();
       paramMap.put("currency",coinName);
       String startTime = harkWithdrawalReqDto.getStartTime();
       if(StringUtils.isNotBlank(startTime)){ // 这个条件有用
//               转换为到秒
           Long startTimeLong = Long.parseLong(startTime);
           startTimeLong = startTimeLong/1000;
           paramMap.put("since",startTimeLong);
       }

       String endTime = harkWithdrawalReqDto.getEndTime();
       if(StringUtils.isNotBlank(endTime)){
           Long endTimeLong = Long.parseLong(endTime);
           endTimeLong = endTimeLong/1000;
           paramMap.put("until",endTimeLong);
       }

       Integer limit = harkWithdrawalReqDto.getPageSize();
       if(null != limit){
           paramMap.put("limit",limit);
       }
        String result = null;
        try{
           result = bitfinexStockRestApi.harkWithdrawalHistory(paramMap);
        }catch (Throwable throwable){
            String temp = "调用 bitfinex 查找充提币记录信息异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(throwable);
        }
       log.info("调用 bitfinex 查找充提币记录信息返回：{}",result);
       if(StringUtils.isBlank(result)){
           String temp = "调用 bitfinex 查找充提币记录信息失败：交易所返回空";
           log.warn(temp);
           throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
       }

       if(!result.startsWith("[")){
           String temp = "调用 bitfinex 查找充提币记录信息失败,交易所返回:"+result;
           log.warn(temp);
           throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
       }

//           成功：
//            [{
//               "id":581183,
//               "txid": 123456,
//               "currency":"BTC",
//               "method":"BITCOIN",
//               "type":"WITHDRAWAL",
//               "amount":".01",
//               "description":"3QXYWgRGX2BPYBpUDBssGbeWEa5zq6snBZ, offchain transfer ",
//               "address":"3QXYWgRGX2BPYBpUDBssGbeWEa5zq6snBZ",
//               "status":"COMPLETED",
//               "timestamp":"1443833327.0",
//               "timestamp_created": "1443833327.1",
//               "fee": 0.1
//           }]
       List<ExchAcctDeptWdralResDto> resultList = turnHarkWithdrawal(result);
       return ResFactory.getInstance().successList(resultList);

    }

    @Override
    public Res<WithdrawalResDto> withdraw(Req<WithdrawalReqDto> withdrawalReqDtoReq) {

        if(null == withdrawalReqDtoReq){
            String temp = "调用 bitfinex 提现 失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        WithdrawalReqDto withdrawalReqDto = withdrawalReqDtoReq.getData();
        if(null == withdrawalReqDto){
            String temp = "调用 bitfinex 提现 失败，必填参数为空";
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
            String temp = "调用 bitfinex 提现 失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        //查找method字段
        String method = this.getCurrencyLabel(coinName);
        if(StringUtils.isBlank(method)){
            String temp = "调用 bitfinex 提现 失败，查找method失败，coinName="+coinName;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
        }

//            wallet* String Select an origin wallet for your withdrawl ("trading" = margin, "exchange" = exchange, "deposit" = funding)
//            method* String Method of withdrawal (methods accepted: “bitcoin”, “litecoin”, “ethereum”, “tetheruso", “tetherusl", “tetherusx", “tetheruss", "ethereumc", "zcash", "monero", "iota"). For an up-to-date listing of supported currencies see: https://dddddd.bitfinex.com/v2/conf/pub:map:currency:label
//            amount* String Amount of Withdrawal
//            address* String Destination address
        String result = "";
        try {
            Map<String,Object> paramMap = new HashMap<>();
            paramMap.put("wallet","exchange");
            paramMap.put("method",method.toLowerCase());
            paramMap.put("amount",amountBig.toPlainString());
            paramMap.put("address",address);
//
            IBitfinexStockRestApi bitfinexStockRestApi = new BitfinexStockRestApi(withdrawalReqDto.getApiKey(),withdrawalReqDto.getApiSecret());
            result = bitfinexStockRestApi.V2_withdraw(paramMap);
        } catch (Throwable throwable) {
            String temp = "调用 bitfinex 提现 异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }
//            成功：
//            [   1568742390999, -- MTS
//                "acc_wd-req", -- TYPE
//                null, -- MESSAGE_ID
//                null, -- null
//                [13080092, -- WITHDRAWAL_ID
//                    null, -- _PLACEHOLDER
//                    "ethereum", -- METHOD
//                    null, -- PAYMENT_ID
//                    "exchange", -- WALLET
//                    0.01,-- AMOUNT
//                    null, -- _PLACEHOLDER
//                    null, -- _PLACEHOLDER
//                    0.00135 -- WITHDRAWAL_FEE
//                ],
//                null, -- CODE
//                "SUCCESS", -- STATUS
//                "Your withdrawal request has been successfully submitted." -- TEXT
//            ]

        if(StringUtils.isBlank(result)){
            String temp = "调用 bitfinex 提现 失败，交易所返回空";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }

        if(!result.startsWith("[")){
            String temp = "调用 bitfinex 提现 失败，交易所返回:"+result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }

        JSONArray resultArr = JSONArray.parseArray(result);
        int size = resultArr == null?0:resultArr.size();
        if(size<=0){
            String temp = "调用 bitfinex 提现 失败，交易所返回:"+result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }

        JSONArray childArr = resultArr.getJSONArray(4);
        int childSize = childArr == null?0:childArr.size();
        if(childSize<=0){
            String temp = "调用 bitfinex 提现 失败，交易所返回:"+result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }

        String id = childArr.getString(0);
        if(StringUtils.isBlank(id)){
            String temp = "调用 bitfinex 提现 失败，交易所返回:"+result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }
        WithdrawalResDto withdrawalResDto = new WithdrawalResDto();
        withdrawalResDto.setThirdId(id);
        return ResFactory.getInstance().success(withdrawalResDto);
    }

    private List<ExchAcctDeptWdralResDto> turnHarkWithdrawal(String result){
        List<ExchAcctDeptWdralResDto> resultList = new ArrayList<>();
        ExchAcctDeptWdralResDto exchAcctDeptWdralResDto = null;
        JSONArray resultArr = JSONArray.parseArray(result);
        JSONObject childJSON = null;

        String thirdId = null;//第三方返回ID
        String coinName = null;
        BigDecimal amount = null;//数量
        String txId = null;//提币哈希记录
        String address = null;//地址
        Integer deptWdralType = null;//充提类型，1-充值、2-提现
        BigDecimal fee = null;//手续费
        Date applyTime = null;//提申请时间
        Integer status = null;//状态，1-申请中、2-已完成、3-已取消、4-失败

        String type = null;
        String timestampCreatedStr = null;
        Long timestampCreated = null;
        String bitfinexStatus = null;

        int size = resultArr == null?0:resultArr.size();
        for(int i=0;i<size;i++){
            childJSON = resultArr.getJSONObject(i);
            thirdId = childJSON.getString("id");
            coinName = childJSON.getString("currency");
            amount = childJSON.getBigDecimal("amount");
            txId = childJSON.getString("txid");
            address = childJSON.getString("address");
            type = childJSON.getString("type");
            if(StringUtils.equals(type,"WITHDRAWAL")){
                deptWdralType = ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_WITHDRAWAL;
            }else{
                deptWdralType = ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_DEPOSIT;
            }
            fee = childJSON.getBigDecimal("fee");
            timestampCreated = null;
            timestampCreatedStr = childJSON.getString("timestamp_created");
            if(StringUtils.isNotBlank(timestampCreatedStr)){
                BigDecimal temp = new BigDecimal(timestampCreatedStr);
                temp = CalculateUtil.multiply(temp,new BigDecimal("1000")).setScale(0);
                timestampCreated = temp.longValue();
            }

            applyTime = null;
            if(null != timestampCreated){
                applyTime = DateUtils.parse(timestampCreated);
            }

            bitfinexStatus = childJSON.getString("status");
            status = BitfinexHarkWithdrawalStatusEnum.getStatus(bitfinexStatus);

            exchAcctDeptWdralResDto = ExchAcctDeptWdralResDto.getInstance(thirdId,coinName,amount,txId,address,
                    deptWdralType,fee,applyTime,status,null);
            resultList.add(exchAcctDeptWdralResDto);

//            {
//               "id":581183,
//               "txid": 123456,
//               "currency":"BTC",
//               "method":"BITCOIN",
//               "type":"WITHDRAWAL",
//               "amount":".01",
//               "description":"3QXYWgRGX2BPYBpUDBssGbeWEa5zq6snBZ, offchain transfer ",
//               "address":"3QXYWgRGX2BPYBpUDBssGbeWEa5zq6snBZ",
//               "status":"COMPLETED",
//               "timestamp":"1443833327.0",
//               "timestamp_created": "1443833327.1",
//               "fee": 0.1
//           }

        }
        return resultList;
    }

    /**
     * 转换三方返回的订单列表实体
     *
     * @param orders
     * @return
     */
    private List<OrderDetailResDto> transferOrderListReturn(JSONArray orders) {
        List<OrderDetailResDto> list = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (Object order : orders) {
                list.add(transfer((JSONObject) order));
            }
            return list;
        }
        return list;
    }

    /**
     * 查询币种简称
     * @return
     */
    private String getCurrencyLabel(String coinName){
        String label = null;
        try {
            if (StringUtils.isBlank(coinName)) {
                return label;
            }

            String coinNameUpper = coinName.toUpperCase();

            IBitfinexStockRestApi bitfinexStockRestApi = new BitfinexStockRestApi();
            String result = bitfinexStockRestApi.V2_currency_label();
//            成功：
//            [
//                [
//                    ["ABS", "The Abyss"],
//                    ["ADD", "ADD"],
//                    ["AGI", "SingularityNET"],
//                    ["AID", "AidCoin"]
//                ]
//            ]
            if(StringUtils.isBlank(result)){
                log.warn("调用bitfinex查询币种简称失败，交易所返回为空");
                return label;
            }
            if(!result.startsWith("[")){
                log.warn("调用bitfinex查询币种简称失败，交易所返回:{}",result);
                return label;
            }

            JSONArray resultArr = JSONArray.parseArray(result);
            int resultSize = resultArr == null?0:resultArr.size();
            if(resultSize>0){
                JSONArray childArr = resultArr.getJSONArray(0);
                JSONArray grandsonArr = null;
                int childArrSize = childArr == null?0:childArr.size();
                for(int i=0;i<childArrSize;i++){
                    grandsonArr = childArr.getJSONArray(i);
                    if(StringUtils.equals(coinNameUpper,grandsonArr.getString(0))){
                        label = grandsonArr.getString(1);
                        break;
                    }
                }
            }
        }catch (Throwable throwable){
            log.error("调用bitfinex查询币种简称异常，异常信息：",throwable);
        }
        return label;
    }

    @Override
    public Res<OrderBookResDto> getOrderBook(Req<OrderBookReqDto> orderBookReqDtoReq) {
        try {
            OrderBookReqDto orderBookReqDto = orderBookReqDtoReq.getData();
            if (null == orderBookReqDto) {
                String temp = "调用 bitfinex 查询买卖挂单记录失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }
            String symbol = orderBookReqDto.getSymbol();
            String tradeSymbol = this.toTradeSymbol(symbol);
            IBitfinexStockRestApi stock = new BitfinexStockRestApi();
            Integer limit = orderBookReqDto.getLimit();
            if (null == limit) {
                limit = 30;
            }
            String orderBooks = stock.getOrderBook(tradeSymbol, limit, limit);
            //成功：
//            {
//                "bids": [
//                    {
//                        "price": "10394",
//                        "amount": "0.39766529",
//                        "timestamp": "1566814482.0"
//                    }
//                ],
//                "asks": [
//                    {
//                        "price": "10395",
//                        "amount": "4.11316879",
//                        "timestamp": "1566814482.0"
//                    }
//                ]
//            }

            if (StringUtils.isBlank(orderBooks)) {//交易所返回为空
                String temp = "调用 bitfinex 查询买卖挂单记录失败，失败原因：交易所返回为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject jsonObject = JSONObject.parseObject(orderBooks);
            String price = null;
            String amount = null;


            JSONArray asksArr = jsonObject.getJSONArray("asks");
            List<List<String>> asksList = new ArrayList<>();
            int asksSize = asksArr == null ? 0 : asksArr.size();
            JSONObject askJson = null;

            for (int i = 0; i < asksSize; i++) {
                askJson = asksArr.getJSONObject(i);
                price = askJson.getString("price");
                amount = askJson.getString("amount");
                List<String> temp = new ArrayList<>();
                temp.add(price);
                temp.add(amount);
                asksList.add(temp);
            }

            Collections.reverse(asksList);

            JSONArray bidsArr = jsonObject.getJSONArray("bids");
            List<List<String>> bidsList = new ArrayList<>();
            int bidsSize = bidsArr == null ? 0 : bidsArr.size();
            JSONObject bidJson = null;

            for (int i = 0; i < bidsSize; i++) {
                bidJson = bidsArr.getJSONObject(i);
                price = bidJson.getString("price");
                amount = bidJson.getString("amount");
                List<String> temp = new ArrayList<>();
                temp.add(price);
                temp.add(amount);
                bidsList.add(temp);
            }

            OrderBookResDto orderBookResDto = new OrderBookResDto(asksList, bidsList);

            log.debug("查找 bitfinex 查找orderbook 记录 返回结果：" + JSONObject.toJSONString(orderBookResDto));
            return ResFactory.getInstance().success(orderBookResDto);
        } catch (Throwable throwable) {
            String temp = "调用 bitfinex 查找orderBook信息异常,异常信息：";
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
                String temp = "调用 bitfinex 查询历史成交记录失败，失败原因：必填参数symbol为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            String tradeSymbol = this.toTradeSymbol(tradeHistoryReqDto.getSymbol()).toUpperCase();
            IBitfinexStockRestApi stock = new BitfinexStockRestApi();
            String trades = stock.tradeHistoryV2(tradeSymbol, 30);

            //成功：[ID,毫秒值,amount,price] amount-买入(正值)或卖出(负值)
//            [
//                [386390101, 1566469700554, 0.00951274, 10055.693456],
//                [386390103, 1566469700554, 0.00221726, 10055.76565219],
//                [386390100, 1566469698258, -0.00972, 10057]
//            ]

            if (StringUtils.isBlank(trades)) {//交易所返回为空
                String temp = "调用 bitfinex 查询历史成交记录失败，失败原因：交易所返回为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONArray tradeArray = JSONArray.parseArray(trades);
            List<TradeHistoryResDto> tradeHistoryResDtoList = new ArrayList<>();
            int arrSize = tradeArray.size();
            if (arrSize <= 0) {//交易所返回为空
                TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
                tradeHistoryListResDto.setTradeHistoryResDtoList(tradeHistoryResDtoList);
                return ResFactory.getInstance().success(tradeHistoryListResDto);
            }

            TradeHistoryResDto tempTradeHistoryResDto = null;
            OrderSideEnum orderSide = null;
            int length = 0;
            for (int i = 0; i < arrSize; i++) {//[ID,毫秒值,amount,price] amount-买入(正值)或卖出(负值)
                JSONArray array = JSONArray.parseArray(tradeArray.get(i).toString());
                length = array.get(2).toString().indexOf("-");
                if (length != -1) {//
                    orderSide = OrderSideEnum.ASK;
                } else {
                    orderSide = OrderSideEnum.BID;
                }
                String amount = array.get(2).toString().replaceAll("-", "");

//              TradeExchangeApiConstant.OrderSide orderSide, String amount, String symbol, String price, Long timestamp, String id) {
                tempTradeHistoryResDto = new TradeHistoryResDto(orderSide, amount,
                        symbol, array.get(3).toString(),
                        array.getLong(1),
                        array.get(0).toString());
                tradeHistoryResDtoList.add(tempTradeHistoryResDto);
            }

            TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
            tradeHistoryListResDto.setTradeHistoryResDtoList(tradeHistoryResDtoList);
            return ResFactory.getInstance().success(tradeHistoryListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 bitfinex 查询历史成交记录异常，异常信息：";
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
                String temp = "调用 bitfinex 查询 交易对信息 失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            SymbolInfoReqDto symbolInfoReqDto = symbolInfoReqDtoReq.getData();

            IBitfinexStockRestApi stock = new BitfinexStockRestApi();
            String result = stock.symbolDetail();

            if (StringUtils.isBlank(result)) {
                String temp = "调用 bitfinex 查找 交易对信息 记录失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            if (!result.startsWith("[")) {
                String temp = "调用 bitfinex 查找 交易对信息 记录失败,返回结果" + result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

//            成功：
//            [
//                {
//                    "pair": "btcusd",
//                    "price_precision": 5,
//                    "initial_margin": "30.0",
//                    "minimum_margin": "15.0",
//                    "maximum_order_size": "2000.0",
//                    "minimum_order_size": "0.0004",
//                    "expiration": "NA",
//                    "margin": true
//                }
//            ]

            //返回结果
            List<SymbolInfoResDto> symbolInfoResDtoList = new ArrayList<>();
            SymbolInfoResDto symbolInfoResDto = null;
            JSONArray resultJSONArray = JSONArray.parseArray(result);
            JSONObject childJson = null;
            String[] coinArr = null;
            int size = resultJSONArray.size();
            for (int i = 0; i < size; i++) {
                childJson = resultJSONArray.getJSONObject(i);
                String pair = childJson.getString("pair");
                Integer decimalPlaces = childJson.getInteger("price_precision");
                BigDecimal minAmount = childJson.getBigDecimal("minimum_order_size");
                Integer basePrecision = minAmount.scale();//货币精度
                Integer quotePrecision = decimalPlaces;//钱币精度
                String symbol = this.symbol(pair);
                coinArr = symbol.split("/");

                String baseName = coinArr[0];
                String quoteName = coinArr[1];
                BigDecimal baseLeast = minAmount;

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

            SymbolInfoListResDto symbolInfoListResDto = new SymbolInfoListResDto();
            symbolInfoListResDto.setExchangeCode(symbolInfoReqDto.getExchCode());
            symbolInfoListResDto.setSymbolInfoResDtoList(symbolInfoResDtoList);
            log.debug("调用 bitfinex 查找 交易对信息 记录 返回结果：" + JSONObject.toJSONString(symbolInfoListResDto));
            return ResFactory.getInstance().success(symbolInfoListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 bitfinex 查找 交易对信息 异常,异常信息：";
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
        log.debug("调用 bitfinex 查询所有交易对tickers信息开始");
        IBitfinexStockRestApi bitfinexStockRestApi = new BitfinexStockRestApi();
        String result = null;
        try {
            FullTickerReqDto fullTickerReqDto = fullTickerReqDtoReq.getData();
            String symbol = fullTickerReqDto.getSymbol();
            if(StringUtils.isBlank(symbol)){
                symbol = BitfinexConstant.STR_ALL;
            }
            String tradeSymbol;
            if(StringUtils.equals(symbol, BitfinexConstant.STR_ALL)){
                tradeSymbol = symbol;
            }else{
                tradeSymbol = this.toTradeSymbol(symbol).toUpperCase();
                tradeSymbol += "t"+tradeSymbol;
            }

            result = bitfinexStockRestApi.V2_tickers(tradeSymbol);
//            成功：
//            [
//                [
//                    "tBTCUSD", -- symbol
//                    9570.1,
//                    22.848503160000003,
//                    9570.2, -- newPrice
//                    35.89090213,
//                    70.1,
//                    0.0074, --- 涨幅 未乘100
//                    9570.1,
//                    4141.32611107, --- 日成交量 货的数量
//                    9635.4,
//                    9380.4
//                ],
//                [
//                    "tLTCUSD",
//                    64.092,
//                    1814.3717592500002,
//                    64.123,
//                    1194.07248196,
//                    -1.304,
//                    -0.0199,
//                    64.116,
//                    55789.25021914,
//                    65.48,
//                    62.202
//                ]
//            ]


            if (StringUtils.isBlank(result)) {
                String temp = "调用 bitfinex 查询全量ticker信息失败，交易所返回为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            if (!result.startsWith("[")) {
                String temp = "调用 bitfinex 查询全量ticker信息失败，交易所返回:" + result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            JSONArray resultJSONArr = JSONArray.parseArray(result);
            JSONArray childArr = null;

            List<FullTickerResDto> fullTickerResDtoList = new ArrayList<>();
            FullTickerResDto fullTickerResDto = null;
            BigDecimal priceChangePercent = null;
            BigDecimal lastPrice = null;
            BigDecimal volume = null;//24小时成交量
            int size = resultJSONArr.size();
            for (int i = 0; i < size; i++) {
                fullTickerResDto = new FullTickerResDto();
                childArr = resultJSONArr.getJSONArray(i);
                tradeSymbol = childArr.getString(0);
                if(tradeSymbol.startsWith("f")){
                    continue;
                }
                lastPrice = childArr.getBigDecimal(3);
                if(null != lastPrice){
                    lastPrice = lastPrice.stripTrailingZeros();
                }
                priceChangePercent = childArr.getBigDecimal(6);//日涨跌幅未乘100
                if(null != priceChangePercent){
                    priceChangePercent = CalculateUtil.multiply(priceChangePercent,new BigDecimal(100));
                }
                volume = childArr.getBigDecimal(8);
                fullTickerResDto.setLast(lastPrice);
                fullTickerResDto.setPriceChangePercent(priceChangePercent);
                fullTickerResDto.setVolume24h(volume);
                fullTickerResDto.setSymbol(this.V2_symbol(tradeSymbol));
                fullTickerResDto.setLowPrice(childArr.getBigDecimal(10));
                fullTickerResDto.setHighPrice(childArr.getBigDecimal(9));
                fullTickerResDtoList.add(fullTickerResDto);
            }

            FullTickerListResDto fullTickerListResDto = new FullTickerListResDto();
            fullTickerListResDto.setFullTickerResDtoList(fullTickerResDtoList);
            log.debug("调用 bitfinex 查询全量ticker信息 返回结果：" + JSONObject.toJSONString(fullTickerListResDto));
            return ResFactory.getInstance().success(fullTickerListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 bitfinex 查询全量ticker信息异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<TickerPriceResDto> tickerPrice(Req<TickerPriceReqDto> tickerPriceReqDtoReq) {
        try {
            if (null == tickerPriceReqDtoReq
                    || null == tickerPriceReqDtoReq.getData()) {
                String temp = "调用 bitfinex 查询 交易对价格信息 失败，失败原因：必填参数为空";
                log.error(temp);
                throw new TradeExchangeApiException(temp);
            }

            TickerPriceReqDto tickerPriceReqDto = tickerPriceReqDtoReq.getData();
            String symbol = tickerPriceReqDto.getSymbol();
            if(StringUtils.isBlank(symbol)){
                String temp = "调用Bitfinex 查询价格信息 失败,必填参数symbol为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            IBitfinexStockRestApi stockGet = new BitfinexStockRestApi();
            String tradeSymbol = this.V2_tradeSymbol(symbol);
            String ticker = stockGet.ticker(tradeSymbol);
            if(StringUtils.isNotBlank(ticker)){
                if(!ticker.startsWith("[")){
                    String temp = "调用Bitfinex 查询价格信息 失败,交易所返回"+ticker;
                    log.error(temp);
                    throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
                }
//                失败：
//                {
//                    "code": 503,
//                    "error": "temporarily_unavailable",
//                    "error_description": "Sorry, the service is temporarily unavailable. See https://www.bitfinex.com/ for more info."
//                }
                JSONArray jsonArray = JSONArray.parseArray(ticker);
                if(jsonArray.size()!=10){
                    String temp = "调用Bitfinex 查询价格信息 失败，交易所返回"+ticker;
                    log.error(temp);
                    throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
                }
                BigDecimal price = new BigDecimal(jsonArray.get(6).toString());
                TickerPriceResDto tickerPriceResDto = new TickerPriceResDto(symbol,price.stripTrailingZeros());
                return ResFactory.getInstance().success(tickerPriceResDto);
            }else{
                String temp = "调用Bitfinex查询 价格信息信息 失败，交易所返回"+ticker;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }
        }catch (Throwable e) {
            String temp = "调用Bitfinex查询 价格信息信息 失败，异常信息：";
            log.error(temp,e);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<AccountInfoResDto> getAccountInfo(Req<AccountInfoReqDto> accountInfoReqDtoReq) {
        return null;
    }

    @Override
    public Res<ResList<QueryBalanceResDto>> getBalance(QueryBalanceReqDto queryBalanceReqDto) {
        try {
            IBitfinexStockRestApi stock = new BitfinexStockRestApi(queryBalanceReqDto.getApiKey(), queryBalanceReqDto.getApiSecret());
            String balance = stock.balance();
            if (!balance.contains("currency")) {
                log.error("调用bitfinex查询账户余额异常，异常信息：{}", balance);
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用bitfinex查询账户余额异常");
            }
            JSONArray balanceArray = JSONArray.parseArray(balance);
            if (balanceArray.isEmpty()) {
                log.error("调用bitfinex查询账户余额为空");
                throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "调用bitfinex查询账户余额为空");
            }

            Map<String, QueryBalanceResDto> resultMap = Maps.newHashMap();
            String currency;
            for (int j = 0; j < balanceArray.size(); j++) {
                JSONObject balanceObject = balanceArray.getJSONObject(j);
                currency = balanceObject.getString("currency").toUpperCase();
                if (!resultMap.containsKey(currency)) {
                    resultMap.put(currency, new QueryBalanceResDto(currency, BigDecimal.ZERO, BigDecimal.ZERO));
                }
                resultMap.get(currency).setUsable(balanceObject.getBigDecimal("available"));
                resultMap.get(currency).setFrozen(balanceObject.getBigDecimal("amount").
                        subtract(balanceObject.getBigDecimal("available")));
            }
            return ResFactory.getInstance().successList(Lists.newArrayList(resultMap.values()));
        } catch (Exception e) {
            log.error("调用bitfinex查询账户余额异常，异常信息：", e);
            throw new CallExchangeRemoteException(e, e.getMessage());
        }
    }

}
