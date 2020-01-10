package com.troy.trade.exchange.binance.service;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.FullTickerReqDto;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.commons.utils.ApplicationContextUtil;
import com.troy.trade.exchange.api.model.dto.in.account.HarkWithdrawalReqDto;
import com.troy.trade.exchange.api.model.dto.in.data.AbnormalChangesReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.MyTradeReqDto;
import com.troy.trade.exchange.core.constant.ExchangeConstant;
import com.troy.trade.exchange.core.service.IBinanceExchangeService;
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
public class BinanceExchangeServiceImplTest {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;

    @Test
    public void getOrderBook() {
        OrderBookReqDto orderBookReqDto = new OrderBookReqDto();
        orderBookReqDto.setLimit(10);
        orderBookReqDto.setExchCode(ExchangeCode.BINANCE);
        orderBookReqDto.setSymbol("BTC/USDT");
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
        tradeHistoryReqDto.setExchCode(ExchangeCode.BINANCE);
        tradeHistoryReqDto.setSymbol("TRX/USDT");
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
    public void getMyTrades() {


        MyTradeReqDto orderDetailReqDto = new MyTradeReqDto();
        orderDetailReqDto.setExchCode(ExchangeCode.BINANCE);
        orderDetailReqDto.setSymbol("TRX/ETH");
        orderDetailReqDto.setOrderId("106277206");
        Req<MyTradeReqDto> orderDetailReqListDtoReq = ReqFactory.getInstance().createReq(orderDetailReqDto);
        IExchangeService exchange = troyExchangeFactory.getExchangeService(orderDetailReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            System.out.println(JSONObject.toJSONString(exchange.getMyTrades(orderDetailReqListDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void getSymbolInfo() {

        SymbolInfoReqDto symbolInfoReqDto = new SymbolInfoReqDto();
        symbolInfoReqDto.setExchCode(ExchangeCode.BINANCE);
        Req<SymbolInfoReqDto> orderBookReqDtoReq = ReqFactory.getInstance().createReq(symbolInfoReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(symbolInfoReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            System.out.println(JSONObject.toJSONString(exchange.getSymbolInfo(orderBookReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void fullTickers() {

        FullTickerReqDto fullTickerReqDto = new FullTickerReqDto();
        fullTickerReqDto.setExchCode(ExchangeCode.BINANCE);
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
        tickerPriceReqDto.setExchCode(ExchangeCode.BINANCE);
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
    public void abnormalChanges() {
        AbnormalChangesReqDto abnormalChangesReqDto = new AbnormalChangesReqDto();
        abnormalChangesReqDto.setExchangeCode(ExchangeCode.BINANCE);
        Req<AbnormalChangesReqDto> abnormalChangesReqDtoReq = ReqFactory.getInstance().createReq(abnormalChangesReqDto);
        IBinanceExchangeService binanceExchangeService = ApplicationContextUtil.getBean(IBinanceExchangeService.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(
                    objectMapper.writeValueAsString(
                            binanceExchangeService.abnormalChanges(abnormalChangesReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void harkWithdrawal() {
        String apiKey = "XXXX";
        String apiSecret = "XXXXX";

        HarkWithdrawalReqDto harkWithdrawalReqDto = new HarkWithdrawalReqDto();
        harkWithdrawalReqDto.setExchangeCode(ExchangeCode.BINANCE);
        harkWithdrawalReqDto.setApiKey(apiKey);
        harkWithdrawalReqDto.setApiSecret(apiSecret);
        harkWithdrawalReqDto.setCoinName("TRX");
        harkWithdrawalReqDto.setStartTime("1571389800000");
        harkWithdrawalReqDto.setType(ExchangeConstant.DEPOSIT_WITHDRAWAL_TYPE_WITHDRAWAL);
        Req<HarkWithdrawalReqDto> harkWithdrawalReqDtoReq = ReqFactory.getInstance().createReq(harkWithdrawalReqDto);
        IBinanceExchangeService binanceExchangeService = ApplicationContextUtil.getBean(IBinanceExchangeService.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(
                    objectMapper.writeValueAsString(
                            binanceExchangeService.harkWithdrawal(harkWithdrawalReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
