package com.medlinker.player.component;

import androidx.annotation.Keep;
import androidx.fragment.app.FragmentActivity;

import com.medlinker.player.entity.MedVideoInfo;
import com.medlinker.player.view.MedPlayerView;

/**
 * 短视频播放组件
 *
 * @author zhangquan
 */
@Keep
public interface MedPlayerComponent {
    /**
     * 初始化完成
     *
     * @param activity
     * @param playerView
     * @param videoInfo  视频信息
     */
    void onInited(FragmentActivity activity, MedPlayerView playerView, MedVideoInfo videoInfo);

    /**
     * 视频播放中
     */
    void onPlay();

    /**
     * 视频暂停中
     */
    void onPause();

    /**
     * 播放完成
     */
    void onComplete();

    /**
     * 横竖屏切换
     *
     * @param landscape true 横屏播放  false竖屏播放
     */
    void onFullScreen(boolean landscape);

    /**
     * 缓冲进度
     *
     * @param bufferPercentage 缓冲进度 0-100
     */
    void onBufferChanged(int bufferPercentage);

    /**
     * 视频时长
     *
     * @param duration 毫秒
     */
    void setDuration(long duration);

    /**
     * 当前播放时长
     *
     * @param currentDuration 毫秒
     */
    void setCurrentPosition(long currentDuration);

    void onActivityCreate(FragmentActivity activity);

    void onActivityResume();

    void onActivityPause();

    void onActivityStop();

    void onActivityDestory();
}
