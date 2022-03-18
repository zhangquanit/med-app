package com.medlinker.reactnative.codepush.util;

import android.text.TextUtils;

import com.medlinker.reactnative.codepush.DownloadProgress;
import com.medlinker.reactnative.codepush.DownloadProgressCallback;
import com.medlinker.reactnative.codepush.RNCodePushConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

public class DownLoadUtil {

    /**
     * 下载文件
     *
     * @param serverUrl
     * @param folder           下载文件夹
     * @param downloadFileName 要下载的文件名
     * @param savePath         保存文件的路径
     * @param progressCallback 下载进度回调
     * @return 是否下载成功
     */
    public static boolean download(String serverUrl, String folder, String downloadFileName, String savePath,
                                   DownloadProgressCallback progressCallback) throws IOException {
        String downloadUrlString = String.format("%s/%s/%s", serverUrl, folder, downloadFileName);
        if (TextUtils.isEmpty(folder)) {
            downloadUrlString = String.format("%s/%s", serverUrl, downloadFileName);
        }
        HttpURLConnection connection = null;
        BufferedInputStream bin = null;
        FileOutputStream fos = null;
        BufferedOutputStream bout = null;
        File downloadFile = null;
        boolean isZip = false;
        boolean isDownLoadSuccess = false;

        try {
            URL downloadUrl = new URL(downloadUrlString);
            connection = (HttpURLConnection) (downloadUrl.openConnection());
            connection.setRequestProperty("Accept-Encoding", "identity");
            bin = new BufferedInputStream(connection.getInputStream());

            long totalBytes = connection.getContentLength();
            long receivedBytes = 0;
            File downloadFolder = new File(savePath);
            downloadFolder.mkdirs();

            downloadFile = new File(downloadFolder, downloadFileName);
            fos = new FileOutputStream(downloadFile);
            bout = new BufferedOutputStream(fos, RNCodePushConstants.DOWNLOAD_BUFFER_SIZE);
            byte[] data = new byte[RNCodePushConstants.DOWNLOAD_BUFFER_SIZE];
            byte[] header = new byte[4];

            int numBytesRead;
            while ((numBytesRead = bin.read(data, 0, RNCodePushConstants.DOWNLOAD_BUFFER_SIZE)) >= 0) {
                if (receivedBytes < 4) {
                    for (int i = 0; i < numBytesRead; i++) {
                        int headerOffset = (int) (receivedBytes) + i;
                        if (headerOffset >= 4) {
                            break;
                        }

                        header[headerOffset] = data[i];
                    }
                }

                receivedBytes += numBytesRead;
                bout.write(data, 0, numBytesRead);
                if (progressCallback != null) {
                    progressCallback.call(new DownloadProgress(totalBytes, receivedBytes));
                }
            }

            if (totalBytes != receivedBytes) {
                throw new RuntimeException("Received " + receivedBytes + " bytes, expected " + totalBytes);
            }

            isZip = ByteBuffer.wrap(header).getInt() == 0x504b0304;
            isDownLoadSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bout != null) bout.close();
                if (fos != null) fos.close();
                if (bin != null) bin.close();
                if (connection != null) connection.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("Error closing IO resources.", e);
            }
        }

        if (isZip) {
            FileUtils.unzipFile(downloadFile, savePath);
            FileUtils.deleteFileOrFolderSilently(downloadFile);
        }
        return isDownLoadSuccess;
    }
}
