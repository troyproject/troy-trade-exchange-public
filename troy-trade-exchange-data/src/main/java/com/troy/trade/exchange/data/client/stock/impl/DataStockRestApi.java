package com.troy.trade.exchange.data.client.stock.impl;

import com.troy.trade.exchange.data.client.DataHttpUtilManager;
import com.troy.trade.exchange.data.client.constant.DataConstant;
import com.troy.trade.exchange.data.client.stock.IDataStockRestApi;

import java.util.Map;

public class DataStockRestApi implements IDataStockRestApi {

    private String apikey;
    private String apiSecret;

    public DataStockRestApi() {
        super();
    }

    public DataStockRestApi(String apikey, String apiSecret) {
        this.apikey = apikey;
        this.apiSecret = apiSecret;
    }

    @Override
    public String largetransfer(Map<String, String> paramMap) throws Throwable {
        String url = DataConstant.LARGETRANSFER_URL.replace("{coin}",paramMap.get("coin")).replace("{page}",paramMap.get("page"));
        DataHttpUtilManager dataHttpUtilManager = DataHttpUtilManager.getInstance();
        String result = dataHttpUtilManager.requestHttpGet(url, "");
        return result;
    }

    @Override
    public String usdExchangeCny() throws Throwable {
        DataHttpUtilManager dataHttpUtilManager = DataHttpUtilManager.getInstance();
        String result = dataHttpUtilManager.requestHttpGet(DataConstant.GET_USD_URL, "");
        return result;
    }

    @Override
    public String todamoonSourceData(Map<String, String> paramMap) throws Throwable {
        DataHttpUtilManager dataHttpUtilManager = DataHttpUtilManager.getInstance();
        return dataHttpUtilManager.requestHttp(DataConstant.TROY_BASE_URL, DataConstant.TODAMOON_SOURCE_DATA_PATH,
                "POST", apikey,
                apiSecret,paramMap);
    }
}
