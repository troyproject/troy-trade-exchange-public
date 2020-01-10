package com.troy.trade.exchange.okex.client.service.account.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.troy.trade.exchange.okex.client.bean.account.param.*;
import com.troy.trade.exchange.okex.client.bean.account.param.Withdraw;
import com.troy.trade.exchange.okex.client.bean.account.result.Currency;
import com.troy.trade.exchange.okex.client.bean.account.result.Ledger;
import com.troy.trade.exchange.okex.client.bean.account.result.Wallet;
import com.troy.trade.exchange.okex.client.bean.account.result.WithdrawFee;
import com.troy.trade.exchange.okex.client.client.APIClient;
import com.troy.trade.exchange.okex.client.config.APIConfiguration;
import com.troy.trade.exchange.okex.client.service.account.AccountAPIService;

import java.math.BigDecimal;
import java.util.List;

public class AccountAPIServiceImpl implements AccountAPIService {

    private APIClient client;
    private AccountAPI api;

    public AccountAPIServiceImpl(APIConfiguration config) {
        this.client = new APIClient(config);
        this.api = client.createService(AccountAPI.class);
    }

    @Override
    public JSONObject transfer(Transfer transfer) {
        return this.client.executeSync(this.api.transfer(JSONObject.parseObject(JSON.toJSONString(transfer))));
    }

    @Override
    public JSONObject withdraw(Withdraw withdraw) {
        return this.client.executeSync(this.api.withdraw(JSONObject.parseObject(JSON.toJSONString(withdraw))));
    }

    @Override
    public List<Currency> getCurrencies() {
        return this.client.executeSync(this.api.getCurrencies());
    }

    @Override
    public List<Ledger> getLedger(Integer type, String currency, Integer before, Integer after, int limit) {
        return this.client.executeSync(this.api.getLedger(type, currency, before, after, limit));
    }

    @Override
    public List<Wallet> getWallet() {
        return this.client.executeSync(this.api.getWallet());
    }

    @Override
    public List<Wallet> getWallet(String currency) {
        return this.client.executeSync(this.api.getWallet(currency));
    }

    @Override
    public JSONArray getDepositAddress(String currency) {
        return this.client.executeSync(this.api.getDepositAddress(currency));
    }

    @Override
    public List<WithdrawFee> getWithdrawFee(String currency) {
        return this.client.executeSync(this.api.getWithdrawFee(currency));
    }

    @Override
    public JSONArray getOnHold(String currency) {
        return this.client.executeSync(this.api.getOnHold(currency));
    }

    @Override
    public JSONObject lock(String currency, BigDecimal amount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currency", currency);
        jsonObject.put("size", amount);
        return this.client.executeSync(this.api.lock(jsonObject));
    }

    @Override
    public JSONObject unlock(String currency, BigDecimal amount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currency", currency);
        jsonObject.put("size", amount);
        return this.client.executeSync(this.api.unlock(jsonObject));
    }

    @Override
    public JSONArray getDepositHistory() {
        return this.client.executeSync(this.api.getDepositHistory());
    }

    @Override
    public JSONArray getDepositHistory(String currency) {
        return this.client.executeSync(this.api.getDepositHistory(currency));
    }
}
