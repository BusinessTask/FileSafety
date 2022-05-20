package com.snut.sm2AndSm4.utils.sm2;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SM2Utils {


	public static AsymmetricCipherKeyPair generateKey() throws NoSuchAlgorithmException {
        //获取一条SM2曲线参数
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        //构造domain参数
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(), sm2ECParameters.getG(), sm2ECParameters.getN());
        //1.创建密钥生成器
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
        //2.初始化生成器,带上随机数
        keyPairGenerator.init(new ECKeyGenerationParameters(domainParameters, SecureRandom.getInstance("SHA1PRNG")));
        //3.生成密钥对
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
        return asymmetricCipherKeyPair;
    }
	
    /**
         * 生成公钥
     *
     * @param
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public static String getPublicKey(AsymmetricCipherKeyPair asymmetricCipherKeyPair) throws NoSuchAlgorithmException {
        //提取公钥点
        ECPoint ecPoint = ((ECPublicKeyParameters) asymmetricCipherKeyPair.getPublic()).getQ();
        //公钥前面的02或者03表示是压缩公钥,04表示未压缩公钥,04的时候,可以去掉前面的04
        byte[] pubbyte = ecPoint.getEncoded(false);
        String pubKey = Hex.toHexString(pubbyte);
        return pubKey.toUpperCase();
    }
    
    /**
     * 生成私钥
     *
     * @param
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public static String getPrivateKey(AsymmetricCipherKeyPair asymmetricCipherKeyPair) throws NoSuchAlgorithmException {
    	BigInteger privatekey = ((ECPrivateKeyParameters) asymmetricCipherKeyPair.getPrivate()).getD();
    	String priKey = privatekey.toString(16);
    	return priKey.toUpperCase();
    }

    /**
     * @param pubKey 16进制字符串-SM2公钥
     * @param data   明文
     * @return HexString
     * @throws Exception 
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(String pubKey, String data) {
    	return encodeSM2CipherTextToShamDER(encrypt(pubKey, data.getBytes(StandardCharsets.UTF_8))).toUpperCase();
    }

    /**
     * @param pubKey   16进制字符串-SM2公钥
     * @param dataByte 明文
     * @return C1||C3||C2新标准输出格式
     * @throws UnsupportedEncodingException
     */
    private static byte[] encrypt(String pubKey, byte[] dataByte) {
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(), sm2ECParameters.getG(), sm2ECParameters.getN());
        ECPoint ecPoint = sm2ECParameters.getCurve().decodePoint(Hex.decode(pubKey));
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(ecPoint, domainParameters);
        SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C2C3);
        sm2Engine.init(true, new ParametersWithRandom(publicKeyParameters));
        byte[] encrypteData = null;
        try {
            encrypteData = sm2Engine.processBlock(dataByte, 0, dataByte.length);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException("SM2---encrypt failed", e);
        }
        return encrypteData;
    }
    
    /**
     * @param priKey      16进制字符串-SM2私钥
     * @param encryptData 16进制字符串-密文
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(String priKey, String encryptData) {
        return new String(decrypt(priKey, Hex.decode(decodeDERToSM2CipherShamText(Hex.decode(encryptData)))));
    }


    /**
     * @param priKey      16进制字符串-SM2私钥
     * @param encryptData 16进制字符串-密文
     * @return C1||C3||C2新标准输出格式
     * @throws UnsupportedEncodingException
     */
    private static byte[] decrypt(String priKey, byte[] encryptData) {
        X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        ECDomainParameters domainParameters = new ECDomainParameters(sm2ECParameters.getCurve(), sm2ECParameters.getG(), sm2ECParameters.getN());
        BigInteger privateKey = new BigInteger(priKey, 16);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKey, domainParameters);

        SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C2C3);
        sm2Engine.init(false, privateKeyParameters);
        byte[] decrypteData = null;
        try {
            decrypteData = sm2Engine.processBlock(encryptData, 0, encryptData.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SM2---decrypt failed", e);
        }
        return decrypteData;
    }

    public static String encodeSM2CipherTextToShamDER(byte[] encryptByte) {
        String data = Hex.toHexString(encryptByte);
        String der = "30";
        String encryptStr = der + data.substring(2, data.length());
        return encryptStr;
    }


    /**
     * 将密文转成ASN.1输出
     * Sequence{
     * X           INTERAGE(32) --分量
     * Y           INTERAGE(32)--分量
     * HASH        String(32) 杂凑值,使用SM3算法对明文数据运算得到的杂凑值。
     * cipherText  String --密文 ,是和明文长度相等的密文。
     * <p>
     * }
     *
     * @param cipher 密文
     * @return DER_TYPE_SEQUENCE   0x30
     * 点超过整形最大值时候 有BUB  暂时无好的解决方法。 解决伪造报文
     * @throws IOException
     */
    public static byte[] encodeSM2CipherTextToDER(byte[] cipher)
            throws IOException {
        int startPos = 1;
        int curveLength = 32;
        int sm3Size = 32;
        //ECC 256位
        //X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
        byte[] x = new byte[curveLength];
        System.arraycopy(cipher, startPos, x, 0, x.length);
        startPos += x.length;

        byte[] y = new byte[curveLength];
        System.arraycopy(cipher, startPos, y, 0, y.length);
        startPos += y.length;

        byte[] sm3 = new byte[sm3Size];
        System.arraycopy(cipher, startPos, sm3, 0, sm3.length);
        startPos += sm3.length;

        byte[] cipherText = new byte[cipher.length - x.length - y.length - 1 - sm3Size];
        System.arraycopy(cipher, startPos, cipherText, 0, cipherText.length);

        ASN1Encodable[] arr = new ASN1Encodable[4];
        arr[0] = new ASN1Integer(x);
        arr[1] = new ASN1Integer(y);
        arr[2] = new DEROctetString(sm3);
        arr[3] = new DEROctetString(cipherText);
        DERSequence ds = new DERSequence(arr);
        return ds.getEncoded(ASN1Encoding.DER);
    }

    public static String decodeDERToSM2CipherShamText(byte[] encryptByte) {
        String data = Hex.toHexString(encryptByte);
        String der = "04";
        String encryptStr = der + data.substring(2, data.length());
        return encryptStr;
    }


    /**
     *
     * @param derCipher
     * @return
     */
    public static byte[] decodeDERToSM2CipherText(byte[] derCipher) {
        ASN1Sequence as = DERSequence.getInstance(derCipher);
        byte[] x = ((ASN1Integer) as.getObjectAt(0)).getValue().toByteArray();
        byte[] y = ((ASN1Integer) as.getObjectAt(1)).getValue().toByteArray();
        byte[] sm3 = ((DEROctetString) as.getObjectAt(2)).getOctets();
        byte[] text = ((DEROctetString) as.getObjectAt(3)).getOctets();
        int pos = 0;
        byte[] cipherText = new byte[1 + x.length + x.length + sm3.length + text.length];

        final byte uncompressedFlag = 0x04;
        cipherText[0] = uncompressedFlag;
        pos += 1;
        System.arraycopy(x, 0, cipherText, pos, x.length);
        pos += x.length;
        System.arraycopy(y, 0, cipherText, pos, y.length);
        pos += y.length;
        System.arraycopy(sm3, 0, cipherText, pos, sm3.length);
        pos += sm3.length;
        System.arraycopy(text, 0, cipherText, pos, text.length);
        return cipherText;
    }
}
