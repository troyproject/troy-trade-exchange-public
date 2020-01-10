package com.troy.trade.exchange.api.exception;

public class ApiException extends RuntimeException {
    private String errCode;

    public ApiException(String errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Exception e) {
        super(e);
        this.errCode = e.getClass().getName();
    }
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
    public String getErrCode() {
        return this.errCode;
    }
}
