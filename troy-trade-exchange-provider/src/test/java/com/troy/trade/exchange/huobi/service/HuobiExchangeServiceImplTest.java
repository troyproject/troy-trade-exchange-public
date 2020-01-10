package com.troy.trade.exchange.huobi.service;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.FullTickerReqDto;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.commons.exchange.model.out.OrderBookResDto;
import com.troy.commons.utils.DateUtils;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.CoinInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.KlineReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.MyTradeReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.OrderDetailReqDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.CoinInfoListResDto;
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
public class HuobiExchangeServiceImplTest {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;

    @Test
    public void getOrderBook() {
        OrderBookReqDto orderBookReqDto = new OrderBookReqDto();
        orderBookReqDto.setLimit(10);
        orderBookReqDto.setExchCode(ExchangeCode.HUOBI);
        orderBookReqDto.setSymbol("BTC/USDT");
        Req<OrderBookReqDto> orderBookReqDtoReq = ReqFactory.getInstance().createReq(orderBookReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(orderBookReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            Res<OrderBookResDto> resDtoRes = exchange.getOrderBook(orderBookReqDtoReq);
            System.out.println(resDtoRes.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCoinInfo() {
        CoinInfoReqDto coinInfoReqDto = new CoinInfoReqDto();
        coinInfoReqDto.setExchCode(ExchangeCode.HUOBI);
        Req<CoinInfoReqDto> coinInfoReqDtoReq = ReqFactory.getInstance().createReq(coinInfoReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(coinInfoReqDto.getExchCode());
        Res<CoinInfoListResDto>  resDtoRes = exchange.getCoinInfo(coinInfoReqDtoReq);
        try {
            System.out.println(JSONObject.toJSONString(resDtoRes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTrades() {
        TradeHistoryReqDto tradeHistoryReqDto = new TradeHistoryReqDto();
        tradeHistoryReqDto.setExchCode(ExchangeCode.HUOBI);
        tradeHistoryReqDto.setSymbol("BTC/USDT");
        Req<TradeHistoryReqDto> orderBookReqDtoReq = ReqFactory.getInstance().createReq(tradeHistoryReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(tradeHistoryReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            System.out.println(JSONObject.toJSONString(exchange.getTrades(orderBookReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSymbolInfo() {
        SymbolInfoReqDto symbolInfoReqDto = new SymbolInfoReqDto();
        symbolInfoReqDto.setExchCode(ExchangeCode.HUOBI);
        Req<SymbolInfoReqDto> orderBookReqDtoReq = ReqFactory.getInstance().createReq(symbolInfoReqDto);
        IExchangeService exchange = troyExchangeFactory.getExchangeService(symbolInfoReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(exchange.getSymbolInfo(orderBookReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fullTickers() {

        FullTickerReqDto fullTickerReqDto = new FullTickerReqDto();
        fullTickerReqDto.setExchCode(ExchangeCode.HUOBI);
        Req<FullTickerReqDto> fullTickerReqDtoReq = ReqFactory.getInstance().createReq(fullTickerReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(fullTickerReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(exchange.fullTickers(fullTickerReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tickerPrice() {

        TickerPriceReqDto tickerPriceReqDto = new TickerPriceReqDto();
        tickerPriceReqDto.setExchCode(ExchangeCode.HUOBI);
        tickerPriceReqDto.setSymbol("BTC/USDT");
        Req<TickerPriceReqDto> tickerPriceReqDtoReq = ReqFactory.getInstance().createReq(tickerPriceReqDto);
        IExchangeService exchange = troyExchangeFactory.getExchangeService(tickerPriceReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(exchange.tickerPrice(tickerPriceReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void orderList() {

        TickerPriceReqDto tickerPriceReqDto = new TickerPriceReqDto();
        tickerPriceReqDto.setExchCode(ExchangeCode.HUOBI);
        tickerPriceReqDto.setSymbol("BTC/USDT");
        Req<TickerPriceReqDto> tickerPriceReqDtoReq = ReqFactory.getInstance().createReq(tickerPriceReqDto);
        IExchangeService exchange = troyExchangeFactory.getExchangeService(tickerPriceReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(exchange.tickerPrice(tickerPriceReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void BINANCgetMyTrades() {
        MyTradeReqDto createOrderReqDto = new MyTradeReqDto();
        createOrderReqDto.setApiKey("ec0fa852-nbtycf4rw2-031e30ff-2def6");
        createOrderReqDto.setApiSecret("70ceb97a-94a06763-a4754e78-8b3d8");
        createOrderReqDto.setExchCode(ExchangeCode.HUOBI);
        createOrderReqDto.setThirdAcctId("4255949");
        createOrderReqDto.setOrderId("52051390046");
        createOrderReqDto.setSymbol("TRX/ETH");
        createOrderReqDto.setLimit(1000);

        Req<MyTradeReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());

        System.out.printf(JSONObject.toJSONString(exchange.getMyTrades(createOrderReqDtoReq)));

    }

    @Test
    public void orderListByPagee() {

        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();
        createOrderReqDto.setApiKey("ec0fa852-nbtycf4rw2-031e30ff-2def6");
        createOrderReqDto.setApiSecret("70ceb97a-94a06763-a4754e78-8b3d8");
        createOrderReqDto.setExchCode(ExchangeCode.HUOBI);
        createOrderReqDto.setThirdAcctId("4255949");
        createOrderReqDto.setLimit(2);
        createOrderReqDto.setEndCondition("55683748444");
        // createOrderReqDto.setPassphrase("");
        createOrderReqDto.setSymbol("TRX/ETH");

        Req<OrderDetailReqDto> tickerPriceReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);
        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(exchange.orderListByPage(tickerPriceReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void kline() {

        KlineReqDto tickerPriceReqDto = new KlineReqDto();

        tickerPriceReqDto.setExchCode(ExchangeCode.HUOBI);
        tickerPriceReqDto.setSymbol("BTC/USDT");
        tickerPriceReqDto.setPeriod("1");
        tickerPriceReqDto.setStartDate(DateUtils.getDate(DateUtils.FORMAT_DATE_TIME_ISO8601));

        Req<KlineReqDto> tickerPriceReqDtoReq = ReqFactory.getInstance().createReq(tickerPriceReqDto);
        IExchangeService exchange = troyExchangeFactory.getExchangeService(tickerPriceReqDto.getExchCode());

        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(exchange.kline(tickerPriceReqDtoReq)));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
