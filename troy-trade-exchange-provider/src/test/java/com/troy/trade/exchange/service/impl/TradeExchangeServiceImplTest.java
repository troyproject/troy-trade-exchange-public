package com.troy.trade.exchange.service.impl;

import cn.hutool.core.lang.Assert;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.OrderSideEnum;
import com.troy.trade.exchange.api.model.constant.TradeExchangeApiConstant;
import com.troy.trade.exchange.api.model.dto.in.order.CreateOrderReqDto;
import com.troy.trade.exchange.core.service.IExchangeService;
import com.troy.trade.exchange.service.TroyExchangeFactory;
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
public class TradeExchangeServiceImplTest {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;

    @Test
    public void createOrder() {
        CreateOrderReqDto createOrderReqDto = new CreateOrderReqDto();
//        createOrderReqDto.setApiKey("Z78ENFcCKageLg2SPBZUncNuWlqVA5I3JjnHM8vhxgxVk7GO70uwRUTTkzY4V9Gj");
//        createOrderReqDto.setApiSecret("Wfec0CjQrNWLTBcqLCfkEYpI1YWZIabqkHz65bYp6aAUxKuACbb46hYfsJPGHZU6");
//        createOrderReqDto.setExchCode(ExchangeCode.BINANCE);
        create(createOrderReqDto,1);

        createOrderReqDto.setAmount(new BigDecimal(10));
        createOrderReqDto.setPrice(new BigDecimal("0.018298"));
        createOrderReqDto.setOrderSide(OrderSideEnum.ASK);
        createOrderReqDto.setOrderType(TradeExchangeApiConstant.OrderType.LIMIT);
        Req<CreateOrderReqDto> createOrderReqDtoReq = ReqFactory.getInstance().createReq(createOrderReqDto);

        IExchangeService exchange = troyExchangeFactory.getExchangeService(createOrderReqDto.getExchCode());
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        try {
            System.out.println(exchange.createOrder(createOrderReqDtoReq).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CreateOrderReqDto create(CreateOrderReqDto createOrderReqDto,int i) {
        if (i==1){
            createOrderReqDto.setApiKey("37e387bf-1532d22c-ez2xc4vb6n-de831");
            createOrderReqDto.setApiSecret("0e032dd6-a8b31698-30757359-6c595");
            createOrderReqDto.setExchCode(ExchangeCode.HUOBI);
            createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("BTC/USDT");
            createOrderReqDto.setTradeSymbol("BTCUSDT");
        }else if (i==2){
            createOrderReqDto.setApiKey("26ac867a-f8da-4c6b-bcff-a93002c831aa");
            createOrderReqDto.setApiSecret("038E8B90EC5C9AA93E21B0217A3DDEA0");
            createOrderReqDto.setExchCode(ExchangeCode.OKEX);
            createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("TRX/USDT");
            createOrderReqDto.setTradeSymbol("TRXUSDT");
        }else if (i==3){
            createOrderReqDto.setApiKey("01E105DC-FE79-4DB5-A2A4-2FF524498E2F");
            createOrderReqDto.setApiSecret("4e160ae668893f0bc16c130b621e277ba1dd12c6e20a45b43b57059feb1c4790");
            createOrderReqDto.setExchCode(ExchangeCode.GATEIO);
            createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("SKM/ETH");
            createOrderReqDto.setTradeSymbol("SKM/ETH");
        }else if (i==4){
            createOrderReqDto.setApiKey("Z78ENFcCKageLg2SPBZUncNuWlqVA5I3JjnHM8vhxgxVk7GO70uwRUTTkzY4V9Gj");
            createOrderReqDto.setApiSecret("Wfec0CjQrNWLTBcqLCfkEYpI1YWZIabqkHz65bYp6aAUxKuACbb46hYfsJPGHZU6");
            createOrderReqDto.setExchCode(ExchangeCode.BINANCE);
            createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("TRX/USDT");
            createOrderReqDto.setTradeSymbol("TRXUSDT");
        }else if (i==5){
            createOrderReqDto.setApiKey("hNtvp6EfJXAjZe0qX58c3vwqPEt9EetbSoBn2jgrq4H");
            createOrderReqDto.setApiSecret("OdKpYsyEWyFf8Z523iW3gY1JfrPzJxcCZpehDB55AXQ");
            createOrderReqDto.setExchCode(ExchangeCode.BITFINEX);
            createOrderReqDto.setPassphrase("");
            createOrderReqDto.setSymbol("TRX/USDT");
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