package com.troy.trade.exchange.data.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.trade.exchange.api.model.dto.in.data.HeYueDiLargetransferReqDto;
import com.troy.trade.exchange.api.model.dto.in.data.SyncTodamoonReqDto;
import com.troy.trade.exchange.api.model.dto.out.data.LargetransferListResDto;
import com.troy.trade.exchange.api.model.dto.out.data.SyncTodamoonResDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataServiceImplTest {


    @Autowired
    private DataServiceImpl dataService;

    @Test
    public void todamoonSourceData() {
        SyncTodamoonReqDto syncTodamoonReqDto = new SyncTodamoonReqDto();
        syncTodamoonReqDto.setType("bitfinexpositionratio");
        syncTodamoonReqDto.setTimeGranularity("hour");
        Req<SyncTodamoonReqDto> syncTodamoonReqDtoReq = ReqFactory.getInstance().createReq(syncTodamoonReqDto);
        Res<SyncTodamoonResDto> syncTodamoonResDtoRes = dataService.todamoonSourceData(syncTodamoonReqDtoReq);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(syncTodamoonResDtoRes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void largetransfer() {
        HeYueDiLargetransferReqDto heYueDiLargetransferReqDto = new HeYueDiLargetransferReqDto();
        heYueDiLargetransferReqDto.setCoinName("BTC");
        heYueDiLargetransferReqDto.setPage(1);
        Req<HeYueDiLargetransferReqDto> heYueDiLargetransferReqDtoReq = ReqFactory.getInstance().createReq(heYueDiLargetransferReqDto);
        Res<LargetransferListResDto> syncTodamoonResDtoRes = dataService.largetransfer(heYueDiLargetransferReqDtoReq);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(syncTodamoonResDtoRes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}