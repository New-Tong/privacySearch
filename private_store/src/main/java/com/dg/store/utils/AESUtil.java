package com.dg.store.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    // 生成AES密钥
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // 选择密钥强度，可以是128, 192或256位
        return keyGenerator.generateKey();
    }

    // AES加密
    public static String encrypt(String data, String key) throws Exception {
        // 检查数据是否为空
        if (data == null || data.isEmpty()) {
            return data; // 直接返回空数据或抛出异常均可，根据你的业务逻辑决定
        }

        byte[] keyBytes = Base64.getDecoder().decode(key);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // AES解密
    public static String decrypt(String encryptedData, String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    // 将密钥转换为字符串，以便存储或传输
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
