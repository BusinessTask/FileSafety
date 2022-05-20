package com.snut.sm2AndSm4.service;


import com.snut.sm2AndSm4.utils.sm2.SM2Utils;
import com.snut.sm2AndSm4.utils.sm3.sm3Utils;
import com.snut.sm2AndSm4.utils.sm4.SM4Utils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SmService {

    public Map<String, Object> sm4(Map<String, Object> map) throws Exception {
        Map<String, Object> res = new HashMap<>();
        String oldFile = String.valueOf(map.get("oldFile"));

        // 读取文件内容加密
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {

            reader = new BufferedReader(new FileReader(new File(oldFile)));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();

            if (sbf.length() <= 0) {
                throw new RuntimeException("文件内容为空");
            }

            Map<String, String> cache = CacheManager.getInstance();
            String privateKey = cache.get("privateKey");
            String sm4SecretKey = cache.get("sm4SecretKey");
            if (privateKey == null || sm4SecretKey == null ) {
                throw new RuntimeException("请重新生成公私钥");
            }

            String sm4ClearKey = SM2Utils.decrypt(privateKey, sm4SecretKey);

            String encrypt = SM4Utils.encrypt(sm4ClearKey, sbf.toString());

            // 生成SIGN
            String sign = sm3Utils.encrypt(encrypt);
            String newFileTemp = oldFile.substring(0, oldFile.lastIndexOf(".")) + "." + sign + ".txt";

            Path path = Paths.get(newFileTemp);
            // 使用newBufferedWriter创建文件并写文件
            // 这里使用了try-with-resources方法来关闭流，不用手动关闭
            try (BufferedWriter writer =
                         Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write(encrypt);
            }

            res.put("newFile", newFileTemp);
            return res;
        } catch (RuntimeException r) {
            throw r;
        } catch (Exception e) {
            throw e;
        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

    }

    public boolean checkSign(Map<String, Object> map) {
        String targetPath = map.get("oldFile") + "";
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(new File(targetPath)));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();


            String sign = targetPath.substring(targetPath.indexOf(".") + 1, targetPath.lastIndexOf("."));
            return sm3Utils.verify(sbf.toString(), sign);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成公私钥
     *
     * @return
     */
    public Map<String, Object> createSecretKey() throws NoSuchAlgorithmException {

        AsymmetricCipherKeyPair asymmetricCipherKeyPair = SM2Utils.generateKey();

        String publicKey = SM2Utils.getPublicKey(asymmetricCipherKeyPair);
        String privateKey = SM2Utils.getPrivateKey(asymmetricCipherKeyPair);

        Map<String, Object> res = new HashMap<>();
        res.put("publicKey", publicKey);
        res.put("privateKey", privateKey);

        Map<String, String> cache = CacheManager.getInstance();
        cache.put("publicKey", publicKey);
        cache.put("privateKey", privateKey);

        String sm4ClearKey = UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(16);
        String sm4SecretKey = SM2Utils.encrypt(publicKey, sm4ClearKey);
        res.put("sm4ClearKey", sm4ClearKey);
        res.put("sm4SecretKey", sm4SecretKey);
        cache.put("sm4SecretKey", sm4SecretKey);

        return res;
    }

    /**
     * 解密
     * @param map
     * @return
     */
    public Map<String, Object> sm4decrypt(Map<String, Object> map) throws IOException {

        Map<String, Object> res = new HashMap<>();
        String oldFile = String.valueOf(map.get("oldFile"));

        // 验证签名
        if(!checkSign(map)){
            throw new RuntimeException("签名错误");
        }

        // 读取文件内容加密
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {

            reader = new BufferedReader(new FileReader(new File(oldFile)));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();

            if (sbf.length() <= 0) {
                throw new RuntimeException("文件内容为空");
            }

            Map<String, String> cache = CacheManager.getInstance();
            String privateKey = cache.get("privateKey");
            String sm4SecretKey = cache.get("sm4SecretKey");
            if (privateKey == null || sm4SecretKey == null ) {
                throw new RuntimeException("请重新生成公私钥");
            }

            String sm4ClearKey = SM2Utils.decrypt(privateKey, sm4SecretKey);

            String encrypt = SM4Utils.decrypt(sm4ClearKey, sbf.toString());

            // 生成文件
            String newFileTemp = oldFile.substring(0, oldFile.indexOf(".")) +  System.currentTimeMillis() + ".txt";
            Path path = Paths.get(newFileTemp);
            // 使用newBufferedWriter创建文件并写文件
            // 这里使用了try-with-resources方法来关闭流，不用手动关闭
            try (BufferedWriter writer =
                         Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write(encrypt);
            }

            res.put("newFile", newFileTemp);
            return res;
        } catch (RuntimeException | IOException r) {
            throw r;
        }  finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }


    }

}
