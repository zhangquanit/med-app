package com.medlinker.lib.qiniu.pili.push;

import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.medlinker.lib.qiniu.R;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import java.util.List;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 4.0
 * @description
 * @time 2016-08-17-16:58
 */
public abstract class SimpleStreamBaseActivity extends FragmentActivity implements
        StreamingSessionListener,
        StreamingStateChangedListener,
        StreamStatusCallback {

    private static final String TAG = "LiveStreaming";
    //推流相关
    protected MediaStreamingManager mMediaStreamingManager;
    protected CameraStreamingSetting mCameraStreamingSetting;
    protected MicrophoneStreamingSetting mMicrophoneStreamingSetting;
    protected StreamingProfile mProfile;

    protected boolean mIsReady = false;
    private boolean mShutterButtonPressed = false;
    protected String mStatusMsgContent;
    protected Switcher mSwitcher = new Switcher();

    private static final int MSG_START_STREAMING = 0;
    private static final int MSG_STOP_STREAMING = 1;
    private int mCurrentCamFacingIndex;

    protected TextView mTvInfo;

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_STREAMING:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mShutterButtonPressed = false;
                            boolean res = mMediaStreamingManager.startStreaming();
                            mShutterButtonPressed = true;
                            Log.i(TAG, "res:" + res);
                            if (!res) {
                                mShutterButtonPressed = false;
                                // TODO: 2016/8/17 让按钮可以点击
                            }
                        }
                    }).start();
                    break;
                case MSG_STOP_STREAMING:
                    if (mShutterButtonPressed) {
                        // TODO: 2016/8/17 让按钮不可以点击
                        boolean res = mMediaStreamingManager.stopStreaming();
                        if (!res) {
                            mShutterButtonPressed = true;
                            // TODO: 2016/8/17 让按钮可以点击
                        }
                    }
                    break;
                default:
                    Log.e(TAG, "Invalid message");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushSettingFactory.initActivitySetting(this);
        mProfile = PushSettingFactory.getStreamingProfile(this);
        mCurrentCamFacingIndex = PushSettingFactory.chooseCameraFacingId().ordinal();
        mCameraStreamingSetting = PushSettingFactory.getCameraStreamingSetting();
        mMicrophoneStreamingSetting = PushSettingFactory.getMicrophoneStreamingSetting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsReady = false;
        mShutterButtonPressed = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.destroy();
        }
    }

    protected void startStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_STREAMING), 50);
    }

    protected void stopStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_STOP_STREAMING), 50);
    }

    @Override
    public void notifyStreamStatusChanged(final StreamingProfile.StreamStatus streamStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "bitrate:" + streamStatus.totalAVBitrate / 1024 + " kbps"
                        + "\naudio:" + streamStatus.audioFps + " fps"
                        + "\nvideo:" + streamStatus.videoFps + " fps");
            }
        });
    }

    @Override
    public boolean onRecordAudioFailedHandled(int i) {
        Log.i(TAG, "onRecordAudioFailedHandled");
        mMediaStreamingManager.updateEncodingType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 ?
                AVCodecType.HW_VIDEO_WITH_HW_AUDIO_CODEC :
                AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);
        mMediaStreamingManager.startStreaming();
        return true;
    }

    @Override
    public boolean onRestartStreamingHandled(int i) {
        Log.i(TAG, "onRestartStreamingHandled");
        return mMediaStreamingManager != null && mMediaStreamingManager.startStreaming();
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        Camera.Size size = null;
        if (list != null) {
            for (Camera.Size s : list) {
                if (s.height >= 480) {
                    size = s;
                    break;
                }
            }
        }
        return size;
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        Log.e(TAG, "StreamingState streamingState:" + streamingState + ",extra:" + extra);
        switch (streamingState) {
            case PREPARING:
                showReconnectLoading(true);
                mStatusMsgContent = getString(R.string.string_state_preparing);
                break;
            case READY:
                showReconnectLoading(true);
                mIsReady = true;
                mStatusMsgContent = getString(R.string.string_state_ready);
                onReady();
                break;
            case CONNECTING:
                showReconnectLoading(true);
                mStatusMsgContent = getString(R.string.string_state_connecting);
                break;
            case STREAMING:
                showReconnectLoading(false);
                mStatusMsgContent = getString(R.string.string_state_streaming);
                onStreaming();
                break;
            case SHUTDOWN:
                mStatusMsgContent = getString(R.string.string_state_ready) + "-----SHUTDOWN";
                break;
            case IOERROR:
                onShutDown();
                mStatusMsgContent = getString(R.string.string_state_ready) + "-----IOERROR";
                break;
            case UNKNOWN:
                onShutDown();
                mStatusMsgContent = getString(R.string.string_state_ready) + "-----UNKNOWN";
                break;
            case SENDING_BUFFER_EMPTY:
                break;
            case SENDING_BUFFER_FULL:
                break;
            case AUDIO_RECORDING_FAIL:
                break;
            case OPEN_CAMERA_FAIL:
                Log.e(TAG, "Open Camera Fail. id:" + extra);
                break;
            case DISCONNECTED:
                mStatusMsgContent = getString(R.string.string_state_ready) + "-----DISCONNECTED";
                break;
            case INVALID_STREAMING_URL:
                Log.e(TAG, "Invalid streaming url:" + extra);
                break;
            case UNAUTHORIZED_STREAMING_URL:
                Log.e(TAG, "Unauthorized streaming url:" + extra);
                break;
            case CAMERA_SWITCHED:
                Log.i(TAG, "camera switched");
                final int currentCamId = (Integer) extra;
                break;
            case TORCH_INFO:
                if (extra != null) {
                    final boolean isSupportedTorch = (Boolean) extra;
                    Log.i(TAG, "isSupportedTorch=" + isSupportedTorch);
                }
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTvInfo != null) {
                    mTvInfo.setText(mStatusMsgContent);
                }
            }
        });
    }

    protected abstract void onReady();

    protected abstract void onShutDown();

    protected abstract void onStreaming();

    protected abstract void showReconnectLoading(boolean show);

    private class Switcher implements Runnable {
        @Override
        public void run() {
            mCurrentCamFacingIndex = (mCurrentCamFacingIndex + 1) % CameraStreamingSetting.getNumberOfCameras();
            CameraStreamingSetting.CAMERA_FACING_ID facingId;
            if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK.ordinal()) {
                facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
            } else if (mCurrentCamFacingIndex == CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT.ordinal()) {
                facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
            } else {
                facingId = CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
            }
            Log.i(TAG, "switchCamera:" + facingId);
            mMediaStreamingManager.switchCamera(facingId);
        }
    }
}
