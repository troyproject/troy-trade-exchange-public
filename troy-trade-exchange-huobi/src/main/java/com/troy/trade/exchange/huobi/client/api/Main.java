package com.troy.trade.exchange.huobi.client.api;


import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.troy.trade.exchange.api.exception.TradeExchangeApiException;
import com.troy.trade.exchange.huobi.dto.response.OrdersDetailResponse;
import com.troy.trade.exchange.huobi.dto.response.OrdersListResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Main {

    static final String API_KEY = "ec0fa852-nbtycf4rw2-031e30ff-2def6";
    static final String API_SECRET = "70ceb97a-94a06763-a4754e78-8b3d8";

    static void apiSample() {
        // create ApiClient using your api key and api secret:
        ApiClient client = new ApiClient(API_KEY, API_SECRET);
        long start = System.currentTimeMillis();

        OrdersListResponse<Map> ordersDetailResponse = client.ordersHistory("trxeth", 100);

        List<Map<String, Object>> params = (List<Map<String, Object>>) ordersDetailResponse.getData();
        // 全部历史订单
        OrdersListResponse<Map> ordersDetailResponseALl = client.ordersHistoryAll("trxeth", "100","","","submitted,partial-filled");
        params.addAll((Collection<? extends Map<String, Object>>) ordersDetailResponseALl.getData());
        for (int i=0;i<5;i++){
            List<Map<String, Object>> params2 = (List<Map<String, Object>>) ordersDetailResponseALl.getData();
            if (params2!=null && params2.size()>0){
                System.out.println("123");
                ordersDetailResponseALl = client.ordersHistoryAll("trxeth", "100","next",params2.get(params2.size()-1).get("id").toString(),"submitted,partial-filled");
                params.addAll((Collection<? extends Map<String, Object>>) ordersDetailResponseALl.getData());
            }
            break;

        }
        System.out.println(JSONObject.toJSONString(params));
        // get symbol list:
//        print(client.getSymbols());

        //获取 K 线
        //------------------------------------------------------ kline -------------------------------------------------------
//        KlineResponse kline = client.kline("btcusdt", "5min", "100");
////        print(kline);
//        System.out.println("k线耗时：" + (System.currentTimeMillis() - start));
//        start = System.currentTimeMillis();
//        //------------------------------------------------------ merged -------------------------------------------------------
//
//        MergedResponse merged = client.merged("ethusdt");
////        print(merged);
//        System.out.println("聚合行情耗时：" + (System.currentTimeMillis() - start));
//        start = System.currentTimeMillis();
//        //------------------------------------------------------ depth -------------------------------------------------------

//        DepthRequest depthRequest = new DepthRequest();
//        depthRequest.setSymbol("btcusdt");
//        depthRequest.setType("step0");
//        DepthResponse depth = client.depth(depthRequest);
//        print(depth);

//        //------------------------------------------------------ trade -------------------------------------------------------
//        TradeResponse trade = client.trade("ethusdt");
//        print(trade);

//        //------------------------------------------------------ historyTrade -------------------------------------------------------
//        HistoryTradeResponse historyTrade = client.historyTrade("ethusdt", "20");
//        print(historyTrade);
//
//        //------------------------------------------------------ historyTrade -------------------------------------------------------
//        DetailResponse detailTrade = client.detail("ethusdt");
//        print(detailTrade);
//
//        //------------------------------------------------------ symbols -------------------------------------------------------
//        SymbolsResponse symbols = client.symbols("btcusdt");
//        print(symbols);
//
//        //------------------------------------------------------ Currencys -------------------------------------------------------
//        CurrencysResponse currencys = client.currencys("btcusdt");
//        print(currencys);
//
//        //------------------------------------------------------ Currencys -------------------------------------------------------
//        TimestampResponse timestamp = client.timestamp();
//        print(timestamp);
//
        //------------------------------------------------------ accounts -------------------------------------------------------
//        AccountsResponse accounts = client.accounts();
//        print(accounts);
//        System.out.println("accounts耗时：" + (System.currentTimeMillis() - start));
//
//        //------------------------------------------------------ balance -------------------------------------------------------
//        List<Accounts> list = (List<Accounts>) accounts.getData();
//        BalanceResponse balance = client.balance(String.valueOf(list.get(0).getId()));
//        BalanceResponse balance2 = client.balance(String.valueOf(list.get(1).getId()));
//
//        print(balance); //spot
//        print(balance2);//otc
//
//        Long orderId = 123L;
//        if (!list.isEmpty()) {
//            // find account id:
//            Accounts account = list.get(0);
//            long accountId = account.getId();
//            // create order:
//            CreateOrderRequest createOrderReq = new CreateOrderRequest();
//            createOrderReq.accountId = String.valueOf(accountId);
//            createOrderReq.amount = "0.02";
//            createOrderReq.price = "0.1";
//            createOrderReq.symbol = "eosusdt";
//            createOrderReq.type = CreateOrderRequest.OrderType.BUY_LIMIT;
//            createOrderReq.source = "api";
//
//            //------------------------------------------------------ 创建订单  -------------------------------------------------------
//            orderId = client.createOrder(createOrderReq);
//            print(orderId);
//            // place order:
//
//            //------------------------------------------------------ 执行订单  -------------------------------------------------------
//            String r = client.placeOrder(orderId);
//            print(r);
//        }
//
//        //------------------------------------------------------ submitcancel 取消订单 -------------------------------------------------------
//
//        SubmitcancelResponse submitcancel = client.submitcancel("46155761006");
//       print(submitcancel);
//
//        //------------------------------------------------------ submitcancel 批量取消订单-------------------------------------------------------
////    String[] orderList = {"727554767","727554766",""};
////    String[] orderList = {String.valueOf(orderId)};
//        List orderList = new ArrayList();
//        orderList.add(orderId);
//        BatchcancelResponse submitcancels = client.submitcancels(orderList);
//        print(submitcancels);
//
//        //------------------------------------------------------ ordersDetail 订单详情 -------------------------------------------------------
     //   OrdersDetailResponse ordersDetailResponse = client.ordersDetail("47505356651");
     //   print(ordersDetailResponse);
//
//        //------------------------------------------------------ ordersDetail 已经成交的订单详情 -------------------------------------------------------
////    String.valueOf(orderId)
//        MatchresultsOrdersDetailResponse matchresults = client.matchresults("714746923");
//        print(ordersDetail);
//
//        //------------------------------------------------------ ordersDetail 已经成交的订单详情 -------------------------------------------------------
////    String.valueOf(orderId)
//        IntrustOrdersDetailRequest req = new IntrustOrdersDetailRequest();
//        req.symbol = "btcusdt";
//        req.types = IntrustOrdersDetailRequest.OrderType.BUY_LIMIT;
////    req.startDate = "2018-01-01";
////    req.endDate = "2018-01-14";
//        req.states = IntrustOrdersDetailRequest.OrderStates.FILLED;
////    req.from = "";
////    req.direct = "";
////    req.size = "";
//
//
////    public String symbol;	   //true	string	交易对		btcusdt, bccbtc, rcneth ...
////    public String types;	   //false	string	查询的订单类型组合，使用','分割		buy-market：市价买, sell-market：市价卖, buy-limit：限价买, sell-limit：限价卖
////    public String startDate;   //false	string	查询开始日期, 日期格式yyyy-mm-dd
////    public String endDate;	   //false	string	查询结束日期, 日期格式yyyy-mm-dd
////    public String states;	   //true	string	查询的订单状态组合，使用','分割		pre-submitted 准备提交, submitted 已提交, partial-filled 部分成交,
////    // partial-canceled 部分成交撤销, filled 完全成交, canceled 已撤销
////    public String from;	       //false	string	查询起始 ID
////    public String direct;	   //false	string	查询方向		prev 向前，next 向后
////    public String size;	       //false	string	查询记录大小
//
//
//        //------------------------------------------------------ order 查询当前委托、历史委托 -------------------------------------------------------

//        IntrustOrdersDetailRequest req = new IntrustOrdersDetailRequest();
//        req.symbol = "pnteth";
//        req.types = IntrustOrdersDetailRequest.OrderType.BUY_LIMIT;
//        req.startDate = "2018-01-01";
//        req.endDate = "2018-01-14";
//        req.states = IntrustOrdersDetailRequest.OrderStates.PARTIAL_FILLED + "," + IntrustOrdersDetailRequest.OrderStates.SUBMITTED;
//        req.from = "";
//        req.direct = "";
//        req.size = "";
//        IntrustDetailResponse<List<IntrustDetail>> intrustDetail = client.intrustOrdersDetailHadax(req);
//        print(intrustDetail);
//        MergedResponse mergedResponse = client.mergedHadax(req.symbol);
//        print(mergedResponse.getTick());
//
    // get accounts:
//    List<Account> accounts1 = client.getAccounts();
//    print(accounts1);

    }

    static void print(Object obj) {
        try {
            System.out.println(JsonUtil.writeValue(obj));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
