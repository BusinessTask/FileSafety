package com.snut.sm2AndSm4.service;


import com.snut.sm2AndSm4.utils.Util;
import com.snut.sm2AndSm4.utils.sm2.SM2KeyVO;
import com.snut.sm2AndSm4.utils.sm2.SM2SignVO;
import com.snut.sm2AndSm4.utils.smUtil.SM4StringUtils;
import com.snut.sm2AndSm4.utils.smUtil.SM4Utils;
import com.snut.sm2AndSm4.utils.smUtil.SecurityTestAll;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SmService {
//sm2String
    public Map<String, Object> sm2StringSymmetry(Map<String, Object> map) throws Exception{
        Map<String, Object> returnMap = new HashMap<>();
        String str = map.get("str") + "";
        returnMap.put("str", str);
        // 如果没公钥是加密
        if(map.get("gong") == null){
            // 产生SM2密钥
            SM2KeyVO sm2KeyVO = SecurityTestAll.generateSM2Key();
            returnMap.put("str", str);
            returnMap.put("gong", sm2KeyVO.getPubHexInSoft());
            returnMap.put("si", sm2KeyVO.getPriHexInSoft());
            returnMap.put("newStr", SecurityTestAll.SM2Enc(sm2KeyVO.getPubHexInSoft(), str));
        } else {
            returnMap = map;
            returnMap.put("newStr", SecurityTestAll.SM2Dec(map.get("si") + "", str));
        }
        return returnMap;
    }
//sm2签名
    public Map<String, Object> sm2StringAsymmetric(Map<String, Object> map) throws Exception{
        Map<String, Object> returnMap = new HashMap<>();
        String str = map.get("str") + "";
        returnMap.put("str", str);
        // 如果没公钥是加密
        if(map.get("gong") == null){
            // 产生SM2密钥
            SM2KeyVO sm2KeyVO = SecurityTestAll.generateSM2Key();
            returnMap.put("str", str);
            returnMap.put("gong", sm2KeyVO.getPubHexInSoft());
            returnMap.put("si", sm2KeyVO.getPriHexInSoft());
            SM2SignVO sign = SecurityTestAll.genSM2Signature(sm2KeyVO.getPriHexInSoft(), str);
            // 软加密签名结果
            returnMap.put("ruan", sign.getSm2_signForSoft());
            returnMap.put("jia", sign.getSm2_signForHard());
            returnMap.put("newStrRuan", null);
            returnMap.put("newStrYing", null);
        } else {
            returnMap = map;
            try{
                if(SecurityTestAll.verifySM2Signature(map.get("gong") + "", str, map.get("ruan") + "")){
                    returnMap.put("newStrRuan", "成功");
                } else {
                    returnMap.put("newStrRuan", "失败");
                }
                if(SecurityTestAll.verifySM2Signature(map.get("gong") + "", str, SecurityTestAll.SM2SignHardToSoft(map.get("jia") + ""))){
                    returnMap.put("newStrYing", "成功");
                } else {
                    returnMap.put("newStrYing", "失败");
                }
            } catch (Exception e){
                returnMap.put("newStrRuan", "失败");
                returnMap.put("newStrYing", "失败");
            }

        }
        return returnMap;
    }

    public Map<String, Object> sm4StringECB(Map<String, Object> map) throws Exception{
        Map<String, Object> returnMap = new HashMap<>();
        String str = map.get("str") + "";
        returnMap.put("str", str);
        SM4StringUtils sm4 = new SM4StringUtils();
        sm4.hexString = true;
        if(map.get("secretKey") == null){
            String secretKey = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            sm4.secretKey = secretKey;
            returnMap.put("secretKey", secretKey);
            returnMap.put("newStr", sm4.encryptData_ECB(str));
        } else {
            returnMap = map;
            sm4.secretKey = map.get("secretKey") + "";
            returnMap.put("newStr", sm4.decryptData_ECB(str));
        }
        return returnMap;
    }

    public Map<String, Object> sm4StringCBC(Map<String, Object> map) throws Exception{
        Map<String, Object> returnMap = new HashMap<>();
        String str = map.get("str") + "";
        returnMap.put("str", str);
        SM4StringUtils sm4 = new SM4StringUtils();
        sm4.hexString = true;
        if(map.get("secretKey") == null){
            String secretKey = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            String iv = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            sm4.secretKey = secretKey;
            sm4.iv = iv;
            returnMap.put("secretKey", secretKey);
            returnMap.put("iv", iv);
            returnMap.put("newStr", sm4.encryptData_CBC(str));
        } else {
            returnMap = map;
            sm4.secretKey = map.get("secretKey") + "";
            sm4.iv = map.get("iv") + "";
            returnMap.put("newStr", sm4.decryptData_CBC(str));
        }
        return returnMap;
    }

    public Map<String, Object> sm4(Map<String, Object> map) throws Exception{
        Map<String, Object> returnMap = new HashMap<>();
        if(map.get("key") == null){
            returnMap = map;
            String key = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(16);
            byte[] keyData = key.getBytes();
            returnMap.put("key", key);
            SM4Utils.encryptFile(keyData, map.get("oldFile") + "", map.get("newFile") + "");
        } else {
            returnMap = map;
            byte[] keyData = (map.get("key") + "").getBytes();
            SM4Utils.decryptFile(keyData, map.get("oldFile") + "", map.get("newFile") + "");
        }
        return returnMap;
    }

}