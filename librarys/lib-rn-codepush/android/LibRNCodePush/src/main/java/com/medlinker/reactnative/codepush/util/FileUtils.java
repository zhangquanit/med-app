package com.medlinker.reactnative.codepush.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author hmy
 */
public class FileUtils {
    private static final int WRITE_BUFFER_SIZE = 1024 * 8;

    public static String appendPathComponent(String basePath, String appendPathComponent) {
        return new File(basePath, appendPathComponent).getAbsolutePath();
    }

    public static boolean fileAtPathExists(String filePath) {
        return new File(filePath).exists();
    }

    public static JSONObject getJsonObjectFromFile(String filePath) throws IOException {
        String content = FileUtils.readFileToString(filePath);
        try {
            return new JSONObject(content);
        } catch (JSONException jsonException) {
            // Should not happen
            return null;
        }
    }

    public static String readFileToString(String filePath) throws IOException {
        FileInputStream fin = null;
        BufferedReader reader = null;
        try {
            File fl = new File(filePath);
            fin = new FileInputStream(fl);
            reader = new BufferedReader(new InputStreamReader(fin));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        } finally {
            if (reader != null) reader.close();
            if (fin != null) fin.close();
        }
    }

    public static String getJsonStringFromFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream f = new FileInputStream(file);
            BufferedReader bis = new BufferedReader(new InputStreamReader(f));
            String line;
            while ((line = bis.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public static String getJsonStringFromAsset(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //??????assets???????????????
            AssetManager assetManager = context.getAssets();
            //????????????????????????????????????
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void deleteDirectoryAtPath(String directoryPath) {
        if (directoryPath == null) {
            return;
        }
        File file = new File(directoryPath);
        if (file.exists()) {
            deleteFileOrFolderSilently(file);
        }
    }

    public static void deleteFileOrFolderSilently(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileEntry : files) {
                if (fileEntry.isDirectory()) {
                    deleteFileOrFolderSilently(fileEntry);
                } else {
                    fileEntry.delete();
                }
            }
        }

        if (!file.delete()) {
        }
    }

    public static void writeStringToFile(String content, String filePath) throws FileNotFoundException {
        PrintWriter out = null;
        try {
            out = new PrintWriter(filePath);
            out.print(content);
        } finally {
            if (out != null) out.close();
        }
    }

    public static void unzipFile(File zipFile, String destination) throws IOException {
        FileInputStream fileStream = null;
        BufferedInputStream bufferedStream = null;
        ZipInputStream zipStream = null;
        try {
            fileStream = new FileInputStream(zipFile);
            bufferedStream = new BufferedInputStream(fileStream);
            zipStream = new ZipInputStream(bufferedStream);
            ZipEntry entry;

            File destinationFolder = new File(destination);
            if (destinationFolder.exists()) {
                deleteFileOrFolderSilently(destinationFolder);
            }

            destinationFolder.mkdirs();

            byte[] buffer = new byte[WRITE_BUFFER_SIZE];
            while ((entry = zipStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                File file = new File(destinationFolder, fileName);
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    FileOutputStream fout = new FileOutputStream(file);
                    try {
                        int numBytesRead;
                        while ((numBytesRead = zipStream.read(buffer)) != -1) {
                            fout.write(buffer, 0, numBytesRead);
                        }
                    } finally {
                        fout.close();
                    }
                }
                long time = entry.getTime();
                if (time > 0) {
                    file.setLastModified(time);
                }
            }
        } finally {
            try {
                if (zipStream != null) zipStream.close();
                if (bufferedStream != null) bufferedStream.close();
                if (fileStream != null) fileStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error closing IO resources.", e);
            }
        }
    }

    /**
     * ???????????????
     *
     * @param oldPath ?????????????????????
     * @param newPath ??????????????????
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        //???????????????
        oleFile.renameTo(newFile);
    }

    /**
     * ??????????????????
     *
     * @param path
     * @return
     */
    public static boolean isEmptyFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        File[] listFiles = file.listFiles();
        return listFiles == null || listFiles.length == 0;
    }

    /**
     * ????????????
     *
     * @param context    ???????????????
     * @param zipPath    ?????????
     * @param targetPath ????????????
     */
    public static void copyAssetsToFile(Context context, String zipPath, String targetPath) {
        if (TextUtils.isEmpty(zipPath) || TextUtils.isEmpty(targetPath)) {
            return;
        }
        File dest = new File(targetPath);
        dest.getParentFile().mkdirs();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(context.getAssets().open(zipPath));
            out = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????
     *
     * @param originPath ?????????
     * @param targetPath ????????????
     */
    public static void copyFile(String originPath, String targetPath) {
        if (TextUtils.isEmpty(originPath) || TextUtils.isEmpty(targetPath)) {
            return;
        }
        File dest = new File(targetPath);
        dest.getParentFile().mkdirs();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(originPath));
            out = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ??????assets??????????????????????????????
     *
     * @param context
     * @param assetDirOrFile ?????????/?????????
     * @param targetDir      ???????????????
     */
    public static void copyAssets(Context context, String assetDirOrFile, String targetDir) {
        if (TextUtils.isEmpty(assetDirOrFile) || TextUtils.isEmpty(targetDir)) {
            return;
        }
        String separator = File.separator;
        try {
            // ??????assets??????assetDir????????????????????????????????????
            String[] fileNames = context.getResources().getAssets().list(assetDirOrFile);
            // ??????????????????(??????),?????????????????????
            if (fileNames != null && fileNames.length > 0) {
                File targetFile = new File(targetDir);
                if (!targetFile.exists() && !targetFile.mkdirs()) {
                    return;
                }
                for (String fileName : fileNames) {
                    copyAssets(context, assetDirOrFile + separator + fileName, targetDir + separator + fileName);
                }
            } else { // ??????,???????????????
                copyAssetsToFile(context, assetDirOrFile, targetDir + separator + assetDirOrFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAppVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
}
