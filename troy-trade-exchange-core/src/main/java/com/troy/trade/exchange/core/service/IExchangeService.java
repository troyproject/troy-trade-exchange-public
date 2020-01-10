package com.troy.trade.exchange.core.service;

import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResList;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.FullTickerReqDto;
import com.troy.commons.exchange.model.in.OrderBookReqDto;
import com.troy.commons.exchange.model.in.TradeHistoryReqDto;
import com.troy.commons.exchange.model.out.FullTickerListResDto;
import com.troy.commons.exchange.model.out.OrderBookResDto;
import com.troy.commons.exchange.model.out.TradeHistoryListResDto;
import com.troy.trade.exchange.api.model.dto.in.account.*;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.CoinInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.exchangeInfo.SymbolInfoReqDto;
import com.troy.trade.exchange.api.model.dto.in.market.*;
import com.troy.trade.exchange.api.model.dto.in.order.*;
import com.troy.trade.exchange.api.model.dto.out.account.*;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.CoinInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.exchangeInfo.SymbolInfoListResDto;
import com.troy.trade.exchange.api.model.dto.out.market.*;
import com.troy.trade.exchange.api.model.dto.out.order.CancelOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.CreateOrderResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderDetailResDto;
import com.troy.trade.exchange.api.model.dto.out.order.OrderListResData;

/**
 * 交易所服务
 *
 * @author dp
 */
public interface IExchangeService {

    /**
     * 交易所对应的exchCode
     *
     * @return
     * @see ExchangeCode
     */
    ExchangeCode getExchCode();

    /**
     * TROY交易对转换为交易所交易对
     *
     * @param symbol
     * @return
     */
    String toTradeSymbol(String symbol);

    /**
     * 将tradeSymbol转换为symbol
     *
     * @param tradeSymbol
     * @return
     */
    String symbol(String tradeSymbol);

    /**
     * 创建订单
     *
     * @param createOrderReqDtoReq
     * @return
     */
    Res<CreateOrderResDto> createOrder(Req<CreateOrderReqDto> createOrderReqDtoReq);

    /**
     * 撤销订单
     *
     * @param cancelOrderReqDtoReq
     * @return
     */
    Res<CancelOrderResDto> cancelOrder(Req<CancelOrderReqDto> cancelOrderReqDtoReq);

    /**
     * 查询订单详情
     *
     * @param orderDetailReqDtoReq
     * @return
     */
    Res<OrderDetailResDto> orderDetail(Req<OrderDetailReqDto> orderDetailReqDtoReq);

    /**
     * 查询订单列表
     *
     * @param orderDetailReqDtoReq
     * @return
     */
    Res<OrderListResData> orderList(Req<OrderDetailReqDto> orderDetailReqDtoReq);


    /**
     * 获取历史成交列表
     *
     * @return
     * @throws Throwable
     */
    Res<TradeHistoryListResDto> getTrades(Req<TradeHistoryReqDto> tradeHistoryReqDtoReq);

    /**
     * 我的交易账单
     *
     * @param myTradeReqDtoReq
     * @return
     */
    Res<MyTradeListResDto> getMyTrades(Req<MyTradeReqDto> myTradeReqDtoReq);

    /**
     * 查看账户当前挂单
     *
     * @param ordersReqDtoReq
     * @return
     */
    Res<OrderListResData> getOpenOrders(Req<OpenOrdersReqDto> ordersReqDtoReq);

    /**
     * 查询充提币信息
     * @param harkWithdrawalReqDtoReq
     * @author yanping
     * @return
     */
    Res<ResList<ExchAcctDeptWdralResDto>> harkWithdrawal(Req<HarkWithdrawalReqDto> harkWithdrawalReqDtoReq);

    /**
     * 提现
     * @param withdrawalReqDtoReq
     * @return
     */
    Res<WithdrawalResDto> withdraw(Req<WithdrawalReqDto> withdrawalReqDtoReq);


    // -=-=-=-=-=-=-=-=-=-=-=-=- Market Start -=-=-=-=-=-=-=-=-=-=-=-=-

    /**
     * depth 查询
     *
     * @param orderBookReqDtoReq
     * @return
     */
    Res<OrderBookResDto> getOrderBook(Req<OrderBookReqDto> orderBookReqDtoReq);

    /**
     * 交易对信息查询
     *
     * @param symbolInfoReqDtoReq
     * @return
     */
    Res<SymbolInfoListResDto> getSymbolInfo(Req<SymbolInfoReqDto> symbolInfoReqDtoReq);

    /**
     * 获取币种信息列表
     * @param coinInfoReqDtoReq
     * @return
     */
    Res<CoinInfoListResDto> getCoinInfo(Req<CoinInfoReqDto> coinInfoReqDtoReq);

    /**
     * 全部交易对ticker信息查询
     *
     * @return
     */
    Res<FullTickerListResDto> fullTickers(Req<FullTickerReqDto> fullTickerReqDtoReq);

    /**
     * 根据交易对查询价格信息
     * @param tickerPriceReqDtoReq
     * @return
     */
    Res<TickerPriceResDto> tickerPrice(Req<TickerPriceReqDto> tickerPriceReqDtoReq);

    // -=-=-=-=-=-=-=-=-=-=-=-=- Market End -=-=-=-=-=-=-=-=-=-=-=-=-

    // -=-=-=-=-=-=-=-=-=-=-=-=- Account Start -=-=-=-=-=-=-=-=-=-=-=-=-

    /**
     * 做资金划转
     * @author yanping
     * @param transferReqDtoReq
     * @return
     */
    default Res<TransferResDto> transfer(Req<TransferReqDto> transferReqDtoReq) {
        return null;
    }

    /**
     * 做充币地址查询
     * @author yanping
     * @param depositAddressReqDtoReq
     * @return
     */
    default Res<DepositAddressResDto> depositAddress(Req<DepositAddressReqDto> depositAddressReqDtoReq) {
        return null;
    }


    // -=-=-=-=-=-=-=-=-=-=-=-=- Account End -=-=-=-=-=-=-=-=-=-=-=-=-

    /**
     * 查询账号信息（huobi）
     *
     * @param accountInfoReqDtoReq
     * @return
     */
    default Res<AccountInfoResDto> getAccountInfo(Req<AccountInfoReqDto> accountInfoReqDtoReq) {
        return null;
    }

    /**
     * 获取账户余额
     *
     * @param queryBalanceReqDto
     * @return
     */
    default Res<ResList<QueryBalanceResDto>> getBalance(QueryBalanceReqDto queryBalanceReqDto) {
        return null;
    }


    /**
     * 查询订单列表
     *
     * @param orderDetailReqDtoReq
     * @return
     */
    default Res<OrderListResData> orderListByPage(Req<OrderDetailReqDto> orderDetailReqDtoReq) {
        return null;
    }

    /**
     * 资金账户信息
     *
     * @return
     */
    default Res<ResList<QueryBalanceResDto>> wallet(Req<QueryBalanceReqDto> walletReqDtoReq)  {
        return null;
    }


    default Res<ResList<KLineResDto>> kline(Req<KlineReqDto> klineReqDto) throws Throwable  {
        return null;
    }
}
