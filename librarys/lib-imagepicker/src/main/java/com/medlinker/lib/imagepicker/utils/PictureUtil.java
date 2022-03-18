package com.medlinker.lib.imagepicker.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cn.glidelib.glide.GlideApp;
import com.medlinker.lib.imagepicker.R;
import com.medlinker.lib.utils.MedPictureUtil;
import com.medlinker.lib.utils.MedToastUtil;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述 对图片进行压缩处理
 * @time 2015/11/2 11:10
 */
public class PictureUtil {

    public static void downLoadImg(final Context context, final String url, final boolean isShowToast) {
        if (url.startsWith("http:") || url.startsWith("https:")) {
            GlideApp.with(context)
                    .downloadOnly()
                    .load(url)
                    .into(new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(File resource, Transition<? super File> transition) {
                            if (resource != null && resource.exists()) {
                                String fileName = UUID.randomUUID().toString() + ".jpg";
                                String localPath = getPictureFilePath() + fileName;
                                File dstFile = new File(localPath);
                                if (!dstFile.exists()) {
                                    dstFile.getParentFile().mkdirs();
                                    try {
                                        dstFile.createNewFile();
                                        FileUtil.copyFile(resource, dstFile);
                                        String path = getPictureFilePath() + "/" + fileName;
                                        MedPictureUtil.sendBraodCastToGallary(context, path);
                                        if (isShowToast)
                                            MedToastUtil.showMessage(context, context.getString(R.string.pick_image_save_addr) + FileUtil.Constants.DEFAULT_IMAGES_DIR + "/" + fileName);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            if (isShowToast) {
                                MedToastUtil.showMessage(context, R.string.network_failed_please_retry);
                            }
                        }
                    });
        } else {
            if (isShowToast) {
                MedToastUtil.showMessage(context, context.getString(R.string.pick_image_save_already_exist) + url);
            }
        }
    }


    /**
     * 图片保存路径.
     */
    public static String getPictureFilePath() {
        return getPictureFile().getPath() + File.separator;
    }

    /**
     * 图片保存
     */
    public static File getPictureFile() {
//        File dir = new File(ROOT_PATH);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        return dir;
        return FileUtil.getImagesDir();
    }

    /**
     * 临时文件夹路径
     *
     * @return
     */
    public static String getTempFilePath() {
        return getTempFile().getPath() + File.separator;
    }

    /**
     * 临时文件
     *
     * @return
     */
    public static File getTempFile() {
        return FileUtil.getTempDir();
    }

}
