package com.troy.trade.exchange.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.troy.commons.exchange.model.constant.ExchangeCode;
import com.troy.trade.exchange.core.service.IExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * Troy交易所工厂
 *
 * @author dp
 */
@Slf4j
@Service
public class TroyExchangeFactory implements ApplicationContextAware, InitializingBean {

    private Map<ExchangeCode, IExchangeService> exchangeMap = Maps.newHashMap();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, IExchangeService> exchangeMapSpring = this.applicationContext.getBeansOfType(IExchangeService.class);
        if (CollectionUtils.isEmpty(exchangeMapSpring)) {
            return;
        }
        exchangeMapSpring.forEach((beanName, exchange) -> exchangeMap.put(exchange.getExchCode(), exchange));
        log.info("对接交易所信息 {}", JSONObject.toJSONString(exchangeMap));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Map<ExchangeCode, IExchangeService> getExchange() {
        return exchangeMap;
    }

    /**
     * 获取交易所服务
     *
     * @param exchangeCode
     * @return
     */
    public IExchangeService getExchangeService(ExchangeCode exchangeCode) {
        // 交易所开放判断
        /*Boolean exchangeSwitch = (Boolean) applicationContext.getBean(RedisUtil.class).get(TradeExchangeConstant.EXCHANGE_SWITCH_KEY.replace("{exchangeCode}", exchangeCode.code()));
        // 交易所未开放
        if(exchangeSwitch != null && !exchangeSwitch){
            throw new ServiceException(TradeExchangeErrorCode.FAIL_EXCHANGE_NOT_OPEN);
        }*/
        return exchangeMap.get(exchangeCode);
    }
}
