package com.medlinker.video.floating;

import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;

import com.medlinker.video.widget.TRTCVideoLayout;
import com.medlinker.video.widget.TRTCVideoLayoutManager;

/**
 * @author: pengdaosong CreateTime:  2019-09-26 10:29 Email：pengdaosong@medlinker.com Description:
 */
public class TRTCVideoLayoutManagerHolder {

    private static TRTCVideoLayoutManagerHolder sHolder;
    private volatile TRTCVideoLayoutManager mTRTCVideoLayoutManager;

    private int mSmallTRTCVideoLayoutX;
    private int mSmallTRTCVideoLayoutY;
    private boolean mIsFloatingWindowMode = false;
    private boolean mIsExistVideoCallActivity = false;

    public int getSmallTRTCVideoLayoutX() {
        return mSmallTRTCVideoLayoutX;
    }

    public void setSmallTRTCVideoLayoutX(int smallTRTCVideoLayoutX) {
        mSmallTRTCVideoLayoutX = smallTRTCVideoLayoutX;
    }

    public int getSmallTRTCVideoLayoutY() {
        return mSmallTRTCVideoLayoutY;
    }

    public void setSmallTRTCVideoLayoutY(int smallTRTCVideoLayoutY) {
        mSmallTRTCVideoLayoutY = smallTRTCVideoLayoutY;
    }

    private TRTCVideoLayoutManagerHolder() {

    }

    public boolean isFloatingWindowMode() {
        return mIsFloatingWindowMode;
    }

    public void setFloatingWindowMode(boolean floatingWindowMode) {
        mIsFloatingWindowMode = floatingWindowMode;
    }

    public boolean isExistVideoCallActivity() {
        return mIsExistVideoCallActivity;
    }

    public void setIsExistVideoCallActivity(boolean isExistVideoCallActivity) {
        this.mIsExistVideoCallActivity = isExistVideoCallActivity;
    }

    public boolean isCanVideoCall() {
        return !isExistVideoCallActivity() && !isFloatingWindowMode();
    }

    public static TRTCVideoLayoutManagerHolder instance() {
        createInstance();
        return sHolder;
    }

    public static void init(TRTCVideoLayoutManager manager) {
        createInstance();
        sHolder.mTRTCVideoLayoutManager = manager;
    }

    private static void createInstance() {
        if (null == sHolder) {
            synchronized (TRTCVideoLayoutManagerHolder.class) {
                if (null == sHolder) {
                    sHolder = new TRTCVideoLayoutManagerHolder();
                }
            }
        }
    }

    TRTCVideoLayoutManager getTRTCVideoLayoutManager() {
        return mTRTCVideoLayoutManager;
    }

    public static void destroy() {
        if (null != sHolder) {
            sHolder.mTRTCVideoLayoutManager = null;
            sHolder = null;
        }
    }

    boolean checkNull() {
        return null == mTRTCVideoLayoutManager;
    }

    void syncTRTCVideoLayoutLocation(int x, int y) {
        if (null == mTRTCVideoLayoutManager) {
            return;
        }
        TRTCVideoLayout videoLayout = mTRTCVideoLayoutManager.findFirstUserCloudView();
        if (null == videoLayout) {
            return;
        }

        ViewGroup.LayoutParams params = videoLayout.getLayoutParams();
        if (params instanceof LayoutParams) {
            LayoutParams layoutParams = (LayoutParams) params;
            layoutParams.leftMargin = x;
            layoutParams.topMargin = y;
            videoLayout.setLayoutParams(layoutParams);
        }
    }
}
