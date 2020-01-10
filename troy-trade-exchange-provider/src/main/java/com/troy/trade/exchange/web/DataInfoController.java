package com.troy.trade.exchange.web;

import com.troy.commons.BaseController;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResList;
import com.troy.trade.exchange.api.model.dto.in.data.AbnormalChangesReqDto;
import com.troy.trade.exchange.api.model.dto.in.data.HeYueDiLargetransferReqDto;
import com.troy.trade.exchange.api.model.dto.in.data.SyncTodamoonReqDto;
import com.troy.trade.exchange.api.model.dto.in.data.UsdExchangeCnyReqDto;
import com.troy.trade.exchange.api.model.dto.out.data.AbnormalChangesResDto;
import com.troy.trade.exchange.api.model.dto.out.data.LargetransferListResDto;
import com.troy.trade.exchange.api.model.dto.out.data.SyncTodamoonResDto;
import com.troy.trade.exchange.api.model.dto.out.data.UsdExchangeCnyResDto;
import com.troy.trade.exchange.api.service.DataInfoApi;
import com.troy.trade.exchange.core.service.IBinanceExchangeService;
import com.troy.trade.exchange.data.service.IDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易所服务
 *
 * @author yanping
 */
@Slf4j
@RestController
public class DataInfoController extends BaseController implements DataInfoApi {

    @Autowired
    IDataService dataService;

    @Autowired
    IBinanceExchangeService binanceExchangeService;

    @Override
    public Res<LargetransferListResDto> largetransfer(@RequestBody Req<HeYueDiLargetransferReqDto> heYueDiLargetransferReqDtoReq) {
        return dataService.largetransfer(heYueDiLargetransferReqDtoReq);
    }

    /**
     * 查询abnormalChanges信息
     *
     */
    @Override
    public Res<ResList<AbnormalChangesResDto>> abnormalChanges(@RequestBody Req<AbnormalChangesReqDto> abnormalChangesReqDtoReq) {
        return binanceExchangeService.abnormalChanges(abnormalChangesReqDtoReq);
    }

    /**
     * 查询usdExchangeCny信息
     *
     */
    @Override
    public Res<UsdExchangeCnyResDto> usdExchangeCny(@RequestBody Req<UsdExchangeCnyReqDto> usdExchangeCnyReqDtoReq) {
        return dataService.usdExchangeCny(usdExchangeCnyReqDtoReq);
    }

    /**
     * 调用troy官网测试环境同步数据
     * @param syncTodamoonReqDtoReq
     * @return
     */
    @Override
    public Res<SyncTodamoonResDto> todamoonSourceData(@RequestBody Req<SyncTodamoonReqDto> syncTodamoonReqDtoReq) {
        return dataService.todamoonSourceData(syncTodamoonReqDtoReq);
    }

}
