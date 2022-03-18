package net.medlinker.im.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.medlinker.record.MP3RecorderManager;

import net.medlinker.im.router.ModuleIMManager;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.SoftReference;

import io.reactivex.functions.Consumer;


/**
 * Created by Terry on 16/2/15.
 */
public class MsgVoiceManager {

    public static MsgVoiceManager mMsgVoiceManager;
    //音频文件存储本地路径
    public static final String mVoiceDir = Utils.getApp().getCacheDir().getPath() + "/Medlinker/voice";
    //音频录制对象
    private MediaRecorder mMediaRecorder;
    //音频播放对象
    private MediaPlayer mMediaPlayer;
    //mediaplayer播放状态监听器
    private OnVoicePlayStatusListener mOnVoicePlayStatusListener;
    //当前播放的消息的标示，用于定位识别当前正在播放的是哪条语音消息
    private String mCurrentPlayingMsg;
    //当前录音文件
    private String mCurrentFilePath = "";
    //当前播放文件
    private String mCurrentPlayVoiceFilePath = "";

    private static AudioManager mAm;

    public AudioStateListener mAduioStateListener;

    public static final int TIME_DELAY = 1000;
    public static final int WHAT_PROGRESS = 1;

    private long mAudioStartRecordTime;

    private boolean mIsStopRecord = true;
    private boolean mIsRecordMp3 = false;

    private SoftReference<ProgressHandler> mHandler = new SoftReference<>(new ProgressHandler());

    private class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_PROGRESS && null != mAduioStateListener && mAudioStartRecordTime > 0) {
                mAduioStateListener.progress(System.currentTimeMillis() - mAudioStartRecordTime);
                if (!mIsStopRecord) {
                    sendEmptyMessageDelayed(WHAT_PROGRESS, TIME_DELAY);
                }
            }
        }
    }

    /**
     * 回调准备完毕
     */
    public interface AudioStateListener {
        void wellPrepared();

        void progress(long m);

        void stopRecord(long duration);
    }

    public class SimpleAudioStateListener implements AudioStateListener {

        @Override
        public void wellPrepared() {

        }

        @Override
        public void progress(long m) {

        }

        @Override
        public void stopRecord(long duration) {

        }
    }

    public void setOnAduioStateListener(AudioStateListener mAduioStateListener) {
        this.mAduioStateListener = mAduioStateListener;
    }

    public void removeOnAudioStateListener() {
        this.mAduioStateListener = null;
    }

    private MsgVoiceManager() {
    }

    public static MsgVoiceManager getInstance() {

        if (mMsgVoiceManager == null) {
            mMsgVoiceManager = new MsgVoiceManager();
            mAm = (AudioManager) ModuleIMManager.INSTANCE.getIMService().getApplication().getSystemService(Context.AUDIO_SERVICE);
        }

        return mMsgVoiceManager;
    }

    private ProgressHandler getProgressHandler() {
        if (null == mHandler.get()) {
            mHandler = new SoftReference<>(new ProgressHandler());
        }
        return mHandler.get();
    }

    public void startRecord() {
        startRecord(false);
    }

    public void startRecord(boolean recordMp3) {
        stopPlay();
        File dir = new File(mVoiceDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        mIsRecordMp3 = recordMp3;
        try {
            if (recordMp3) {
                String fileName = createFileNameForMp3();
                mCurrentFilePath = new File(dir, fileName).getAbsolutePath();
                MP3RecorderManager.getInstance().startRecord(mCurrentFilePath);
                if (mAduioStateListener != null) {
                    mAduioStateListener.wellPrepared();
                }
            } else {
                String fileName = createFileName();
                mCurrentFilePath = new File(dir, fileName).getAbsolutePath();
                mMediaRecorder = new MediaRecorder();
                //设置输出文件
                mMediaRecorder.setOutputFile(mCurrentFilePath);
                //设置音频源为麦克风
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                // 设置音频格式
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                // 设置音频编码
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                // 准备录音
                mMediaRecorder.prepare();
                // 开始
                mMediaRecorder.start();
                mAm.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                if (mAduioStateListener != null) {
                    mAduioStateListener.wellPrepared();
                }
                mAudioStartRecordTime = System.currentTimeMillis();
                mIsStopRecord = false;
                getProgressHandler().sendEmptyMessageDelayed(WHAT_PROGRESS, TIME_DELAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getVoiceLevel(int maxLevel) {
        try {
            if (mIsRecordMp3) {
                int level = (int) (maxLevel * MP3RecorderManager.getInstance().getVolumeScale() + 1);
                return Math.min(level, maxLevel);
            } else {
                //mMediaRecorder.getMaxAmplitude() 1-32767,得到0-1之间的值
                //最后取整是1 - 7
                if (null != mMediaRecorder) {
                    return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        mIsStopRecord = true;
        if (mIsRecordMp3) {
            long audioStartRecordTime = MP3RecorderManager.getInstance().stopRecord();
            if (null != mAduioStateListener && audioStartRecordTime > 0) {
                mAduioStateListener.stopRecord(System.currentTimeMillis() - mAudioStartRecordTime);
            }
        } else {
            if (null != mHandler.get()) {
                getProgressHandler().removeCallbacksAndMessages(null);
            }
            if (mMediaRecorder != null) {
                mAm.abandonAudioFocus(mOnAudioFocusChangeListener);
                try {
                    mMediaRecorder.setOnErrorListener(null);
                    mMediaRecorder.setPreviewDisplay(null);
                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (null != mAduioStateListener && mAudioStartRecordTime > 0) {
                mAduioStateListener.stopRecord(System.currentTimeMillis() - mAudioStartRecordTime);
            }
            mAudioStartRecordTime = 0;
        }
        removeOnAudioStateListener();
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {
        stopRecord();
        File file = new File(mCurrentFilePath);
        if (file.exists()) {
            file.delete();//删除已经录制的声音文件
        }
    }

    public boolean isPlaying() {
        return null != mMediaPlayer && mMediaPlayer.isPlaying();
    }

    public boolean isPlayingCurrent(long msgTime) {
        return isPlayingCurrent(String.valueOf(msgTime));
    }

    public boolean isPlayingCurrent(String msgId) {
        return msgId != null && mCurrentPlayingMsg != null && mCurrentPlayingMsg.equals(msgId);
    }

    public void stopPlay() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mAm.abandonAudioFocus(mOnAudioFocusChangeListener);
            mMediaPlayer.stop();
            mCurrentPlayingMsg = null;
            if (mOnVoicePlayStatusListener != null) {
                mOnVoicePlayStatusListener.onStop(mMediaPlayer);
            }
        }
    }


    /**
     * 语音播放状态监听器
     */
    public interface OnVoicePlayStatusListener {
        //播放前
        void onPrepared(MediaPlayer mp);

        //播放结束
        void onCompletion(MediaPlayer mp);

        //播放停止
        void onStop(MediaPlayer mp);

        //下载音频前
        void onDownBefore();

    }

    public boolean isSameAudioFile(String filePath) {
        return TextUtils.equals(filePath, mCurrentPlayVoiceFilePath);
    }

    public void startPlay(String filePath, OnVoicePlayStatusListener listener, long msgTime) {
        startPlay(filePath, listener, String.valueOf(msgTime));
    }

    public void startPlay(String filePath, OnVoicePlayStatusListener listener, String msgId) {
        mCurrentPlayVoiceFilePath = filePath;
        mAm.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            stopPlay();
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            mMediaPlayer.setDataSource(fis.getFD());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mOnVoicePlayStatusListener = listener;
            mCurrentPlayingMsg = msgId;
        } catch (Exception e) {
            mCurrentPlayingMsg = null;
            e.printStackTrace();
        }

    }

    /**
     * 将收到的语音文件加载到手机本地存储
     */
    public void loadVoiceFile(final String fileUrl, final Consumer<String> action0) {
        File dir = new File(mVoiceDir);
        if (!dir.exists()) dir.mkdirs();
        String fileName = createFileName();
        File file = new File(dir, fileName);
        final String path = file.getAbsolutePath();
         download(fileUrl, path, new FileDownloadListener() {
             @Override
             protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

             }

             @Override
             protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

             }

             @Override
            protected void completed(BaseDownloadTask task) {
                try {
                    action0.accept(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

             @Override
             protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

             }

             @Override
             protected void error(BaseDownloadTask task, Throwable e) {

             }

             @Override
             protected void warn(BaseDownloadTask task) {

             }
         });
    }


    /**
     * 将收到的语音文件加载到手机本地存储播放
     *
     * @param id       病例id
     * @param fileUrl  语音录音
     * @param listener 返回
     */
    public void startPlayVoiceFile(int id, String fileUrl, OnVoicePlayStatusListener listener) {
        if (id == -1) {
            //病例清单播放语音
            startPlay(fileUrl, listener, 0);
        }
        File dir = new File(mVoiceDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = createFileName(id);
        File file = new File(dir, fileName);
        String path = file.getAbsolutePath();
        if (file.exists()) {
            startPlay(path, listener, 0);
        } else {
            startLoadVoiceFile(fileUrl, path, listener);
        }
    }


    public void startLoadVoiceFile(int voiceId, String fileUrl) {
        File dir = new File(mVoiceDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = createFileName(voiceId);
        File file = new File(dir, fileName);
        String path = file.getAbsolutePath();
        if (!file.exists()) {
            FileDownloader.getImpl().create(fileUrl).setPath(path);
        }
    }

    public int download(String url, String savePath, FileDownloadListener l) {
        return FileDownloader.getImpl()
                .create(url)
                .setPath(savePath)
                .setListener(l)
                .setAutoRetryTimes(3)
                .start();
    }

    public void startLoadVoiceFile(String fileUrl, final String path, final OnVoicePlayStatusListener listener) {
        download(fileUrl, path, new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                listener.onDownBefore();
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void completed(BaseDownloadTask task) {
                startPlay(path, listener, 0);
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void warn(BaseDownloadTask task) {
                listener.onStop(mMediaPlayer);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                listener.onStop(mMediaPlayer);
            }
        });
    }

    public String createFileNameForMp3() {
        return "voice" + System.currentTimeMillis() + ".mp3";
    }

    public String createFileName() {
        return "voice" + System.currentTimeMillis() + ".aac";
    }

    public String createFileName(int id) {
        return "voice" + id + ".aac";
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }


    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mAm.abandonAudioFocus(mOnAudioFocusChangeListener);
            if (mOnVoicePlayStatusListener != null) {
                mCurrentPlayingMsg = null;
                mOnVoicePlayStatusListener.onCompletion(mp);
            }
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (null != mOnVoicePlayStatusListener)
                mOnVoicePlayStatusListener.onPrepared(mp);
        }
    };

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    stopPlay();
                    break;

            }
        }
    };

    public static class DefaultVoicePlayStatusListener implements OnVoicePlayStatusListener {

        @Override
        public void onPrepared(MediaPlayer mp) {

        }

        @Override
        public void onCompletion(MediaPlayer mp) {

        }

        @Override
        public void onStop(MediaPlayer mp) {

        }

        @Override
        public void onDownBefore() {

        }
    }
}
