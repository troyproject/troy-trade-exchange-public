package com.troy.trade.exchange.api.exception;

import com.troy.commons.exception.business.BusinessException;
import com.troy.commons.exception.enums.StateTypeSuper;
import com.troy.commons.exchange.model.enums.TradeExchangeErrorCode;

/**
 * 交易所服务API异常
 * @author dp
 */
public class TradeExchangeApiException extends BusinessException {

    public TradeExchangeApiException() {
        super();
    }

    public TradeExchangeApiException(String message) {
        super(TradeExchangeErrorCode.FAIL_EXCHANGE_CUSTOM, message);
    }

    public TradeExchangeApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public TradeExchangeApiException(Throwable cause) {
        super(cause);
    }

    public TradeExchangeApiException(StateTypeSuper stateTypeSuper) {
        super(stateTypeSuper);
    }

    public TradeExchangeApiException(StateTypeSuper stateTypeSuper, Object... stateDepictArguments) {
        super(stateTypeSuper, stateDepictArguments);
    }

    public TradeExchangeApiException(StateTypeSuper stateTypeSuper, Exception cause, Object... stateDepictArguments) {
        super(stateTypeSuper, cause, stateDepictArguments);
    }

    @Override
    public String getStateCode() {
        return super.getStateCode();
    }

    @Override
    public String getStateDepict() {
        return super.getStateDepict();
    }

    @Override
    public StateTypeSuper getState() {
        return super.getState();
    }

    @Override
    public void setState(StateTypeSuper stateTypeSuper) {
        super.setState(stateTypeSuper);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }
}
