package com.troy.trade.exchange.model.dto.out;

import com.troy.commons.dto.out.ResData;
import lombok.*;

/**
 * 测试resDto
 *
 * @author dp
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestResDto extends ResData {

    private Long id;

    private String tKey;

    private String tValue;
}
