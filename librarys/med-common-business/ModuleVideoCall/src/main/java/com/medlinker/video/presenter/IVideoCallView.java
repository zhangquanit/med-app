package com.medlinker.video.presenter;

import android.content.Context;

import com.medlinker.video.widget.TRTCVideoLayoutManager;

/**
 * @author hmy
 */
public interface IVideoCallView {
    Context getContext();

    TRTCVideoLayoutManager getTRTCVideoLayoutManager();

    void onPatientEnter();

    void onPatientExit();

    int[] getMainVideoParam();

    int[] getRemoteVideoParam();
}
