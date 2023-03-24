package com.ddy.httplib;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by xuwei on 2019/6/26.
 */
public class EncryptUtils {
    static AlgorithmParameterSpec iv = null;// 加密算法的参数接口，IvParameterSpec是它的一个实例
    private static Key key = null;

    /*DES加密*/
    private static final byte[] CloudHookDESkey = {66, 28, 59, 34, 83, 16, 116, 12};
    private static final byte[] CloudHookDESIV = {85, 60, 5, 26, 121, 68, 19, 73};

    private static final String[] KeyArray = {
            "3949729b-9a58-4ffc-b720-770f67212d1c",
            "2d6aa33c-9638-42ab-87fc-78ae6d8f923c",
            "9628a2e0-9bda-4c0b-b2fd-902063900474",
            "0b2ce052-5930-42bc-a2d2-7e0e20aeeb35",
            "26c0da7d-9fe5-4236-8936-f0229dc63dc4",
            "db36880a-a810-46eb-9c73-603f1e6ce686",
            "626dfc8e-7d53-4532-a85d-6ef09882a49d",
            "c4de589e-a409-46fe-bbee-60e0d3e5d7dd"
    };

    public static String encode(String data) throws Exception {
        return encode(data, CloudHookDESkey, CloudHookDESIV);
    }
    /**
     * 加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String encode(String data, byte[] DESkey, byte[] DESIV) throws Exception {
        init(DESkey, DESIV);
        Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher
        enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
        byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
        return Base64.encodeToString(pasByte, Base64.NO_WRAP);
    }

    private static void init(byte[] DESkey, byte[] DESIV) {
        DESKeySpec keySpec;
        try {
            keySpec = new DESKeySpec(DESkey);// 设置密钥参数
            iv = new IvParameterSpec(DESIV);// 设置向量
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
            key = keyFactory.generateSecret(keySpec);// 得到密钥对象
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public static String decode(String data) throws Exception {
        return decode(data, CloudHookDESkey, CloudHookDESIV);
    }
    /**
     * 解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String decode(String data, byte[] DESkey, byte[] DESIV) throws Exception {
        init(DESkey, DESIV);
        Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        deCipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] pasByte = deCipher.doFinal(Base64.decode(data, Base64.DEFAULT));
        return new String(pasByte, "UTF-8");
    }

    public static String sign(String data, int index) {
        if (index < 0 || index >= KeyArray.length) return "";
        return encryptMD5ToString(data + KeyArray[index]);
    }

    /**
     * Return the hex string of MD5 encryption.
     *
     * @param data The data.
     * @return the hex string of MD5 encryption
     */
    public static String encryptMD5ToString(final String data) {
        if (data == null || data.length() == 0) return "";
        return encryptMD5ToString(data.getBytes());
    }

    /**
     * Return the hex string of MD5 encryption.
     *
     * @param data The data.
     * @return the hex string of MD5 encryption
     */
    public static String encryptMD5ToString(final byte[] data) {
        return bytes2HexString(encryptMD5(data));
    }

    /**
     * Return the bytes of MD5 encryption.
     *
     * @param data The data.
     * @return the bytes of MD5 encryption
     */
    public static byte[] encryptMD5(final byte[] data) {
        return hashTemplate(data, "MD5");
    }

    /**
     * Return the bytes of hash encryption.
     *
     * @param data      The data.
     * @param algorithm The name of hash encryption.
     * @return the bytes of hash encryption
     */
    private static byte[] hashTemplate(final byte[] data, final String algorithm) {
        if (data == null || data.length <= 0) return null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final char HEX_DIGITS[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) return "";
        int len = bytes.length;
        if (len <= 0) return "";
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = HEX_DIGITS[bytes[i] >> 4 & 0x0f];
            ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
        }
        return new String(ret);
    }
}
