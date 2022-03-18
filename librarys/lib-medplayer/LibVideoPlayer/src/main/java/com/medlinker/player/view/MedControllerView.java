package com.medlinker.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.medlinker.player.R;
import com.medlinker.player.util.MedPlayerConfig;
import com.pili.pldroid.player.IMediaController;

/**
 * 播放器控制组件
 */
public class MedControllerView extends FrameLayout implements View.OnClickListener {

    private TextView mCurrentTimeView, mTotalTimeView;
    private ImageView mPauseStartIcon, mFullScreenSwitchIcon;
    private MedControllerTopView mTopView;
    private SeekBar mProgressBar;
    private OnClickListener mOnClickListener;
    public static final int MAX = 1000;
    private IMediaController mController;

    public MedControllerView(Context context) {
        super(context);
        setupViews(context);
    }

    public MedControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupViews(context);
    }

    public MedControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews(context);
    }

    private void setupViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.med_video_player_controller, this);
        mTopView = view.findViewById(R.id.top_view);
        mTopView.setViewsOnClickListener(this);
        //播放/暂停
        mPauseStartIcon = view.findViewById(R.id.video_start_pause_image_view);
        mPauseStartIcon.setOnClickListener(this);
        mPauseStartIcon.setTag(R.id.video_start_pause_image_view, MedPlayerConfig.ID_PAUSE_START);
        //全屏
        mFullScreenSwitchIcon = view.findViewById(R.id.video_fullscreen_switch_view);
        mFullScreenSwitchIcon.setOnClickListener(this);
        mFullScreenSwitchIcon.setTag(R.id.video_fullscreen_switch_view, MedPlayerConfig.ID_FULL_SCREEN_SWITCH);
        //当前时间
        mCurrentTimeView = view.findViewById(R.id.video_current_time_text_view);
        //总时长
        mTotalTimeView = view.findViewById(R.id.video_total_time_text_view);
        //进度条
        mProgressBar = view.findViewById(R.id.video_seekbar);
        mProgressBar.setThumbOffset(1);
        mProgressBar.setMax(MAX);
    }



    public void setEndTime(String time) {
        mTotalTimeView.setText(time);
    }

    public void setCurrentTime(String time) {
        mCurrentTimeView.setText(time);
    }

    public void setTitle(String title) {
        mTopView.setTitle(title);
    }

    public void updatePausePlay(boolean playing) {
        mPauseStartIcon.setImageResource(playing ? R.drawable.med_player_pause : R.drawable.med_player_play);
    }

    /**
     * 播放完毕
     */
    public void reset() {
        mProgressBar.setProgress(0);
        setCurrentTime("00:00");
        updatePausePlay(false);
    }

    public void setViewsOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    /**
     * 横竖屏切换
     *
     * @param isFullScreen
     */
    public void setScreenMode(boolean isFullScreen) {
        mTopView.setScreenMode(isFullScreen);
        if (isFullScreen) {
            mFullScreenSwitchIcon.setImageResource(R.drawable.med_player_portraint);
        } else {
            mFullScreenSwitchIcon.setImageResource(R.drawable.med_player_fullscreen);
        }
    }

    public void setPlayerController(IMediaController controller) {
        mController = controller;
    }

    @Override
    public void onClick(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(view);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mController.show();
        return true;
    }

    public SeekBar getProgressBar() {
        return mProgressBar;
    }

    public MedControllerTopView getTopView() {
        return mTopView;
    }

    public ImageView getPlayPauseView() {
        return mPauseStartIcon;
    }

    public ImageView getFullScreenSwitchView() {
        return mFullScreenSwitchIcon;
    }

    public TextView getCurrentTimeView() {
        return mCurrentTimeView;
    }

    public TextView getTotalTimeView() {
        return mTotalTimeView;
    }

}
