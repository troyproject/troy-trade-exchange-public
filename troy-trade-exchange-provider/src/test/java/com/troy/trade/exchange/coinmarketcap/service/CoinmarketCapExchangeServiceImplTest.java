package com.troy.trade.exchange.coinmarketcap.service;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.exchange.model.constant.ExchangeCode;
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
public class CoinmarketCapExchangeServiceImplTest {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;

    @Test
    public void tickerPrice() {
        TickerPriceReqDto tickerPriceReqDto = new TickerPriceReqDto();
        tickerPriceReqDto.setExchCode(ExchangeCode.COINMARKETCAP);
        tickerPriceReqDto.setSymbol("USDT/CNY");
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