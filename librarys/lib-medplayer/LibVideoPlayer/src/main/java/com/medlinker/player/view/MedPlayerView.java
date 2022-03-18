package com.medlinker.player.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;

import com.medlinker.player.R;
import com.medlinker.player.component.MedPlayerComponent;
import com.medlinker.player.entity.ControllerStatus;
import com.medlinker.player.util.MedPlayerUtils;
import com.medlinker.player.util.MedPlayerWakeLocker;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoView;

@Keep
public class MedPlayerView extends RelativeLayout {
    private PLVideoView mMediaPlayerVideoView; //七牛播放器
    private MedPlayerController mMediaController; //播放控制器
    private View mLoadingView; //视频加载loading
    private String mVideoUrl; //视频url
    private boolean isAutoPause = false; //是否人为暂停
    private MedPlayerComponent medPlayerComponent;
    private boolean mAutoCloseOnComplete;
    private long mSeekPos;

    public MedPlayerView(Context context) {
        super(context);
        initView();
    }

    public MedPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MedPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.med_video_player_view, this);
        mMediaPlayerVideoView = findViewById(R.id.plVideoView);

        mLoadingView = findViewById(R.id.loadingView);
        mMediaPlayerVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.GONE);

        mMediaPlayerVideoView.setAVOptions(getAVOptions(0));
        mMediaPlayerVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
        mMediaPlayerVideoView.setScreenOnWhilePlaying(true);

        mMediaController = new MedPlayerController(getContext());
        mMediaPlayerVideoView.setMediaController(mMediaController);
        ViewGroup viewGroup = (ViewGroup) mMediaPlayerVideoView.getParent();
        mMediaController.setAnchorView(viewGroup);

        mMediaPlayerVideoView.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayerVideoView.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayerVideoView.setOnErrorListener(mOnErrorListener);
        mMediaPlayerVideoView.setOnInfoListener(mOnInfoListener);
        mMediaPlayerVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mMediaPlayerVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);

    }

    public void setAVOptions(int startPos) {
        mMediaPlayerVideoView.setAVOptions(getAVOptions(startPos));
    }

    public void setVideoPath(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new RuntimeException("视频url为空");
        }
        mVideoUrl = url;
        mMediaPlayerVideoView.setVideoPath(mVideoUrl);
    }

    public void setTitle(String title) {
        mMediaController.setTitle(title);
    }

    public void start() {
        MedPlayerUtils.log("player start");
        mMediaPlayerVideoView.start();
        if (null != medPlayerComponent) {
            medPlayerComponent.onPlay();
        }
    }

    public void pause() {
        MedPlayerUtils.log("player pause");
        mMediaPlayerVideoView.pause();
        if (null != medPlayerComponent) {
            medPlayerComponent.onPause();
        }
    }

//    public void seekTo(long msec) {
//        MedPlayerUtils.log("player seekTo ->" + msec);
//        if (isPlaying()) {
//            mMediaPlayerVideoView.seekTo(msec);
//        } else {
//            mSeekPos = msec;
//        }
//    }

    public void setLooping(boolean looping) {
        mMediaPlayerVideoView.setLooping(looping);
    }

    public void setAutoCloseOnComplete(boolean close) {
        mAutoCloseOnComplete = close;
    }

    public void setPlayComponent(MedPlayerComponent component) {
        this.medPlayerComponent = component;
        this.mMediaController.setPlayComponent(component);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        mMediaController.setScreenMode(isLandscape);
        MedPlayerUtils.log("onConfigurationChanged isLandscape=" + isLandscape);
    }

    public void onResume() {
        MedPlayerUtils.log("onResume isAutoPause=" + isAutoPause);
        if (isAutoPause) {
            isAutoPause = false;
            start();
        }
    }


    public void onPause() {
        MedPlayerUtils.log("onPause isPlaying=" + mMediaPlayerVideoView.isPlaying());
        if (mMediaPlayerVideoView.isPlaying()) {
            isAutoPause = true;
            pause();
        }
    }

    public void onDestroy() {
        try {
            mMediaPlayerVideoView.stopPlayback();
            MedPlayerWakeLocker.release();
            MedPlayerUtils.log("onDestroy");
        } catch (Exception e) {

        }
    }

    public boolean handleBackPressed() {
        if (mMediaController.isFullScreenMode()) {
            switchFullScreen();
            return true;
        }
        return false;
    }

    public void switchFullScreen() {
        mMediaController.switchFullScreen();
    }

    public boolean isPlaying() {
        return (mMediaPlayerVideoView != null) && mMediaPlayerVideoView.isPlaying();
    }

    public MedControllerView getControllView() {
        return mMediaController.getControllView();
    }

    public void setControllerStatus(ControllerStatus status) {
        mMediaController.setControllStatus(status);
    }

    private AVOptions getAVOptions(int startPos) {
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 0); //点播
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE); //软解
        options.setInteger(AVOptions.KEY_LOG_LEVEL, 5);//5清除日志  0日志输出
        if (startPos > 0) {
            options.setInteger(AVOptions.KEY_START_POSITION, startPos * 1000);
        }
        return options;
    }

    private PLOnErrorListener mOnErrorListener = new PLOnErrorListener() {
        @Override
        public boolean onError(int errorCode) {
            String tips = null;
            boolean seekError = false;
            boolean ioError = false;
            switch (errorCode) {
                case PLOnErrorListener.ERROR_CODE_OPEN_FAILED:
                    tips = getResources().getString(R.string.med_videoplayer_open_failed);
                    break;
                case PLOnErrorListener.ERROR_CODE_SEEK_FAILED:
                    seekError = true;
                    tips = getResources().getString(R.string.med_videoplayer_seek_failed);
                    break;
                case PLOnErrorListener.ERROR_CODE_HW_DECODE_FAILURE:
                    tips = getResources().getString(R.string.med_videoplayer_hw_decode_failure);
                    break;
                case PLOnErrorListener.ERROR_CODE_PLAYER_DESTROYED:
                    tips = getResources().getString(R.string.med_videoplayer_player_destroyed);
                    break;
                case PLOnErrorListener.ERROR_CODE_PLAYER_VERSION_NOT_MATCH:
                    tips = getResources().getString(R.string.med_videoplayer_player_version_not_match);
                    break;
                case PLOnErrorListener.ERROR_CODE_PLAYER_CREATE_AUDIO_FAILED:
                    tips = getResources().getString(R.string.med_videoplayer_player_create_audio_failed);
                    break;
                case PLOnErrorListener.ERROR_CODE_IO_ERROR:
                    tips = getResources().getString(R.string.med_videoplayer_network_error);
                    ioError = true;
                    break;
                case PLOnErrorListener.MEDIA_ERROR_UNKNOWN:
                default:
                    break;
            }
            MedPlayerUtils.log("mOnErrorListener->onError errorCode=[" + errorCode + "],info=[" + tips + "]");
            if (seekError) { //拖动进度条加载失败

            } else if (ioError) { //网络加载失败

            } else {
                mMediaController.show(0);
                if (mMediaPlayerVideoView.canPause()) {
                    mMediaPlayerVideoView.pause();
                } else {
                    mMediaPlayerVideoView.stopPlayback();
                }
                MedPlayerUtils.showToast(getContext(), tips == null ? getResources().getString(R.string.med_videoplayer_unknown_error) : tips);
            }
            return true;
        }
    };

    private PLOnCompletionListener mOnCompletionListener = new PLOnCompletionListener() {
        @Override
        public void onCompletion() {
            MedPlayerUtils.log("mOnCompletionListener->onCompletion");
            mMediaController.setCompletedState();
            if (null != medPlayerComponent) {
                medPlayerComponent.onComplete();
            }
            if (mAutoCloseOnComplete) {
                ((Activity) getContext()).finish();
            }
        }
    };

    private PLOnPreparedListener mOnPreparedListener = new PLOnPreparedListener() {

        @Override
        public void onPrepared(int preparedTime) {
            MedPlayerUtils.log("mOnPreparedListener->onPrepared,preparedTime=" + preparedTime);
            if (!MedPlayerWakeLocker.isScreenOn(getContext()) && mMediaPlayerVideoView.canPause()) {
                mMediaPlayerVideoView.pause();
            } else {
                MedPlayerWakeLocker.acquire(getContext());
            }

            if (mSeekPos > 0) {
                long pos = mSeekPos;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMediaPlayerVideoView.seekTo(pos);
                    }
                }, 1000);
                mSeekPos = 0;
            }
        }
    };

    private PLOnInfoListener mOnInfoListener = new PLOnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {
//            MedPlayerUtils.log("mOnInfoListener->onInfo,what=" + what + ",extra=" + extra);
            if (what == PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START) {
                //第一帧视频已成功渲染
                MedPlayerWakeLocker.acquire(getContext());
            } else if (what == PLOnInfoListener.MEDIA_INFO_BUFFERING_START) {
//                mLoadingView.setVisibility(VISIBLE);
            } else if (what == PLOnInfoListener.MEDIA_INFO_BUFFERING_END) {
//                mLoadingView.setVisibility(GONE);
            }
        }
    };

    private PLOnBufferingUpdateListener mOnBufferingUpdateListener = new PLOnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(int precent) {
            MedPlayerUtils.log("onBufferingUpdate->precent=" + precent);
        }
    };

    private PLOnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLOnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
            MedPlayerUtils.log("onVideoSizeChanged->width=" + width + ",height=" + height);
        }
    };

}
