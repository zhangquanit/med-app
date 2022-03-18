package com.medlinker.reactnative.codepush.util;

import android.content.Context;

import com.medlinker.reactnative.codepush.RNCodePushInvalidUpdateException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author hmy
 * @time 2019-10-21 16:12
 */
public class RNCodePushUpdateUtils {

    public static void verifyHash(String filePath, String expectedHash) throws FileNotFoundException {
        String hash = getFileHash(filePath);
        if (!hash.equals(expectedHash)) {
            throw new RNCodePushInvalidUpdateException("The update contents failed the data integrity check.");
        }
    }

    private static String getFileHash(String filePath) throws FileNotFoundException {
        byte[] hashBytes = SHA(new FileInputStream(filePath));
        return byteToHex(hashBytes);
    }

    public static String getAssetsFileHash(Context context, String assetsFilePath) throws IOException {
        byte[] hashBytes = SHA(new BufferedInputStream(context.getAssets().open(assetsFilePath)));
        return byteToHex(hashBytes);
    }

    /***
     * 字符串 SHA 加密
     *
     * @return
     */
    private static byte[] SHA(InputStream dataStream) {
        MessageDigest messageDigest = null;
        DigestInputStream digestInputStream = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            digestInputStream = new DigestInputStream(dataStream, messageDigest);
            byte[] byteBuffer = new byte[1024 * 8];
            while (digestInputStream.read(byteBuffer) != -1) ;
        } catch (NoSuchAlgorithmException | IOException e) {
            // Should not happen.
            throw new RNCodePushInvalidUpdateException(e);
        } finally {
            try {
                if (digestInputStream != null) digestInputStream.close();
                if (dataStream != null) dataStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return messageDigest.digest();

    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byteToHex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //16进制数值长度为2,长度为1时补0
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}
