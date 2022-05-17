package com.snut.sm2AndSm4.utils.smUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;

public class SM4Utils {

    public static final String ALGORITHM_NAME = "SM4";

    public static final int KEY_SIZE = 128;

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

    /**
     * 加密文件
     *
     * @param keyData
     * @param sourcePath
     * @param targetPath
     */
    public static void encryptFile(byte[] keyData, String sourcePath, String targetPath) {
        try {
            Cipher cipher = generateCipher(Cipher.ENCRYPT_MODE, keyData);
            CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(sourcePath), cipher);
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
    }

    /**
     * 解密文件
     *
     * @param sourcePath 待解密的文件路径
     * @param targetPath 解密后的文件路径
     */
    public static void decryptFile(byte[] keyData, String sourcePath, String targetPath) {
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
            out = new FileOutputStream(targetFile);
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
