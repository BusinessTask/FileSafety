package com.snut.sm2AndSm4.utils.sm4;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;

/**
 * 国密SM4 对称加密算法 工具
 * 此算法是一个分组算法，
 * 该算法的分组长度为128比特，密钥长度为128比特。加密算法与密钥扩展算法都采用32轮非线性迭代结构。
 * 解密算法与加密算法的结构相同，只是轮密钥的使用顺序相反，解密轮密钥是加密轮密钥的逆序
 */
public class SM4Utils {


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ALGORITHM_NAME = "SM4";
    private static final String ALGORITHM_NAME_CBC_PKCS7_PADDING = "SM4/CBC/PKCS7Padding";
    private static final String IV = "pay-brand-open.1";

    /**
     * CBC模式-加密
     *
     * @param key  秘钥 Hex
     * @param data 报文
     * @return base64String
     */
    public static String encrypt(String key, String data) {
        byte[] keyBytes = paddingKey(key.getBytes(StandardCharsets.UTF_8), 16);
        return Hex.toHexString(encryptCbcPadding(keyBytes,
                data.getBytes(StandardCharsets.UTF_8), ALGORITHM_NAME_CBC_PKCS7_PADDING));
    }

    /**
     * CBC模式-解密
     *
     * @param key         秘钥 Hex
     * @param encryptData 密文
     * @return
     */
    public static String decrypt(String key, String encryptData) {
        byte[] keyBytes = paddingKey(key.getBytes(StandardCharsets.UTF_8), 16);
        return new String(decryptCbcPadding(keyBytes, Hex.decode(encryptData.getBytes(StandardCharsets.UTF_8)),
                ALGORITHM_NAME_CBC_PKCS7_PADDING), StandardCharsets.UTF_8);
    }

    /**
     * CBC模式-加密
     *
     * @param key           秘钥 Hex
     * @param data          报文
     * @param algorithmName 填充方式
     * @return
     */
    private static byte[] encryptCbcPadding(byte[] key, byte[] data, String algorithmName) {
        try {
            Cipher cipher = generateCbcCipher(algorithmName, Cipher.ENCRYPT_MODE, key, IV.getBytes());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("sm4 encrypt cbc error", e);
        }
    }

    /**
     * CBC模式-解密
     *
     * @param key           秘钥 byte[]
     * @param encryptData   密文
     * @param algorithmName 填充方式
     * @return
     */
    private static byte[] decryptCbcPadding(byte[] key, byte[] encryptData, String algorithmName) {
        try {
            Cipher cipher = generateCbcCipher(algorithmName, Cipher.DECRYPT_MODE, key, IV.getBytes());
            return cipher.doFinal(encryptData);
        } catch (Exception e) {
            throw new RuntimeException("sm4 decrypt cbc error", e);
        }
    }

    /**
     * @param algorithmName 算法名称
     * @param mode          算法模式
     * @param key           密钥
     * @param iv            扰动参数
     * @return
     */
    private static Cipher generateCbcCipher(String algorithmName, int mode, byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
            Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(mode, sm4Key, ivParameterSpec);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException("sm4 generate cbc cipher error", e);
        }
    }


    /**
     * 处理key的长度为指定byte长度
     *
     * @param key 密钥
     * @param bit 位数
     * @return
     */
    private static byte[] paddingKey(byte[] key, int bit) {
        byte[] padded = new byte[bit];
        if (key.length < bit) {
            System.arraycopy(key, 0, padded, 0, key.length);
        } else if (key.length > bit) {
            System.arraycopy(key, 0, padded, 0, bit);
        }
        return padded;
    }
}
