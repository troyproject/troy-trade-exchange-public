package com.troy.trade.exchange.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.OrderSideEnum;
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import com.troy.trade.exchange.api.model.dto.in.order.MyTradeReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.OpenOrdersReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.OrderDetailReqDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderDetailResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderListResData;
import com.troy.trade.exchange.core.service.IExchangeService;
import com.troy.trade.exchange.service.TroyExchangeFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderTest {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;

    @Test
    public void HUOBIdetail() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();

        create(createOrderReqDto,1);
        createOrderReqDto.setOrderId("53621223529");

        createOrderReqDto.setOrderSide(OrderSideEnum.ASK);
        createOrderReqDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderDetailResDto> detailResDtoRes  = exchange.orderDetail(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));

    }

    @Test
    public void HUOBIList() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();

        create(createOrderReqDto,1);

        createOrderReqDto.setLimit(1000);
        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderListResData> detailResDtoRes  = exchange.orderList(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));

    }

    @Test
    public void BINANCEdetail() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();

        create(createOrderReqDto,4);
        // createOrderReqDto.setOrderId("101914454");
        createOrderReqDto.setOrderId("81773000");
        // 2161891  2162311
       // createOrderReqDto.setOrderSide(TradeExchangeApiConstant.OrderSide.ASK);
       // createOrderReqDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderDetailResDto> detailResDtoRes  = exchange.orderDetail(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));
    }


    @Test
    public void BINANCEList() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();
        create(createOrderReqDto,4);
        createOrderReqDto.setLimit(1000);
        createOrderReqDto.setOrderSide(OrderSideEnum.ASK);
        createOrderReqDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderListResData> detailResDtoRes  = exchange.orderList(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));

    }

    @Test
    public void BINANCgetMyTrades() {
        MyTradeReqDto createOrderReqDto = new MyTradeReqDto();
        createOrderReqDto.setApiKey("Z78ENFcCKageLg2SPBZUncNuWlqVA5I3JjnHM8vhxgxVk7GO70uwRUTTkzY4V9Gj");
        createOrderReqDto.setApiSecret("Wfec0CjQrNWLTBcqLCfkEYpI1YWZIabqkHz65bYp6aAUxKuACbb46hYfsJPGHZU6");
        createOrderReqDto.setExchCode(ExchangeCode.BINANCE);
        createOrderReqDto.setSymbol("TRX/USDT");
        createOrderReqDto.setLimit(1000);

        Req<MyTradeReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());

        System.out.printf("11="+exchange.getMyTrades(createOrderReqDtoReq));

    }

    @Test
    public void GATEIOdetail() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();

        create(createOrderReqDto,3);
        createOrderReqDto.setOrderId("6924405901");
        createOrderReqDto.setOrderSide(OrderSideEnum.ASK);
        createOrderReqDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderDetailResDto> detailResDtoRes  = exchange.orderDetail(createOrderReqDtoReq);
        System.out.println(JSONObject.toJSONString(detailResDtoRes));

    }

    @Test
    public void GATEIOList() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();

        create(createOrderReqDto,3);
        createOrderReqDto.setLimit(1000);
        createOrderReqDto.setOrderSide(OrderSideEnum.ASK);
        createOrderReqDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderListResData> detailResDtoRes  = exchange.orderList(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));

    }
    @Test
    public void GATEIOOpen() {

        OpenOrdersReqDto createOrderReqDto = new OpenOrdersReqDto();
        createOrderReqDto.setApiKey("DB79794C-5D08-4ACA-8636-B4F6FDA0CE50");
        createOrderReqDto.setApiSecret("325fd89dce1dd9896fa8bce34d8f8e17158b3449f2aae0dc8961a555ff120acf");
        createOrderReqDto.setExchCode(ExchangeCode.GATEIO);
        createOrderReqDto.setPassphrase("");
        createOrderReqDto.setSymbol("TRX/ETH");
        createOrderReqDto.setTradeSymbol("TRX/ETH");


        Req<OpenOrdersReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderListResData> detailResDtoRes  = exchange.getOpenOrders(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));

    }

    @Test
    public void OKEXdetail() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();

        create(createOrderReqDto,2);
        createOrderReqDto.setOrderId("3814618324740096");
        createOrderReqDto.setOrderSide(OrderSideEnum.ASK);
        createOrderReqDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderDetailResDto> detailResDtoRes  = exchange.orderDetail(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));
    }
    @Test
    public void OKEXList() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();

        create(createOrderReqDto,2);
        createOrderReqDto.setLimit(1000);
        createOrderReqDto.setOrderId("3679416542175232");
     //   createOrderReqDto.setOrderStatus(7);

        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderListResData> detailResDtoRes  = exchange.orderList(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));

    }

    @Test
    public void OKgetMyTrades() {
        MyTradeReqDto createOrderReqDto = new MyTradeReqDto();
        createOrderReqDto.setApiKey("26ac867a-f8da-4c6b-bcff-a93002c831aa");
        createOrderReqDto.setApiSecret("038E8B90EC5C9AA93E21B0217A3DDEA0");
        createOrderReqDto.setExchCode(ExchangeCode.OKEX);
        createOrderReqDto.setPassphrase("666666");
        createOrderReqDto.setSymbol("ETH/USDT");
        createOrderReqDto.setLimit(1000);
        createOrderReqDto.setOrderId("3814618324740096");

        Req<MyTradeReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());

        System.out.printf(JSONObject.toJSONString(exchange.getMyTrades(createOrderReqDtoReq)));

    }

    @Test
    public void BITFINEXdetail() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();

        create(createOrderReqDto,5);
        createOrderReqDto.setOrderId("30164521335");
        createOrderReqDto.setOrderSide(OrderSideEnum.ASK);
        createOrderReqDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderDetailResDto> detailResDtoRes  = exchange.orderDetail(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));
    }
    @Test
    public void BITFINEXList() {
        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();

        create(createOrderReqDto,5);
        createOrderReqDto.setLimit(1000);
        createOrderReqDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.DEAL);

        Req<OrderDetailReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Res<OrderListResData> detailResDtoRes  = exchange.orderList(createOrderReqDtoReq);
        System.out.printf(JSONObject.toJSONString(detailResDtoRes));

    }

    private OrderDetailReqDto create(OrderDetailReqDto createOrderReqDto,int i) {
        if (i==1){
            createOrderReqDto.setApiKey("ec0fa852-nbtycf4rw2-031e30ff-2def6");
            createOrderReqDto.setApiSecret("70ceb97a-94a06763-a4754e78-8b3d8");
            createOrderReqDto.setExchCode(ExchangeCode.HUOBI);
            createOrderReqDto.setThirdAcctId("4255949");
           // createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("TRX/ETH");
            createOrderReqDto.setTradeSymbol("BTCUSDT");
        }else if (i==2){
            createOrderReqDto.setApiKey("26ac867a-f8da-4c6b-bcff-a93002c831aa");
            createOrderReqDto.setApiSecret("038E8B90EC5C9AA93E21B0217A3DDEA0");
            createOrderReqDto.setExchCode(ExchangeCode.OKEX);
            createOrderReqDto.setPassphrase("666666");
            createOrderReqDto.setSymbol("ETH/USDT");
            createOrderReqDto.setTradeSymbol("TRXUSDT");
        }else if (i==3){
            createOrderReqDto.setApiKey("674187D0-5BF5-4D3B-B560-C701A98B6B4E");
            createOrderReqDto.setApiSecret("kHHFhYSdnN8cLP60yKHA6ondLrwJHyH5f92Cai4OF3BI/YaLqeCKJ4qMuCINi6nE1dKIyFGNLkadPGWGWQcnPsAXmYITg1mjIzbaXKwJMs56p2VoHssKUficGOENxV5ZZBco8QyJB7QhfF8i0laKImcI6FzhIPGERFOL8YY61Is=");
            createOrderReqDto.setExchCode(ExchangeCode.GATEIO);
            createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("MANA/ETH");
            createOrderReqDto.setTradeSymbol("MANA/ETH");
        }else if (i==4){
            createOrderReqDto.setApiKey("Z78ENFcCKageLg2SPBZUncNuWlqVA5I3JjnHM8vhxgxVk7GO70uwRUTTkzY4V9Gj");
            createOrderReqDto.setApiSecret("Wfec0CjQrNWLTBcqLCfkEYpI1YWZIabqkHz65bYp6aAUxKuACbb46hYfsJPGHZU6");
            createOrderReqDto.setExchCode(ExchangeCode.BINANCE);
            createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("TRX/ETH");
            createOrderReqDto.setTradeSymbol("TRXUSDT");
        }else if (i==5){
            createOrderReqDto.setApiKey("hNtvp6EfJXAjZe0qX58c3vwqPEt9EetbSoBn2jgrq4H");
            createOrderReqDto.setApiSecret("OdKpYsyEWyFf8Z523iW3gY1JfrPzJxcCZpehDB55AXQ");
            createOrderReqDto.setExchCode(ExchangeCode.BITFINEX);
            createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("TRX/ETH");
            createOrderReqDto.setTradeSymbol("TRXUSDT");
        }else if (i==6){
            createOrderReqDto.setApiKey("37e387bf-1532d22c-ez2xc4vb6n-de831");
            createOrderReqDto.setApiSecret("0e032dd6-a8b31698-30757359-6c595");
            createOrderReqDto.setExchCode(ExchangeCode.HUOBI);
            createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("TRX/USDT");
            createOrderReqDto.setTradeSymbol("TRXUSDT");
        }

        return createOrderReqDto;
    }
}
