package com.medlinker.player.component;

import androidx.fragment.app.FragmentActivity;

import com.medlinker.player.entity.MedVideoInfo;
import com.medlinker.player.view.MedPlayerView;

import java.util.List;

/**
 * 短视频播放组件
 *
 * @author zhangquan
 */
public final class MedPlayerComponentProxy implements MedPlayerComponent {
    private List<MedPlayerComponent> mComponents;
    private boolean mHasComponents;
    private long lastDuration;
    private long lastTotalDuration;
    private long lastBufferPercent;

    public MedPlayerComponentProxy(List<MedPlayerComponent> componentList) {
        this.mComponents = componentList;
        mHasComponents = null != componentList && !componentList.isEmpty();
    }

    @Override
    public void onInited(FragmentActivity activity, MedPlayerView playerView, MedVideoInfo videoInfo) {
        if (mHasComponents) {
            for (MedPlayerComponent model : mComponents) {
                model.onInited(activity, playerView, videoInfo);
            }
        }
    }

    @Override
    public void onPlay() {
        if (mHasComponents) {
            for (MedPlayerComponent model : mComponents) {
                model.onPlay();
            }
        }
    }

    @Override
    public void onPause() {
        if (mHasComponents) {
            for (MedPlayerComponent model : mComponents) {
                model.onPause();
            }
        }
    }

    @Override
    public void onComplete() {
        if (mHasComponents) {
            for (MedPlayerComponent model : mComponents) {
                model.onComplete();
            }
        }
    }

    @Override
    public void onFullScreen(boolean landscape) {
        if (mHasComponents) {
            for (MedPlayerComponent model : mComponents) {
                model.onFullScreen(landscape);
            }
        }
    }

    @Override
    public void onBufferChanged(int bufferPercentage) {
        if (!mHasComponents || lastBufferPercent == bufferPercentage) {
            return;
        }

        for (MedPlayerComponent model : mComponents) {
            model.onBufferChanged(bufferPercentage);
        }
        lastBufferPercent = bufferPercentage;

    }

    @Override
    public void setDuration(long duration) {
        if (!mHasComponents || lastTotalDuration == duration) {
            return;
        }

        for (MedPlayerComponent model : mComponents) {
            model.setDuration(duration);
        }
        lastTotalDuration = duration;
    }

    @Override
    public void setCurrentPosition(long currentDuration) {
        if (!mHasComponents || lastDuration == currentDuration) {
            return;
        }

        for (MedPlayerComponent model : mComponents) {
            model.setCurrentPosition(currentDuration);
        }
        lastDuration = currentDuration;
    }

    @Override
    public void onActivityCreate(FragmentActivity activity) {
        if (mHasComponents) {
            for (MedPlayerComponent model : mComponents) {
                model.onActivityCreate(activity);
            }
        }
    }

    @Override
    public void onActivityResume() {
        if (mHasComponents) {
            for (MedPlayerComponent model : mComponents) {
                model.onActivityResume();
            }
        }
    }

    @Override
    public void onActivityPause() {
        if (mHasComponents) {
            for (MedPlayerComponent model : mComponents) {
                model.onActivityPause();
            }
        }
    }

    @Override
    public void onActivityStop() {

    }

    @Override
    public void onActivityDestory() {
        if (mHasComponents) {
            for (MedPlayerComponent model : mComponents) {
                model.onActivityDestory();
            }
        }
    }
}
