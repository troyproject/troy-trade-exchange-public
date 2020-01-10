package com.troy.trade.exchange.binance.client.stock.impl;

import com.troy.trade.exchange.binance.client.BinanceHttpUtilManager;
import com.troy.trade.exchange.binance.client.constant.BinanceConstant;
import com.troy.trade.exchange.binance.client.stock.IBinanceStockRestApi;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class BinanceStockRestApi implements IBinanceStockRestApi {

    private String apikey;

    private String apiSecret;


    private static String url_prex = "https://dddddd.binance.com";

    private final static String ORDERBOOK_URL = "/dddddd/v3/depth";//盘口查询

    private final static String TRADES_URL = "/dddddd/v3/xxxxxs";//最新成交地址

    private final static String EXCHANGE_INFO_URL = "/dddddd/v3/xxxxxangeInfo";//交易对信息查询地址

    //ticker 信息查询接口
    private final static String TICKERS_URL = "/dddddd/v3/ticker/24hr";

    //最新的价格信息查询
    private final static String LAST_PRICE_URL = "/dddddd/v3/ticker/price";

    //历史充值记录查询
    private final static String DEPOSIT_HISTORY_URL = "/wapi/v3/depositHistory.html";

    //历史提现记录查询
    private final static String WITHDRAW_HISTORY_URL = "/wapi/v3/withdrawHistory.html";

    //充币地址多网络查询接口
    private final static String DEPOSIT_ADDRESS_URL = "/sapi/v1/capital/deposit/address";

    //提现
    private final static String WITHDRAW_URL = "/wapi/v3/withdraw.html";

    /**
     * 获取账户币种信息
     */
    private final static String CAPITAL_CONFIG_GETALL_URL = "/sapi/v1/capital/config/getall";

    /****** 数据同步相关 ******************************/
    private final static String ABNORMAL_CHANGES_URL = "https://www.binance.com/gateway-api/v1/public/notification/xxxxx-notice/get-abnormal-trade-notice-list";



    public BinanceStockRestApi() {
        super();
    }

    public BinanceStockRestApi(String apikey, String apiSecret) {
        this.apikey = apikey;
        this.apiSecret = apiSecret;
    }

    @Override
    public String orderBook(String tradeSymbol, Integer limit) throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        String param = "";
        if(!StringUtils.isEmpty(tradeSymbol)) {
            param += "symbol=";
            param += tradeSymbol;
        }

        if(null != limit){
            if(StringUtils.isNotBlank(param)){
                param += "&";
            }
            param += "limit=";
            param += limit;
        }

        String result = httpUtil.requestHttpGet(this.url_prex, this.ORDERBOOK_URL, param);
        return result;
    }

    @Override
    public String getTrades(String tradeSymbol, Integer limit) throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        String param = "";
        if(!StringUtils.isEmpty(tradeSymbol)) {
            param += "symbol=";
            param += tradeSymbol;
        }

        if(null != limit){
            if(StringUtils.isNotBlank(param)){
                param += "&";
            }
            param += "limit=";
            param += limit;
        }

        String result = httpUtil.requestHttpGet(this.url_prex, this.TRADES_URL, param);
        return result;
    }

    @Override
    public String exchangeInfo() throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpGet(this.url_prex, this.EXCHANGE_INFO_URL, "");
        return result;
    }

    @Override
    public String tickers24() throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        return httpUtil.requestHttpGet(this.url_prex , this.TICKERS_URL,"");
    }

    @Override
    public String getLatestPrice(String tradeSymbol) throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        String param = "";
        if(StringUtils.isNotBlank(tradeSymbol)){
            param = "symbol="+tradeSymbol;
        }
        return httpUtil.requestHttpGet(this.url_prex , this.LAST_PRICE_URL,param);
    }

    @Override
    public String abnormalChanges() throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        String param = "";
        return httpUtil.requestHttpGet("" , this.ABNORMAL_CHANGES_URL,param);
    }

    @Override
    public String depositHistory(Map<String, String> paramMap) throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        return httpUtil.requestHttp(this.url_prex, this.DEPOSIT_HISTORY_URL,
                BinanceConstant.METHOD_GET, apikey, apiSecret, paramMap);
    }

    @Override
    public String withdrawHistory(Map<String, String> paramMap) throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        return httpUtil.requestHttp(this.url_prex, this.WITHDRAW_HISTORY_URL,
                BinanceConstant.METHOD_GET, apikey, apiSecret, paramMap);
    }

    @Override
    public String depositAddress(Map<String, String> paramMap) throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        return httpUtil.requestHttp(this.url_prex, this.DEPOSIT_ADDRESS_URL,
                BinanceConstant.METHOD_GET, apikey, apiSecret, paramMap);
    }

    @Override
    public String withdraw(Map<String, String> paramMap) throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        return httpUtil.requestHttp(this.url_prex, this.WITHDRAW_URL,
                BinanceConstant.METHOD_POST, apikey, apiSecret, paramMap);
    }

    @Override
    public String capitalConfigGetall(Map<String, String> paramMap) throws Throwable {
        BinanceHttpUtilManager httpUtil = BinanceHttpUtilManager.getInstance();
        return httpUtil.requestHttp(this.url_prex, this.CAPITAL_CONFIG_GETALL_URL,
                BinanceConstant.METHOD_GET, apikey, apiSecret, paramMap);
    }
}
