package com.medlinker.player.component;

import androidx.fragment.app.FragmentActivity;

import com.medlinker.player.entity.MedVideoInfo;
import com.medlinker.player.view.MedPlayerView;

/**
 * @author zhangquan
 */
public abstract class SimpleMedPlayerComponent implements MedPlayerComponent {
    @Override
    public void onInited(FragmentActivity activity, MedPlayerView playerView, MedVideoInfo videoInfo) {

    }

    @Override
    public void onPlay() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onFullScreen(boolean landscape) {

    }

    @Override
    public void onBufferChanged(int bufferPercentage) {

    }

    @Override
    public void setDuration(long duration) {

    }

    @Override
    public void setCurrentPosition(long currentDuration) {

    }

    @Override
    public void onActivityCreate(FragmentActivity activity) {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityStop() {

    }

    @Override
    public void onActivityDestory() {

    }
}
