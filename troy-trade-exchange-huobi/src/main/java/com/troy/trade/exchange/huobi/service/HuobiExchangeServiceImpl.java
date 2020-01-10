package com.troy.trade.exchange.huobi.service;

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
import com.troy.trade.exchange.huobi.client.HuobiConstant;
import com.troy.trade.exchange.huobi.client.api.ApiClient;
import com.troy.trade.exchange.huobi.client.stock.IHuobiStockRestApi;
import com.troy.trade.exchange.huobi.client.stock.impl.HuobiStockRestApi;
import com.troy.trade.exchange.huobi.dto.HuobiHarkWithdrawalStatusEnum;
import com.troy.trade.exchange.huobi.dto.request.CreateOrderRequest;
import com.troy.trade.exchange.huobi.dto.request.DepthRequest;
import com.troy.trade.exchange.huobi.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * huobi交易所服务
 *
 * @author dp
 */
@Slf4j
@Component
public class HuobiExchangeServiceImpl implements IExchangeService {
    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.HUOBI;
    }

    @Override
    public String toTradeSymbol(String symbol) {
        return SymbolUtil.ToTradeSymbol.lowerCaseSymbol(symbol);
    }

    @Override
    public String symbol(String tradeSymbol) {
        // etcusdt
        if (StringUtils.isBlank(tradeSymbol)) {
            return null;
        }
        String coinName;
        String moneyName;
        // trx、ht、eth、btc、husd、usdt
        if (tradeSymbol.endsWith("trx")
                || tradeSymbol.endsWith("eth")
                || tradeSymbol.endsWith("btc")) {
            coinName = tradeSymbol.substring(0, tradeSymbol.length() - 3);
            moneyName = tradeSymbol.substring(tradeSymbol.length() - 3);
        } else if (tradeSymbol.endsWith("ht")) {
            coinName = tradeSymbol.substring(0, tradeSymbol.length() - 2);
            moneyName = tradeSymbol.substring(tradeSymbol.length() - 2);
        } else {
            coinName = tradeSymbol.substring(0, tradeSymbol.length() - 4);
            moneyName = tradeSymbol.substring(tradeSymbol.length() - 4);
        }

        return new StringBuffer(coinName).append("/").append(moneyName).toString().toUpperCase();
    }

    @Override
    public Res<CreateOrderResDto> createOrder(Req<CreateOrderReqDto> createOrderReqDtoReq) {
        CreateOrderReqDto createOrderReqDto = createOrderReqDtoReq.getData();
        final String appkey = createOrderReqDto.getApiKey();
        final String appsecret = createOrderReqDto.getApiSecret();
        final String currencyPair = createOrderReqDto.getTradeSymbol();// 交易对
        final int direction = createOrderReqDto.getOrderSide().code();// 购买方向（1-买 2-卖）
        final Integer transType = createOrderReqDto.getOrderType().code();//交易类型（1-限价交易 2-市价交易）

        String accountId = createOrderReqDto.getThirdAcctId();

        ApiClient apiClient = new ApiClient(appkey, appsecret);
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setSymbol(currencyPair);
        createOrderRequest.setAccountId(accountId);
        createOrderRequest.setSymbolType(createOrderReqDto.getTradeSymbol());
        String type;
        //交易类型，限价交易
        if (TradeExchangeApiConstant.OrderType.LIMIT.code().equals(transType)) {
            createOrderRequest.setAmount(createOrderReqDto.getAmount().toPlainString());
            createOrderRequest.setPrice(createOrderReqDto.getPrice().toPlainString());
            if (OrderSideEnum.BID.code().equals(direction)) {
                //买入
                createOrderRequest.setType(CreateOrderRequest.OrderType.BUY_LIMIT);
                type = "限价买入";
            } else {
                //卖出
                createOrderRequest.setType(CreateOrderRequest.OrderType.SELL_LIMIT);
                type = "限价卖出";
            }
        } else {//交易类型，市价交易
            //买入
            if (OrderSideEnum.BID.code().equals(direction)) {
                createOrderRequest.setAmount(createOrderReqDto.getMarketCashAmount().toPlainString());
                createOrderRequest.setType(CreateOrderRequest.OrderType.BUY_MARKET);
                type = "市价买入";
            } else {//卖出
                createOrderRequest.setAmount(createOrderReqDto.getAmount().toPlainString());
                createOrderRequest.setType(CreateOrderRequest.OrderType.SELL_MARKET);
                type = "市价卖出";
            }
            createOrderRequest.setPrice(null);
        }

        try {
            return ResFactory.getInstance().success(new CreateOrderResDto(apiClient.trade(createOrderRequest)));
        } catch (Exception e) {
            log.error("调用火币" + type + "下单失败：" + e.getMessage());
            throw new CallExchangeRemoteException(e, e.getMessage());
        }
    }

    @Override
    public Res<CancelOrderResDto> cancelOrder(Req<CancelOrderReqDto> cancelOrderReqDtoReq) {
        CancelOrderReqDto cancelOrderReqDto = cancelOrderReqDtoReq.getData();
        Assert.notNull(cancelOrderReqDto, TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_DATA,"撤单数据不能为空");
        final List<String> orderIds = cancelOrderReqDto.getOrderIds();
        log.info("调用火币撤销订单，开始，本次撤销订单ID列表大小为：{}", orderIds.size());

        Assert.notEmpty(orderIds, TradeExchangeErrorCode.FAIL_EMPTY_CANCEL_LIST, "撤单列表不能为空");

        List<String> successOrderIds = Lists.newArrayList();
        List<String> failOrderIds = Lists.newArrayList();

        ApiClient apiClient = new ApiClient(cancelOrderReqDto.getApiKey(), cancelOrderReqDto.getApiSecret());

        // 调用批量撤单
        if (orderIds.size() > 1) {
            // 按照大小分隔需要撤单的列表
            List<List<String>> orderIdsList = Lists.partition(orderIds, HuobiConstant.MAX_CANCEL_BATCH_SIZE);
            for(List<String> splitOrderIds : orderIdsList){
                BatchcancelResponse<Batchcancel<List<String>, List<BatchcancelBean>>> batchcancelResponse = apiClient.submitcancels(splitOrderIds);
                if (!StringUtils.equals(HuobiConstant.RESPONSE_STATUS_OK, batchcancelResponse.getStatus())) {
                    failOrderIds.addAll(splitOrderIds);
                    log.warn("调用火币批量订单撤销失败{}，三方返回：{}", splitOrderIds, batchcancelResponse);
                    break;
                }
                successOrderIds.addAll(batchcancelResponse.getData().getSuccess());
                failOrderIds.addAll(splitOrderIds.stream().filter(s -> !successOrderIds.contains(s)).collect(Collectors.toList()));
            }
        } else {
            // 单个撤单
            String orderId = orderIds.get(0);
            SubmitcancelResponse response = apiClient.submitcancel(orderId);
            if (StringUtils.equals(HuobiConstant.RESPONSE_STATUS_OK, response.getStatus())) {
                successOrderIds.add(orderId);
            } else {
                log.warn("调用火币单个订单撤销失败{}，三方返回：{}", orderId, response);
                failOrderIds.add(orderId);
            }
        }
        return ResFactory.getInstance().success(new CancelOrderResDto(successOrderIds, failOrderIds));
    }

    @Override
    public Res<OrderDetailResDto> orderDetail(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        final String orderId = orderDetailReqDto.getOrderId();
        ApiClient apiClient = new ApiClient(orderDetailReqDto.getApiKey(), orderDetailReqDto.getApiSecret());
        log.debug("调用火币orderDetail-------start,参数：" + orderDetailReqDto == null ? "" : JSONObject.toJSONString(orderDetailReqDto));
        OrdersDetailResponse<Map> ordersDetailResponse = apiClient.ordersDetail(orderId);
        if (ordersDetailResponse == null) {
            log.warn("查询订单信息{}--火币返回为空", orderId);
            throw new CallExchangeRemoteException("查询订单信息[" + orderId + "]--火币返回为空");

        }
        if (!HuobiConstant.RESPONSE_STATUS_OK.equals(ordersDetailResponse.getStatus())) {
            log.warn("查询订单信息{}--失败", ordersDetailResponse.getErrMsg());
            throw new CallExchangeRemoteException("查询订单信息[" + orderId + "]--失败：" + ordersDetailResponse.getErrMsg());

        }

        Map<String, Object> params = ordersDetailResponse.getData();
        params.put("numDecimal", orderDetailReqDto.getNumDecimal());
        params.put("amountDecimal", orderDetailReqDto.getAmountDecimal());
        return ResFactory.getInstance().success(transferOrderDetailDto(transferOrigin(params), orderDetailReqDto.getSymbol()));

    }

    @Override
    public Res<OrderListResData> orderList(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        ApiClient apiClient = new ApiClient(orderDetailReqDto.getApiKey(), orderDetailReqDto.getApiSecret());
        log.debug("调用火币ordersHistory-------start,参数：" + orderDetailReqDto == null ? "" : JSONObject.toJSONString(orderDetailReqDto));

        // 48小时历史订单
        OrdersListResponse<Map> ordersDetailResponse = apiClient.ordersHistory(toTradeSymbol(orderDetailReqDto.getSymbol()), orderDetailReqDto.getLimit());

        if (ordersDetailResponse == null) {
            log.warn("查询订单列表，交易对={}--火币返回为空", orderDetailReqDto.getSymbol());
            throw new CallExchangeRemoteException("查询订单，交易对=[" + orderDetailReqDto.getSymbol() + "]--火币返回为空");

        }
        if (!HuobiConstant.RESPONSE_STATUS_OK.equals(ordersDetailResponse.getStatus())) {
            log.warn("查询订单信息{}--失败", ordersDetailResponse.getErrMsg());
            throw new CallExchangeRemoteException("查询订单信息[" + orderDetailReqDto.getSymbol() + "]--失败：" + ordersDetailResponse.getErrMsg());

        }

        List<Map<String, Object>> params = (List<Map<String, Object>>) ordersDetailResponse.getData();
        // 48 小时查询接口
        List<OrderDetailResDto> detailResDtos = transferOrderListReturn(params, orderDetailReqDto.getSymbol());

        // 当前挂单

        OrdersListResponse<Map> openOrders = apiClient.openOrders(orderDetailReqDto.getThirdAcctId(), toTradeSymbol(orderDetailReqDto.getSymbol()), 500);

        List<Map<String, Object>> openOrderParams = (List<Map<String, Object>>) openOrders.getData();
        if (openOrderParams!=null && openOrderParams.size()>0){
            List<OrderDetailResDto> openOrderResDtos = transferOpenOrderListReturn(openOrderParams, orderDetailReqDto.getSymbol());
            detailResDtos.addAll(openOrderResDtos);
        }
        List<String> ids = new ArrayList<>();//用来临时存储person的id
        List<OrderDetailResDto> newList = detailResDtos.stream().filter(// 过滤去重
                v -> {
                    boolean flag = !ids.contains(v.getOrderId());
                    ids.add(v.getOrderId());
                    return flag;
                }
        ).collect(Collectors.toList());
        // params.put("numDecimal", orderDetailReqDto.getNumDecimal());
        // params.put("amountDecimal", orderDetailReqDto.getAmountDecimal());
        return ResFactory.getInstance().success(new OrderListResData(newList));
    }

    private String transferStatus(Integer status) {//

        //查询状态范围，0-下单中、1-部分成交、2-已撤销、3-已成交、4-部分撤销、10-未成交、11-失败、13-申请撤单中、-1-所有
        if (status == TradeExchangeApiConstant.OrderStatus.INIT.code()) {//下单中
            return HuobiConstant.ORDER_STATUS_SUBMITTING;
        } else if (status == TradeExchangeApiConstant.OrderStatus.PART.code()) {//部分成交
            return HuobiConstant.ORDER_STATUS_PARTIAL_FILLED;
        } else if (status == TradeExchangeApiConstant.OrderStatus.CANCEL.code()) {//已撤销
            return HuobiConstant.ORDER_STATUS_CANCELED;
        } else if (status == TradeExchangeApiConstant.OrderStatus.DEAL.code()) {//已成交
            return HuobiConstant.ORDER_STATUS_FILLED;
        } else if (status == TradeExchangeApiConstant.OrderStatus.COMMIT.code()) {//未成交
            return HuobiConstant.ORDER_STATUS_SUBMITTED;
        } else if (status == TradeExchangeApiConstant.OrderStatus.PARTIAL_CANCELED.code()) {//失败
            return HuobiConstant.ORDER_STATUS_PARTIAL_CANCELED;
        } else {
            return "";
        }
    }
    @Override
    public Res<OrderListResData> orderListByPage(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        OrderDetailReqDto orderDetailReqDto = orderDetailReqDtoReq.getData();
        ApiClient apiClient = new ApiClient(orderDetailReqDto.getApiKey(), orderDetailReqDto.getApiSecret());
        log.debug("调用火币ordersHistory-------start,参数：" + orderDetailReqDto == null ? "" : JSONObject.toJSONString(orderDetailReqDto));

        Map<String, String> params = Maps.newHashMap();
        params.put("symbol", toTradeSymbol(orderDetailReqDto.getSymbol()));
      //  params.put("states", "filled,partial-canceled,submitted");
        if (orderDetailReqDto.getOrderStatus().code()==null) {
            String temp = "调用huobiV3,status=" + orderDetailReqDto.getOrderStatus().code() + ",返回信息为空";
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM, temp);
        }
        String state = transferStatus(orderDetailReqDto.getOrderStatus().code());
        if (StringUtils.isBlank(state)) {
            String temp = "调用huobiV3,status=" + orderDetailReqDto.getOrderStatus().code() + ",返回信息为空";
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM, temp);
        }
        params.put("states", state);
        log.info("火币杠杆账户查询历史成交入参{}", JSONObject.toJSONString(params));

            String startCondition = orderDetailReqDto.getStartCondition();

            if (StringUtils.isNotBlank(startCondition)) {
                params.put("direct", "next");
                params.put("from", startCondition);
            }

            String endCondition = orderDetailReqDto.getEndCondition();
            if (StringUtils.isNotBlank(endCondition)) {
                params.put("direct", "prev");
                params.put("from", endCondition);
            }

            Integer size = orderDetailReqDto.getLimit();
            if (null != size) {
                params.put("size", String.valueOf(size+1));
            }

        OrdersListResponse<Map> ordersDetailResponse = apiClient.ordersListByPage(params);

        if (ordersDetailResponse == null) {
            log.warn("查询订单列表，交易对={}--火币返回为空", orderDetailReqDto.getSymbol());
            throw new CallExchangeRemoteException("查询订单，交易对=[" + orderDetailReqDto.getSymbol() + "]--火币返回为空");

        }
        if (!HuobiConstant.RESPONSE_STATUS_OK.equals(ordersDetailResponse.getStatus())) {
            log.warn("查询订单信息{}--失败", ordersDetailResponse.getErrMsg());
            throw new CallExchangeRemoteException("查询订单信息[" + orderDetailReqDto.getSymbol() + "]--失败：" + ordersDetailResponse.getErrMsg());

        }

        List<Map<String, Object>> mapList = (List<Map<String, Object>>) ordersDetailResponse.getData();
        List<OrderDetailResDto> detailResDtos = transferOrderListReturn(mapList, orderDetailReqDto.getSymbol());


        int listSize = detailResDtos.size();
        if (listSize>0){
            String bigId=detailResDtos.get(0).getOrderId();
            String smallId=detailResDtos.get(listSize-1).getOrderId();
            return ResFactory.getInstance().success(new OrderListResData(detailResDtos.subList(0,listSize-1),smallId,bigId));
        }
        return ResFactory.getInstance().success(new OrderListResData(null,null,null));

    }

    @Override
    public Res<OrderListResData> getOpenOrders(Req<OpenOrdersReqDto> ordersReqDtoReq) {
        OpenOrdersReqDto orderDetailReqDto = ordersReqDtoReq.getData();
        ApiClient apiClient = new ApiClient(orderDetailReqDto.getApiKey(), orderDetailReqDto.getApiSecret());
        log.debug("调用火币getOpenOrders-------start,参数：" + orderDetailReqDto == null ? "" : JSONObject.toJSONString(orderDetailReqDto));

        OrdersListResponse<Map> ordersDetailResponse = apiClient.openOrders(orderDetailReqDto.getThirdAcctId(), orderDetailReqDto.getTradeSymbol(), Integer.parseInt(orderDetailReqDto.getLimit()));
        if (ordersDetailResponse == null) {
            log.warn("查询当前未成交订单，交易对={}--火币返回为空", orderDetailReqDto.getSymbol());
            throw new CallExchangeRemoteException("查询订单，交易对=[" + orderDetailReqDto.getSymbol() + "]--火币返回为空");

        }
        if (!HuobiConstant.RESPONSE_STATUS_OK.equals(ordersDetailResponse.getStatus())) {
            log.warn("查询当前未成交订单{}--失败", ordersDetailResponse.getErrMsg());
            throw new CallExchangeRemoteException("查询当前未成交订单[" + orderDetailReqDto.getSymbol() + "]--失败：" + ordersDetailResponse.getErrMsg());

        }

        List<Map<String, Object>> params = (List<Map<String, Object>>) ordersDetailResponse.getData();
        // params.put("numDecimal", orderDetailReqDto.getNumDecimal());
        // params.put("amountDecimal", orderDetailReqDto.getAmountDecimal());
        return ResFactory.getInstance().success(new OrderListResData(transferOpenOrderListReturn(params, orderDetailReqDto.getSymbol())));
    }

    @Override
    public Res<ResList<ExchAcctDeptWdralResDto>> harkWithdrawal(Req<HarkWithdrawalReqDto> harkWithdrawalReqDtoReq) {

        if(null == harkWithdrawalReqDtoReq){
            log.error("调用 huobi 查询 历史充提记录 失败，入参为空");
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        HarkWithdrawalReqDto harkWithdrawalReqDto = harkWithdrawalReqDtoReq.getData();
        if(null == harkWithdrawalReqDto){
            log.error("调用 huobi 查询 历史充提记录 失败，入参为空");
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        log.info("调用 huobi 查询 历史充提记录，入参:{}",harkWithdrawalReqDto);

        Map<String, String> paramMap = new HashMap<>();
        Integer typeInt = harkWithdrawalReqDto.getType();
        if(null != typeInt){
            String type = null;
            if(typeInt==ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_DEPOSIT){//充值
                type = HuobiConstant.DEPT_WIAL_TYPE_DEPOSIT;
            }else{
                type = HuobiConstant.DEPT_WIAL_TYPE_WIAL;
            }
            paramMap.put("type",type);
        }

        String coin = harkWithdrawalReqDto.getCoinName();
        if(StringUtils.isNotBlank(coin)){
            paramMap.put("currency",coin.toLowerCase());
        }

        String from = harkWithdrawalReqDto.getFrom();
        if(StringUtils.isNotBlank(from)){
            paramMap.put("from",from);
        }

        Integer pageSize = harkWithdrawalReqDto.getPageSize();
        if(null != pageSize){
            paramMap.put("size", String.valueOf(pageSize));
        }

        paramMap.put("direct", "next");//倒序查询
        String harkWithdrawal = null;
        try{
            IHuobiStockRestApi stock = new HuobiStockRestApi(harkWithdrawalReqDto.getApiKey(),harkWithdrawalReqDto.getApiSecret());
            harkWithdrawal = stock.harkWithdrawalHistory(paramMap);
        }catch (Throwable throwable){
            String temp = "调用 huobi 查询 历史充提 记录异常，异常信息：";
            log.error(temp,throwable);
            throw new CallExchangeRemoteException(throwable);
        }

        log.info("调用 huobi 查询 历史充提 记录 返回:{}",harkWithdrawal);
        if(StringUtils.isBlank(harkWithdrawal)){
            String temp = "调用 huobi 查询 历史充提记录 失败，失败原因：交易所返回为空";
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }

        JSONObject resultJson = JSONObject.parseObject(harkWithdrawal);
        if(!resultJson.containsKey("status")){
            String temp = "调用 huobi 查询 历史充提记录 失败，返回信息:"+harkWithdrawal;
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,harkWithdrawal);
        }

        String status1 = resultJson.getString("status");
        if(!StringUtils.equals(status1,HuobiConstant.RESPONSE_STATUS_OK)) {
            String temp = "调用 huobi 查询 历史充提记录 失败，返回信息:"+harkWithdrawal;
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,harkWithdrawal);
        }


        /**
         * 将结果转换为返回实体
         */
        List<ExchAcctDeptWdralResDto> exchAcctDeptWdralResDtoList = new ArrayList<>();
        JSONArray dataArr = resultJson.getJSONArray("data");
        JSONObject dataJSON = null;
        String huobiType = null;
        String huobiStatus = null;
        String addrTag = null;
        Long huobiApplyTime = null;
        ExchAcctDeptWdralResDto exchAcctDeptWdralResDto = null;

        String thirdId;//第三方返回ID
        String coinName;
        BigDecimal amount;//数量
        String txId;//提币哈希记录
        String address;//地址
        Integer deptWdralType;//充提类型，1-充值、2-提现
        BigDecimal fee;//手续费
        Date applyTime;//提申请时间
        Integer status;//状态，1-申请中、2-已完成、3-已取消、4-失败

        int size = dataArr == null?0:dataArr.size();
        for(int i=0;i<size;i++){
            dataJSON = dataArr.getJSONObject(i);

            thirdId = dataJSON.getString("id");
            huobiType = dataJSON.getString("type");
            deptWdralType = null;
            if(StringUtils.isNotBlank(huobiType)){
                if(StringUtils.equals(huobiType,HuobiConstant.DEPT_WIAL_TYPE_DEPOSIT)){
                    deptWdralType = ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_DEPOSIT;
                }else{
                    deptWdralType = ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_WITHDRAWAL;
                }
            }
            coinName = dataJSON.getString("currency");
            amount = dataJSON.getBigDecimal("amount");
            txId = dataJSON.getString("tx-hash");
            address = dataJSON.getString("address");
            fee = dataJSON.getBigDecimal("fee");

            huobiStatus = dataJSON.getString("state");
            status = null;
            if(StringUtils.isNotBlank(huobiStatus)){
                status = HuobiHarkWithdrawalStatusEnum.getStatus(huobiStatus);
            }

            applyTime = null;
            huobiApplyTime = dataJSON.getLong("created-at");//单位：毫秒
            if(null != huobiApplyTime){
                applyTime = DateUtils.parse(huobiApplyTime);
            }

            addrTag = dataJSON.getString("address-tag");

//          String thirdId, String coinName,
//          BigDecimal amount, String txId, String address,
//          Integer deptWdralType, BigDecimal fee, Date applyTime,
//          Integer status,String addrTag
            exchAcctDeptWdralResDto = ExchAcctDeptWdralResDto.getInstance( thirdId, coinName,
                    amount, txId, address,
                    deptWdralType, fee, applyTime,
                    status,addrTag);
            exchAcctDeptWdralResDtoList.add(exchAcctDeptWdralResDto);
        }

//            成功：
//            {
//                "status": "ok",
//                "data": [{
//                    "id": 9579923,
//                    "type": "deposit",
//                    "currency": "eth",
//                    "chain": "eth",
//                    "tx-hash": "e6fe40bee2301d8ccb9ff0879de218ad7295fdb7d083fee5bfe45ce0dcdbe755",
//                    "amount": 1.999100000000000000,
//                    "address": "2d533337487fe775cc002d54faae14c30ca95aaf",
//                    "address-tag": "",
//                    "fee": 0,
//                    "state": "safe",
//                    "created-at": 1547112154921,
//                    "updated-at": 1547112769260
//                }]
//            }


        return ResFactory.getInstance().successList(exchAcctDeptWdralResDtoList);


//        currency	false	string	币种		缺省时，返回所有币种
//        type	true	string	充值或提现		deposit 或 withdraw
//        from	false	string	查询起始 ID 缺省时，默认值direct相关。当direct为‘prev’时，from 为1 ，从旧到新升序返回；当direct为’next‘时，from为最新的一条记录的ID，从新到旧降序返回
//        size	false	string	查询记录大小	100	1-500
//        direct	false	string	返回记录排序方向	缺省时，默认为“prev” （升序）	“prev” （升序）or “next” （降序）
    }

    @Override
    public Res<WithdrawalResDto> withdraw(Req<WithdrawalReqDto> withdrawalReqDtoReq) {


        if(null == withdrawalReqDtoReq){
            String temp = "调用 huobi 提现失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        WithdrawalReqDto withdrawalReqDto = withdrawalReqDtoReq.getData();
        if(null == withdrawalReqDto){
            String temp = "调用 huobi 提现失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        String coinName = withdrawalReqDto.getCoinName();
        //提币地址
        String address = withdrawalReqDto.getAddress();
        BigDecimal amountBig = withdrawalReqDto.getReceivedAmount();

        if(StringUtils.isBlank(coinName)
                ||StringUtils.isBlank(address)
                ||null == amountBig){
            String temp = "调用 huobi 提现失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }

        coinName = coinName.toLowerCase();

//            address	true	string	提现地址	仅支持在官网上相应币种地址列表 中的地址
//            amount	true	string	提币数量
//            currency	true	string	资产类型	btc, ltc, bch, eth, etc ...(火币全球站支持的币种)
//            fee	    true	string	转账手续费
//            chain	    false	string	提USDT至OMNI时须设置此参数为"usdt"，提USDT至TRX时须设置此参数为"trc20usdt"，其他币种提现无须设置此参数
//            addr-tag	false	string	虚拟币共享地址tag，适用于xrp，xem，bts，steem，eos，xmr	格式, "123"类的整数字符串

        BigDecimal fee = withdrawalFee(withdrawalReqDto.getApiKey(),withdrawalReqDto.getApiSecret(),coinName);
        if(null == fee){
            String temp = "调用 huobi 提现失败，手续费查询为空";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM," 调用 huobi 提现失败，手续费查询为空");
        }

        String result = "";
        try{

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
            paramMap.put("address", address);
            paramMap.put("amount", amountBig.toPlainString());
            paramMap.put("currency", coinName);
            paramMap.put("fee", fee.toPlainString());//转账手续费
            if(StringUtils.equals(coinName,"usdt")){
                paramMap.put("chain", "usdterc20");
            }
            if(StringUtils.isNotBlank(addrTag)){
                paramMap.put("addr-tag", addrTag);
            }
            IHuobiStockRestApi stock = new HuobiStockRestApi(withdrawalReqDto.getApiKey(),withdrawalReqDto.getApiSecret());
            result = stock.withdraw(paramMap);
        }catch (Throwable throwable){
            String temp = "调用 huobi 提现 异常，异常信息：";
            log.error(temp,throwable);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }
//            成功：
//            {
//                "data": 700
//            }
        if(StringUtils.isBlank(result)){
            String temp = "调用 huobi 提现 失败,返回结果为空";
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }

        JSONObject resultJSON = JSONObject.parseObject(result);
        if(!resultJSON.containsKey("data")){
            String temp = "调用 huobi 提现 失败,返回结果为:"+result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }

        String id = resultJSON.getString("data");
        if(StringUtils.isBlank(id)){
            String temp = "调用 huobi 提现 失败,返回结果为:"+result;
            log.warn(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM,result);
        }

        WithdrawalResDto withdrawalResDto = new WithdrawalResDto();
        withdrawalResDto.setThirdId(id);
        return ResFactory.getInstance().success(withdrawalResDto);
    }

    /**
     * 查询手续费
     * @param apiKey
     * @param apiSecret
     * @param coinName
     * @return
     */
    private BigDecimal withdrawalFee(String apiKey,String apiSecret,String coinName){
        BigDecimal withdrawsFee = null;
        try{
            if(StringUtils.isBlank(coinName)){
                return withdrawsFee;
            }

            CoinInfoReqDto coinInfoReqDto = new CoinInfoReqDto();
            coinInfoReqDto.setApiKey(apiKey);
            coinInfoReqDto.setApiSecret(apiSecret);
            coinInfoReqDto.setExchCode(ExchangeCode.HUOBI);
            Req<CoinInfoReqDto> coinInfoReqDtoReq = ReqFactory.getInstance().createReq(coinInfoReqDto);
            Res<CoinInfoListResDto> resDtoRes = this.getCoinInfo(coinInfoReqDtoReq);
            if(null == resDtoRes
                    || !resDtoRes.isSuccess()){
                log.error("调用 huobi 查询币种提现手续费失败");
                return withdrawsFee;
            }

            CoinInfoListResDto coinInfoListResDto = resDtoRes.getData();
            CoinInfoResDto coinInfoResDto = null;
            List<CoinInfoResDto> coinInfoResDtoList = coinInfoListResDto.getCoinInfoResDtoList();
            int size = coinInfoResDtoList == null?0:coinInfoResDtoList.size();
            String coinInfoName = null;
            String coinNameUpper = coinName.toUpperCase();
            for(int i=0;i<size;i++){
                coinInfoResDto = coinInfoResDtoList.get(i);
                coinInfoName = coinInfoResDto.getCoinName().toUpperCase();
                if(StringUtils.equals(coinNameUpper,coinInfoName)){
                    withdrawsFee = coinInfoResDto.getWithdrawsFee();
                    break;
                }
            }
        }catch (Throwable throwable){
            log.error("调用 huobi 查询币种提现手续费异常，异常信息：",throwable);
        }
        return withdrawsFee;
    }

    private Res isOk(String result,String desc){
        if(StringUtils.isBlank(result)){
            String temp = "调用 huobi 查询 "+desc+" 失败，失败原因：交易所返回为空";
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
        }

        JSONObject resultJson = JSONObject.parseObject(result);
        if(!resultJson.containsKey("status")){
            String temp = "调用 huobi 查询 "+desc+"失败，返回信息:"+result;
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
        }

        String status = resultJson.getString("status");
        if(!StringUtils.equals(status,HuobiConstant.RESPONSE_STATUS_OK)) {
            String temp = "调用 huobi 查询 "+desc+"失败，返回信息:"+result;
            log.error(temp);
            throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
        }

        return ResFactory.getInstance().successList(null);
    }

    @Override
    public Res<OrderBookResDto> getOrderBook(Req<OrderBookReqDto> orderBookReqDtoReq) {
        try {
            OrderBookReqDto orderBookReqDto = orderBookReqDtoReq.getData();
            if (null == orderBookReqDto
                    || StringUtils.isBlank(orderBookReqDto.getSymbol())) {
                String temp = "调用 huobi 查询买卖挂单记录失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            final String tradeSymbol = toTradeSymbol(orderBookReqDto.getSymbol());
            ApiClient apiClient = new ApiClient(null, null);
            DepthRequest request = new DepthRequest();
            request.setSymbol(tradeSymbol);
            request.setType("step0");
            DepthResponse depthResponse = apiClient.depth(request);
            if (null == depthResponse) {
                String temp = "调用 huobi 查询买卖挂单记录失败，失败原因：交易所返回为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            String status = depthResponse.getStatus();
            if (StringUtils.isBlank(status)
                    || !StringUtils.equals(status, HuobiConstant.RESPONSE_STATUS_OK)) {
                String temp = "调用 huobi 查询买卖挂单记录失败，失败原因：交易所返回" + JSONObject.toJSONString(depthResponse);
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

//            成功：
//            {
//                "status": "ok",
//                "ch": "market.btcusdt.depth.step0",
//                "ts": 1566817166808,
//                "tick": {
            //                "bids": [
            //                    [10324.000000000000000000, 0.002400000000000000],
            //                    [10323.680000000000000000, 2.000000000000000000]
            //                ],
            //                "asks": [
            //                    [10378.370000000000000000, 0.000800000000000000],
            //                    [10378.500000000000000000, 0.172527000000000000]
            //                ],
            //                "ts": 1566817166036,
            //                "version": 102216225928
//                }
//            }

            Depth depth = depthResponse.getTick();
            if (null == depth) {
                OrderBookResDto orderBookResDto = new OrderBookResDto(new ArrayList<>(), new ArrayList<>());
                log.debug("查找 huobi 查找买卖挂单 记录 返回结果：" + JSONObject.toJSONString(orderBookResDto));
                return ResFactory.getInstance().success(orderBookResDto);
            }

            BigDecimal price = null;
            BigDecimal amount = null;
            List<BigDecimal> childTemp = null;
            List<List<String>> asksList = new ArrayList<>();
            List<List<BigDecimal>> asksBigList = depth.getAsks();
            int asksSize = asksBigList == null ? 0 : asksBigList.size();
            for (int i = 0; i < asksSize; i++) {
                childTemp = asksBigList.get(i);
                price = childTemp.get(0);
                amount = childTemp.get(1);
                List<String> temp = new ArrayList<>();
                temp.add(price.stripTrailingZeros().toPlainString());
                temp.add(amount.stripTrailingZeros().toPlainString());
                asksList.add(temp);
            }

            Collections.reverse(asksList);

            List<List<String>> bidsList = new ArrayList<>();
            List<List<BigDecimal>> bidsBigList = depth.getBids();
            int bidsSize = bidsBigList == null ? 0 : bidsBigList.size();
            for (int i = 0; i < bidsSize; i++) {
                childTemp = bidsBigList.get(i);
                price = childTemp.get(0);
                amount = childTemp.get(1);
                List<String> temp = new ArrayList<>();
                temp.add(price.stripTrailingZeros().toPlainString());
                temp.add(amount.stripTrailingZeros().toPlainString());
                bidsList.add(temp);
            }
            OrderBookResDto orderBookResDto = new OrderBookResDto(asksList, bidsList);
            log.debug("查找 huobi 查找买卖挂单 记录 返回结果：" + JSONObject.toJSONString(orderBookResDto));
            return ResFactory.getInstance().success(orderBookResDto);
        } catch (Throwable throwable) {
            String temp = "调用 huobi 查找买卖挂单信息异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<TradeHistoryListResDto> getTrades(Req<TradeHistoryReqDto> tradeHistoryReqDtoReq) {

        try{
            TradeHistoryReqDto tradeHistoryReqDto = tradeHistoryReqDtoReq.getData();
            if(null == tradeHistoryReqDto
                    || StringUtils.isBlank(tradeHistoryReqDto.getSymbol())){
                String temp = "调用 huobi 查询 最新成交 记录失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            String symbol = tradeHistoryReqDto.getSymbol();
            final String tradeSymbol = toTradeSymbol(symbol);

            Integer limit = tradeHistoryReqDto.getLimit();
            if(null == limit){
                limit = 30;
            }

            IHuobiStockRestApi huobiStockRestApi = new HuobiStockRestApi();
            String marketTrades = huobiStockRestApi.marketTrade(tradeSymbol,limit);
            if(StringUtils.isBlank(marketTrades)){
                String temp = "调用 huobi 查询 最新成交 记录失败，失败原因：交易所返回为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject resultJSon = JSONObject.parseObject(marketTrades);
            String status = resultJSon.getString("status");
            if(StringUtils.isBlank(status)
                    ||!StringUtils.equals(status,HuobiConstant.RESPONSE_STATUS_OK)){
                String temp = "查找 huobi 查找 最新成交 信息失败，交易所返回："+marketTrades;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            JSONArray dataArr = resultJSon.getJSONArray("data");
            int size = dataArr == null?0:dataArr.size();
            List<TradeHistoryResDto> tradeHistoryResDtoList = new ArrayList<>();
            if(size<=0){//交易所返回为空
                TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
                tradeHistoryListResDto.setTradeHistoryResDtoList(tradeHistoryResDtoList);
                return ResFactory.getInstance().success(tradeHistoryListResDto);
            }

            TradeHistoryResDto tempTradeHistoryResDto = null;
            OrderSideEnum orderSide = null;

            JSONObject jsonObject = null;
            JSONObject childJsonObject = null;
            for(int i=0;i<size;i++){
                jsonObject = dataArr.getJSONObject(i);
                JSONArray childDataArr = jsonObject.getJSONArray("data");
                int childSize = childDataArr == null?0:childDataArr.size();
                for(int j=0;j<childSize;j++){
                    childJsonObject = childDataArr.getJSONObject(j);
                    BigDecimal amountBig = childJsonObject.getBigDecimal("amount");
                    BigDecimal priceBig = childJsonObject.getBigDecimal("price");
                    Long timestamp = childJsonObject.getLong("ts");
                    String direction = childJsonObject.getString("direction");
                    if(StringUtils.equals(direction,HuobiConstant.ORDER_TYPE_SELL)){
                        orderSide = OrderSideEnum.ASK;
                    }else{
                        orderSide = OrderSideEnum.BID;
                    }

                    String tradeID = childJsonObject.getString("trade-id");

                    String amount = amountBig.stripTrailingZeros().toPlainString();
                    String price = priceBig.stripTrailingZeros().toPlainString();

//              TradeExchangeApiConstant.OrderSide orderSide, String amount, String symbol, String price, Long timestamp, String id) {
                    tempTradeHistoryResDto = new TradeHistoryResDto(orderSide, amount,
                            symbol, price, timestamp, tradeID);
                    tradeHistoryResDtoList.add(tempTradeHistoryResDto);
                }
            }


//            成功：
//            {
//                "status": "ok",
//                "ch": "market.btcusdt.trade.detail",
//                "ts": 1566960465857,
//                "data": [{
//                    "id": 102233780707,
//                    "ts": 1566960460187,
//                    "data": [{
//                        "amount": 0.099310000000000000,
//                        "ts": 1566960460187,
//                        "id": 10223378070746037691851,
//                        "price": 10113.990000000000000000,
//                        "direction": "sell"
//                    }]
//                }, {
//                    "id": 102233780138,
//                    "ts": 1566960453406,
//                    "data": [{
//                        "amount": 0.131328831292408035,
//                        "ts": 1566960453406,
//                        "id": 10223378013846037631334,
//                        "price": 10113.060000000000000000,
//                        "direction": "buy"
//                        }, {
//                        "amount": 0.007161420367327000,
//                        "ts": 1566960453406,
//                        "id": 10223378013846037681345,
//                        "price": 10113.060000000000000000,
//                        "direction": "buy"
//                    }]
//                }]
//            }
            TradeHistoryListResDto tradeHistoryListResDto = new TradeHistoryListResDto();
            tradeHistoryListResDto.setTradeHistoryResDtoList(tradeHistoryResDtoList);
            return ResFactory.getInstance().success(tradeHistoryListResDto);
        }catch (Throwable throwable){
            String temp = "调用 huobi 查找 最新成交 信息异常,异常信息：";
            log.error(temp,throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<MyTradeListResDto> getMyTrades(Req<MyTradeReqDto> myTradeReqDtoReq) {
        try {
            MyTradeReqDto myTradeReqDto = myTradeReqDtoReq.getData();

            String symbol = myTradeReqDto.getSymbol();
            if (StringUtils.isBlank(symbol)) {
                String temp = "调用 火币 查账户交易清单失败，失败原因：必填参数symbol为空";
                log.error(temp);
                throw new TradeExchangeApiException(temp);
            }


            ApiClient apiClient = new ApiClient(myTradeReqDto.getApiKey(), myTradeReqDto.getApiSecret());

            MatchresultsOrdersDetailResponse<Map> ordersDetailResponse = apiClient.matchresults(myTradeReqDto.getOrderId());
            if (ordersDetailResponse == null) {
                log.warn("查询当前未成交订单，orderId={}--火币返回为空", myTradeReqDto.getOrderId());
                throw new CallExchangeRemoteException("查询订单，orderId=[" + myTradeReqDto.getOrderId() + "]--火币返回为空");

            }
            if (!HuobiConstant.RESPONSE_STATUS_OK.equals(ordersDetailResponse.getStatus())) {
                log.warn("查询当前未成交订单，orderId={}--火币返回为空", myTradeReqDto.getOrderId());
                throw new CallExchangeRemoteException("查询订单，orderId=[" + myTradeReqDto.getOrderId() + "]--火币返回为空");
            }
            List<Map<String, Object>> params = (List<Map<String, Object>>) ordersDetailResponse.getData();
            List<MatchresultsOrdersDetail> ordersDetailList = JSONObject.parseArray(JSONObject.toJSONString(params),MatchresultsOrdersDetail.class);



            MyTradeResDto tempTradeHistoryResDto = null;
            List<MyTradeResDto> trades = new ArrayList<>();
            MatchresultsOrdersDetail tradeHistoryItem = null;

            OrderSideEnum orderSide = null;
            TradeExchangeApiConstant.OrderRole orderRole = null;
            int size = ordersDetailList.size();
            for (int i = 0; i < size; i++) {
                tradeHistoryItem = ordersDetailList.get(i);
                String role = tradeHistoryItem.getRole();
                if ("taker".equals(role)) {//卖
                    orderRole = TradeExchangeApiConstant.OrderRole.TAKER;
                } else {//买
                    orderRole = TradeExchangeApiConstant.OrderRole.MAKER;
                }

                tempTradeHistoryResDto = new MyTradeResDto(null, tradeHistoryItem.getOrderid()+"", null, new BigDecimal(tradeHistoryItem.getFilledamount()),
                        symbol, new BigDecimal(tradeHistoryItem.getPrice()),
                        DateUtils.parse(tradeHistoryItem.getCreatedat()).getTime(),
                        String.valueOf(tradeHistoryItem.getId()), new BigDecimal(tradeHistoryItem.getFilledfees().toString()),orderRole);

                trades.add(tempTradeHistoryResDto);
            }
            MyTradeListResDto tradeHistoryListResDto = new MyTradeListResDto();
            tradeHistoryListResDto.setMyTradeResDtoList(trades);
            return ResFactory.getInstance().success(tradeHistoryListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 火币 查询账户交易清单异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, temp + throwable.getLocalizedMessage());

        }
    }

    @Override
    public Res<SymbolInfoListResDto> getSymbolInfo(Req<SymbolInfoReqDto> symbolInfoReqDtoReq) {
        try {
            if (null == symbolInfoReqDtoReq
                    || null == symbolInfoReqDtoReq.getData()) {
                String temp = "调用 huobi 查询 交易对信息 失败，失败原因：必填参数为空";
                log.error(temp);
                throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
            }

            SymbolInfoReqDto symbolInfoReqDto = symbolInfoReqDtoReq.getData();

            IHuobiStockRestApi stock = new HuobiStockRestApi();
            String symbolsResult = stock.symbols();//查找所有交易对市场信息

            if (StringUtils.isBlank(symbolsResult)) {
                String temp = "调用 huobi 查找 交易对信息 记录失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject symbolsJson = JSONObject.parseObject(symbolsResult);
            if (!symbolsJson.containsKey("status")) {
                String temp = "调用 huobi 查找 交易对信息 记录失败,返回结果为 " + symbolsResult;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            String resultStatus = symbolsJson.getString("status");
            if (StringUtils.isBlank(resultStatus)
                    || !StringUtils.equals(resultStatus, HuobiConstant.RESPONSE_STATUS_OK)) {
                String temp = "调用 huobi 查找 交易对信息 记录失败,返回结果为 " + symbolsResult;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            List<SymbolInfoResDto> symbolInfoResDtoList = new ArrayList<>();
            SymbolInfoResDto symbolInfoResDto = null;

            JSONObject childJSON = null;
            JSONArray dataArr = symbolsJson.getJSONArray("data");
            int size = dataArr == null ? 0 : dataArr.size();
            for (int i = 0; i < size; i++) {//
                childJSON = dataArr.getJSONObject(i);

                String baseName = childJSON.getString("base-currency").toUpperCase();
                String quoteName = childJSON.getString("quote-currency").toUpperCase();

                Integer basePrecision = childJSON.getInteger("amount-precision");//货币精度
                Integer quotePrecision = childJSON.getInteger("price-precision");
                ;//钱币精度
                String symbol = baseName + "/" + quoteName;

                Integer status = ExchangeConstant.SYMBOL_STATUS_OFF;
                String state = childJSON.getString("state");//交易对状态；可能值: [online，offline,suspend] online - 已上线；offline - 交易对已下线，不可交易；suspend -- 交易暂停
                if (StringUtils.equals(state, HuobiConstant.SYMBOL_STATE_ONLINE)) {
                    status = ExchangeConstant.SYMBOL_STATUS_ON;
                }else if(StringUtils.equals(state, HuobiConstant.SYMBOL_STATE_SUSPEND)){
                    status = ExchangeConstant.SYMBOL_STATUS_UNTRADE;
                }

                BigDecimal baseLeast = childJSON.getBigDecimal("min-order-amt");//最小下单量
                BigDecimal quoteLeast = childJSON.getBigDecimal("min-order-value");//最小下单金额

                symbolInfoResDto = new SymbolInfoResDto();
                symbolInfoResDto.setSymbol(symbol);
                symbolInfoResDto.setStatus(status);
                symbolInfoResDto.setQuoteName(quoteName);
                symbolInfoResDto.setBaseName(baseName);
                symbolInfoResDto.setBaseLeast(baseLeast);
                symbolInfoResDto.setQuoteLeast(quoteLeast);
                symbolInfoResDto.setBasePrecision(basePrecision);
                symbolInfoResDto.setQuotePrecision(quotePrecision);
                symbolInfoResDtoList.add(symbolInfoResDto);
            }


//            成功：
//            {
//                "status": "ok",
//                "data": [
//                    {
//                        "base-currency": "egt",
//                        "quote-currency": "usdt",
//                        "price-precision": 6,
//                        "amount-precision": 2,
//                        "symbol-partition": "innovation",
//                        "symbol": "egtusdt",
//                        "state": "online",
//                        "value-precision": 8,
//                        "min-order-amt": 1,
//                        "max-order-amt": 16000000,
//                        "min-order-value": 0.1
//                    },
//                    {
//                        "base-currency": "loom",
//                        "quote-currency": "eth",
//                        "price-precision": 6,
//                        "amount-precision": 4,
//                        "symbol-partition": "innovation",
//                        "symbol": "loometh",
//                        "state": "online",
//                        "value-precision": 8,
//                        "min-order-amt": 1,
//                        "max-order-amt": 14000000,
//                        "min-order-value": 0.001
//                    }
//                ]
//            }
            SymbolInfoListResDto symbolInfoListResDto = new SymbolInfoListResDto();
            symbolInfoListResDto.setExchangeCode(symbolInfoReqDto.getExchCode());
            symbolInfoListResDto.setSymbolInfoResDtoList(symbolInfoResDtoList);
            log.debug("调用 huobi 查找 交易对信息 记录 返回结果：" + JSONObject.toJSONString(symbolInfoListResDto));
            return ResFactory.getInstance().success(symbolInfoListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 huobi 查找 交易对信息 异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<CoinInfoListResDto> getCoinInfo(Req<CoinInfoReqDto> coinInfoReqDtoReq) {
        log.debug("调用 huobi 查询所有币种信息开始");
        IHuobiStockRestApi huobiStockRestApi = new HuobiStockRestApi();
        String result;
        try{
            result = huobiStockRestApi.currencies();

//            成功：
//            {
//                "code": 200,
//                "data": [{
//                    "currency": "18c",
//                    "chains": [{
//                        "chain": "18c",
//                        "numOfConfirmations": 12,
//                        "numOfFastConfirmations": 12,
//                        "depositStatus": "allowed",
//                        "minDepositAmt": "100",
//                        "withdrawStatus": "allowed",
//                        "minWithdrawAmt": "200",
//                        "withdrawPrecision": 8,
//                        "maxWithdrawAmt": "100000000.00000000",
//                        "withdrawQuotaPerDay": "100000000.00000000",
//                        "withdrawQuotaPerYear": "10000000000.00000000",
//                        "withdrawQuotaTotal": "100000000000.00000000",
//                        "withdrawFeeType": "fixed",
//                        "transactFeeWithdraw": "157.67820000"
//                    }],
//                    "instStatus": "normal"
//                },
//                {
//                    "currency": "aac",
//                    "chains": [{
//                        "chain": "aac",
//                        "numOfConfirmations": 12,
//                        "numOfFastConfirmations": 12,
//                        "depositStatus": "allowed",
//                        "minDepositAmt": "5",
//                        "withdrawStatus": "allowed",
//                        "minWithdrawAmt": "10",
//                        "withdrawPrecision": 5,
//                        "maxWithdrawAmt": "5000000.00000000",
//                        "withdrawQuotaPerDay": "5000000.00000000",
//                        "withdrawQuotaPerYear": "500000000.00000000",
//                        "withdrawQuotaTotal": "5000000000.00000000",
//                        "withdrawFeeType": "fixed",
//                        "transactFeeWithdraw": "110.04250000"
//                    }],
//                    "instStatus": "normal"
//                }]
//            }

            if(StringUtils.isBlank(result)){
                String temp = "调用 huobi 查询所有币种信息 记录失败,返回结果为空";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject jsonObject = JSONObject.parseObject(result);
            if(!jsonObject.containsKey("code")){
                String temp = "调用 huobi 查询所有币种信息 记录失败,返回结果"+result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            Integer code = jsonObject.getInteger("code");
            if(null == code
                    || code != 200){
                String temp = "调用 huobi 查询所有币种信息 记录失败,返回结果"+result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            JSONArray dataArr = jsonObject.getJSONArray("data");

            JSONObject childDataJSON = null;

            JSONArray chainsArr = null;

            JSONObject chainsChildJSON = null;

            List<CoinInfoResDto> coinInfoResDtoList = new ArrayList<>();
            CoinInfoResDto coinInfoResDto = null;

            String currency = null;
            String aliasName = null;
            BigDecimal withdrawsLeast = null;
            BigDecimal withdrawsFee = null;
            Integer operationStatus = null;
            Integer bookedConfirmNum = null;
            Integer finalConfirmNum = null;
            Integer operationPrecision = null;

            String depositStatus = null;
            String withdrawStatus = null;

            String withdrawFeeTypeJSON = null;

            Integer withdrawFeeType = null;

            int dataSize = dataArr == null?0:dataArr.size();
            for(int i=0;i<dataSize;i++){
                childDataJSON = dataArr.getJSONObject(i);
                currency = childDataJSON.getString("currency").toUpperCase();
                chainsArr = childDataJSON.getJSONArray("chains");
                int chainsSize = chainsArr == null?0:chainsArr.size();

                for(int j=0;j<chainsSize;j++){
                    chainsChildJSON = chainsArr.getJSONObject(j);
                    depositStatus = chainsChildJSON.getString("depositStatus");
                    withdrawStatus = chainsChildJSON.getString("withdrawStatus");

                    //取第一个能提的
                    if(j<(chainsSize-1)){
                        if(StringUtils.equals(withdrawStatus,HuobiConstant.DEPT_WIAL_STATUS_PROHIBITED)){
                            continue;
                        }
                    }

                    //USDT查ERC20的chain
                    if(StringUtils.equals("USDT",currency)){
                        String chain = chainsChildJSON.getString("chain");
                        if(!StringUtils.equals(chain,"usdterc20")){
                            continue;
                        }
                    }

                    operationStatus = null;
                    if(StringUtils.equals(depositStatus,HuobiConstant.DEPT_WIAL_STATUS_ALLOWED)
                            && StringUtils.equals(withdrawStatus,HuobiConstant.DEPT_WIAL_STATUS_ALLOWED)){
                        operationStatus = ExchangeConstant.COINSTSTUS_DEPOSIT_WITHDRAW_CAN;
                    }else if(StringUtils.equals(depositStatus,HuobiConstant.DEPT_WIAL_STATUS_ALLOWED)){
                        operationStatus = ExchangeConstant.COINSTSTUS_DEPOSIT_CAN;
                    }else if(StringUtils.equals(withdrawStatus,HuobiConstant.DEPT_WIAL_STATUS_ALLOWED)){
                        operationStatus = ExchangeConstant.COINSTSTUS_WITHDRAW_CAN;
                    }else{
                        operationStatus = ExchangeConstant.COINSTSTUS_DEPOSIT_WITHDRAW_CANNOT;
                    }

                    operationPrecision = chainsChildJSON.getInteger("withdrawPrecision");

                    withdrawsLeast = chainsChildJSON.getBigDecimal("minWithdrawAmt");

                    bookedConfirmNum = chainsChildJSON.getInteger("numOfFastConfirmations");
                    finalConfirmNum = chainsChildJSON.getInteger("numOfConfirmations");

                    withdrawFeeTypeJSON = chainsChildJSON.getString("withdrawFeeType");
                    withdrawFeeType = null;

                    if(StringUtils.isNotBlank(withdrawFeeTypeJSON)){
                        if(StringUtils.equals(withdrawFeeTypeJSON,HuobiConstant.WITHDRAWFEE_TYPE_FIXED)){//按量
                            withdrawFeeType = 1;
                        }else if(StringUtils.equals(withdrawFeeTypeJSON,HuobiConstant.WITHDRAWFEE_TYPE_RATIO)){//按比例
                            withdrawFeeType = 2;
                        }else{
                            withdrawFeeType = 3;
                        }
                    }else{
                        withdrawFeeType = 1;
                    }

                    if(withdrawFeeType == 1){
                        withdrawsFee = chainsChildJSON.getBigDecimal("transactFeeWithdraw");
                    }else if(withdrawFeeType == 2){
                        withdrawsFee = chainsChildJSON.getBigDecimal("transactFeeRateWithdraw");
                    }else{
                        withdrawsFee = chainsChildJSON.getBigDecimal("minTransactFeeWithdraw");
                    }

                    coinInfoResDto = new CoinInfoResDto();
                    coinInfoResDto.setCoinName(currency);
                    coinInfoResDto.setAliasName(aliasName);
                    coinInfoResDto.setWithdrawsLeast(withdrawsLeast);
                    coinInfoResDto.setWithdrawsFee(withdrawsFee);
                    coinInfoResDto.setWithdrawsFeeType(withdrawFeeType);
                    coinInfoResDto.setOperationStatus(operationStatus);
                    coinInfoResDto.setOperationPrecision(operationPrecision);
                    coinInfoResDto.setBookedConfirmNum(bookedConfirmNum);
                    coinInfoResDto.setFinalConfirmNum(finalConfirmNum);
                    coinInfoResDtoList.add(coinInfoResDto);
                    break;
                }
            }
            CoinInfoListResDto coinInfoListResDto = new CoinInfoListResDto();
            coinInfoListResDto.setCoinInfoResDtoList(coinInfoResDtoList);
            log.debug("调用 huobi 查找 所有币种信息 记录 返回结果：" + JSONObject.toJSONString(coinInfoListResDto));
            return ResFactory.getInstance().success(coinInfoListResDto);
        }catch (Throwable throwable){
            String temp = "调用 huobi 查找 交易对信息 异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<FullTickerListResDto> fullTickers(Req<FullTickerReqDto> fullTickerReqDtoReq) {
        log.debug("调用 huobi 查询所有交易对tickers信息开始");
        IHuobiStockRestApi huobiStockRestApi = new HuobiStockRestApi();
        String result;
        try {
            result = huobiStockRestApi.tickers();
            log.debug("调用 huobi 查询所有交易对tickers信息,交易所返回："+result);
            if (StringUtils.isBlank(result)) {
                String temp = "调用 huobi 查询所有交易对tickers信息失败，huobi返回为空。";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }
            JSONObject resultJSON = JSONObject.parseObject(result);
            String status = resultJSON.getString("status");
            if (StringUtils.isBlank(status)
                    || !StringUtils.equals("ok", status)) {//
                String temp = "调用 huobi 查询所有交易对tickers信息失败，huobi返回:" + result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            if (!resultJSON.containsKey("data")) {
                String temp = "调用 huobi 查询所有交易对tickers信息失败，huobi返回:" + result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            JSONArray dataArray = resultJSON.getJSONArray("data");
            JSONObject jsonObject = null;
            List<FullTickerResDto> fullTickerResDtoList = new ArrayList<>();
            FullTickerResDto fullTickerResDto = null;
            BigDecimal priceChangePercent = null;
            BigDecimal openBig = null;
            BigDecimal closeBig = null;
            BigDecimal amountBig = null;//以基础币种计量的交易量
            String tradeSymbol = null;
            BigDecimal dividend = null;
            BigDecimal lowPrice = null;
            BigDecimal highPrice = null;
            int size = dataArray.size();
            for (int i = 0; i < size; i++) {
                fullTickerResDto = new FullTickerResDto();
                jsonObject = dataArray.getJSONObject(i);
                if(null != jsonObject){
                    openBig = jsonObject.getBigDecimal("open");

                    closeBig = jsonObject.getBigDecimal("close");
                    if(null != closeBig){
                        closeBig = closeBig.stripTrailingZeros();
                    }

                    tradeSymbol = jsonObject.getString("symbol");
                    amountBig = jsonObject.getBigDecimal("amount");
                    if(null != openBig && null != closeBig && CalculateUtil.compareTo(openBig,BigDecimal.ZERO)>0){
                        dividend = (closeBig.subtract(openBig)).multiply(new BigDecimal(100L));
                        priceChangePercent = CalculateUtil.divide(dividend, openBig, 2, BigDecimal.ROUND_FLOOR);
                    }else{
                        priceChangePercent = BigDecimal.ZERO;
                    }

                    lowPrice = jsonObject.getBigDecimal("low");
                    if(null != lowPrice){
                        lowPrice = lowPrice.stripTrailingZeros();
                    }

                    highPrice = jsonObject.getBigDecimal("high");
                    if(null != highPrice){
                        highPrice = highPrice.stripTrailingZeros();
                    }


                    if(null != closeBig){
                        fullTickerResDto.setLast(closeBig);
                        fullTickerResDto.setPriceChangePercent(priceChangePercent);
                        fullTickerResDto.setSymbol(this.symbol(tradeSymbol));
                        fullTickerResDto.setVolume24h(amountBig);
                        fullTickerResDto.setLowPrice(lowPrice);
                        fullTickerResDto.setHighPrice(highPrice);
                        fullTickerResDtoList.add(fullTickerResDto);
                    }
                }
            }


//            成功：
//            {
//                "status": "ok",
//                "ts": 1560775316324,
//                "data": [{
//                    "open": 0.3562,
//                    "close": 0.362993,
//                    "low": 0.354664,
//                    "high": 0.366335,
//                    "amount": 12864.52764219686,
//                    "count": 5728,
//                    "vol": 4631.4138790012,
//                    "symbol": "xmreth"
//                },
//                {
//                    "open": 0.00017046,
//                    "close": 0.00017629,
//                    "low": 0.00017007,
//                    "high": 0.00018339,
//                    "amount": 8363.7,
//                    "count": 5419,
//                    "vol": 1.4455379216,
//                    "symbol": "kmdbtc"
//            }]}

            FullTickerListResDto fullTickerListResDto = new FullTickerListResDto();
            fullTickerListResDto.setFullTickerResDtoList(fullTickerResDtoList);
            log.debug("调用 huobi 查询全量ticker信息 返回结果：" + JSONObject.toJSONString(fullTickerListResDto));
            return ResFactory.getInstance().success(fullTickerListResDto);
        } catch (Throwable throwable) {
            String temp = "调用 huobi 查找所有交易对的ticker信息失败，异常信息：";
            log.warn(temp);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
    }

    @Override
    public Res<TickerPriceResDto> tickerPrice(Req<TickerPriceReqDto> tickerPriceReqDtoReq) {
        String symbol = null;
        try {
            if (null == tickerPriceReqDtoReq
                    || null == tickerPriceReqDtoReq.getData()) {
                String temp = "调用 huobi 查询 交易对价格信息 失败，失败原因：必填参数为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }
            TickerPriceReqDto tickerPriceReqDto = tickerPriceReqDtoReq.getData();
            symbol = tickerPriceReqDto.getSymbol();

            String tradeSymbol = this.toTradeSymbol(symbol);
            IHuobiStockRestApi huobiStockRestApi = new HuobiStockRestApi();
            String result = huobiStockRestApi.trade(tradeSymbol);
            if (StringUtils.isBlank(result)) {
                String temp = "调用 huobi 查询 交易对价格信息 失败，huobi返回为空。";
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }
            JSONObject resultJSON = JSONObject.parseObject(result);
            String status = resultJSON.getString("status");
            if (StringUtils.isBlank(status)
                    || !StringUtils.equals("ok", status)) {//
                String temp = "调用 huobi 查询 交易对价格信息 失败，huobi返回:" + result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            if (!resultJSON.containsKey("tick")) {
                String temp = "调用 huobi 查询 交易对价格信息 失败，huobi返回:" + result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

//            成功：
//            {
//                "status": "ok",
//                "ch": "market.ethusdt.trade.detail",
//                "ts": 1567411263598,
//                "tick": {
//                    "id": 102074287965,
//                    "ts": 1567411263399,
//                    "data": [{
//                        "amount": 4.517000000000000000,
//                        "ts": 1567411263399,
//                        "id": 10207428796546660870302,
//                        "price": 170.770000000000000000,
//                        "direction": "sell"
//                        }, {
//                        "amount": 68.377000000000000000,
//                        "ts": 1567411263399,
//                        "id": 10207428796546660740195,
//                        "price": 170.750000000000000000,
//                        "direction": "sell"
//                    }]
//                }
//            }

            JSONObject tickJson = resultJSON.getJSONObject("tick");
            if(null == tickJson){
                String temp = "调用 huobi 查询 交易对价格信息 失败，huobi返回:" + result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            JSONArray tickDataArr = tickJson.getJSONArray("data");
            int jsonListSize = tickDataArr == null?0:tickDataArr.size();
            if(jsonListSize<=0){
                String temp = "调用huobi查询价格信息失败,交易对名称symbol="+symbol+",返回信息:"+result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }
            JSONObject tickDataJSON = tickDataArr.getJSONObject(0);
            if(null == tickDataJSON){
                String temp = "调用huobi查询价格信息失败,交易对名称symbol="+symbol+",返回信息:"+result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }
            BigDecimal priceBig = tickDataJSON.getBigDecimal("price");
            if(null == priceBig){
                String temp = "调用huobi查询价格信息失败,交易对名称symbol="+symbol+",返回信息:"+result;
                log.warn(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            TickerPriceResDto tickerPriceResDto = new TickerPriceResDto(symbol,priceBig.stripTrailingZeros());
            return ResFactory.getInstance().success(tickerPriceResDto);
        } catch (Throwable e) {
            String temp = "调用huobi查询价格信息失败,交易对名称symbol="+symbol+",异常信息：";
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
    private List<OrderDetailResDto> transferOrderListReturn(List<Map<String, Object>> orders, String symbol) {
        List<OrderDetailResDto> list = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (Map<String, Object> order : orders) {
                list.add(transferOrderDetailDto(transferOrigin(order), symbol));
            }
            return list;
        }
        return list;
    }

    /**
     * 转换三方返回的订单列表实体
     *
     * @param orders
     * @return
     */
    private List<OrderDetailResDto> transferOpenOrderListReturn(List<Map<String, Object>> orders, String symbol) {
        List<OrderDetailResDto> list = new ArrayList<>();
        if (orders != null && orders.size() > 0) {
            for (Map<String, Object> order : orders) {
                list.add(transferOrderDetailDto(transferOpenOrigin(order), symbol));
            }
            return list;
        }
        return list;
    }
    /**
     * 转换huobi返回对象为可解析的ordersDetail
     *
     * @param ordersDetail
     * @return
     */
    public OrderDetailResDto transferOrderDetailDto(OrdersDetail ordersDetail, String symbol) {

        if (ordersDetail.getFieldAmount()==null){
            ordersDetail.setFieldAmount(BigDecimal.ZERO);
        }
        OrderDetailResDto result = new OrderDetailResDto();
        result.setOrderId(ordersDetail.getId());
        String status = ordersDetail.getState();
        if (StringUtils.equals(status, HuobiConstant.ORDER_STATUS_CANCELED)) {// 已取消
            result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.CANCEL);
            result.setFinishedAt(ordersDetail.getFinishedAt());//订单变为最终状态的时间
        } else if (StringUtils.equals(status, HuobiConstant.ORDER_STATUS_FILLED)) {// 已完成
            result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.DEAL);
            result.setFinishedAt(ordersDetail.getFinishedAt());//订单变为最终状态的时间
        } else if (StringUtils.equals(status, HuobiConstant.ORDER_STATUS_PARTIAL_FILLED)) {//部分成交
            result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.PART);
        } else if (StringUtils.equals(status, HuobiConstant.ORDER_STATUS_PARTIAL_CANCELED)) {//部分撤销
            result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.CANCEL);
            result.setFinishedAt(ordersDetail.getFinishedAt());//订单变为最终状态的时间
        } else if (StringUtils.equals(status, HuobiConstant.ORDER_STATUS_SUBMITTING)
                || StringUtils.equals(status, HuobiConstant.ORDER_STATUS_SUBMITTED)) {//已挂单
            result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.COMMIT);
        } else {
            result.setOrderStatus(TradeExchangeApiConstant.OrderStatus.UNKNOW);
        }
        result.setSymbol(symbol);

        String type = ordersDetail.getType();//买卖类型 sell卖出, buy买入
        //buy-market：市价买, sell-market：市价卖, buy-limit：限价买, sell-limit：限价卖, buy-ioc：IOC买单, sell-ioc：IOC卖单
        if (StringUtils.equals(type, "buy-market")) {//市价买
            result.setOrderSide(OrderSideEnum.BID);
            result.setOrderType(TradeExchangeApiConstant.OrderType.MARKET);
        } else if (StringUtils.equals(type, "sell-market")) {//市价卖
            result.setOrderSide(OrderSideEnum.ASK);

            result.setOrderType(TradeExchangeApiConstant.OrderType.MARKET);
        } else if (StringUtils.equals(type, "buy-limit")) {//限价买
            result.setOrderSide(OrderSideEnum.BID);

            result.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        } else if (StringUtils.equals(type, "sell-limit")) {//限价卖
            result.setOrderSide(OrderSideEnum.ASK);
            result.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        }
        result.setThirdAccountId(ordersDetail.getAccountId());
        result.setAmount(ordersDetail.getAmount());//下单量
        result.setPrice(ordersDetail.getPrice());//下单价格
        result.setFilledPrice(ordersDetail.getFieldPrice());//已成交价格
        result.setFilledAmount(ordersDetail.getFieldAmount());//已成交数量
        result.setFilledCashAmount(ordersDetail.getFieldCashAmount());//已成交总金额
        result.setFeeValue(ordersDetail.getFieldFees());//手续费
        result.setThirdCreateTime(ordersDetail.getCreatedAt());
        result.setLeftAmount(ordersDetail.getAmount().subtract(ordersDetail.getFieldAmount()));
        result.setTradeSymbol(toTradeSymbol(symbol));
        if (null == result.getFinishedAt()) {
            result.setFinishedAt(new Date());//订单变为最终状态的时间
        }
        return result;
    }

    /**
     * 转换火币返回对象为可解析的ordersDetail
     *
     * @param params
     * @return
     */
    public OrdersDetail transferOrigin(Map<String, Object> params) {
        /**
         * id : 59378 symbol : ethusdt account-id : 100009 amount : 10.1000000000 price : 100.1000000000
         * created-at : 1494901162595 type : buy-limit field-amount : 10.1000000000
         * field-cash-amount : 1011.0100000000 field-fees : 0.0202000000
         * finished-at : 1494901400468 user-id : 1000 source : api
         * state : filled canceled-at : 0 exchange : huobi batch :
         */
        OrdersDetail ordersDetail = new OrdersDetail();
        ordersDetail.setId(String.valueOf(params.get("id")));
        ordersDetail.setAccountId(String.valueOf(params.get("account-id")));

        String createAt = String.valueOf(params.get("created-at"));
        ordersDetail.setCreatedAt(new Date(Long.valueOf(createAt)));
        String finishAt = String.valueOf(params.get("finished-at"));
        if (StringUtils.isNotEmpty(finishAt) && !"null".equals(finishAt)){
            ordersDetail.setFinishedAt(new Date(Long.valueOf(finishAt)));
        }
        ordersDetail.setSource(String.valueOf(params.get("source")));
        ordersDetail.setState(String.valueOf(params.get("state")));
        ordersDetail.setSymbol(String.valueOf(params.get("symbol")));

        String type = String.valueOf(params.get("type"));
        ordersDetail.setType(type);

        Integer numDecimal = params.get("numDecimal") == null ? 18 : (Integer) params.get("numDecimal");//货数量精度
        Integer amountDecimal = params.get("amountDecimal") == null ? 18 : (Integer) params.get("amountDecimal");//金额精度

        if (params.get("field-fees")!=null && !"null".equals(params.get("field-fees"))){
            ordersDetail.setFieldFees(CalculateUtil.roundFloor(String.valueOf(params.get("field-fees")), amountDecimal));//手续费
        }
        BigDecimal fieldAmount = BigDecimal.ZERO;
        if (params.get("field-amount")!=null && !"null".equals(params.get("field-amount"))){
            fieldAmount = new BigDecimal(String.valueOf(params.get("field-amount")));//已成交数量
            ordersDetail.setFieldAmount(CalculateUtil.roundFloor(fieldAmount, numDecimal));
        }

        BigDecimal fieldCashAmount = BigDecimal.ZERO;
        if (params.get("field-cash-amount")!=null && !"null".equals(params.get("field-cash-amount"))){
             fieldCashAmount = new BigDecimal(String.valueOf(params.get("field-cash-amount")));//已成交总金额
            ordersDetail.setFieldCashAmount(CalculateUtil.roundFloor(fieldCashAmount, amountDecimal));
        }


        ordersDetail.setPrice(new BigDecimal(String.valueOf(params.get("price"))));//下单价格

        BigDecimal fieldPrice = BigDecimal.ZERO;
        if (StringUtils.equals(CreateOrderRequest.OrderType.BUY_LIMIT, type)
                || StringUtils.equals(CreateOrderRequest.OrderType.SELL_LIMIT, type)) {//限价买入/卖出

            ordersDetail.setAmount(CalculateUtil.roundFloor(String.valueOf(params.get("amount")), numDecimal));//下单数量

            if (fieldAmount.compareTo(BigDecimal.ZERO) != 0
                    && fieldCashAmount.compareTo(BigDecimal.ZERO) != 0) {//成交额不为0
                fieldPrice = CalculateUtil.divide(fieldCashAmount, fieldAmount, amountDecimal);
            }
            ordersDetail.setFieldPrice(fieldPrice);//成交均价
        } else if (StringUtils.equals(CreateOrderRequest.OrderType.BUY_MARKET, type)) {//市价买入
            if (fieldAmount.compareTo(BigDecimal.ZERO) != 0
                    && fieldCashAmount.compareTo(BigDecimal.ZERO) != 0) {//成交额不为0
                fieldPrice = CalculateUtil.divide(fieldCashAmount, fieldAmount, amountDecimal);
            }
            ordersDetail.setAmount(CalculateUtil.roundFloor(fieldAmount, numDecimal));
            ordersDetail.setFieldPrice(fieldPrice);//成交均价
        } else if (StringUtils.equals(CreateOrderRequest.OrderType.SELL_MARKET, type)) {//市价卖出
            ordersDetail.setAmount(CalculateUtil.roundFloor(String.valueOf(params.get("amount")), numDecimal));
            if (fieldAmount.compareTo(BigDecimal.ZERO) != 0
                    && fieldCashAmount.compareTo(BigDecimal.ZERO) != 0) {//成交额不为0
                fieldPrice = CalculateUtil.divide(fieldCashAmount, fieldAmount, amountDecimal);
            }
            ordersDetail.setFieldPrice(fieldPrice);//成交均价
        }

        return ordersDetail;
    }
    /**
     * 转换火币返回对象为可解析的openOrder
     *
     * @param params
     * @return
     */
    public OrdersDetail transferOpenOrigin(Map<String, Object> params) {
        /**
         * id : 59378 symbol : ethusdt account-id : 100009 amount : 10.1000000000 price : 100.1000000000
         * created-at : 1494901162595 type : buy-limit field-amount : 10.1000000000
         * field-cash-amount : 1011.0100000000 field-fees : 0.0202000000
         * finished-at : 1494901400468 user-id : 1000 source : api
         * state : filled canceled-at : 0 exchange : huobi batch :
         */
        OrdersDetail ordersDetail = new OrdersDetail();
        ordersDetail.setId(String.valueOf(params.get("id")));
        ordersDetail.setAccountId(String.valueOf(params.get("account-id")));

        String createAt = String.valueOf(params.get("created-at"));
        ordersDetail.setCreatedAt(new Date(Long.valueOf(createAt)));
        String finishAt = String.valueOf(params.get("finished-at"));
        if (StringUtils.isNotEmpty(finishAt) && !"null".equals(finishAt)){
            ordersDetail.setFinishedAt(new Date(Long.valueOf(finishAt)));
        }
        ordersDetail.setSource(String.valueOf(params.get("source")));
        ordersDetail.setState(String.valueOf(params.get("state")));
        ordersDetail.setSymbol(String.valueOf(params.get("symbol")));

        String type = String.valueOf(params.get("type"));
        ordersDetail.setType(type);

        Integer numDecimal = params.get("numDecimal") == null ? 18 : (Integer) params.get("numDecimal");//货数量精度
        Integer amountDecimal = params.get("amountDecimal") == null ? 18 : (Integer) params.get("amountDecimal");//金额精度

        if (params.get("filled-fees")!=null && !"null".equals(params.get("filled-fees"))){
            ordersDetail.setFieldFees(CalculateUtil.roundFloor(String.valueOf(params.get("filled-fees")), amountDecimal));//手续费
        }
        BigDecimal fieldAmount = BigDecimal.ZERO;
        if (params.get("filled-amount")!=null && !"null".equals(params.get("filled-amount"))){
            fieldAmount = new BigDecimal(String.valueOf(params.get("filled-amount")));//已成交数量
            ordersDetail.setFieldAmount(CalculateUtil.roundFloor(fieldAmount, numDecimal));
        }

        BigDecimal fieldCashAmount = BigDecimal.ZERO;
        if (params.get("filled-cash-amount")!=null && !"null".equals(params.get("filled-cash-amount"))){
            fieldCashAmount = new BigDecimal(String.valueOf(params.get("filled-cash-amount")));//已成交总金额
            ordersDetail.setFieldCashAmount(CalculateUtil.roundFloor(fieldCashAmount, amountDecimal));
        }


        ordersDetail.setPrice(new BigDecimal(String.valueOf(params.get("price"))));//下单价格

        BigDecimal fieldPrice = BigDecimal.ZERO;
        if (StringUtils.equals(CreateOrderRequest.OrderType.BUY_LIMIT, type)
                || StringUtils.equals(CreateOrderRequest.OrderType.SELL_LIMIT, type)) {//限价买入/卖出

            ordersDetail.setAmount(CalculateUtil.roundFloor(String.valueOf(params.get("amount")), numDecimal));//下单数量

            if (fieldAmount.compareTo(BigDecimal.ZERO) != 0
                    && fieldCashAmount.compareTo(BigDecimal.ZERO) != 0) {//成交额不为0
                fieldPrice = CalculateUtil.divide(fieldCashAmount, fieldAmount, amountDecimal);
            }
            ordersDetail.setFieldPrice(fieldPrice);//成交均价
        } else if (StringUtils.equals(CreateOrderRequest.OrderType.BUY_MARKET, type)) {//市价买入
            if (fieldAmount.compareTo(BigDecimal.ZERO) != 0
                    && fieldCashAmount.compareTo(BigDecimal.ZERO) != 0) {//成交额不为0
                fieldPrice = CalculateUtil.divide(fieldCashAmount, fieldAmount, amountDecimal);
            }
            ordersDetail.setAmount(CalculateUtil.roundFloor(fieldAmount, numDecimal));
            ordersDetail.setFieldPrice(fieldPrice);//成交均价
        } else if (StringUtils.equals(CreateOrderRequest.OrderType.SELL_MARKET, type)) {//市价卖出
            ordersDetail.setAmount(CalculateUtil.roundFloor(String.valueOf(params.get("amount")), numDecimal));
            if (fieldAmount.compareTo(BigDecimal.ZERO) != 0
                    && fieldCashAmount.compareTo(BigDecimal.ZERO) != 0) {//成交额不为0
                fieldPrice = CalculateUtil.divide(fieldCashAmount, fieldAmount, amountDecimal);
            }
            ordersDetail.setFieldPrice(fieldPrice);//成交均价
        }

        return ordersDetail;
    }

    @Override
    public Res<AccountInfoResDto> getAccountInfo(Req<AccountInfoReqDto> accountInfoReqDtoReq) {
        AccountInfoReqDto accountInfoReqDto = accountInfoReqDtoReq.getData();
        ApiClient apiClient = new ApiClient(accountInfoReqDto.getApiKey(), accountInfoReqDto.getApiSecret());
        AccountsResponse<List<Accounts>> accountsResponse = apiClient.accounts();
        if (accountsResponse == null) {
            throw new CallExchangeRemoteException("火币返回账户信息为空");
        }
        if (!HuobiConstant.RESPONSE_STATUS_OK.equals(accountsResponse.getStatus())) {
            throw new CallExchangeRemoteException("查询火币账户信息失败：" + accountsResponse.getErrMsg());
        }
        return ResFactory.getInstance().success(new AccountInfoResDto(accountsResponse.getData().stream().map(accounts -> new HuobiAccount(accounts.getId(), accounts.getType(), accounts.getState(), accounts.getUserid())).collect(Collectors.toList())));
    }

    @Override
    public Res<ResList<KLineResDto>> kline(Req<KlineReqDto> reqDtoReq) throws Throwable {
        KlineReqDto klineReqDto = reqDtoReq.getData();
        List<KLineResDto> resultList = new ArrayList<>();
        String instrument_id = klineReqDto.getSymbol();
        if (null == instrument_id) {
            String temp = "调用 okex kline失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }
        String period= klineReqDto.getPeriod();
        if (null == period) {
            String temp = "调用 okex kline失败，必填参数为空";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }
        String tradePeriod = null;
        if ("1".equals(period)){
            tradePeriod="1min";
        }else if ("5".equals(period)){
            tradePeriod="5min";
        }if ("15".equals(period)){
            tradePeriod="15min";
        }if ("30".equals(period)){
            tradePeriod="30min";
        }if ("60".equals(period)){
            tradePeriod="60min";
        }if ("240".equals(period)){
            tradePeriod="4hour";
        }if ("1440".equals(period)){
            tradePeriod="1day";
        }if ("10080".equals(period)){
            tradePeriod="1week";
        }if ("43200".equals(period)){
            tradePeriod="1mon";
        }if ("525600".equals(period)){
            tradePeriod="1year";
        }
        if (null == tradePeriod) {
            String temp = "调用 okex kline失败，时间粒度不支持";
            log.warn(temp);
            throw new VerificationException(StateTypeSuper.FAIL_PARAMETER);
        }
        ApiClient apiClient = new ApiClient();
        KlineResponse<List<Kline>> klineResponse = apiClient.kline(instrument_id.replace("/", "").toLowerCase(),tradePeriod
                ,klineReqDto.getSize());
        if (klineResponse == null) {
            throw new CallExchangeRemoteException("火币返回kline为空");
        }
        if (!HuobiConstant.RESPONSE_STATUS_OK.equals(klineResponse.getStatus())) {
            throw new CallExchangeRemoteException("查询火币账户信息失败：" + klineResponse.errMsg);
        }
        List<Kline> klineList = klineResponse.data;
        for (Kline kline : klineList){
            KLineResDto dto = new KLineResDto();
            dto.setClose(BigDecimal.valueOf(kline.getClose()));
            dto.setHighPrice(BigDecimal.valueOf(kline.getHigh()));
            dto.setLowPrice(BigDecimal.valueOf(kline.getLow()));
            dto.setOpen(BigDecimal.valueOf(kline.getOpen()));
            dto.setSymbol(instrument_id);
            dto.setVolume(BigDecimal.valueOf(kline.getVol()));
            dto.setAmount(BigDecimal.valueOf(kline.getAmount()));
            dto.setId(kline.getId());
            resultList.add(dto);
        }
        return ResFactory.getInstance().successList(resultList);
    }
    /**
     * 查询账户余额信息
     *
     * @param
     * @return
     */
    @Override
    public Res<ResList<QueryBalanceResDto>> getBalance(QueryBalanceReqDto queryBalanceReqDto) {
        ApiClient apiClient = new ApiClient(queryBalanceReqDto.getApiKey(), queryBalanceReqDto.getApiSecret());
        BalanceResponse balanceResponse = apiClient.balance(queryBalanceReqDto.getThirdAcctId());

        String status = balanceResponse.getStatus();

        if(StringUtils.equals(status, "error")){
            log.warn("调用火币查询余额error，失败信息：errCode=" + balanceResponse.errCode + ",errMsg=" + balanceResponse.getErrMsg());
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "失败信息：errCode=" + balanceResponse.errCode + ",errMsg=" + balanceResponse.getErrMsg());
        }

        List<BalanceBean> balanceBeans = balanceResponse.getData().getList();

        List<QueryBalanceResDto> queryBalanceResDtos = Lists.newArrayList();
        Map<String, List<BalanceBean>> collect = balanceBeans.stream().collect(Collectors.groupingBy(balanceBean -> balanceBean.getCurrency()));

        collect.forEach((currency, balanceBeans1) -> {
            QueryBalanceResDto queryBalanceResDto = new QueryBalanceResDto();
            queryBalanceResDto.setCurrency(currency.toUpperCase());
            balanceBeans1.stream().forEach(balanceBean -> {
                String type = balanceBean.getType();
                String balance = balanceBean.getBalance();
                if (StringUtils.equals(type, HuobiConstant.CURRENCY_TYPE_TRADE)) {
                    queryBalanceResDto.setUsable(new BigDecimal(balance));
                } else if (StringUtils.equals(type, HuobiConstant.CURRENCY_TYPE_FROZEN)) {
                    queryBalanceResDto.setFrozen(new BigDecimal(balance));
                }
            });

            queryBalanceResDtos.add(queryBalanceResDto);
        });

        return ResFactory.getInstance().successList(queryBalanceResDtos);
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

        IHuobiStockRestApi huobiStockRestApi = new HuobiStockRestApi(depositAddressReqDto.getApiKey(),depositAddressReqDto.getApiSecret());
        String result = null;
        try {
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("currency",coinName.toLowerCase());
            result = huobiStockRestApi.depositAddress(paramMap);

//            成功：
//            {
//                "code": 200,
//                "data": [{
//                    "currency": "btc",
//                    "address": "1PSRjPg53cX7hMRYAXGJnL8mqHtzmQgPUs",
//                    "addressTag": "",
//                    "chain": "btc"
//                }]
//            }

            if (StringUtils.isBlank(result)) {
                String temp = "调用 huobi 查询币种充值地址信息失败，交易所返回为空";
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_EMPTY_RESULT);
            }

            JSONObject jsonObject = JSONObject.parseObject(result);
            Integer code = jsonObject.getInteger("code");
            if (null == code || 200 != code) {
                String temp = "调用 huobi 查询币种充值地址信息失败，币种名称："+depositAddressReqDto.getCoinName()+"交易所返回:" + result;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }

            JSONArray dataArr = jsonObject.getJSONArray("data");
            JSONObject dataJSON = null;
            int size = dataArr == null?0:dataArr.size();
            if(size>0){
                dataJSON = dataArr.getJSONObject(0);
                String address = dataJSON.getString("address");
                DepositAddressResDto depositAddressResDto = new DepositAddressResDto();
                depositAddressResDto.setAddress(address);
                depositAddressResDto.setCoinName(depositAddressReqDto.getCoinName());
                return ResFactory.getInstance().success(depositAddressResDto);
            }else{
                String temp = "调用 huobi 查询币种充值地址信息失败，币种名称："+depositAddressReqDto.getCoinName()+"交易所返回:" + result;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_NONSTANDARD_RESULT);
            }
        } catch (Throwable throwable) {
            String temp = "调用 huobi 查询币种充值地址信息异常";
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
                tickerPriceReqDto.setExchCode(ExchangeCode.HUOBI);

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
