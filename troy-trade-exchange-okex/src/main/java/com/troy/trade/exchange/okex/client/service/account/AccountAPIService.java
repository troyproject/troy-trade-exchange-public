package com.troy.trade.exchange.okex.client.service.account;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.troy.trade.exchange.okex.client.bean.account.param.*;
import com.troy.trade.exchange.okex.client.bean.account.param.Withdraw;
import com.troy.trade.exchange.okex.client.bean.account.result.Currency;
import com.troy.trade.exchange.okex.client.bean.account.result.Ledger;
import com.troy.trade.exchange.okex.client.bean.account.result.Wallet;
import com.troy.trade.exchange.okex.client.bean.account.result.WithdrawFee;

import java.math.BigDecimal;

import java.util.List;


public interface AccountAPIService {

    JSONObject transfer(Transfer transfer);

    JSONObject withdraw(Withdraw withdraw);

    List<Currency> getCurrencies();

    List<Ledger> getLedger(Integer type, String currency, Integer before, Integer after, int limit);

    List<Wallet> getWallet();

    List<Wallet> getWallet(String currency);

    JSONArray getDepositAddress(String currency);

    List<WithdrawFee> getWithdrawFee(String currency);

    JSONArray getOnHold(String currency);

    JSONObject lock(String currency, BigDecimal amount);

    JSONObject unlock(String currency, BigDecimal amount);

    JSONArray getDepositHistory();

    JSONArray getDepositHistory(String currency);

}
