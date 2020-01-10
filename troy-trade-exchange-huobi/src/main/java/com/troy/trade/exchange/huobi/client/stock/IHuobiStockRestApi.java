package com.troy.trade.exchange.huobi.client.stock;

import org.apache.http.HttpException;

import java.io.IOException;
import java.util.Map;

public interface IHuobiStockRestApi {

    /**
     * 获取所有交易对信息
     *
     * @return
     * @throws HttpException
     * @throws IOException
     */
    String symbols() throws Exception;

    /**
     * 根据参数获取 最新成交记录
     * @param symbol
     * @param size
     * @return
     * @throws Exception
     */
    String marketTrade(String symbol,Integer size) throws Exception;

    /**
     * 获取所有交易对ticker信息
     * @return
     * @throws Throwable
     */
    String tickers() throws Throwable;

    /**
     * 获取最新成交记录
     * @return
     * @throws Throwable
     */
    String trade(String symbol) throws Throwable;

    /**
     * 币种列表信息查询
     * @return
     * @throws Throwable
     */
    String currencies() throws Throwable;

    /**
     * 查询账号历史充提币信息列表
     * @param paramMap
     * @author yanping
     * @return
     * @throws Throwable
     */
    String harkWithdrawalHistory(Map<String,String> paramMap) throws Throwable;

    /**
     * 充币地址查询
     * @param paramMap currency - 币种名称
     * @return
     * @throws Throwable
     */
    String depositAddress(Map<String,String> paramMap) throws Throwable;


    /**
     * 提现
     * @param paramMap
     *  address	    true	string	提现地址	仅支持在官网上相应币种地址列表 中的地址
        amount	    true	string	提币数量
        currency	true	string	资产类型	btc, ltc, bch, eth, etc ...(火币全球站支持的币种)
        fee	        true	string	转账手续费
        chain	    false	string	提USDT至OMNI时须设置此参数为"usdt"，提USDT至TRX时须设置此参数为"trc20usdt"，其他币种提现无须设置此参数
        addr-tag	false	string	虚拟币共享地址tag，适用于xrp，xem，bts，steem，eos，xmr	格式, "123"类的整数字符串
     * @return
     * @throws Throwable
     */
    String withdraw(Map<String,String> paramMap) throws Throwable;

}
