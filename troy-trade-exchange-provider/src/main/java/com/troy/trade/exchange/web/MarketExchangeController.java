package com.troy.trade.exchange.web;

import cn.hutool.core.lang.Assert;
import com.troy.commons.BaseController;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResList;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.FullTickerReqDto;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.PublicMarketReqData;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.commons.exchange.model.out.FullTickerListResDto;
import com.troy.commons.exchange.model.out.OrderBookResDto;
import com.troy.commons.exchange.model.out.TradeHistoryListResDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.CoinInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.KlineReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.CoinInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.SymbolInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.market.KLineResDto;
import com.troy.trade.exchange.api.model.dto.out.market.TickerPriceResDto;
import com.troy.trade.exchange.api.service.MarketExchangeApi;
import com.troy.trade.exchange.core.service.IExchangeService;
import com.troy.trade.exchange.service.TroyExchangeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易所服务
 *
 * @author yanping
 */
@Slf4j
@RestController
public class MarketExchangeController extends BaseController implements MarketExchangeApi {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;

    @Override
    public Res<OrderBookResDto> getOrderBook(@RequestBody Req<OrderBookReqDto> orderBookReqDtoReq) {
        return getExchangService(orderBookReqDtoReq.getData()).getOrderBook(orderBookReqDtoReq);
    }

    @Override
    public Res<TradeHistoryListResDto> getTrades(@RequestBody Req<TradeHistoryReqDto> tradeHistoryReqDtoReq) {
        return getExchangService(tradeHistoryReqDtoReq.getData()).getTrades(tradeHistoryReqDtoReq);
    }

    @Override
    public Res<SymbolInfoListResDto> getSymbolInfo(@RequestBody Req<SymbolInfoReqDto> symbolInfoReqDtoReq) {
        return getExchangService(symbolInfoReqDtoReq.getData()).getSymbolInfo(symbolInfoReqDtoReq);
    }

    @Override
    public Res<CoinInfoListResDto> getCoinInfo(@RequestBody Req<CoinInfoReqDto> coinInfoReqDtoReq) {
        return getExchangService(coinInfoReqDtoReq.getData()).getCoinInfo(coinInfoReqDtoReq);
    }

    @Override
    public Res<FullTickerListResDto> getAllTickers(@RequestBody Req<FullTickerReqDto> fullTickerReqDtoReq) {
        log.error("调用MarketExchangeController.getAllTickers入参：{}",fullTickerReqDtoReq);
        return getExchangService(fullTickerReqDtoReq.getData()).fullTickers(fullTickerReqDtoReq);
    }

    @Override
    public Res<TickerPriceResDto> tickerPrice(@RequestBody Req<TickerPriceReqDto> tickerPriceReqDtoReq) {
        return getExchangService(tickerPriceReqDtoReq.getData()).tickerPrice(tickerPriceReqDtoReq);
    }

    @Override
    public  Res<ResList<KLineResDto>> kline(@RequestBody Req<KlineReqDto> klineReqDto) throws Throwable{
        return getExchangService(klineReqDto.getData()).kline(klineReqDto);
    }

    /**
     * 获取交易所服务对象
     *
     * @param publicMarketReqData
     * @return
     */
    private IExchangeService getExchangService(PublicMarketReqData publicMarketReqData) {
        ExchangeCode exchCode = publicMarketReqData.getExchCode();
        Assert.notNull(exchCode, "交易所Code不能为空");

        IExchangeService exchange = troyExchangeFactory.getExchangeService(exchCode);
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        if(StringUtils.isNotBlank(publicMarketReqData.getSymbol())
                && publicMarketReqData.getSymbol().contains("/")){
            publicMarketReqData.setTradeSymbol(exchange.toTradeSymbol(publicMarketReqData.getSymbol()));
        }
        return exchange;
    }
}
