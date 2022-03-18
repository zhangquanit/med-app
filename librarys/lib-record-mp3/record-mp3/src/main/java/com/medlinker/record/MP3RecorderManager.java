package com.medlinker.record;

import android.os.Handler;
import android.os.Message;

import com.czt.mp3recorder.MP3Recorder;

import java.io.File;
import java.io.IOException;

/**
 * @author hmy
 * @time 2020/8/13 16:13
 */
public class MP3RecorderManager {

    private static MP3RecorderManager sInstance;
    private MP3Recorder mRecorder;
    private String mFilePath;
    /**
     * 录音时间标记参数
     */
    private long mDuration;

    public static MP3RecorderManager getInstance() {
        if (sInstance == null) {
            sInstance = new MP3RecorderManager();
        }
        return sInstance;
    }

    public void startRecord(String filePath) {
        mFilePath = filePath;
        mRecorder = new MP3Recorder(new File(mFilePath));
        mRecorder.setHandler(mHandler);
        mRecorder.setmRecordFile(new File(mFilePath));
        try {
            mRecorder.start();
            mDuration = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long stopRecord() {
        if (mRecorder != null && mRecorder.isRecording()) {
            mRecorder.setPause(false);
            mRecorder.stop();
        }
        return mDuration;
    }

    public float getVolumeScale() {
        if (mRecorder != null) {
            return mRecorder.getVolume() / mRecorder.getMaxVolume();
        }
        return 0;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
