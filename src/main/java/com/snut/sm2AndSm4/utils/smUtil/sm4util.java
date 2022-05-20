package com.snut.sm2AndSm4.utils.smUtil;

import com.snut.sm2AndSm4.utils.Util;
import com.snut.sm2AndSm4.utils.sm2.SM2KeyVO;
import com.snut.sm2AndSm4.utils.sm3.sm3Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.UUID;

public class sm4util {

    public static final String ALGORITHM_NAME = "SM4";

    public static final int KEY_SIZE = 128;

	private static final String SM2Enc = null;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // 生成 Cipher
    public static Cipher generateCipher(int mode, byte[] keyData)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(keyData, "SM4");
        cipher.init(mode, sm4Key);
        return cipher;
    }
    //生成对称密钥
    public static String generateSM4Key() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    //将file转化成string
    public static String file2String(File sourcePath) throws IOException {
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
    * @param args 
    * @param b 私钥解密得到hash
    * @param arcs 原文进行hash
 * @return 
    * @throws Exception
    */
    
//验证签名
    public static boolean main15( String[] args, String a ,String arcs) throws Exception {
    	 new SM2KeyVO().getPubHexInSoft();
    	 SM2KeyVO sm2KeyVO = SecurityTestAll.generateSM2Key();
    	 String b = SecurityTestAll.SM2Dec(sm2KeyVO.getPubHexInSoft(), a);//用私钥解密
    	 boolean verify = sm3Utils.verify(b, arcs);//调用sm3Utils中的verify方法进行验证
    	 return verify; 	
    	
    }


}
