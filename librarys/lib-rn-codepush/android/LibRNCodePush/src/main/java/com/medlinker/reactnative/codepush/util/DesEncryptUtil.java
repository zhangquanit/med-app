package com.medlinker.reactnative.codepush.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author hmy
 * @time 2020-02-05 18:53
 */
public class DesEncryptUtil {

    /**
     * DES加密文件
     *
     * @param file 明文文件
     * @param dest 加密后的密文文件
     * @param key  密钥
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static void encryptFile(File file, File dest, Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(dest);
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
        }
        cis.close();
        is.close();
        out.close();
    }

    /**
     * DES解密文件
     *
     * @param file 需要解密的密文文件
     * @param dest 解密出的明文文件
     * @param key  密钥
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static void decryptFile(File file, File dest, Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IOException {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(dest);
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
        }
        cis.close();
        is.close();
        out.close();
    }

    /**
     * DES解密文件
     *
     * @param fileIS 需要解密的密文文件
     * @param dest   解密出的明文文件
     * @param key    密钥
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static void decryptFile(InputStream fileIS, File dest, Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IOException {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        OutputStream out = new FileOutputStream(dest);
        CipherInputStream cis = new CipherInputStream(fileIS, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
        }
        cis.close();
        fileIS.close();
        out.close();
    }

    /**
     * 自定义一个key
     */
    public static Key getKey(String keyRule) {
        // Key key = null;
        byte[] keyByte = keyRule.getBytes();
        // 创建一个空的八位数组,默认情况下为0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        Key key = new SecretKeySpec(byteTemp, "DES");
        return key;
    }
}
