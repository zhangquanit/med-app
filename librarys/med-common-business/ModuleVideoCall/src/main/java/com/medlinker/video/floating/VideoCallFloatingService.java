package com.medlinker.video.floating;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.medlinker.lib.log.LogUtil;
import com.medlinker.lib.utils.MedDimenUtil;
import com.medlinker.lib.utils.MedScreenUtil;
import com.medlinker.video.VideoCallActivity;
import com.medlinker.video.VideoCallManager;
import com.medlinker.video.entity.VideoRoomEntity;
import com.medlinker.video.utils.NotificationUtil;
import com.medlinker.video.widget.TRTCVideoLayout;
import com.medlinker.video.widget.TRTCVideoLayoutManager;
import com.tencent.liteav.renderer.TXCGLSurfaceView;
import com.tencent.rtmp.ui.TXCloudVideoView;


import java.util.List;

/**
 * 视频通话悬浮窗服务
 */
public class VideoCallFloatingService extends Service {

    private static final String TAG = "vcfs";
    public static final String DATA_KEY = "DATA_KEY";

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    TXCloudVideoView mFloatingLayout;
    /**
     * 当前用户id
     */
    private String mUserId;
    private int mStreamType;

    public static final String USER_ID = "user_id";
    public static final String STREAM_TYPE = "stream_type";

    private VideoRoomEntity mIntentData;

    @Override
    public void onCreate() {
        super.onCreate();
        initWindow();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initWindow() {
        LogUtil.d(TAG, "init window");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(this)) {
            return;
        }
        // 获取WindowManager服务
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        if (mWindowManager == null) {
            return;
        }
        // 新建悬浮窗控件
        mFloatingLayout = new TXCloudVideoView(getApplicationContext());
        mFloatingLayout.setOnTouchListener(new FloatingOnTouchListener());
        mFloatingLayout.setOnClickListener(mOnClickListener);

        // 设置LayoutParam
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
//        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = MedDimenUtil.dip2px(getApplicationContext(), 100);
        mLayoutParams.height = MedDimenUtil.dip2px(getApplicationContext(), 175);
        syncTRTCVideoLayoutLocation(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mFloatingLayout.setEnabled(true);
        mUserId = intent.getStringExtra(USER_ID);
        mStreamType = intent.getIntExtra(STREAM_TYPE, -1);
        mIntentData = intent.getParcelableExtra(DATA_KEY);
        initVideoFloating();

        Notification notification = NotificationUtil.createCallKeepAliveNotification(getApplication(), mIntentData);
        startForeground(NotificationUtil.NOTIFICATION_FLAG, notification);
        return new FloatingBinder();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        mFloatingLayout.removeVideoView();
    }

    public class FloatingBinder extends Binder {
        public VideoCallFloatingService getService() {
            return VideoCallFloatingService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 同步悬浮框位置
     *
     * @param isAdd
     */
    private void syncTRTCVideoLayoutLocation(boolean isAdd) {
        TRTCVideoLayout trtcVideoLayout = getTRTCVideoLayout();
        if (null == trtcVideoLayout) {
            int screenW = MedScreenUtil.getScreenWidth(getApplicationContext());
            mLayoutParams.x = screenW - mLayoutParams.width - 20;
            mLayoutParams.y = 0;
        } else {
            int x = TRTCVideoLayoutManagerHolder.instance().getSmallTRTCVideoLayoutX();
            int y = TRTCVideoLayoutManagerHolder.instance().getSmallTRTCVideoLayoutY();
            mLayoutParams.x = x;
            mLayoutParams.y = y;
        }

        if (isAdd) {
            // 将悬浮窗控件添加到WindowManager
            mWindowManager.addView(mFloatingLayout, mLayoutParams);
        } else {
            mWindowManager.updateViewLayout(mFloatingLayout, mLayoutParams);
        }
    }

    private TRTCVideoLayout getTRTCVideoLayout() {
        TRTCVideoLayoutManagerHolder videoLayoutHolder = TRTCVideoLayoutManagerHolder.instance();
        if (videoLayoutHolder.checkNull()) {
            LogUtil.d(TAG, "TRTCVideoLayoutManager is null");
            return null;
        }

        TRTCVideoLayoutManager manager = videoLayoutHolder.getTRTCVideoLayoutManager();
        return manager.findFirstUserCloudView();
    }

    private void initVideoFloating() {
        LogUtil.d(TAG, "start init video floating");
        TRTCVideoLayout trtcVideoLayout = getTRTCVideoLayout();
        if (null == trtcVideoLayout) {
            LogUtil.d(TAG, "TXCloudVideoView is null");
            return;
        }

        TXCloudVideoView cloudVideoView = trtcVideoLayout.getVideoView();
        if (null == cloudVideoView) {
            LogUtil.d(TAG, "cloudVideoView is null");
            return;
        }

        TRTCVideoLayoutManager manager = TRTCVideoLayoutManagerHolder.instance().getTRTCVideoLayoutManager();
        mUserId = manager.findFirstUserCloudUserId();
        if (!TextUtils.isEmpty(mUserId) && mUserId.equals(manager.getSelfUserId())) {
            TXCGLSurfaceView mTXCGLSurfaceView = cloudVideoView.getGLSurfaceView();
            if (mTXCGLSurfaceView != null && mTXCGLSurfaceView.getParent() != null) {
                ((ViewGroup) mTXCGLSurfaceView.getParent()).removeView(mTXCGLSurfaceView);
                mFloatingLayout.addVideoView(mTXCGLSurfaceView);
            }
        } else {
            TextureView mTextureView = cloudVideoView.getVideoView();
            if (mTextureView != null && mTextureView.getParent() != null) {
                ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
                mFloatingLayout.addVideoView(mTextureView);
            }
        }

        syncTRTCVideoLayoutLocation(false);
        TRTCVideoLayoutManagerHolder.instance().setFloatingWindowMode(true);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int[] l = new int[2];
            mFloatingLayout.getLocationOnScreen(l);
            int x = l[0];
            int y = l[1];
            TRTCVideoLayoutManagerHolder.instance().syncTRTCVideoLayoutLocation(x, y);

            mFloatingLayout.setEnabled(false);
            mFloatingLayout.removeVideoView();
            mWindowManager.removeViewImmediate(mFloatingLayout);

            Intent intent = new Intent(VideoCallFloatingService.this, VideoCallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.putExtra(DATA_KEY, mIntentData);
            startActivity(intent);
        }
    };

    protected void moveToFront() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> recentTasks = manager.getRunningTasks(Integer.MAX_VALUE);
        for (int i = 0; i < recentTasks.size(); i++) {
            if (recentTasks.get(i).baseActivity.toShortString().contains("net.medlinker.medlinker/com.medlinker.video.VideoCallActivity")) {
                manager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                break;
            }
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {

        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    mLayoutParams.x = mLayoutParams.x + movedX;
                    mLayoutParams.y = mLayoutParams.y + movedY;

                    // 更新悬浮窗控件布局
                    mWindowManager.updateViewLayout(view, mLayoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mFloatingLayout != null) {
            try {
                // 移除悬浮窗口
                mFloatingLayout.removeVideoView();
                mWindowManager.removeViewImmediate(mFloatingLayout);
            } catch (Exception w) {
                w.printStackTrace();
            }

            mFloatingLayout = null;
        }
        TRTCVideoLayoutManagerHolder.instance().setFloatingWindowMode(false);
        if (TRTCVideoLayoutManagerHolder.instance().isCanVideoCall()) {
            VideoCallManager.INSTANCE.getModuleService().onVideoCallFinished();
        }
    }
}
