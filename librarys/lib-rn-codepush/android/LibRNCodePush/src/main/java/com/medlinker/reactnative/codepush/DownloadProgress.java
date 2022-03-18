package com.medlinker.reactnative.codepush;

public class DownloadProgress {
    private long mTotalBytes;
    private long mReceivedBytes;

    public DownloadProgress(long totalBytes, long receivedBytes) {
        mTotalBytes = totalBytes;
        mReceivedBytes = receivedBytes;
    }

    public boolean isCompleted() {
        return mTotalBytes == mReceivedBytes;
    }

    public long getTotalBytes() {
        return mTotalBytes;
    }

    public long getReceivedBytes() {
        return mReceivedBytes;
    }
}
