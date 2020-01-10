package com.troy.trade.exchange.web;

import cn.hutool.core.lang.Assert;
import com.troy.commons.BaseController;
import com.troy.commons.constraints.Log;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResList;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.commons.exchange.model.in.PrivateTradeReqData;
import com.troy.trade.exchange.api.model.dto.in.account.*;
import com.troy.trade.exchange.api.model.dto.out.account.*;
import com.troy.trade.exchange.api.service.AccountExchangeApi;
import com.troy.trade.exchange.core.service.IExchangeService;
import com.troy.trade.exchange.service.TroyExchangeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易所账号相关服务
 *
 * @author dp
 */
@Slf4j
@RestController
public class AccountExchangeController extends BaseController implements AccountExchangeApi {

    @Autowired
    TroyExchangeFactory troyExchangeFactory;


    @Override
    public  Res<ResList<QueryBalanceResDto>> wallet(@RequestBody Req<QueryBalanceReqDto> queryBalanceReqDtoReq) {
        return getPrivateExchangService(queryBalanceReqDtoReq.getData()).wallet(queryBalanceReqDtoReq);
    }

    @Log("accountInfo")
    @Override
    public Res<AccountInfoResDto> accountInfo(@RequestBody Req<AccountInfoReqDto> accountInfoReqDtoReq) {
        return getAccountExchangService(accountInfoReqDtoReq.getData()).getAccountInfo(accountInfoReqDtoReq);
    }

    @Override
    public Res<ResList<QueryBalanceResDto>> getBalance(@RequestBody Req<QueryBalanceReqDto> queryBalanceReqDtoReq) {
        log.error("调用AccountExchangeController.getBalance入参：{}",queryBalanceReqDtoReq);
        return getPrivateExchangService(queryBalanceReqDtoReq.getData()).getBalance(queryBalanceReqDtoReq.getData());
    }

    @Override
    public Res<ResList<ExchAcctDeptWdralResDto>> harkWithdrawal(@RequestBody Req<HarkWithdrawalReqDto> harkWithdrawalReqDtoReq) {
        return getExchangService(harkWithdrawalReqDtoReq.getData().getExchangeCode()).harkWithdrawal(harkWithdrawalReqDtoReq);
    }

    @Override
    public Res<TransferResDto> transfer(@RequestBody Req<TransferReqDto> transferReqDtoReq) {
        return getExchangService(transferReqDtoReq.getData().getExchangeCode()).transfer(transferReqDtoReq);
    }

    @Override
    public Res<WithdrawalResDto> withdraw(@RequestBody Req<WithdrawalReqDto> withdrawalReqDtoReq) {
        return getExchangService(withdrawalReqDtoReq.getData().getExchangeCode()).withdraw(withdrawalReqDtoReq);
    }

    @Override
    public Res<DepositAddressResDto> depositAddress(@RequestBody Req<DepositAddressReqDto> depositAddressReqDtoReq) {
        return getExchangService(depositAddressReqDtoReq.getData().getExchangeCode()).depositAddress(depositAddressReqDtoReq);
    }

    /**
     * 获取交易所服务对象
     *
     * @param privateTradeReqData
     * @return
     */
    public IExchangeService getPrivateExchangService(PrivateTradeReqData privateTradeReqData) {
        return getExchangService(privateTradeReqData.getExchCode());
    }

    /**
     * 获取交易所服务对象
     *
     * @param accountInfoReqDto
     * @return
     */
    public IExchangeService getAccountExchangService(AccountInfoReqDto accountInfoReqDto) {
        return getExchangService(accountInfoReqDto.getExchCode());
    }

    /**
     * 获取交易所服务对象
     *
     * @param exchCode
     * @return
     */
    public IExchangeService getExchangService(ExchangeCode exchCode) {
        Assert.notNull(exchCode, "交易所Code不能为空");

        IExchangeService exchange = troyExchangeFactory.getExchangeService(exchCode);
        Assert.notNull(exchange, "交易所Code对应的交易所未对接");
        return exchange;
    }
}
