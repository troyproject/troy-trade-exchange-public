package com.troy.trade.exchange.model.dto.in;

import com.troy.commons.dto.in.ReqData;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 测试reqDto
 *
 * @author dp
 */
@Setter
@Getter
public class TestReqDto extends ReqData {

    @NotBlank
    private String key;

}
