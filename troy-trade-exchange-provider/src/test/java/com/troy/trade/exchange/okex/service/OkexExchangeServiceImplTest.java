package com.troy.trade.exchange.okex.service;

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
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import com.troy.trade.exchange.api.model.dto.in.account.QueryBalanceReqDto;
import com.troy.trade.exchange.api.model.dto.in.account.TransferReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.CoinInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.KlineReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.OrderDetailReqDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.CoinInfoListResDto;
import com.troy.trade.exchange.core.service.IExchangeService;
import com.troy.trade.exchange.service.TroyExchangeFactory;
import com.troy.trade.exchange.web.MarketExchangeController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OkexExchangeServiceImplTest {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;

    @Autowired
    MarketExchangeController marketExchangeController;

    @Test
    public void getOrderBook() {
        OrderBookReqDto orderBookReqDto = new OrderBookReqDto();
        orderBookReqDto.setLimit(10);
        orderBookReqDto.setExchCode(ExchangeCode.OKEX);
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
    public void getTrades() {
        TradeHistoryReqDto tradeHistoryReqDto = new TradeHistoryReqDto();
        tradeHistoryReqDto.setExchCode(ExchangeCode.OKEX);
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
        symbolInfoReqDto.setExchCode(ExchangeCode.OKEX);
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
    public void getCoinInfo() {
//        TradeHistoryReqDto tradeHistoryReqDto = new TradeHistoryReqDto();
//        tradeHistoryReqDto.setExchCode(ExchangeCode.OKEX);
//        tradeHistoryReqDto.setSymbol("BTC/USDT");
//        Req<TradeHistoryReqDto> orderBookReqDtoReq = ReqFactory.getInstance().createReq(tradeHistoryReqDto);

        CoinInfoReqDto coinInfoReqDto = new CoinInfoReqDto();
        coinInfoReqDto.setExchCode(ExchangeCode.OKEX);
        coinInfoReqDto.setApiKey("26ac867a-f8da-4c6b-bcff-a93002c831aa");
        coinInfoReqDto.setApiSecret("038E8B90EC5C9AA93E21B0217A3DDEA0");
        coinInfoReqDto.setPassphrase("666666");
        Req<CoinInfoReqDto> coinInfoReqDtoReq = ReqFactory.getInstance().createReq(coinInfoReqDto);

        Res<CoinInfoListResDto>  resDtoRes = marketExchangeController.getCoinInfo(coinInfoReqDtoReq);
//        IExchangeService exchange = troyExchangeFactory.getExchangeService(coinInfoReqDto.getExchCode());
//        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            System.out.println(JSONObject.toJSONString(resDtoRes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void fullTickers() {

        FullTickerReqDto fullTickerReqDto = new FullTickerReqDto();
        fullTickerReqDto.setExchCode(ExchangeCode.OKEX);
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
        tickerPriceReqDto.setExchCode(ExchangeCode.OKEX);
        tickerPriceReqDto.setSymbol("BTC/USDT");
        tickerPriceReqDto.setTradeSymbol("BTC-USDT");
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
    public void orderDetail() {

        OrderDetailReqDto tickerPriceReqDto = new OrderDetailReqDto();
        tickerPriceReqDto.setOrderId("3666296095057920");
        tickerPriceReqDto.setExchCode(ExchangeCode.OKEX);
        tickerPriceReqDto.setSymbol("TRIO/USDT");
        tickerPriceReqDto.setTradeSymbol("TRIO/USDT");
        Req<OrderDetailReqDto> tickerPriceReqDtoReq = ReqFactory.getInstance().createReq(tickerPriceReqDto);
        IExchangeService exchange = troyExchangeFactory.getExchangeService(tickerPriceReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(exchange.orderDetail(tickerPriceReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void transfer() {

        TransferReqDto transferReqDto = new TransferReqDto();
        transferReqDto.setCoinName("EOS");
        transferReqDto.setAmount(new BigDecimal("0.1"));
        transferReqDto.setFrom("6");
        transferReqDto.setTo("1");
        transferReqDto.setApiKey("26ac867a-f8da-4c6b-bcff-a93002c831aa");
        transferReqDto.setApiSecret("038E8B90EC5C9AA93E21B0217A3DDEA0");
        transferReqDto.setPassphrase("666666");
        transferReqDto.setExchangeCode(ExchangeCode.OKEX);

        Req<TransferReqDto> transferReqDtoReq = ReqFactory.getInstance().createReq(transferReqDto);
        IExchangeService exchange = troyExchangeFactory.getExchangeService(transferReqDtoReq.getData().getExchangeCode());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(exchange.transfer(transferReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void orderListByPagee() {

        OrderDetailReqDto createOrderReqDto = new OrderDetailReqDto();
        createOrderReqDto.setApiKey("26ac867a-f8da-4c6b-bcff-a93002c831aa");
        createOrderReqDto.setApiSecret("038E8B90EC5C9AA93E21B0217A3DDEA0");
        createOrderReqDto.setPassphrase("666666");
        createOrderReqDto.setExchCode(ExchangeCode.OKEX);
        createOrderReqDto.setThirdAcctId("4255949");
        createOrderReqDto.setLimit(40);
        createOrderReqDto.setStartCondition("3819617916488704");
        // createOrderReqDto.setPassphrase("");
        createOrderReqDto.setSymbol("TRX/ETH");
        createOrderReqDto.setOrderStatus(TradeExchangeApiConstant.OrderStatus.DEAL);

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
    public void wallet() {

        QueryBalanceReqDto tickerPriceReqDto = new QueryBalanceReqDto();

        tickerPriceReqDto.setExchCode(ExchangeCode.OKEX_CAPITAL);
        tickerPriceReqDto.setApiKey("26ac867a-f8da-4c6b-bcff-a93002c831aa");
        tickerPriceReqDto.setApiSecret("038E8B90EC5C9AA93E21B0217A3DDEA0");
        tickerPriceReqDto.setPassphrase("666666");
        Req<QueryBalanceReqDto> tickerPriceReqDtoReq = ReqFactory.getInstance().createReq(tickerPriceReqDto);
        IExchangeService exchange = troyExchangeFactory.getExchangeService(tickerPriceReqDto.getExchCode());

        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(exchange.wallet(tickerPriceReqDtoReq)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void kline() {

        KlineReqDto tickerPriceReqDto = new KlineReqDto();

        tickerPriceReqDto.setExchCode(ExchangeCode.OKEX);
        tickerPriceReqDto.setSymbol("BTC/USDT");
        tickerPriceReqDto.setPeriod("1");
        tickerPriceReqDto.setStartDate(DateUtils.getDate(DateUtils.FORMAT_DATE_TIME_ISO8601));
       // tickerPriceReqDto.setEndDate(DateUtils.getDate(DateUtils.FORMAT_DATE_TIME_ISO8601));
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
