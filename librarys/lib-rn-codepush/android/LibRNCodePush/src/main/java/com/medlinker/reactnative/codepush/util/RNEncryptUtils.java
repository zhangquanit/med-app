package com.medlinker.reactnative.codepush.util;

import android.content.Context;
import android.text.TextUtils;

import com.medlinker.reactnative.codepush.RNCodePush;
import com.medlinker.reactnative.codepush.RNCodePushConstants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.security.Key;

/**
 * @author hmy
 * @time 2020-02-05 17:56
 */
public class RNEncryptUtils {

    public static synchronized void initEncryptFiles(Context context) {

        //是否对文件解密
        boolean isDecrypt = false;

        //获取上次缓存的解密的目标jsBundle文件的hash
        String cacheBaseMetaHash = RNCodePush.getInstance().getSettingsManager().getBaseMetaHash();
        String rootPath = FileUtils.appendPathComponent(context.getFilesDir().getAbsolutePath(),
                RNCodePushConstants.BASE_BUNDLE_FOLDER);
        String assetsMetaHash = null;
        try {
            assetsMetaHash = RNCodePushUpdateUtils.getAssetsFileHash(context, RNCodePushConstants.STATUS_FILE_NAME);
        } catch (IOException e) {
            RNCodePush.getRnCodePushLog().setAssetsFileHash(e.getMessage());
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(cacheBaseMetaHash)) {
            File rootFile = new File(rootPath);
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }

            File[] files = rootFile.listFiles();
            //如果jsBundle、manifest、meta文件都有，代表已经解密
            if (files != null && files.length == 3) {
                // assets中的meta文件的hash与缓存的hash一样，代表已经解密
                if (TextUtils.equals(assetsMetaHash, cacheBaseMetaHash)) {
                    isDecrypt = true;
                }
            }
        }


        if (!isDecrypt) {
            FileUtils.deleteDirectoryAtPath(rootPath);
            try {
                File rootFile = new File(rootPath);
                if (!rootFile.exists()) {
                    rootFile.mkdirs();
                }
                RNCodePush.getRnCodePushLog().setHasStartDecrypt(true);
                Key key = DesEncryptUtil.getKey("medrn1580977272874");
                DesEncryptUtil.decryptFile(getAssetsFileIS(context, RNCodePushConstants.STATUS_FILE_NAME),
                        new File(FileUtils.appendPathComponent(rootPath, RNCodePushConstants.STATUS_FILE_NAME)),
                        key);
                DesEncryptUtil.decryptFile(getAssetsFileIS(context, RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME),
                        new File(FileUtils.appendPathComponent(rootPath, RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME)),
                        key);
                DesEncryptUtil.decryptFile(getAssetsFileIS(context, RNCodePushConstants.DEFAULT_MANIFEST_FILE_NAME),
                        new File(FileUtils.appendPathComponent(rootPath, RNCodePushConstants.DEFAULT_MANIFEST_FILE_NAME)),
                        key);
                RNCodePush.getRnCodePushLog().setDecryptSuccess(true);
                RNCodePush.getInstance().getSettingsManager().saveBaseMetaHash(assetsMetaHash);
                RNCodePush.getInstance().clearUpdates();
            } catch (Exception e) {
                RNCodePush.getRnCodePushLog().setDecryptError(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static BufferedInputStream getAssetsFileIS(Context ctx, String assetsFilePath) throws IOException {
        return new BufferedInputStream(ctx.getAssets().open(assetsFilePath));
    }

}
