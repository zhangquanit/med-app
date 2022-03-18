package com.medlinker.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.ConvertUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.UUID;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述 对图片进行压缩处理
 * @time 2015/11/2 11:10
 */
public class MedPictureUtil {

    public static final String LOCAL_FILE_NAME = "Medlinker";

    private static final String SPLASH_FILE_NAME = "splash.jpg";

    /**
     * 把bitmap转换成String
     *
     * @param filePath
     * @return
     */
    public static byte[] bitmapToByte(String filePath) {
        int or = readPictureDegree(filePath); // 旋转的度数
        Bitmap bm = getSmallBitmap(filePath);
        if (null == bm) {
            return null;
        }
        bm = rotateBitmapByDegree(or, bm);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }

    public static byte[] bitmapToByte(Bitmap bm) {
        if (null == bm) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }

    /**
     * 获取我们需要的整理过旋转角度的Uri
     *
     * @param activity 上下文环境
     * @param path     路径
     * @return 正常的Uri
     */
    public static Uri getRotatedUri(Activity activity, String path) {
        int degree = readPictureDegree(path);
        if (degree != 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Bitmap newBitmap = rotateBitmapByDegree(degree, bitmap);
            return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), newBitmap, null, null));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    /**
     * 把原图bitmap转换成byte
     *
     * @param filePath
     * @return
     */
    public static byte[] originalBitmapToByte(String filePath) {
        FileInputStream input = null;
        int or = readPictureDegree(filePath); // 旋转的度数
        try {
            input = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(input);
        if (or != 0) {
            bmp = rotateBitmapByDegree(or, bmp);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotateBitmapByDegree(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 获取缩放后的bitmap
     *
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    public static void getHW(String filePath, int[] xy) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        xy[0] = options.outWidth;
        xy[1] = options.outHeight;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    /**
     * 保存文件
     *
     * @param bitmap
     * @return
     */
    public static String saveImgToLocal(Bitmap bitmap, String filePath) {
        if (null == bitmap) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!file.canWrite()) {
            return null;
        }
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 把一个bitmap写入到本地sdcard临时文件夹.
     *
     * @param bitmap
     * @param tmpDir
     */
    public static String saveImgToTemp(Bitmap bitmap, String tmpDir) {
        if (null == bitmap) {
            return null;
        }
        String fileName = UUID.randomUUID().toString().concat(".jpg");
        if (!tmpDir.endsWith("/")) {
            fileName = "/" + fileName;
        }

        String localPath = tmpDir + fileName;
        return saveImgToLocal(bitmap, localPath);
    }


    /**
     * 把一个bitmap写入到本地sdcard.
     *
     * @param bitmap
     */
    public static String saveImgToLocal(Context context, Bitmap bitmap, String saveDir) {
        if (null == bitmap) {
            return null;
        }
        String fileName = UUID.randomUUID().toString() + ".jpg";
        if (!saveDir.endsWith("/")) {
            fileName = "/" + fileName;
        }
        String localPath = saveDir + fileName;
        File file = new File(localPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
            sendImageToAlbum(context, file, fileName);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 把图片文件插入到系统图库
     *
     * @param context
     * @param imageFile
     * @param fileName
     */
    public static void sendImageToAlbum(Context context, File imageFile, String fileName) {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    imageFile.getAbsolutePath(), fileName, null);
            //通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(context, MedAppInfo.getApplicationId().concat(".provider"), imageFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
            } else {
                intent.setData(Uri.fromFile(imageFile));
            }
            context.sendBroadcast(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveImgToLocal(final Context context, final View v, final boolean isShowToast, String toastMsg,  String saveDir) {
        if (v == null || v.getWidth() <= 0 || v.getHeight() <= 0) {
            return;
        }

        MedAsyncJobUtil.doInBackground(new MedAsyncJobUtil.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                v.setDrawingCacheEnabled(true);
                Bitmap bitmap = v.getDrawingCache();

                final String fileName = MedPictureUtil.saveImgToLocal(context, bitmap, saveDir);
                v.setDrawingCacheEnabled(false);
                v.destroyDrawingCache();

                // Send the result to the UI thread and show it on a Toast
                MedAsyncJobUtil.doOnMainThread(new MedAsyncJobUtil.OnMainThreadJob() {
                    @Override
                    public void doInUIThread() {
                        if (isShowToast && !TextUtils.isEmpty(fileName)) {
                            MedToastUtil.showMessage(context, toastMsg + saveDir + "/" + fileName);
                        }
                    }
                });
            }
        });
    }



    /**
     * 将广告图片存入本地
     *
     * @param bitmap
     */
    public static void saveSplashImgToLocal(Context context, Bitmap bitmap) {
        //File file = new File(localPath);
        File fileDir = context.getCacheDir();
        File file = new File(fileDir, SPLASH_FILE_NAME);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
        }
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断广告图片是否存在
     *
     * @return
     */
    public static String getSplashImgFilePath(Context context) {
        File fileDir = context.getCacheDir();
        File file = new File(fileDir, SPLASH_FILE_NAME);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }
        return "";
    }


    /**
     * 原图加圈圈
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int cornerDp) {
        int radius = bitmap.getWidth() / 2;
        int borderWidth = ConvertUtils.dp2px(cornerDp);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dest);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);

        Paint mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setStrokeWidth(borderWidth);
        c.drawCircle(radius, radius, radius - borderWidth, paint);
        c.drawCircle(radius, radius, radius - (borderWidth * 0.5f), mBorderPaint);
        return dest;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 获取文件的大小
     *
     * @param filePathOrUrl 本地文件路径或者网络地址
     * @return
     */
    public static String getFileSize(String filePathOrUrl) {
        double result = 0;
        File file = new File(filePathOrUrl);
        if (file.exists()) {
            result = file.length() / 1024.00;
        }
        if (result < 1) {
            return formatDouble2(result) + "k";
        }

        if (result < 1000) {
            return (int) result + "k";
        }
        result = result / 1024;
        return formatDouble2(result) + "M";
    }

    /**
     * NumberFormat is the abstract base class for all number formats.
     * This class provides the interface for formatting and parsing numbers.
     *
     * @param d
     * @return
     */
    public static String formatDouble2(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(1);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.UP);
        return nf.format(d);
    }

    /**
     * 本地图片改变后 通知系统相册更新
     *
     * @param context
     * @param filePath
     */
    public static void sendBraodCastToGallary(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        if (file != null && file.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(context, MedAppInfo.getApplicationId().concat(".provider"), file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(uri);
            } else {
                intent.setData(Uri.fromFile(file));
            }
            context.sendBroadcast(intent);
        }

        MediaScannerConnection.scanFile(MedAppInfo.getAppContext(), new String[]{filePath}, new String[]{"image/jpeg"}, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(final String path, final Uri uri) {
                //your file has been scanned!
            }
        });
    }
}
