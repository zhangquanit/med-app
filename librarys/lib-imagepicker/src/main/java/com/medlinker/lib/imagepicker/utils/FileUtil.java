package com.medlinker.lib.imagepicker.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;


import com.blankj.utilcode.util.Utils;
import com.medlinker.lib.utils.MedAppInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 功能描述:文件操作工具类
 *
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * 2016/5/5 11:33
 * @version 1.0
 */
public class FileUtil {


    /**
     * app路径配置
     */
    static class Constants {
        /**
         * app主目录
         */
        public static final String APP_FILE_DIR = "/Medlinker";

        /**
         * temp dir
         */
        public static final String DEFAULT_TEMP_DIR = "/Medlinker/temp";

        /**
         * temp目录下的图片，随时删除
         */
        public static final String DEFAULT_TEMP_IMAGES_DIR = DEFAULT_TEMP_DIR + "/images";

        /**
         * 缓存目录
         */
        public static final String DEFAULT_CACHE_DIR = "/Medlinker/cache";

        /**
         * 缓存目录：网络
         */
        public static final String DEFAULT_NET_CACHE_DIR = "/Medlinker/cache/network";

        /**
         * 缓存目录：视频
         */
        public static final String DEFAULT_VIDEO_CACHE_DIR = "/Medlinker/cache/video";

        /**
         * 缓存目录：fresco
         */
        public static final String DEFAULT_FRESCO_CACHE_DIR = "/Medlinker/cache/fresco";

        public static final String DEFAULT_DOWNLOADS_DIR = "/Medlinker/downloads";

        /**
         * 下载保存图片的目录
         */
        public static final String DEFAULT_IMAGES_DIR = "/Medlinker/images";
        /**
         * 天使谷图片的目录
         */
        public static final String ANGEL_SHARE_DIR = "/Medlinker/angelshare";

        /**
         * 崩溃日志, 目录
         */
        public static final String DEFAULT_LOG_DIR = "/Medlinker/log";

        /**
         * 直播数据目录
         */
        public static final String LIVE_DATA_DIR = "/Medlinker/livedata";

        /**
         * 患教录制视频目录
         */
        public static final String PATIENT_VIDEO_RECORD_DIR = "/Medlinker/cache/patientVideoRecord";
    }

    public static String getLogDir() {
        return new File(Utils.getApp().getCacheDir(), Constants.DEFAULT_LOG_DIR).getAbsolutePath();
    }

    public static String getPatientVideoRecordDir() {
        return new File(Utils.getApp().getCacheDir(), Constants.PATIENT_VIDEO_RECORD_DIR).getAbsolutePath();
    }

    /**
     * @return
     */
    public static File getAppRootDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.APP_FILE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * @return
     */
    public static File getTempDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.DEFAULT_TEMP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getTempImagesDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.DEFAULT_TEMP_IMAGES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取视频缓存目录
     */
    public static File getVideoCacheDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.DEFAULT_VIDEO_CACHE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取直播数据目录
     */
    public static File getLiveDataDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.LIVE_DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取fresco缓存目录
     */
    public static File getFrescoCacheDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.DEFAULT_FRESCO_CACHE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    /**
     * 获取网络缓存目录
     *
     * @return
     */
    public static File getNetCacheDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.DEFAULT_NET_CACHE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 缓存目录
     *
     * @return
     */
    public static File getCacheDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.DEFAULT_CACHE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 图片保存目录
     *
     * @return
     */
    public static File getImagesDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.DEFAULT_IMAGES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 天使谷分享图片
     *
     * @return
     */
    public static File getAngelShareDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.ANGEL_SHARE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * @return
     */
    public static File getDownloadDir() {
        File dir = new File(Utils.getApp().getCacheDir(), Constants.DEFAULT_DOWNLOADS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static void copyFile(File srcFile, File destFile) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            byte[] buffer = new byte[8192];
            int count = 0;
            // 开始复制文件
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
        } catch (IOException e) {
            if (MedAppInfo.isDebug()) {
                Log.i("zz", e.getMessage(), e);
            }
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                if (MedAppInfo.isDebug()) {
                    Log.i("zz", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 删除文件或文件夹
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
            file.renameTo(to);
            to.delete();
        } else {
            String[] fileNames = file.list();
            if (fileNames != null && fileNames.length > 0) {
                for (String name : fileNames) {
                    deleteFile(new File(file, name));
                }
            }
            final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
            file.renameTo(to);
            to.delete();
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
     * 获取文件后缀
     *
     * @param url
     * @return
     */
    public static String getFileType(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String path = Uri.parse(url).getPath();
        int index = path.lastIndexOf(".");
        if (index != -1) {
            return url.substring(index + 1);
        }
        return null;
    }

    /**
     * 数据文件命名格式：livereplay_1258.log （liveId=1258）
     * 临时缓存文件格式：livereplay_1258.tmp
     * 临时文件保存路径：/medlinker/download/temp/
     * 正式文件保存路径：/medlinker/download/live/replay/
     *
     * @return
     */
    public static File getDownloadTempFile(int liveId) {
        return new File(getTempDir(), "livereplay_" + liveId + ".tmp");
    }

    public static File getDownloadLiveFile(int liveId) {
        return new File(getLiveDataDir(), "livereplay_" + liveId + ".log");
    }

    /**
     * 医联Logo文件名
     */
    private final static String MEDLINKER_LOGO_PATH = "medlinker_logo.png";



    public static String generatePathForResource(String fileName, int resId) {
        String path = PictureUtil.getPictureFilePath() + fileName;
        File file = new File(path);
        if (!file.exists()) {//不存在就保存到SD卡
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeResource(MedAppInfo.getAppContext().getResources(), resId);
            FileOutputStream os;
            try {
                os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public static boolean unZip(String zipFile, String targetDir) {
        int BUFFER = 4096; //这里缓冲区我们使用4KB，
        String strEntry; //保存每个zip的条目名称
        try {
            BufferedOutputStream dest = null; //缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; //每个zip条目的实例
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    Log.i("Unzip: ", entry.toString());
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();
                    if (entry.isDirectory()) {
                        File folder = new File(targetDir + File.separator + strEntry);
                        folder.mkdirs();
                    } else {
                        File entryFile = new File(targetDir + File.separator + strEntry);
                        File entryDir = new File(entryFile.getParent());
                        if (!entryDir.exists()) {
                            entryDir.mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(entryFile);
                        dest = new BufferedOutputStream(fos, BUFFER);
                        while ((count = zis.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    deleteFile(new File(targetDir));//解压失败删除文件夹及文件
                    return false;
                }
            }
            zis.close();
            fis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            deleteFile(new File(targetDir));//解压失败删除文件夹及文件
            return false;
        }
    }


    /**
     * TAG for log messages.
     */
    private static final String TAG = "FileUtil";

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", ex.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    public static void copyFile(@NonNull String pathFrom, @NonNull String pathTo) throws IOException {
        if (pathFrom.equalsIgnoreCase(pathTo)) {
            return;
        }

        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            inputChannel = new FileInputStream(new File(pathFrom)).getChannel();
            outputChannel = new FileOutputStream(new File(pathTo)).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

    public static String getFilePathFromContentUri(Context context, Uri selectedVideoUri) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = context.getContentResolver().query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    /**
     * 获取FileProvider path
     * author zx
     * version 1.0
     * since 2018/5/4  .
     */
    public static String getFPUriToPath(Context context, Uri uri) {
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.toString();
        }

        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs != null) {
                String fileProviderClassName = FileProvider.class.getName();
                for (PackageInfo pack : packs) {
                    ProviderInfo[] providers = pack.providers;
                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            if (uri.getAuthority().equals(provider.authority)) {
                                if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                                    Class<FileProvider> fileProviderClass = FileProvider.class;
                                    try {
                                        Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                                        getPathStrategy.setAccessible(true);
                                        Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
                                        if (invoke != null) {
                                            String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                            Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                            Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                            getFileForUri.setAccessible(true);
                                            Object invoke1 = getFileForUri.invoke(invoke, uri);
                                            if (invoke1 instanceof File) {
//                                                String filePath = ((File) invoke1).getAbsolutePath();
                                                Uri u = Uri.fromFile((File) invoke1);
                                                return u.toString();
                                            }
                                        }
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String saveBitmap2TempImagesFile(Bitmap bitmap, String fileName) {
        File dirFile = getTempImagesDir();
        File file = new File(dirFile, fileName);
        try {
            if (file.exists()) {
                deleteFile(file);
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file.getAbsolutePath();

    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
