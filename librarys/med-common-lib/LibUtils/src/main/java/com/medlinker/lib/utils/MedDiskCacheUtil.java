package com.medlinker.lib.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:ganyu@medlinker.net">ganyu</a>
 * @version 3.0
 * @description 本地缓存工具类
 * @time 2015/11/4 18:12
 */
public class MedDiskCacheUtil {
    /**
     * 默认缓存大小
     */
    public static final String DEFAULT_CACHE_SIZE = "0M";

    private MedDiskCacheUtil() {
    }

    /**
     * 清除应用内部缓存(/data/data/com.xxx.xxx/cache)
     *
     * @param context 应用上下文
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 清除应用数据库(/data/data/com.xxx.xxx/databases)
     *
     * @param context 应用上下文
     */
    public static void cleanDatabase(Context context) {
        deleteFilesByDirectory(new File(Environment.getDataDirectory() + "/" + context.getPackageName() + "/databases"));
    }

    /**
     * 清除应用指定名称的数据库
     *
     * @param context 应用上下文
     * @param dbName  数据库名称
     */
    public static void cleanDatabase(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)
     *
     * @param context 应用上下文
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File(Environment.getDataDirectory() + "/" + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容
     *
     * @param context 应用上下文
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context 应用上下文
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * 清除自定义路径下的文件
     *
     * @param filePath 文件目录
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * 清除应用数据
     *
     * @param context   应用上下文
     * @param filePaths 文件目录集
     */
    public static void cleanApplicationData(Context context, String... filePaths) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabase(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        if (filePaths == null) {
            return;
        }
        for (String filePath : filePaths) {
            cleanCustomCache(filePath);
        }
    }

    /**
     * 删除某个文件夹下的文件
     *
     * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory() && directory.listFiles() != null) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
    }

    /**
     * 获取文件大小<br />
     * Context.getExternalFilesDir() --> SDCard/Android/data/应用的包名/files/目录，一般放一些长时间保存的数据<br />
     * Context.getExternalCacheDir() --> SDCard/Android/data/应用包名/cache/目录，一般存放临时缓存数据<br />
     *
     * @param file 文件
     * @return
     * @throws Exception
     * @see Context#getExternalCacheDir()
     * @see Context#getExternalFilesDir(String)
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 判断是否为文件夹
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录文件
     *
     * @param filePath 文件路径
     * @param isDelete 是否删除目录文件
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean isDelete) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        try {
            File file = new File(filePath);
            // 判断是否为文件夹
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFolderFile(files[i].getAbsolutePath(), true);
                }
            }
            if (isDelete) {
                // 判断是否为文件夹
                if (!file.isDirectory()) {
                    file.delete();
                } else {
                    // 判断目录下是否有文件
                    if (file.listFiles().length == 0) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String getCacheSize(File file) throws Exception {
        return getFormatSize(getFolderSize(file));
    }

    /**
     * 格式化单位
     *
     * @param size 大小
     * @return 指定格式的字符串
     */
    public static String getFormatSize(long size) {
        if (size <= 0) {
            return DEFAULT_CACHE_SIZE;
        }
        double kiloByte = size / 1024;
        double megaByte = kiloByte / 1024;
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
    }

    /**
     * 格式化数据，保留一位小数
     *
     * @param size
     * @return
     */
    public static String getFormatSize1(long size) {
        if (size <= 0) {
            return "0.0M";
        }
        double kiloByte = size / 1024;
        double megaByte = kiloByte / 1024;
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return result.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
    }

    /**
     * 格式化数据，保留一位小数，少于1G，不保留小数
     *
     * @param size
     * @return
     */
    public static String getFormatSize2(long size) {
        if (size <= 0) {
            return DEFAULT_CACHE_SIZE;
        }
        double kiloByte = size / 1024;
        double megaByte = kiloByte / 1024;
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return result.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
    }

    /**
     * 格式化数据，保留一位小数,小数位为0，就去掉0
     *
     * @param size
     * @return
     */
    public static String getFormatSize3(long size) {
        if (size <= 0) {
            return "0M";
        }
        double kiloByte = size / 1024;
        double megaByte = kiloByte / 1024;
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return doubleTrans(result.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()) + "M";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return doubleTrans(result.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()) + "G";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return doubleTrans(result.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()) + "T";
    }

    /**
     * 一位小数为0就去掉0
     *
     * @param d
     * @return
     */
    private static String doubleTrans(double d) {
        if (d % 1.0 == 0) {
            // 小数是0
            return String.valueOf(Math.round(d));
        }
        return String.valueOf(d);
    }

    /**
     * 格式化单位
     *
     * @param size 大小
     * @return 指定格式的字符串
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(kiloByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result = new BigDecimal(Double.toString(megaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result = new BigDecimal(Double.toString(gigaByte));
            return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G";
        }
        BigDecimal result = new BigDecimal(teraBytes);
        return result.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T";
    }

    /**
     * 清除WebView默认缓存
     *
     * @param context 应用上下文
     */
    public static void clearWebCache(Context context) {
        if (context == null) {
            return;
        }
        context.getApplicationContext().deleteDatabase("webview.db");
        context.getApplicationContext().deleteDatabase("webviewCache.db");
    }




    /**
     * 获取总内存
     *
     * @return
     */
    public static long getTotalMemory() {
        long totalsize;
        //获得SD卡空间的信息
        File path = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blocksize = statFs.getBlockSize();
        long totalblocks = statFs.getBlockCount();
        //计算SD卡的空间大小
        totalsize = blocksize * totalblocks;
        return totalsize;
    }

    /**
     * 获取可用内存
     *
     * @return
     */
    public static long getAvailableMemory() {
        long availablesize;
        //获得SD卡空间的信息
        File path = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blocksize = statFs.getBlockSize();
        long availableblocks = statFs.getAvailableBlocks();
        //计算SD卡的空间大小
        availablesize = availableblocks * blocksize;
        return availablesize;
    }

    /**
     * 删除文件
     *
     * @param path
     */
    public static void deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
