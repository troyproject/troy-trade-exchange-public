package com.troy.trade.exchange.gateio.client.stock.impl;

import com.alibaba.fastjson.JSONArray;

import com.google.common.collect.Maps;
import com.troy.trade.exchange.gateio.client.GateioHttpUtilManager;
import com.troy.trade.exchange.gateio.client.stock.IGateioStockRestApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GateioStockRestApi implements IGateioStockRestApi {

    private String url_prex = "https://data.gateio.co";

    public GateioStockRestApi() {
        super();
    }

    public GateioStockRestApi(String url_prex) {
        this.url_prex = url_prex;
    }

    public final String PAIRS_URL = "/dddddd2/1/pairs";

    public final String MARKETINFO_URL = "/dddddd2/1/xxxxxinfo";

    public final String MARKETLIST_URL = "/dddddd2/1/xxxxxlist";

    public final String TICKERS_URL = "/dddddd2/1/tickers";

    public final String TICKER_URL = "/dddddd2/1/ticker";

    public final String KLINE_URL = "/dddddd2/1/candlestick2";

    public final String ORDERBOOK_URL = "/dddddd2/1/orderBook";

    public final String BALANCE_URL = "/dddddd2/1/private/balances";

    private final String DEPOSITADDRESS_URL = "/dddddd2/1/private/depositAddress";

    private final String DEPOSITESWITHDRAWALS_URL = "/dddddd2/1/private/depositsWithdrawals";

    private final String BUY_URL = "/dddddd2/1/private/buy";

    private final String SELL_URL = "/dddddd2/1/private/sell";

    private final String CANCELORDER_URL = "/dddddd2/1/private/cancelOrder";

    private final String CANCELORDERS_URL = "/dddddd2/1/private/cancelOrders";

    private final String CANCELALLORDERS_URL = "/dddddd2/1/private/cancelAllOrders";

    private final String GETORDER_URL = "/dddddd2/1/private/getOrder";

    private final String OPENORDERS_URL = "/dddddd2/1/private/openOrders";

    private final String TRADEHISTORY_URL = "/dddddd2/1/xxxxxHistory";

    /**
     * 提现
     */
    private final String WITHDRAW_URL = "/dddddd2/1/private/withdraw";

    private final String MYTRADEHISTORY_URL = "/dddddd2/1/private/xxxxxHistory";

    /**
     * 充币地址查询
     */
    private final String DEPOSIT_ADDRESS_URL = "/dddddd2/1/private/depositAddress";

//    @Override
//	public String pairs() throws HttpException, IOException {
//        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
//		String param = "";
//		String result = httpUtil.requestHttpGet(url_prex, PAIRS_URL, param);
//	    return result;
//	}

	@Override
	public String marketinfo() throws HttpException, IOException {
		GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
		String param = "";
		String result = httpUtil.requestHttpGet(url_prex, MARKETINFO_URL, param);
		return result;
	}
//
//	@Override
//	public String marketlist() throws HttpException, IOException {
//		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
//		String param = "";
//		String result = httpUtil.requestHttpGet(url_prex, MARKETLIST_URL, param);
//		return result;
//	}

    @Override
    public String tickers() throws Throwable {
        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String param = "";
        String result = httpUtil.requestHttpGet(url_prex, TICKERS_URL, param);
        return result;
    }

    @Override
    public String ticker(String symbol) throws HttpException, IOException {
        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String param = "";

        param += "/" + symbol;

        String result = httpUtil.requestHttpGet(url_prex, TICKER_URL + param, "");
        return result;
    }


	@Override
	public String orderBook(String symbol) throws Throwable {
		GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
		String param = "";
		if(!StringUtils.isEmpty(symbol)) {
			if(param.equals("")) {
				param += "/";
			}
			param += symbol;
		}
		String result = httpUtil.requestHttpGet(url_prex, ORDERBOOK_URL + param, "");
		return result;
	}

	@Override
	public String tradeHistory(String symbol) throws HttpException, IOException {
	    // https://data.gateio.co/dddddd2/1/xxxxxHistory/eth_btc
        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
		String param = "";
		if(StringUtils.isNotBlank(symbol)) {
            param += "/";
			param += symbol;
		}
		String result = httpUtil.requestHttpGet(url_prex, TRADEHISTORY_URL + param, "");
		return result;
	}

    @Override
    public String balance(String apiKey, String apiSecret) throws HttpException, IOException {
        Map<String, String> params = new HashMap();

        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();

        String result = httpUtil.doRequest("data", "post", url_prex + BALANCE_URL, params, apiKey, apiSecret);
        return result;
    }
//
//	@Override
//	public String depositAddress(String symbol) throws HttpException, IOException {
//		Map<String, String> params = new HashMap<String, String>();
//
//		HttpUtilManager httpUtil = HttpUtilManager.getInstance();
//		String result = httpUtil.doRequest( "data", "post", url_prex+DEPOSITADDRESS_URL, params );
//		return result;
//	}
//
	@Override
	public String depositsWithdrawals(String startTime,String endTime, String appkey, String appsecret) throws HttpException, IOException {
		Map<String, String> params = new HashMap();
		if(StringUtils.isNotBlank(startTime)){
            params.put("start", startTime);
        }

        if(StringUtils.isNotBlank(endTime)){
            params.put("end", endTime);
        }
		GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
		String result = httpUtil.doRequest( "data", "post", url_prex+ DEPOSITESWITHDRAWALS_URL, params, appkey, appsecret);
		return result;
	}

    @Override
    public String buy(String currencyPair, String rate, String amount, String appkey, String appsecret) throws HttpException, IOException {
        Map<String, String> params = Maps.newHashMap();
        params.put("currencyPair", currencyPair);
        params.put("rate", rate);
        params.put("amount", amount);

        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String result = httpUtil.doRequest("data", "post", url_prex + BUY_URL, params, appkey, appsecret);
        return result;
    }

    @Override
    public String sell(String currencyPair, String rate, String amount, String appkey, String appsecret) throws HttpException, IOException {
        Map<String, String> params = Maps.newHashMap();
        params.put("currencyPair", currencyPair);
        params.put("rate", rate);
        params.put("amount", amount);

        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String result = httpUtil.doRequest("data", "post", url_prex + SELL_URL, params, appkey, appsecret);
        return result;
    }


    @Override
    public String cancelOrder(String orderNumber, String currencyPair, String appkey, String appsecret) throws HttpException, IOException {
        Map<String, String> params = Maps.newHashMap();
        params.put("orderNumber", orderNumber);
        params.put("currencyPair", currencyPair);

        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String result = httpUtil.doRequest("data", "post", url_prex + CANCELORDER_URL, params,appkey, appsecret);
        return result;
    }

    @Override
    public String cancelOrders(JSONArray array, String appkey, String appsecret) throws HttpException, IOException {
        Map<String, String> params = Maps.newHashMap();
        params.put("orders_json", array.toJSONString());
        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String result = httpUtil.doRequest("data", "post", url_prex + CANCELORDERS_URL, params,appkey, appsecret);
        return result;
    }

    @Override
    public String cancelAllOrders(String type, String currencyPair, String appkey, String appsecret) throws HttpException, IOException {
        Map<String, String> params = Maps.newHashMap();
        params.put("type", type);
        params.put("currencyPair", currencyPair);

        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String result = httpUtil.doRequest("data", "post", url_prex + CANCELALLORDERS_URL, params,appkey, appsecret);
        return result;
    }

    @Override
    public String getOrder(String orderNumber, String currencyPair, String appkey, String appsecret) throws HttpException, IOException {
        Map<String, String> params = Maps.newHashMap();
        params.put("orderNumber", orderNumber);
        params.put("currencyPair", currencyPair);

        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String result = httpUtil.doRequest("data", "post", url_prex + GETORDER_URL, params,appkey, appsecret);
        return result;
    }

    @Override
    public String getKline(String currencyPair, String groupSec, String rangeHour) throws HttpException, IOException {
        String params = "groupSec=" + groupSec + "&" + "rangeHour=" + rangeHour;
        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String result = httpUtil.requestHttpGet(url_prex, KLINE_URL + "/" + currencyPair, params);
        return result;
    }

    @Override
    public String openOrders(String currencyPair, String appkey, String appsecret) throws HttpException, IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("currencyPair", currencyPair);

        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String result = httpUtil.doRequest("data", "post", url_prex + OPENORDERS_URL, params,appkey, appsecret);
        return result;
    }


	@Override
	public String myTradeHistory(String currencyPair,String orderNumber, String appkey, String appsecret) throws HttpException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("currencyPair", currencyPair);
	//	params.put("orderNumber", orderNumber);

		GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
		String result = httpUtil.doRequest( "data", "post", url_prex+ MYTRADEHISTORY_URL, params ,appkey, appsecret);
		return result;
	}

    @Override
    public String depositAddress(String apiKey,String apiSecret,String currency) throws Throwable {
        Map<String, String> params = new HashMap();
        params.put("currency",currency);
        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
        String result = httpUtil.doRequest("data", "post", url_prex + DEPOSIT_ADDRESS_URL, params, apiKey, apiSecret);
        return result;
    }

	@Override
	public String withdraw(String currency,String amount, String address, String apiKey, String apiSecret) throws HttpException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("currency", currency);
		params.put("amount", amount);
		params.put("address", address);

        GateioHttpUtilManager httpUtil = GateioHttpUtilManager.getInstance();
		String result = httpUtil.doRequest( "data", "post", url_prex+ WITHDRAW_URL, params, apiKey, apiSecret);
		return result;
	}

    public String getUrl_prex() {
        return url_prex;
    }

    public void setUrl_prex(String url_prex) {
        this.url_prex = url_prex;
    }

}
