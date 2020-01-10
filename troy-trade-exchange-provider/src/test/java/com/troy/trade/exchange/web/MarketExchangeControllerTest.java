package com.troy.trade.exchange.web;

import cn.hutool.core.lang.Assert;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.PublicMarketReqData;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.commons.exchange.model.out.OrderBookResDto;
import com.troy.commons.exchange.model.out.TradeHistoryListResDto;
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
public class MarketExchangeControllerTest {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;

    @Test
    public void getOrderBook() {
        OrderBookReqDto orderBookReqDto = new OrderBookReqDto();
        orderBookReqDto.setSymbol("BTC/USDT");
        orderBookReqDto.setExchCode(ExchangeCode.OKEX);
        orderBookReqDto.setLimit(50);
        Req<OrderBookReqDto> orderBookReqDtoReq = ReqFactory.getInstance().createReq(orderBookReqDto);
        Res<OrderBookResDto> orderBookResDtoRes = getExchangService(orderBookReqDtoReq.getData()).getOrderBook(orderBookReqDtoReq);
        System.out.println(orderBookResDtoRes);
    }

    @Test
    public void getTrades() {
        TradeHistoryReqDto tradeHistoryReqDto = new TradeHistoryReqDto();
        tradeHistoryReqDto.setSymbol("BTC/USDT");
        tradeHistoryReqDto.setExchCode(ExchangeCode.OKEX);
        Req<TradeHistoryReqDto> tradeHistoryReqDtoReq = ReqFactory.getInstance().createReq(tradeHistoryReqDto);
        Res<TradeHistoryListResDto> tradeHistoryListResDtoRes = getExchangService(tradeHistoryReqDtoReq.getData()).getTrades(tradeHistoryReqDtoReq);
        System.out.println(tradeHistoryListResDtoRes);
    }


    /**
     * 获取交易所服务对象
     * @param publicMarketReqData
     * @return
     */
    public IExchangeService getExchangService(PublicMarketReqData publicMarketReqData){
        ExchangeCode exchCode = publicMarketReqData.getExchCode();
        Assert.notNull(exchCode, "交易所Code不能为空");

        IExchangeService exchange = troyExchangeFactory.getExchangeService(exchCode);
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        return exchange;
    }
}