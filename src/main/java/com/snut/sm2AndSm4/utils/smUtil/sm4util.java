package com.snut.sm2AndSm4.utils.smUtil;

import com.snut.sm2AndSm4.utils.smUtil.*;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.snut.sm2AndSm4.utils.Util;
import com.snut.sm2AndSm4.utils.sm2.*;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.UUID;
import com.snut.sm2AndSm4.utils.sm3.*;

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
     * 加密文件
     *
     * @param keyData
     * @param sourcePath
     * @param targetPath
     *
     */
    public static void encryptFile(byte[] keyData, String sourcePath, String targetPath) throws IOException {
        try {
            Cipher cipher = generateCipher(Cipher.ENCRYPT_MODE, keyData);
            CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(sourcePath), cipher);//加密文件
            FileUtil.writeFromStream(cipherInputStream, targetPath);
            IoUtil.close(cipherInputStream);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //加密对称密钥
        String sm4Key = generateSM4Key();
   	 SM2KeyVO sm2KeyVO = SecurityTestAll.generateSM2Key();
   	new SM2KeyVO().getPubHexInSoft();
   	String SM2Enc = SecurityTestAll.SM2Enc(sm2KeyVO.getPubHexInSoft(), sm4Key); 
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
    
    /**
     * 解密文件
     *
     * @param sourcePath 待解密的文件路径
     * @param targetPath 解密后的文件路径
     * 
     */
   
	
    
    public static void decryptFile(byte[] keyData, String sourcePath, String targetPath) throws IOException  {
    	
    	//解密对称密钥
    	 SM2KeyVO sm2KeyVO = SecurityTestAll.generateSM2Key();
    	 	new SM2KeyVO().getPriHexInSoft();
    	 	String SM2Dec = SecurityTestAll.SM2Dec(sm2KeyVO.getPriHexInSoft(), SM2Enc); 
    	 	
        FileInputStream in = null;
        ByteArrayInputStream byteArrayInputStream = null;
        OutputStream out = null;
        CipherOutputStream cipherOutputStream = null;
        try {
            in = new FileInputStream(sourcePath);
            byte[] bytes = IoUtil.readBytes(in);
            byteArrayInputStream = IoUtil.toStream(bytes);

            Cipher cipher = generateCipher(Cipher.DECRYPT_MODE, keyData);

            // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
            File targetFile = new File(targetPath);
            // 保证这个文件的父文件夹必须要存在
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            targetFile.createNewFile();
            out = new FileOutputStream(targetFile);//
            cipherOutputStream = new CipherOutputStream(out, cipher);
            IoUtil.copy(byteArrayInputStream, cipherOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(cipherOutputStream);
            IoUtil.close(out);
            IoUtil.close(byteArrayInputStream);
            IoUtil.close(in);
        }
    }

    public static void main(String[] args) throws Exception {
        String key = "243B2708BED84D61"; // key
        byte[] keyData = key.getBytes();
        System.out.println(keyData);

        String sp = "D:\\11111zhidao\\sm2.zip";// 原始文件
        String dp = "D:\\11111zhidao\\sm5.zip";// 加密后文件
        String dp2 = "D:\\11111zhidao\\sm9.zip";// 解密后文件
        // 加密文件
        encryptFile(keyData, sp, dp);
        // 解密文件
        decryptFile(keyData, dp, dp2);
    }

}
