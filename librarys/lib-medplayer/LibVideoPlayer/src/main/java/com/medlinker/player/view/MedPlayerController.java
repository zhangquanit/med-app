package com.medlinker.player.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.player.component.MedPlayerComponent;
import com.medlinker.player.entity.ControllerStatus;
import com.medlinker.player.util.MedPlayerConfig;
import com.medlinker.player.util.MedPlayerUtils;
import com.pili.pldroid.player.IMediaController;

/**
 * 播放器控制器
 */
public class MedPlayerController implements IMediaController {
    protected MediaPlayerControl mPlayer;
    private MedControllerView mControllView;
    private ViewGroup mAnchor;
    private ProgressBar mProgress;

    protected AudioManager mAM;
    private Runnable mLastSeekBarRunnable;
    protected static final int FADE_OUT = 1;
    protected static final int SHOW_PROGRESS = 2;
    protected static int sDefaultTimeout = 3000;
    private static final int SEEK_TO_POST_DELAY_MILLIS = 200;
    protected boolean mShowing;
    protected boolean mDragging;
    protected long mDuration;
    private boolean mInstantSeeking = true;
    private Context mContext;
    private MedPlayerComponent medPlayerComponent;
    private ControllerStatus mStatus = ControllerStatus.NORMAL;


    public MedPlayerController(Context context) {
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        mControllView = new MedControllerView(mContext);
        mProgress = mControllView.getProgressBar();
        mControllView.getProgressBar().setOnSeekBarChangeListener(mSeekListener);
        mControllView.setViewsOnClickListener(mViewsOnClickListener);
        mControllView.setPlayerController(this);
    }

    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT: //隐藏
                    hideView();
                    break;
                case SHOW_PROGRESS: //更新进度条
                    if (!mPlayer.isPlaying()) {
                        return;
                    }
                    pos = setProgress();
                    if (pos == -1) {
                        return;
                    }

                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
//                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        sendMessageDelayed(msg, 50);
                        updatePausePlay();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onTouchEvent(MotionEvent event) {
        if (mPlayer == null) return false;
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                show();
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }


        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;

            final long newposition = (mDuration * progress) / MedControllerView.MAX;
            String time = MedPlayerUtils.generateTime(newposition);
            if (mInstantSeeking) {
                mHandler.removeCallbacks(mLastSeekBarRunnable);
                mLastSeekBarRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mPlayer != null) {
                            mPlayer.seekTo(newposition);
                        }
                    }
                };
                mHandler.postDelayed(mLastSeekBarRunnable, SEEK_TO_POST_DELAY_MILLIS);
            }

            mControllView.setCurrentTime(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking)
                mPlayer.seekTo(mDuration * bar.getProgress() / 1000);
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (null != medPlayerComponent && duration > 0) {
            medPlayerComponent.onBufferChanged(mPlayer.getBufferPercentage());
            medPlayerComponent.setDuration(duration);
            medPlayerComponent.setCurrentPosition(position);
        }
        mDuration = duration;
        setTime(MedPlayerUtils.generateTime(position), MedPlayerUtils.generateTime(mDuration));
        return position;
    }

    protected void setTime(String currentTime, String endTime) {
        mControllView.setCurrentTime(currentTime);
        mControllView.setEndTime(endTime);
    }

    private View.OnClickListener mViewsOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Object tag = v.getTag(v.getId());
                int caseId = (int) tag;
                switch (caseId) {
                    case MedPlayerConfig.ID_PAUSE_START:// 播放/暂停按钮
                        doPauseResume();
                        show(sDefaultTimeout);
                        break;
                    case MedPlayerConfig.ID_TOP_BACK: // 返回按钮
                        if (isFullScreenMode()) {
                            switchFullScreen();
                        } else {
                            ((AppCompatActivity) mContext).onBackPressed();
                        }
                        break;
                    case MedPlayerConfig.ID_FULL_SCREEN_SWITCH: // 横竖屏切换
                        switchFullScreen();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    protected void updatePausePlay() {
        if (mControllView == null || mPlayer == null) {
            return;
        }
        mControllView.updatePausePlay(mPlayer.isPlaying());
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            if (null != medPlayerComponent) {
                medPlayerComponent.onPause();
            }
        } else {
            mPlayer.start();
            if (null != medPlayerComponent) {
                medPlayerComponent.onPlay();
            }
        }
        updatePausePlay();
    }

    public void setCompletedState() {
        mControllView.reset();
    }

    public void setTitle(String title) {
        mControllView.setTitle(title);
    }

    public void setScreenMode(boolean isFullScreen) {
        mControllView.setScreenMode(isFullScreen);
    }

    public void switchFullScreen() {
        final Context context = mContext;
        if (context != null && context instanceof Activity) {
            int state = isFullScreenMode() ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            ((Activity) mContext).setRequestedOrientation(state);
            if (null != medPlayerComponent) {
                medPlayerComponent.onFullScreen(isFullScreenMode());
            }
        }
    }

    public boolean isFullScreenMode() {
        return ((Activity) mContext).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    public void setInstantSeeking(boolean seekWhenDragging) {
        mInstantSeeking = seekWhenDragging;
    }

    public MedControllerView getControllView() {
        return mControllView;
    }

    protected void setPlayComponent(MedPlayerComponent component) {
        this.medPlayerComponent = component;
    }

    public void setControllStatus(ControllerStatus status) {
        this.mStatus = status;
        if (mStatus == ControllerStatus.ALWAYS_SHOW) {
            mControllView.setVisibility(View.VISIBLE);
        } else if (mStatus == ControllerStatus.ALWAYS_HIDE) {
            mControllView.setVisibility(View.GONE);
        }
    }

    private void showOrHideControllView(boolean visibile) {
        if (mStatus == ControllerStatus.NORMAL) {
            mControllView.setVisibility(visibile ? View.VISIBLE : View.GONE);
        } else if (mStatus == ControllerStatus.ALWAYS_SHOW) {
            mControllView.setVisibility(View.VISIBLE);
        } else if (mStatus == ControllerStatus.ALWAYS_HIDE) {
            mControllView.setVisibility(View.GONE);
        }
    }

    //-------------------------- IMediaController实现 -----------------------------------------------------
    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayerControl) {
        mPlayer = mediaPlayerControl;
        updatePausePlay();
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    @Override
    public void show(int timeout) {
        if (!mShowing) {
            showOrHideControllView(true);
//            mControllView.setVisibility(View.VISIBLE);
            mShowing = true;
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
                    timeout);
        }
    }

    @Override
    public void hide() {
        hideView();
    }

    public void hideView() {
        if (mShowing) {
            showOrHideControllView(false);
//            mControllView.setVisibility(View.GONE);
            if (mStatus != ControllerStatus.ALWAYS_SHOW) {
                mHandler.removeMessages(SHOW_PROGRESS);
                mShowing = false;
            }

        }
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public void setAnchorView(View view) {
        try {
            mAnchor = (ViewGroup) view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mAnchor == null) {
            sDefaultTimeout = 0; // show forever
        }
        if (!mShowing) {

            ViewGroup.LayoutParams layoutParams =
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ViewParent parent = mControllView.getParent();
            if (null != parent) {
                ViewGroup viewGroup = (ViewGroup) parent;
                viewGroup.removeView(mControllView);
            }
            mAnchor.addView(mControllView, layoutParams);
            mShowing = true;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        mControllView.setEnabled(enabled);
    }

}
