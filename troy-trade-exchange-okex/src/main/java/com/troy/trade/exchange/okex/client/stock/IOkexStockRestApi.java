package com.troy.trade.exchange.okex.client.stock;

import org.apache.http.HttpException;

import java.io.IOException;
import java.util.Map;


/**
 * 现货行情，交易 REST API
 *
 * @author zhangchi
 */
public interface IOkexStockRestApi {

    /**
     * 行情
     *
     * @param symbol btc_usd:比特币    ltc_usd :莱特币
     * @return
     * @throws IOException
     * @throws HttpException
     */
    String ticker(String symbol) throws Throwable;

    /**
     * 市场深度
     *
     * @param symbol btc_usd:比特币    ltc_usd :莱特币
     * @return
     * @throws IOException
     * @throws HttpException
     */
    String depth(String symbol, Integer size) throws Throwable;

    /**
     * 现货历史交易信息
     *
     * @param symbol btc_usd:比特币    ltc_usd :莱特币
     * @param size  不加since参数时，返回最近的60笔交易
     * @return
     * @throws IOException
     * @throws HttpException
     */
    String trades(String symbol, Integer size) throws Throwable;

    /**
     * 现货币对信息查询
     * @return
     * @throws Throwable
     */
    String instruments() throws Throwable;

    /**
     * 币种提币手续费查询
     * @param currency - 币种，不填则返回所有
     * @return
     * @throws Throwable
     */
    String withdrawalFee(String currency) throws Throwable;

    /**
     * 币种列表信息查询
     * @return
     * @throws Throwable
     */
    String currencies() throws Throwable;

    /**
     * 获取多有交易对ticker信息
     * @return
     * @throws Throwable
     */
    String allTickers() throws Throwable;


    /**
     * 历史充值记录查询
     * @param paramMap
     * @return
     * @throws Throwable
     */
    String depositHistory(Map<String,String> paramMap) throws Throwable;

    /**
     * 历史提现记录查询
     * @param paramMap
     * @return
     * @throws Throwable
     */
    String withdrawHistory(Map<String,String> paramMap) throws Throwable;

    /**
     * 资金划转接口
     * @param paramMap
        参数名	            参数类型	是否必须	描述
        currency	        String	是	    币种，如eos
        amount	            String	是	    划转数量
        from	            String	是	    转出账户
                                            0:子账户
                                            1:币币
                                            3:合约
                                            4:C2C
                                            5:币币杠杆
                                            6:资金账户
                                            8:余币宝
                                            9:永续合约
        to	                String	是	    转入账户
                                            0:子账户
                                            1:币币
                                            3:合约
                                            4:C2C
                                            5:币币杠杆
                                            6:资金账户
                                            8:余币宝
                                            9:永续合约
        sub_account	        String	否	    子账号登录名，from或to指定为0时，sub_account为必填项，
        instrument_id	    String	否	    杠杆转出币对，如：eos-usdt，仅限已开通杠杆的币对
        to_instrument_id	String	否	    杠杆转入币对，如：eos-btc，仅限已开通杠杆的币对，仅币币杠杆内转账时用到此参数
     * @return
     * @throws Throwable
     */
    String transfer(Map<String,String> paramMap) throws Throwable;

    /**
     * 充币地址查询
     * @param paramMap currency - 币种名称
     * @return
     * @throws Throwable
     */
    String depositAddress(Map<String,String> paramMap) throws Throwable;

    /**
     *
     * @param paramMap
     *  参数名	    参数类型	是否必须	描述
        currency	String	是	币种
        amount	    String	是	数量
        destination	String	是	提币到
                                2:OKCoin国际; 3:OKEx; 4:数字货币地址; 68:币全CoinAll; 69:OKGAEX;
                                70:2100BIT; 71:OCNex; 72:咖啡交易所; 73:OKTop; 75:BBang;
                                76:TokenClub; 79:VREX; 80:币窗; 81:PAICLUB; 82:淘币网;
                                83: GoTop; 84:ABull; 85:LETDAX; 86:爵爷; 87:币牛牛交易所;
                                88:YAOEOS; 89:TOKR; 90:4A交易平台; 91:币六六; 92:Vipexc;
                                93:imex; 95:DEX.HK; 96:BigEx; 97:StarEX; 98:比特上海;
                                99:LBL.market; 100:CoinGod; 101:WOOT; 102:币小龙Coinxiaolong; 103:VBEX;
                                104:MY1EX; 105:Bitfinance; 106:DBEX 迪交所; 107:LOVEUEX; 108:99EX-久币网;
                                109:Futures Crypto; 110:Exx BK; 111:MyCoin; 112:ROCKEX; 113:以太坊交易所;
                                114:COINZOZ; 115:ECGEX; 116:雷爵爾交易所; 117:EXVALUE; 118:TradeDee;
                                119:HHEX; 120:ERCSTO; 121:Tenspace; 122:APNEX; 123:HelloExc;
                                124:A9CPS; 125:PICKCOIN; 126:WeBit; 127:abv138; 128:币商所;
                                129:66交易所; 130:CBK; 131:Coinpop99; 132:Chain Bay; 133:Longwinex 长胜网;
                                134:BE.TOP; 135:CRYPTEX GLOBAL; 136:FT交易所; 137:牛币网; 138:ybitex;
                                139:大圣交易所; 140:EXFINEX; 141:aiEx; 142:EXIPFS; 143:盘行;
                                144:PeBank; 145:YES交易所; 146:币栈; 147:VitBlock; 148:CPC;
                                149:KGcoin; 150:京东交易所; 151:Korbot Exchange; 152:xFutures; 153:Float SV;
                                154:Y83; 155:雾交所; 156:KoKoEx; 157:MerXader; 158:理想国;
                                159:TEX IO; 160:贝壳国际交易所; 161:币爱交易所; 162:COEX; 163:BOOMEX;
                                164:艾普; 165:币火严选; 166:Token Coin; 167:实通所; 168:诺贝尔;
                                169:Oakiss; 170:魅幻城; 171:ETBBpro; 172:币可富; 173:VVCOIN)
        to_address	String	是	认证过的数字货币地址、邮箱或手机号。某些数字货币地址格式为:地址+标签，例：ARDOR-7JF3-8F2E-QUWZ-CAN7F:123456
        trade_pwd	String	是	交易密码
        fee	        String	是	网络手续费≥0。提币到OKCoin国际或OKEx免手续费，请设置为0。提币到数字货币地址所需网络手续费可通过提币手续费接口查询
     * @return
     * @throws Throwable
     */
    String withdraw(Map<String,String> paramMap) throws Throwable;

    /**
     * 获取资金账户所有资产列表，查询各币种的余额、冻结和可用等信息。
     * @return
     * @throws Throwable
     */
    String wallet() throws Throwable;
}
