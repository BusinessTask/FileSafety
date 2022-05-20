
package com.snut.sm2AndSm4.utils.smUtil;
import com.snut.sm2AndSm4.utils.smUtil.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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

public class SM4Utils {

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
   
    /**
     * 加密文件
     *
     * @param keyData
     * @param sourcePath
     * @param targetPath
     * @throws IOException 
     */



}
