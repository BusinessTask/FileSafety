package com.snut.sm2AndSm4.utils.sm3;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import com.snut.sm2AndSm4.utils.Util;
import com.snut.sm2AndSm4.utils.sm2.SM2KeyVO;
import com.snut.sm2AndSm4.utils.smUtil.SecurityTestAll;
import com.snut.sm2AndSm4.utils.smUtil.sm4util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Arrays;

/**
 * SM3 工具类
 */
//两个方法，用于hash运算，以及hash值对比
public class sm3Utils{

    /**
     * 编码格式
     */
    private static final String ENCODING = "UTF-8";

    static{
        Security.addProvider(new BouncyCastleProvider());
    }

  //将file转化成string
    private static String file2String(String sourcePath) throws IOException {
        //对一串字符进行操作
        StringBuffer fileData = new StringBuffer();
        //
        BufferedReader reader = new BufferedReader(new FileReader(sourcePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        //缓冲区使用完必须关掉
        reader.close();
        return fileData.toString();
    }

    //hash运算，私钥加密
    public static String main12( String[] s, File sourcePath ,String result) throws Exception {
       result= sm4util.file2String(sourcePath);
        String encrypt=sm3Utils.encrypt(result);
   	 new SM2KeyVO().getPriHexInSoft();
   	 SM2KeyVO sm2KeyVO = SecurityTestAll.generateSM2Key();
   	 Util.byteToHex(encrypt.getBytes());
   	 String a = SecurityTestAll.SM2Dec(sm2KeyVO.getPriHexInSoft(), encrypt );
   	 return a;
    }
    /**
     *
     * @param paramStr 要sm3运算的内容
     * @return 摘要值
     */
    public static String encrypt(String paramStr){
        String resultHexString = "";
        try {
            byte[] srcData = paramStr.getBytes(ENCODING);
            byte[] hash = hash(srcData);
            resultHexString = ByteUtils.toHexString(hash);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resultHexString;
    }

    public static byte[] hash(byte[] srcData){
        SM3Digest sm3Digest = new SM3Digest();
        sm3Digest.update(srcData,0,srcData.length);
        byte[] bytes = new byte[sm3Digest.getDigestSize()];
        sm3Digest.doFinal(bytes,0);
        return bytes;
    }

    /**
     *
     * @param str 明文
     * @param hexString 密文
     * @return 明文密文对比结果
     */
    
    public static boolean main( String[] args, String a ,String arcs) throws Exception {
   	 new SM2KeyVO().getPubHexInSoft();
   	 SM2KeyVO sm2KeyVO = SecurityTestAll.generateSM2Key();
   	 String b = SecurityTestAll.SM2Dec(sm2KeyVO.getPubHexInSoft(), a);//用私钥解密
   	 boolean verify = sm3Utils.verify(b, arcs);//调用verify方法进行验证
   	 return verify; 	
    }
    
    public static boolean verify(String str,String hexString){
        boolean flag = false;
        try {
            byte[] srcData = str.getBytes(ENCODING);
            byte[] sm3Hash = ByteUtils.fromHexString(hexString);
            byte[] hash = hash(srcData);
            if (Arrays.equals(hash,sm3Hash)){
                flag = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return flag;
    }


public static void main(String[] args) {
    String str = "qaz5tgb&^$>:{*&";
    String encrypt = sm3Utils.encrypt(str);
    System.out.println("签名 ："+encrypt);

    boolean verify = sm3Utils.verify(str, encrypt);
    System.out.println("验签结果 ：" + verify);
}
}