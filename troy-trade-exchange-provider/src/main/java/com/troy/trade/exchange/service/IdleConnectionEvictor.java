package com.troy.trade.exchange.service;

import com.troy.trade.exchange.binance.client.BinanceHttpUtilManager;
import com.troy.trade.exchange.bitfinex.client.BitfinexHttpUtilManager;
import com.troy.trade.exchange.gateio.client.GateioHttpUtilManager;
import com.troy.trade.exchange.data.client.DataHttpUtilManager;
import com.troy.trade.exchange.huobi.client.HuobiHttpUtilManager;
import com.troy.trade.exchange.okex.client.OkexHttpUtilManager;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * 清理无效连接的线程
 * @author
 */
@Component
public class IdleConnectionEvictor extends Thread {

    private volatile boolean shutdown;

    public IdleConnectionEvictor() {
        super();
        super.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);

                    // 币安关闭失效的连接、关闭空闲超过30秒的连接
                    BinanceHttpUtilManager.cm.closeExpiredConnections();
                    BinanceHttpUtilManager.cm.closeIdleConnections(30, TimeUnit.SECONDS);

                    // gateio关闭失效的连接、关闭空闲超过30秒的连接
                    GateioHttpUtilManager.cm.closeExpiredConnections();
                    GateioHttpUtilManager.cm.closeIdleConnections(30, TimeUnit.SECONDS);

                    // huobi关闭失效的连接、关闭空闲超过30秒的连接
                    HuobiHttpUtilManager.cm.closeExpiredConnections();
                    HuobiHttpUtilManager.cm.closeIdleConnections(30, TimeUnit.SECONDS);

                    // Bibox关闭失效的连接、关闭空闲超过30秒的连接
                    BitfinexHttpUtilManager.cm.closeExpiredConnections();
                    BitfinexHttpUtilManager.cm.closeIdleConnections(30, TimeUnit.SECONDS);

                    // okex关闭失效的连接、关闭空闲超过30秒的连接
                    OkexHttpUtilManager.cm.closeExpiredConnections();
                    OkexHttpUtilManager.cm.closeIdleConnections(30, TimeUnit.SECONDS);

                    // data数据查询关闭失效的连接、关闭空闲超过30秒的连接
                    DataHttpUtilManager.cm.closeExpiredConnections();
                    DataHttpUtilManager.cm.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // 结束
        }
    }

    //关闭清理无效连接的线程
    @PreDestroy
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
