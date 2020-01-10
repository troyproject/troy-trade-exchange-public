package com.troy.trade.exchange.model.enums;

import com.troy.commons.enums.BaseEnum;

/**
 * 测试Demo枚举
 *
 * @author dp
 */
public enum DemoEnum implements BaseEnum<Integer> {

    // AAA
    AAA(1, "ssss"),
    // BBB
    BBB(1, "ssss"),
    ;

    private int code;
    private String desc;

    DemoEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer code() {
        return code;
    }

    @Override
    public String desc() {
        return desc;
    }
}
