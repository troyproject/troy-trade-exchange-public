package com.troy.trade.exchange.bitfinex.service;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.FullTickerReqDto;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
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
public class BitfinexExchangeServiceImplTest {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;

    @Test
    public void getOrderBook() {
        OrderBookReqDto orderBookReqDto = new OrderBookReqDto();
        orderBookReqDto.setLimit(10);
        orderBookReqDto.setExchCode(ExchangeCode.BITFINEX);
        orderBookReqDto.setSymbol("BTC/USD");
        Req<OrderBookReqDto> orderBookReqDtoReq = ReqFactory.getInstance().createReq(orderBookReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(orderBookReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            System.out.println(exchange.getOrderBook(orderBookReqDtoReq).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getTrades() {
        TradeHistoryReqDto tradeHistoryReqDto = new TradeHistoryReqDto();
        tradeHistoryReqDto.setExchCode(ExchangeCode.BITFINEX);
        tradeHistoryReqDto.setSymbol("BTC/USD");
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
        symbolInfoReqDto.setExchCode(ExchangeCode.BITFINEX);
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
        fullTickerReqDto.setExchCode(ExchangeCode.BITFINEX);
        fullTickerReqDto.setSymbol("ALL");
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
        tickerPriceReqDto.setExchCode(ExchangeCode.BITFINEX);
        tickerPriceReqDto.setSymbol("BTC/USD");
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
}