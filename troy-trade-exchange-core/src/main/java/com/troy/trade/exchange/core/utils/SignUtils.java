package com.troy.trade.exchange.core.utils;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 签名串算法
 * @author  
 */
@Component
public class SignUtils {

	Logger logger = LoggerFactory.getLogger(SignUtils.class);

	/**
	 * 对params进行签名，其中ignoreParamNames这些参数不参与签名
	 * 
	 * @param paramMap
	 * @param ignoreParamNames
	 * @param secret
	 * @return
	 */
	public String signJson(Map<String,String> paramMap, List<String> ignoreParamNames, String secret) {
		try {
			StringBuilder sb = new StringBuilder();
			List<String> paramNames = new ArrayList<String>(paramMap.size());
			paramNames.addAll(paramMap.keySet());
			if (ignoreParamNames != null && ignoreParamNames.size() > 0) {
				for (String ignoreParamName : ignoreParamNames) {
					if(paramNames.contains(ignoreParamName)){
						paramNames.remove(ignoreParamName);
					}
				}
			}
			Collections.sort(paramNames);
			sb.append(secret);
			for (String paramName : paramNames) {
				if (paramMap.containsKey(paramName) && paramMap.get(paramName) != null) {
					Object paramValue = paramMap.get(paramName);
					String strs = null;
					if(paramValue instanceof Integer){
						strs = String.valueOf(paramValue);
					}else if(paramValue instanceof JSONArray){
						strs = ((JSONArray) paramValue).toJSONString();
					}else{
						strs = String.valueOf(paramValue);
					}
					sb.append(paramName.toUpperCase()).append(strs);
				}
			}
			sb.append(secret);
			byte[] bytes = getSHA1Digest(sb.toString());
			String sign = byte2hex(bytes);
			logger.info(sb.toString()+"-->"+sign.toUpperCase());
			return sign.toUpperCase();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 获取摘要 
	 */
	private byte[] getSHA1Digest(String data) {
		byte[] bytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			bytes = md.digest(data.getBytes("UTF-8"));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return bytes;
	}

	/**
	 * [描述： desc]
	 *
	 * @param strType 要使用的哈希算法，例如："md5"，"sha256"，"haval160,4" 等。
	 * @param strText 要进行哈希运算的消息。
	 * @return String
	 */
	public static String getDigest(String strType, String strText) {
// 返回值
		String strResult = null;

		// 是否是有效字符串
		if (strText != null && strText.length() > 0)
		{
			try
			{
				// SHA 加密开始
				// 创建加密对象 并傳入加密類型
				MessageDigest messageDigest = MessageDigest.getInstance(strType);
				// 传入要加密的字符串
				messageDigest.update(strText.getBytes());
				// 得到 byte 類型结果
				byte byteBuffer[] = messageDigest.digest();

				// 將 byte 轉換爲 string
				StringBuffer strHexString = new StringBuffer();
				// 遍歷 byte buffer
				for (int i = 0; i < byteBuffer.length; i++)
				{
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1)
					{
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				// 得到返回結果
				strResult = strHexString.toString();
			}
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
		}

		return strResult;
	}

	/**
	 * 二进制转十六进制字符串
	 *
	 * @param bytes
	 * @return
	 */
	private static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toLowerCase());
		}
		return sign.toString();
	}

	/**
	 * HMACSHA512算法
	 * @param data 要加密的内容
	 * @param key 秘钥
	 * @return
	 */
	public static String hmacSHA512(String data,String key) {
		String HMACSHA512 = "HmacSHA512";
		String result = "";
		try {
			byte[] macData = hmac(data, key,HMACSHA512);
			byte[] hex = new Hex().encode(macData);
			result = new String(hex, "UTF-8");
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * HMACSHA256算法
	 * @param data 要加密的内容
	 * @param key 秘钥
	 * @return
	 */
	public static byte[] hmacSHA256(String data,String key) {
		String HMACSHA256 = "HmacSHA256";
		return hmac(data, key,HMACSHA256);
	}


	/**
	 * HMACSHA256算法
	 * @param data 要加密的内容
	 * @param key 秘钥
	 * @return
	 */
	public static String hmacSHA256Hex(String data,String key) {
		String HMACSHA256 = "HmacSHA256";
		String result = "";
		try {
			byte[] macData = hmac(data, key,HMACSHA256);

			return new String(Hex.encodeHex(macData));
		}catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}


	public static String base64(byte[] data){
		return Base64.getEncoder().encodeToString(data);
	}


    public static byte[] base64Decode(String data){
        return Base64.getDecoder().decode(data);
    }

    /**
     * HMAC算法
     * @param data 要加密的内容
     * @param key 秘钥
     * @return
     */
    public static byte[] hmac(String data,String key,String hmac) {
        byte[] bytesKey = key.getBytes();
        final SecretKeySpec secretKey = new SecretKeySpec(bytesKey, hmac);
        try {
            Mac mac = Mac.getInstance(hmac);
            mac.init(secretKey);
            return mac.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

	/**
	 * HMAC算法
	 * @param data 要加密的内容
	 * @param key 秘钥
	 * @return
	 */
	public static String hmacSHA256Base64(String data,String key) {
		byte[] hmacKey = Base64.getDecoder().decode(key);
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec keySpec = new SecretKeySpec(hmacKey, "HmacSHA256");
			mac.init(keySpec);
			byte[] encoded = Base64.getEncoder().encode(mac.doFinal(data.getBytes("UTF-8")));
			return new String(encoded);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * unicode 转字符串
	 * @param str
	 * @return
	 */
	public static String unicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch+"" );
		}
		return str;
	}

	/**
	 * sha1加密
	 * @param data
	 * @return
	 */
	public String getSha1(String data){
		byte[] bytes = getSHA1Digest(data);
		return byte2hex(bytes);
	}

}
