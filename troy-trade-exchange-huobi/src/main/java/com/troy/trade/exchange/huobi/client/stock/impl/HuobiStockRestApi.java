package com.troy.trade.exchange.huobi.client.stock.impl;

import com.troy.trade.exchange.huobi.client.HuobiConstant;
import com.troy.trade.exchange.huobi.client.HuobiHttpUtilManager;
import com.troy.trade.exchange.huobi.client.stock.IHuobiStockRestApi;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class HuobiStockRestApi implements IHuobiStockRestApi {


    private String apikey;

    private String apiSecret;

    private static final String url_prex = "https://"+HuobiConstant.HOST;

    private static final String TICKERS_URL = "/xxxxx/tickers";

    private static final String SYMBOLS_URL = "/v1/common/symbols";

    private static final String MARKET_TRADE_URL = "/xxxxx/history/xxxxx";

    private static final String TRADE_URL = "/xxxxx/xxxxx";

    private static final String CURRENCIES_URL = "/v1/settings/chains";

    //APIv2 币链参考信息
    private static final String REFERENCE_CURRENCIES_URL = "/v2/reference/currencies";


    private static final String HARKWITHDRAWAL_HISTORY_URL = "/v1/query/deposit-withdraw";

    /**
     * 充币地址查询
     */
    private static final String DEPOSIT_ADDRESS_URL = "/v2/account/deposit/address";

    /**
     * 提现
     */
    private static final String WITHDRAW_URL = "/v1/dw/withdraw/dddddd/create";


    public HuobiStockRestApi() {
        super();
    }

    public HuobiStockRestApi(String apikey, String apiSecret) {
        this.apikey = apikey;
        this.apiSecret = apiSecret;
    }

    @Override
    public String symbols() throws Exception {
        HuobiHttpUtilManager httpUtil = HuobiHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpGet(this.url_prex, this.SYMBOLS_URL, "");
        return result;
    }

    @Override
    public String marketTrade(String symbol, Integer size) throws Exception {
        HuobiHttpUtilManager httpUtil = HuobiHttpUtilManager.getInstance();
        String param = "";
        if(StringUtils.isNotBlank(symbol)){
            param = "symbol=";
            param += symbol;
        }
        if(null != size){
            if(StringUtils.isNotBlank(param)){
                param += "&";
            }
            param += "size=";
            param += size;
        }
        String result = httpUtil.requestHttpGet(this.url_prex, this.MARKET_TRADE_URL, param);
        return result;
    }

    @Override
    public String tickers() throws Throwable {
        HuobiHttpUtilManager httpUtil = HuobiHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpGet(this.url_prex , this.TICKERS_URL, null);
        return result;
    }

    @Override
    public String trade(String symbol) throws Throwable {//
        HuobiHttpUtilManager httpUtil = HuobiHttpUtilManager.getInstance();
        String param = "";
        if(StringUtils.isNotBlank(symbol)){
            param = "symbol=";
            param += symbol;
        }
        String result = httpUtil.requestHttpGet(this.url_prex, this.TRADE_URL, param);
        return result;
    }

    @Override
    public String currencies() throws Throwable {
        HuobiHttpUtilManager httpUtil = HuobiHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpGet(this.url_prex, this.REFERENCE_CURRENCIES_URL, "");
        return result;
    }

    @Override
    public String harkWithdrawalHistory(Map<String, String> paramMap) throws Throwable {
        HuobiHttpUtilManager httpUtil = HuobiHttpUtilManager.getInstance();
//        StringBuffer paramSb = new StringBuffer("");
//        if(null != paramMap && !paramMap.isEmpty()){
//            for (Map.Entry<String,String> entry:paramMap.entrySet()) {
//                if(StringUtils.isBlank(entry.getValue())){
//                    continue;
//                }
//
//                if(StringUtils.isNotBlank(paramSb)){
//                    paramSb.append("&");
//                }
//                paramSb.append(entry.getKey());
//                paramSb.append("=");
//                paramSb.append(entry.getValue());
//            }
//        }

//        String url_prex, String path,
//                String methodType,String apiKey,
//                String secretKey,
//                Map<String,String> paramMap
        String result = httpUtil.requestHttp(this.url_prex,
                this.HARKWITHDRAWAL_HISTORY_URL,HuobiConstant.METHOD_GET,
                apikey,apiSecret,paramMap);
        return result;
    }

    @Override
    public String depositAddress(Map<String, String> paramMap) throws Throwable {
        HuobiHttpUtilManager httpUtil = HuobiHttpUtilManager.getInstance();
        String result = httpUtil.requestHttp(this.url_prex,
                this.DEPOSIT_ADDRESS_URL,HuobiConstant.METHOD_GET,
                apikey,apiSecret,paramMap);
        return result;
    }

    @Override
    public String withdraw(Map<String, String> paramMap) throws Throwable {
        HuobiHttpUtilManager httpUtil = HuobiHttpUtilManager.getInstance();
        String result = httpUtil.requestHttp(this.url_prex,
                this.WITHDRAW_URL,HuobiConstant.METHOD_POST,
                apikey,apiSecret,paramMap);
        return result;
    }
}
