package com.troy.trade.exchange.data.service;

import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.trade.exchange.api.model.dto.in.data.HeYueDiLargetransferReqDto;
import com.troy.trade.exchange.api.model.dto.in.data.SyncTodamoonReqDto;
import com.troy.trade.exchange.api.model.dto.in.data.UsdExchangeCnyReqDto;
import com.troy.trade.exchange.api.model.dto.out.data.LargetransferListResDto;
import com.troy.trade.exchange.api.model.dto.out.data.SyncTodamoonResDto;
import com.troy.trade.exchange.api.model.dto.out.data.UsdExchangeCnyResDto;

/**
 * data 接口
 */
public interface IDataService {

    /**
     * 调用合约帝 大额转移接口
     * @param heYueDiLargetransferReqDtoReq - 查询参数
     * @return
     * @throws Throwable
    */
    Res<LargetransferListResDto> largetransfer(Req<HeYueDiLargetransferReqDto> heYueDiLargetransferReqDtoReq);


    /**
     * 获取usd对cny的价格
     * @return
     * @throws Throwable
     */
    Res<UsdExchangeCnyResDto> usdExchangeCny(Req<UsdExchangeCnyReqDto> usdExchangeCnyReqDtoReq);

    /**
     * troy大额订单信息查询
     *
     * @return
     */
    Res<SyncTodamoonResDto> todamoonSourceData(Req<SyncTodamoonReqDto> syncTodamoonReqDtoReq);
}
