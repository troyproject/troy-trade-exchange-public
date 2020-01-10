package com.troy.trade.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 对接交易所系统模块
 *
 * @author dp
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.troy")
public class TroyTradeExchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TroyTradeExchangeApplication.class, args);
    }

}
