package com.troy.trade.exchange.api.service;

import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResList;
import com.troy.commons.exchange.model.in.FullTickerReqDto;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
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
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 交易所调用服务
 *
 * @author dp
 */
@FeignClient(value = "XXXX-XXXX-XXXXX")
public interface MarketExchangeApi {

    /**
     * 根据三方订单号查询订单详情
     *
     * @param orderBookReqDtoReq
     * @return
     */
    @PostMapping("/XXXX/XXXX/a")
    Res<OrderBookResDto> getOrderBook(@RequestBody Req<OrderBookReqDto> orderBookReqDtoReq);

    /**
     * 根据条件查询订单列表
     *
     * @param tradeHistoryReqDtoReq
     * @return
     */
    @PostMapping("/XXXX/XXXX/b")
    Res<TradeHistoryListResDto> getTrades(@RequestBody Req<TradeHistoryReqDto> tradeHistoryReqDtoReq);

    /**
     * 查找币种信息
     * @param symbolInfoReqDtoReq
     * @return
     */
    @PostMapping("/XXXX/XXXX/c")
    Res<SymbolInfoListResDto> getSymbolInfo(@RequestBody Req<SymbolInfoReqDto> symbolInfoReqDtoReq);

    /**
     * 查找币种信息
     * @param coinInfoReqDtoReq
     * @return
     */
    @PostMapping("/XXXX/XXXX/d")
    Res<CoinInfoListResDto> getCoinInfo(@RequestBody Req<CoinInfoReqDto> coinInfoReqDtoReq);

    /**
     * 查找币种信息
     * @param fullTickerReqDtoReq
     * @return
     */
    @PostMapping("/XXXX/XXXX/e")
    Res<FullTickerListResDto> getAllTickers(@RequestBody Req<FullTickerReqDto> fullTickerReqDtoReq);

    /**
     * 查找币对价格信息
     * @param tickerPriceReqDtoReq
     * @return
     */
    @PostMapping("/XXXX/XXXX/f")
    Res<TickerPriceResDto> tickerPrice(@RequestBody Req<TickerPriceReqDto> tickerPriceReqDtoReq);

}
