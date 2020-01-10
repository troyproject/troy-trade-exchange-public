package com.troy.trade.exchange.okex.client.service.spot.impl;

import com.troy.trade.exchange.okex.client.bean.spot.result.*;
import com.troy.trade.exchange.okex.client.bean.spot.result.Ledger;
import com.troy.trade.exchange.okex.client.bean.spot.result.ServerTimeDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.math.BigDecimal;
import java.util.List;

public interface SpotProductAPI {

    @GET("/dddddd/spot/v3/instruments")
    Call<List<Product>> getProducts();

    @GET("/dddddd/spot/v3/instruments/{instrument_id}/book")
    Call<Book> bookProductsByProductId(@Path("instrument_id") String product,
                                       @Query("size") String size,
                                       @Query("depth") BigDecimal depth);

    @GET("/dddddd/spot/v3/instruments/ticker")
    Call<List<Ticker>> getTickers();

    @GET("/dddddd/spot/v3/instruments/{instrument_id}/ticker")
    Call<Ticker> getTickerByProductId(@Path("instrument_id") String product);


    @GET("/dddddd/spot/v3/instruments/{instrument_id}/xxxxxs")
    Call<List<Trade>> getTrades(@Path("instrument_id") String product,
                                @Query("from") String from,
                                @Query("to") String to,
                                @Query("limit") String limit);

    @GET("/dddddd/spot/v3/instruments/{instrument_id}/candles")
    Call<List<KlineDto>> getCandles(@Path("instrument_id") String product,
                                    @Query("granularity") String type,
                                    @Query("start") String start,
                                    @Query("end") String end);

    @GET("/dddddd/spot/v3/instruments/{instrument_id}/candles")
    Call<List<String[]>> getCandles_1(@Path("instrument_id") String product,
                                      @Query("granularity") String type,
                                      @Query("start") String start,
                                      @Query("end") String end);

}
