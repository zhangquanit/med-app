package com.medlinker.photoviewer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Hashtable;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 1.0
 * @description 功能描述
 * @time 2016/4/14 15:11
 */
public class ScanImageUtils {

    /**
     * 通过长按压图片识别二维码
     *
     * @param view
     * @return
     */
    public static Result decodeByView(View view) {
        //打开view缓存绘制
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache();
        return decode(bitmap, view);
    }

    /**
     * 通过图片路径解析
     *
     * @param path
     * @return
     */
    public static Result decodeByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        //生成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 先获取原大小
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        // 获取新的大小
        options.inJustDecodeBounds = false;
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;
        bitmap = BitmapFactory.decodeFile(path, options);
        //获取result
        return decode(bitmap, null);
    }

    /**
     * 识别二维码
     *
     * @param bitmap
     * @return
     */
    private static Result decode(Bitmap bitmap, View view) {
        if (bitmap == null) {
            return null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        // 设置二维码内容的编码
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        //生成可以解析的BinaryBitmap
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        //read
        QRCodeReader reader = new QRCodeReader();
        //关闭图层缓存
        if (view != null) {
            view.setDrawingCacheEnabled(false);
        }
        try {
            return reader.decode(binaryBitmap, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
