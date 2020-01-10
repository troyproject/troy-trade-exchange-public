package com.troy.trade.exchange.okex.client.service.spot.impl;

import com.troy.trade.exchange.okex.client.bean.spot.result.Account;
import com.troy.trade.exchange.okex.client.bean.spot.result.Ledger;
import com.troy.trade.exchange.okex.client.bean.spot.result.ServerTimeDto;
import com.troy.trade.exchange.okex.client.client.APIClient;
import com.troy.trade.exchange.okex.client.config.APIConfiguration;
import com.troy.trade.exchange.okex.client.service.spot.SpotAccountAPIService;

import java.util.List;
import java.util.Map;

public class SpotAccountAPIServiceImpl implements SpotAccountAPIService {

    private final APIClient client;
    private final SpotAccountAPI api;

    public SpotAccountAPIServiceImpl(final APIConfiguration config) {
        this.client = new APIClient(config);
        this.api = this.client.createService(SpotAccountAPI.class);
    }

    @Override
    public ServerTimeDto time() {
        return this.client.executeSync(this.api.time());
    }

    @Override
    public Map<String, Object> getMiningData() {
        return this.client.executeSync(this.api.getMiningdata());
    }

    @Override
    public List<Account> getAccounts() {
        return this.client.executeSync(this.api.getAccounts());
    }

    @Override
    public List<Ledger> getLedgersByCurrency(final String currency, final String from, final String to, final String limit) {
        return this.client.executeSync(this.api.getLedgersByCurrency(currency, from, to, limit));
    }

    @Override
    public Account getAccountByCurrency(final String currency) {
        return this.client.executeSync(this.api.getAccountByCurrency(currency));
    }
}
