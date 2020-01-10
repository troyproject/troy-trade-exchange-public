package com.troy.trade.exchange.okex.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResFactory;
import com.troy.commons.dto.out.ResList;
import com.troy.commons.exception.business.CallExchangeRemoteException;
import com.troy.commons.exception.enums.StateTypeSuper;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.enums.TradeExchangeErrorCode;
import com.troy.commons.exchange.model.in.FullTickerReqDto;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.commons.exchange.model.out.FullTickerListResDto;
import com.troy.commons.exchange.model.out.OrderBookResDto;
import com.troy.commons.exchange.model.out.TradeHistoryListResDto;
import com.troy.trade.exchange.api.model.dto.in.account.HarkWithdrawalReqDto;
import com.troy.trade.exchange.api.model.dto.in.account.QueryBalanceReqDto;
import com.troy.trade.exchange.api.model.dto.in.account.WithdrawalReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.CoinInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.TickerPriceReqDto;
import com.troy.trade.exchange.api.model.dto.in.order.*;
import com.troy.trade.exchange.api.model.dto.out.account.ExchAcctDeptWdralResDto;
import com.troy.trade.exchange.api.model.dto.out.account.QueryBalanceResDto;
import com.troy.trade.exchange.api.model.dto.out.account.WithdrawalResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.CoinInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.SymbolInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.market.MyTradeListResDto;
import com.troy.trade.exchange.api.model.dto.out.market.TickerPriceResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CancelOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CreateOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderDetailResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderListResData;
import com.troy.trade.exchange.api.util.SymbolUtil;
import com.troy.trade.exchange.core.service.IExchangeService;
import com.troy.trade.exchange.okex.client.stock.IOkexStockRestApi;
import com.troy.trade.exchange.okex.client.stock.impl.OkexStockRestApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OkexCapitalExchangeServiceImpl implements IExchangeService {

    @Override
    public ExchangeCode getExchCode() {
        return ExchangeCode.OKEX_CAPITAL;
    }

    @Override
    public String toTradeSymbol(String symbol) {
        return SymbolUtil.ToTradeSymbol.underlineLowerCaseSymbol(symbol);
    }

    @Override
    public String symbol(String tradeSymbol) {
        if (StringUtils.isBlank(tradeSymbol)) {
            return null;
        }
        return tradeSymbol.replace("-", "/").toUpperCase();
    }

    @Override
    public Res<ResList<QueryBalanceResDto>> getBalance(QueryBalanceReqDto queryBalanceReqDto) {
        log.warn("调用okex查询资金账户信息，入参：{}",queryBalanceReqDto);
        List<QueryBalanceResDto> resDtoList = new ArrayList<>();
        try {
            IOkexStockRestApi restApi = new OkexStockRestApi(queryBalanceReqDto.getApiKey(), queryBalanceReqDto.getApiSecret(), queryBalanceReqDto.getPassphrase());
            //查询所有币种列表信息
            String result = restApi.wallet();
            if(StringUtils.isNotBlank(result)){
                JSONArray jsonArray = JSONArray.parseArray(result );
                if(jsonArray.size()>0) {
                    for(int j=0;j<jsonArray.size();j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);

                        String currency = jsonObject.getString("currency");
                        BigDecimal available = jsonObject.getBigDecimal("available");
                        BigDecimal balance = jsonObject.getBigDecimal("balance");
                        BigDecimal hold = jsonObject.getBigDecimal("hold");

                        QueryBalanceResDto walletResDto = new QueryBalanceResDto();
                        walletResDto.setUsable(available);
                        walletResDto.setCurrency(currency);
                        walletResDto.setFrozen(hold);
                        resDtoList.add(walletResDto);
                    }
                }
            }
        }catch (Throwable throwable) {
            String temp = "查找 okex资金账户信息 记录异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
        return ResFactory.getInstance().successList(resDtoList);
    }

    @Override
    public Res<ResList<QueryBalanceResDto>> wallet(Req<QueryBalanceReqDto> walletReqDtoReq) {
        List<QueryBalanceResDto> resDtoList = new ArrayList<>();
        try {
            QueryBalanceReqDto coinInfoReqDto = walletReqDtoReq.getData();
            IOkexStockRestApi restApi = new OkexStockRestApi(coinInfoReqDto.getApiKey(), coinInfoReqDto.getApiSecret(), coinInfoReqDto.getPassphrase());
            //查询所有币种列表信息
            String result = restApi.wallet();
            if(StringUtils.isNotBlank(result)){
                JSONArray jsonArray = JSONArray.parseArray(result );
                if(jsonArray.size()>0) {
                    for(int j=0;j<jsonArray.size();j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);

                        String currency = jsonObject.getString("currency");
                        BigDecimal available = jsonObject.getBigDecimal("available");
                        BigDecimal balance = jsonObject.getBigDecimal("balance");
                        BigDecimal hold = jsonObject.getBigDecimal("hold");

                        QueryBalanceResDto walletResDto = new QueryBalanceResDto();
                        walletResDto.setUsable(available);
                        walletResDto.setCurrency(currency);
                        walletResDto.setFrozen(hold);
                        resDtoList.add(walletResDto);
                    }
                }
            }else{
                String temp = "远程调用OKEX资金账户信息失败，第三方返回："+result;
                log.error(temp);
                throw new CallExchangeRemoteException(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM, temp);
            }
        }catch (Throwable throwable) {
            String temp = "查找 okex资金账户信息 记录异常,异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, "");
        }
        return ResFactory.getInstance().successList(resDtoList);
    }

    @Override
    public Res<CreateOrderResDto> createOrder(Req<CreateOrderReqDto> createOrderReqDtoReq) {
        return null;
    }

    @Override
    public Res<CancelOrderResDto> cancelOrder(Req<CancelOrderReqDto> cancelOrderReqDtoReq) {
        return null;
    }

    @Override
    public Res<OrderDetailResDto> orderDetail(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        return null;
    }

    @Override
    public Res<OrderListResData> orderList(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        return null;
    }

    @Override
    public Res<TradeHistoryListResDto> getTrades(Req<TradeHistoryReqDto> tradeHistoryReqDtoReq) {
        return null;
    }

    @Override
    public Res<MyTradeListResDto> getMyTrades(Req<MyTradeReqDto> myTradeReqDtoReq) {
        return null;
    }

    @Override
    public Res<OrderListResData> getOpenOrders(Req<OpenOrdersReqDto> ordersReqDtoReq) {
        return null;
    }

    @Override
    public Res<ResList<ExchAcctDeptWdralResDto>> harkWithdrawal(Req<HarkWithdrawalReqDto> harkWithdrawalReqDtoReq) {
        return null;
    }

    @Override
    public Res<WithdrawalResDto> withdraw(Req<WithdrawalReqDto> withdrawalReqDtoReq) {
        return null;
    }

    @Override
    public Res<OrderBookResDto> getOrderBook(Req<OrderBookReqDto> orderBookReqDtoReq) {
        return null;
    }

    @Override
    public Res<SymbolInfoListResDto> getSymbolInfo(Req<SymbolInfoReqDto> symbolInfoReqDtoReq) {
        return null;
    }

    @Override
    public Res<CoinInfoListResDto> getCoinInfo(Req<CoinInfoReqDto> coinInfoReqDtoReq) {
        return null;
    }

    @Override
    public Res<FullTickerListResDto> fullTickers(Req<FullTickerReqDto> fullTickerReqDtoReq) {
        return null;
    }

    @Override
    public Res<TickerPriceResDto> tickerPrice(Req<TickerPriceReqDto> tickerPriceReqDtoReq) {
        return null;
    }


}
