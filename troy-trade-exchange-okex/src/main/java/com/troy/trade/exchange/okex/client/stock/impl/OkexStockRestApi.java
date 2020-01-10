package com.troy.trade.exchange.okex.client.stock.impl;

import com.troy.trade.exchange.okex.client.OkexHttpUtilManager;
import com.troy.trade.exchange.okex.client.stock.IOkexStockRestApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class OkexStockRestApi implements IOkexStockRestApi {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String apiKey;
    private String secretKey;
    private String passphrase;

    private String url_prex = "https://www.okex.com";

    public OkexStockRestApi(String apiKey, String secretKey, String passphrase) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.passphrase = passphrase;
    }

    public OkexStockRestApi() {
        super();
    }

    /**
     * 现货行情URL
     */
    private String TICKER_URL = "/dddddd/spot/v3/instruments/{instrument-id}/ticker";

    /**
     * 现货市场深度URL
     */
    private String DEPTH_URL = "/dddddd/spot/v3/instruments/{instrument-id}/book";

    /**
     * 现货历史交易信息URL
     */
    private String TRADES_URL = "/dddddd/spot/v3/instruments/{instrument-id}/xxxxxs";

    /**
     * 现货币对信息查询URL
     */
    private String SYMBOL_URL = "/dddddd/spot/v3/instruments";

    /**
     * 币种提币手续费查询接口
     */
    private String WITHDRAWAL_FEE_URL = "/dddddd/account/v3/withdrawal/fee";

    /**
     * 币种列表信息查询接口
     */
    private String CURRENCIES_URL = "/dddddd/account/v3/currencies";

    /**
     * 资金账户信息
     */
    private String WALLET_URL = "/dddddd/account/v3/wallet";

    /**
     * 获取所有交易对ticker信息
     */
    private String TICKERS_URL = "/dddddd/spot/v3/instruments/ticker";

    /**
     * 充币记录查询
     */
    private String DEPOSIT_HISTORY_URL = "/dddddd/account/v3/deposit/history/<currency>";

    /**
     * 提币记录查询
     */
    private String WITHDRAWAL_HISTORY_URL = "/dddddd/account/v3/withdrawal/history/<currency>";

    /**
     * 资金划转接口
     */
    private String TRANSFER_URL = "/dddddd/account/v3/transfer";

    /**
     * 充币地址查询
     */
    private static final String DEPOSIT_ADDRESS_URL = "/dddddd/account/v3/deposit/address";

    /**
     * 提现
     */
    private static final String WITHDRAW_URL = "/dddddd/account/v3/withdrawal";

    @Override
    public String ticker(String symbol) throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String url = TICKER_URL.replace("{instrument-id}",symbol);
        String result = httpUtil.requestHttpV3(url_prex, url,"get",
                null,null,null,
                null);
        return result;
    }

    @Override
    public String depth(String symbol,Integer size) throws HttpException, IOException {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        DEPTH_URL = DEPTH_URL.replace("{instrument-id}",symbol);
        String param = "";
        if (size != 0) {
            param += "size=" + size;
        }
        String result = httpUtil.requestHttpGet(url_prex, this.DEPTH_URL, param);
        return result;
    }

    @Override
    public String trades(String symbol, Integer size) throws HttpException, IOException {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        TRADES_URL = TRADES_URL.replace("{instrument-id}",symbol);
        String param = "";
        if (size != 0) {
            param += "size=" + size;
        }
        String result = httpUtil.requestHttpGet(url_prex, this.TRADES_URL, param);
        return result;
    }

    @Override
    public String instruments() throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpGet(url_prex, this.SYMBOL_URL, "");
        return result;
    }

    @Override
    public String withdrawalFee(String currency) throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        Map<String, String> paramMap = new HashMap<>();
        if(StringUtils.isNotBlank(currency)){
            paramMap.put("currency",currency);
        }
        String result = httpUtil.requestHttpV3(url_prex, this.WITHDRAWAL_FEE_URL,"get", apiKey,secretKey,passphrase,paramMap);
        return result;
    }

    @Override
    public String currencies() throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpV3(url_prex, this.CURRENCIES_URL,"get", apiKey,secretKey,passphrase,null);
        return result;
    }

    @Override
    public String wallet() throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpV3(url_prex, this.WALLET_URL,"get", apiKey,secretKey,passphrase,null);
        return result;
    }


    @Override
    public String allTickers() throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpV3(url_prex, this.TICKERS_URL,"get", null,null,null,null);
        return result;
    }

    @Override
    public String depositHistory(Map<String, String> paramMap) throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String url = this.DEPOSIT_HISTORY_URL.replace("<currency>",paramMap.get("coinName"));
        String result = httpUtil.requestHttpV3(url_prex, url,"get", apiKey,secretKey,passphrase,paramMap);
        return result;
    }

    @Override
    public String withdrawHistory(Map<String, String> paramMap) throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String url = this.WITHDRAWAL_HISTORY_URL.replace("<currency>",paramMap.get("coinName"));
        String result = httpUtil.requestHttpV3(url_prex, url,"get", apiKey,secretKey,passphrase,paramMap);
        return result;
    }

    @Override
    public String transfer(Map<String, String> paramMap) throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String url = this.TRANSFER_URL;
        String result = httpUtil.requestHttpV3(url_prex, url,"post", apiKey,secretKey,passphrase,paramMap);
        return result;
    }

    @Override
    public String depositAddress(Map<String, String> paramMap) throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String url = this.DEPOSIT_ADDRESS_URL;
        String result = httpUtil.requestHttpV3(url_prex, url,"get", apiKey,secretKey,passphrase,paramMap);
        return result;
    }

    @Override
    public String withdraw(Map<String, String> paramMap) throws Throwable {
        OkexHttpUtilManager httpUtil = OkexHttpUtilManager.getInstance();
        String url = this.WITHDRAW_URL;
        String result = httpUtil.requestHttpV3(url_prex, url,"post", apiKey,secretKey,passphrase,paramMap);
        return result;
    }


}
