package com.troy.trade.exchange.data.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.troy.commons.dto.in.Req;
import com.troy.commons.dto.in.ReqFactory;
import com.troy.commons.dto.out.Res;
import com.troy.commons.dto.out.ResFactory;
import com.troy.commons.exception.business.CallExchangeRemoteException;
import com.troy.commons.exception.enums.StateTypeSuper;
import com.troy.trade.exchange.api.exception.TradeExchangeApiException;
import com.troy.trade.exchange.api.model.dto.in.data.HeYueDiLargetransferReqDto;
import com.troy.trade.exchange.api.model.dto.in.data.SyncTodamoonReqDto;
import com.troy.trade.exchange.api.model.dto.in.data.UsdExchangeCnyReqDto;
import com.troy.trade.exchange.api.model.dto.out.data.LargetransferListResDto;
import com.troy.trade.exchange.api.model.dto.out.data.LargetransferResDto;
import com.troy.trade.exchange.api.model.dto.out.data.SyncTodamoonResDto;
import com.troy.trade.exchange.api.model.dto.out.data.UsdExchangeCnyResDto;
import com.troy.trade.exchange.data.client.stock.IDataStockRestApi;
import com.troy.trade.exchange.data.client.stock.impl.DataStockRestApi;
import com.troy.trade.exchange.data.service.IDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * data 服务相关
 *
 * @author yanping
 */
@Slf4j
@Component
public class DataServiceImpl implements IDataService {


    @Value("${troy.code}")
    private String troyApiKey;

    @Value("${troy.secure-key}")
    private String troyApiSecret;

    @Override
    public Res<LargetransferListResDto> largetransfer(Req<HeYueDiLargetransferReqDto> heYueDiLargetransferReqDtoReq) {
        try {
            HeYueDiLargetransferReqDto heYueDiLargetransferReqDto = heYueDiLargetransferReqDtoReq.getData();
            IDataStockRestApi dataStockRestApi = new DataStockRestApi();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("coin",heYueDiLargetransferReqDto.getCoinName());

            Integer page = heYueDiLargetransferReqDto.getPage();
            if(null == page){
                page = 1;
            }
            paramMap.put("page",String.valueOf(page));
            String result = dataStockRestApi.largetransfer(paramMap);
//            成功：
//            {
//                "code": 0,
//                "msg": "\u6210\u529f",
//                "data": {
//                    "total_count": 79756,
//                    "page": 1,
//                    "page_size": 10,
//                    "data": [{
//                        "block_time": "07-10 10:35",
//                        "amount": 549,
//                        "from_address_source": "",
//                        "from_address": "0x3dfb2bf5***8ea6",
//                        "to_address_source": "Binance",
//                        "to_address": "0x3f5ce5fb***f0be\u3010Binance\u3011"
//                    }, {
//                        "block_time": "07-10 10:34",
//                        "amount": 5006,
//                        "from_address_source": "Binance",
//                        "from_address": "0x3f5ce5fb***f0be\u3010Binance\u3011",
//                        "to_address_source": "Binance",
//                        "to_address": "0x56428636***aced\u3010Binance\u3011"
//                    }]
//                }
//            }
            if (StringUtils.isBlank(result)) {//返回结果为空
                String temp = "调用heyuedi获取大额转账失败，返回数据为空";
                log.error(temp);
                throw new TradeExchangeApiException(temp);
            }

            JSONObject resultJSON = JSONObject.parseObject(result);
            if (!resultJSON.containsKey("data")) {//接口返回数据不包含data
                String temp = "调用heyuedi获取大额转账失败，返回结果:" + result;
                log.error(temp);
                throw new TradeExchangeApiException(temp);
            }
            JSONObject dataJSON = resultJSON.getJSONObject("data");
            if(!dataJSON.containsKey("data")){
                String temp = "调用heyuedi获取大额转账失败，返回结果:" + result;
                log.error(temp);
                throw new TradeExchangeApiException(temp);
            }

            JSONArray dataArr = dataJSON.getJSONArray("data");
            JSONObject dataChildJSON = null;
            List<LargetransferResDto> largetransferResDtoList = new ArrayList<>();
            LargetransferResDto largetransferResDto = null;
            int dataSize = dataArr == null?0:dataArr.size();
            for(int i=0;i<dataSize;i++){
                dataChildJSON = dataArr.getJSONObject(i);
                String blockTime = dataChildJSON.getString("block_time");
                String amount = dataChildJSON.getString("amount");
                String fromSource = dataChildJSON.getString("from_address_source");
                String fromAddress = dataChildJSON.getString("from_address");
                String toSource = dataChildJSON.getString("to_address_source");
                String toAddress = dataChildJSON.getString("to_address");
                largetransferResDto = new LargetransferResDto();
                largetransferResDto.setAmount(amount);
                largetransferResDto.setBlockTime(blockTime);
                largetransferResDto.setFromSource(fromSource);
                largetransferResDto.setToSource(toSource);
                largetransferResDto.setCoin(heYueDiLargetransferReqDto.getCoinName());
                largetransferResDto.setFromAddress(fromAddress);
                largetransferResDto.setToAddress(toAddress);
                largetransferResDtoList.add(largetransferResDto);
            }

            LargetransferListResDto largetransferListResDto = new LargetransferListResDto();
            largetransferListResDto.setLargetransferResDtoList(largetransferResDtoList);
            return ResFactory.getInstance().success(largetransferListResDto);
        } catch (Throwable throwable) {
            String temp = "调用heyuedi获取大额转账异常，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, temp + throwable.getLocalizedMessage());
        }
    }

    @Override
    public Res<UsdExchangeCnyResDto> usdExchangeCny(Req<UsdExchangeCnyReqDto> usdExchangeCnyReqDtoReq) {
        String result = "";
        try{
            IDataStockRestApi dataStockRestApi = new DataStockRestApi();

            result = dataStockRestApi.usdExchangeCny();
            if(StringUtils.isBlank(result)){
                String temp = "调用查询USD对CNY价格失败，被调用方返回空";
                log.warn(temp);
                throw new TradeExchangeApiException(temp);
            }

            JSONObject resultJSON = JSONObject.parseObject(result);
            String status = resultJSON.getString("success");
            if(StringUtils.isBlank(status)
                    || !StringUtils.equals("1",status)){
                String temp = "调用查询USD对CNY价格失败，被调用方返回"+result;
                log.warn(temp);
                throw new TradeExchangeApiException(temp);
            }

            JSONObject childJson = resultJSON.getJSONObject("result");
            if(null == childJson){
                String temp = "调用查询USD对CNY价格失败，被调用方返回"+result;
                log.warn(temp);
                throw new TradeExchangeApiException(temp);
            }

            BigDecimal price = childJson.getBigDecimal("rate");
            UsdExchangeCnyResDto usdExchangeCnyResDto = new UsdExchangeCnyResDto();
            usdExchangeCnyResDto.setPrice(price);
            Res<UsdExchangeCnyResDto> resDtoRes = ResFactory.getInstance().success(usdExchangeCnyResDto);
            return resDtoRes;

//            成功：
//            {
//                "success": "1",
//                "result": {
//                    "status": "ALREADY",
//                    "scur": "USD",
//                    "tcur": "CNY",
//                    "ratenm": "美元/人民币",
//                    "rate": "7.1125",
//                    "update": "2019-09-11 12:06:02"
//                }
//            }


        }catch (Throwable throwable){
            String temp = "调用查询USD转CNY价格异常，被调用方返回 "+result+" ，异常信息：";
            log.error(temp, throwable);
            throw new CallExchangeRemoteException(StateTypeSuper.FAIL_CALL_REMOTE, temp + throwable.getLocalizedMessage());
        }
    }

    @Override
    public Res<SyncTodamoonResDto> todamoonSourceData(Req<SyncTodamoonReqDto> syncTodamoonReqDtoReq) {
        try {
            if(null == syncTodamoonReqDtoReq || null == syncTodamoonReqDtoReq.getData()){
                String temp = "调用testTroy获取todamoonSourceData信息失败，必填参数为空";
                log.error(temp);
                throw new TradeExchangeApiException(temp);
            }

            SyncTodamoonReqDto syncTodamoonReqDto = syncTodamoonReqDtoReq.getData();

            IDataStockRestApi dataStockRestApi = new DataStockRestApi(troyApiKey, troyApiSecret);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("type", syncTodamoonReqDto.getType());
            paramMap.put("timeGranularity", syncTodamoonReqDto.getTimeGranularity());
            String result = dataStockRestApi.todamoonSourceData(paramMap);
            if (StringUtils.isBlank(result)) {
                String temp = "调用testTroy获取todamoonSourceData信息失败，返回为空";
                log.warn(temp);
                throw new TradeExchangeApiException(temp);
            }

            JSONObject resultJSON = JSONObject.parseObject(result);
            if (!resultJSON.containsKey("code")) {
                String temp = "调用testTroy获取todamoonSourceData折线信息失败，返回:" + result;
                log.warn(temp);
                throw new TradeExchangeApiException(temp);
            }

            String code = resultJSON.getString("code");
            if (!StringUtils.equals(code, "200")) {
                String temp = "调用testTroy获取todamoonSourceData折线信息失败，返回:" + result;
                log.warn(temp);
                throw new TradeExchangeApiException(temp);
            }

            JSONObject data = resultJSON.getJSONObject("data");
            SyncTodamoonResDto syncTodamoonResDto = new SyncTodamoonResDto();
            syncTodamoonResDto.setResult(JSONObject.toJSONString(data));
            Res<SyncTodamoonResDto> resDtoRes = ResFactory.getInstance().success(syncTodamoonResDto);
            return resDtoRes;
        } catch (Throwable throwable) {
            String temp = "调用testTroy获取todamoonSourceData折线信息异常，异常信息:";
            log.error(temp,throwable);
            throw new TradeExchangeApiException(temp);
        }
    }

}
