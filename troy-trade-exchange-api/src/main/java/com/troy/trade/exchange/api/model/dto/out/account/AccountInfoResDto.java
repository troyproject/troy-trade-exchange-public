package com.troy.trade.exchange.api.model.dto.out.account;

import com.troy.commons.dto.out.ResData;
import lombok.*;

import java.util.List;

/**
 * 账号信息请求出参
 *
 * @author dp
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoResDto extends ResData {

    /**
     * 火币账号信息
     */
    List<HuobiAccount> huobiAccounts;

}

