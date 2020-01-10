package com.troy.trade.exchange.api.model.dto.out.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HuobiAccount{

    /**
     * id : 100009
     * type : spot
     * state : working
     * user-id : 1000
     */
    private int id;
    private String type;
    private String state;
    private int userid;
}
