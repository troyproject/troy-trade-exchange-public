package com.troy.trade.exchange.core.service;

import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResList;
import com.troy.trade.exchange.api.model.dto.in.data.AbnormalChangesReqDto;
import com.troy.trade.exchange.api.model.dto.out.data.AbnormalChangesResDto;

public interface IBinanceExchangeService extends IExchangeService {

    /**
     * 市场异动查询
     *
     * @return
     */
    Res<ResList<AbnormalChangesResDto>> abnormalChanges(Req<AbnormalChangesReqDto> abnormalChangesReqDtoReq);
}
