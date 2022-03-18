package com.medlinker.video;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cn.glidelib.glide.GlideApp;
import com.google.gson.Gson;
import com.medlinker.lib.utils.MedDeviceUtil;
import com.medlinker.lib.utils.MedImmersiveModeUtil;
import com.medlinker.lib.utils.MedToastUtil;
import com.medlinker.video.entity.TRTCVideoStream;
import com.medlinker.video.entity.VideoRoomEntity;
import com.medlinker.video.floating.TRTCVideoLayoutManagerHolder;
import com.medlinker.video.floating.VideoCallFloatingService;
import com.medlinker.video.presenter.IVideoCallView;
import com.medlinker.video.presenter.VideoCallPresenter;
import com.medlinker.video.utils.NotificationUtil;
import com.medlinker.video.viewmodel.VideoCallViewModel;
import com.medlinker.video.widget.TRTCVideoLayout;
import com.medlinker.video.widget.TRTCVideoLayoutManager;
import com.tencent.liteav.renderer.TXCGLSurfaceView;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 视频聊天（https://www.cnblogs.com/cxk1995/p/7824375.html）
 *
 * @author hmy
 */
//@Route(path = "/call/videocall")
public class VideoCallActivity extends FragmentActivity implements View.OnClickListener, IVideoCallView {

    public static final int CODE_MANAGE_OVERLAY_PERMISSION = 10;
    private TRTCVideoLayoutManager mTRTCVideoLayout;
    public static final String DATA_KEY = "DATA_KEY";

    private View mPatientInfoLayout;
    private View mLlWaitTip;
    private ImageView mPatientHeadIv;
    private TextView mWaitInfoTv;
    private TextView mPatientInfoTv;
    private ImageView mZoomVideoBtn;
    private TextView mWaitingVideoTv;
    private TextView mCloseTv;
    private TextView mTimeTv;
    private View mSwitchCameraLayout;
    private View mBottomLayout;

    private VideoCallPresenter mPresenter;
    private VideoCallViewModel mViewModel;

    private VideoRoomEntity mIntentData;
    private TRTCCloudListener mTRTCListener;    // 回调监听
    private TRTCCloud mTRTCCloud;   // SDK 核心类
    private boolean mIsConnectedVideo = false; //患者视频是否已经连接
    private boolean mIsBindService = false;
    private boolean mIsFinish = false;
    private boolean mIsBackground = false;

    private Disposable mTimeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIntentData = getIntent().getParcelableExtra(DATA_KEY);
        if (mIntentData == null) {
            String json = getIntent().getStringExtra("json");
            mIntentData = new Gson().fromJson(json, VideoRoomEntity.class);
        }
        if (mIntentData == null) {
            finish();
            return;
        }
        getViewModel().setIntentEntity(mIntentData);

        initSDK();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MedImmersiveModeUtil.setStatusBarTransparent(this);
        }
        setContentView(R.layout.activity_video_call);
        initView();
        initObserver();
        initCallView(false);
        reportVideoSend();
        //checkPermission();
    }

    private void initSDK() {
        mTRTCListener = new TRTCCloudListenerImpl(this);
        mTRTCCloud = TRTCCloud.sharedInstance(this);
        mTRTCCloud.setListener(mTRTCListener);
        getPresenter().initTRTC(mTRTCCloud, mIntentData);
    }

    @SuppressLint("CheckResult")
    private void checkPermission() {
//        new RxPermissions(this)
//                .request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        if (aBoolean) {
//                            checkFloatingPermission();
//                        } else {
//                            MedToastUtil.showMessage("开启权限才能使用该功能");
//                            finish();
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                    }
//                });
        checkFloatingPermission();
    }

    private void checkFloatingPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
            initCallView(false);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())), CODE_MANAGE_OVERLAY_PERMISSION);
        } else {
            initCallView(true);
            enterRoom();
        }
    }

    private void initView() {
        mPatientInfoLayout = findViewById(R.id.layout_patient_info);
        mLlWaitTip = findViewById(R.id.ll_wait_tip);
        mPatientHeadIv = findViewById(R.id.iv_patient_head);
        mPatientInfoTv = findViewById(R.id.tv_patient_info);
        mWaitInfoTv = findViewById(R.id.tv_wait);
        mZoomVideoBtn = findViewById(R.id.btn_zoom_video);
        mTRTCVideoLayout = findViewById(R.id.layout_trtc_video_view);
        mWaitingVideoTv = findViewById(R.id.tv_waiting_video);
        mCloseTv = findViewById(R.id.tv_close);
        mSwitchCameraLayout = findViewById(R.id.layout_switch_camera);
        mTimeTv = findViewById(R.id.tv_time);
        mBottomLayout = findViewById(R.id.layout_bottom);

        findViewById(R.id.btn_zoom_video).setOnClickListener(this);
        findViewById(R.id.btn_close_video).setOnClickListener(this);
        findViewById(R.id.btn_switch_camera).setOnClickListener(this);

        mTRTCVideoLayout.setMySelfUserId(getPresenter().getTRTCParams().userId);
        // 视频悬浮框使用
        TRTCVideoLayoutManagerHolder.init(mTRTCVideoLayout);
        TRTCVideoLayoutManagerHolder.instance().setIsExistVideoCallActivity(true);
    }

    private void initCallView(boolean havePermission) {
        mTimeTv.setVisibility(havePermission ? View.VISIBLE : View.GONE);
        mBottomLayout.setVisibility(havePermission ? View.VISIBLE : View.GONE);
        mPatientInfoLayout.setVisibility(havePermission ? View.VISIBLE : View.GONE);
        if (havePermission) {
            int placeholder = mIntentData.isWoman() ? R.drawable.ic_patient_default_woman : R.drawable.ic_patient_default;
            GlideApp.with(this).load(mIntentData.getAvatar())
                    .placeholder(placeholder)
                    .error(placeholder)
                    .dontAnimate()
                    .into(mPatientHeadIv);
            mPatientInfoTv.setText(mIntentData.getUserInfo());
            mWaitInfoTv.setText(mIntentData.getWaitInfo());
        }
        mWaitingVideoTv.setText(mIntentData.isDoctor()
                ? getResources().getString(R.string.wait_patient_conn)
                : getResources().getString(R.string.wait_doctor_conn));
    }

    private void initObserver() {
        getViewModel().videoSendLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isSuccess) {
                if (isSuccess != null && !isSuccess) {
                    delayFinish();
                }
                if (isSuccess != null && isSuccess) {
                    checkPermission();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_zoom_video) {
            TRTCVideoLayout videoLayout = mTRTCVideoLayout.findFirstUserCloudView();
            if (null != videoLayout) {
                videoLayout.setVisibility(View.GONE);
                int[] l = new int[2];
                videoLayout.getLocationOnScreen(l);
                TRTCVideoLayoutManagerHolder.instance().setSmallTRTCVideoLayoutX(l[0]);
                TRTCVideoLayoutManagerHolder.instance().setSmallTRTCVideoLayoutY(l[1] - MedDeviceUtil.getStatusBarHeight(this));
            }

            moveTaskToBack(true);//最小化Activity
            openFloatingCall();
        } else if (v.getId() == R.id.btn_close_video) {
            if (mIsConnectedVideo) {
                MedToastUtil.showMessage("已挂断，结束视频");
            } else {
                MedToastUtil.showMessage("已取消，结束视频");
            }
            getPresenter().exitRoom();
            delayFinish();
        } else if (v.getId() == R.id.btn_switch_camera) {
            getPresenter().switchCamera();
        }
    }

    /**
     * 悬浮框切回全屏处理
     */
    private void addTRTCVideoLayoutView() {
        if (null != mTRTCVideoLayout) {
            TRTCVideoLayout trtcVideoLayout = mTRTCVideoLayout.findFirstUserCloudView();
            if (null == trtcVideoLayout) {
                return;
            }
            trtcVideoLayout.setVisibility(View.VISIBLE);
            TXCloudVideoView cloudVideoView = trtcVideoLayout.getVideoView();
            if (null == cloudVideoView) {
                return;
            }

            String mUserId = mTRTCVideoLayout.findFirstUserCloudUserId();
            if (!TextUtils.isEmpty(mUserId) && mUserId.equals(mTRTCVideoLayout.getSelfUserId())) {
                TXCGLSurfaceView mTXCGLSurfaceView = cloudVideoView.getGLSurfaceView();
                if (mTXCGLSurfaceView != null) {
                    if (mTXCGLSurfaceView.getParent() != null) {
                        ((ViewGroup) mTXCGLSurfaceView.getParent()).removeView(mTXCGLSurfaceView);
                    }
                    cloudVideoView.addVideoView(mTXCGLSurfaceView);
                }
            } else {
                TextureView mTextureView = cloudVideoView.getVideoView();
                if (mTextureView != null) {
                    if (mTextureView.getParent() != null) {
                        ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
                    }
                    cloudVideoView.addVideoView(mTextureView);

                }
            }
        }
    }

    /**
     * 打开悬浮窗
     */
    private void openFloatingCall() {
        moveTaskToBack(true);//最小化Activity
        Intent intent = new Intent(this, VideoCallFloatingService.class);
        intent.putExtra(DATA_KEY, mIntentData);
        bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
        mIsBindService = true;
    }

    private void enterRoom() {
        getPresenter().enterRoom(mViewModel.getRecordId());
    }

    /**
     * 关闭悬浮窗
     */
    private void closeFloatingCall() {
        try {
            if (mIsBindService) {
                unbindService(mVideoServiceConnection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIsBindService = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mIsBindService) {
            closeFloatingCall();
        }
        VideoRoomEntity videoRoomEntity = getIntent().getParcelableExtra(DATA_KEY);
        if (videoRoomEntity != null) {
            mIntentData = videoRoomEntity;
        }
        if (mIsConnectedVideo) {
            NotificationUtil.createCallKeepAliveNotification(getApplication(), mIntentData);
        }
        addTRTCVideoLayoutView();
        TRTCVideoLayoutManagerHolder.instance().setFloatingWindowMode(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        closeFloatingCall();
        addTRTCVideoLayoutView();
        TRTCVideoLayoutManagerHolder.instance().setFloatingWindowMode(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsBackground = false;
        if (mIsConnectedVideo) {
            startRefreshTime();
        }
        closeFloatingCall();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsBackground = true;
        if (!mIsFinish && mIsConnectedVideo) {
            openFloatingCall();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public TRTCVideoLayoutManager getTRTCVideoLayoutManager() {
        return mTRTCVideoLayout;
    }

    @Override
    public void onPatientEnter() {
        mIsConnectedVideo = true;
        mPatientInfoLayout.setVisibility(View.GONE);
        mLlWaitTip.setVisibility(View.GONE);
        mSwitchCameraLayout.setVisibility(View.VISIBLE);
        mZoomVideoBtn.setVisibility(View.VISIBLE);
        mCloseTv.setText("挂断");

        getViewModel().reportVideoStart();
        startRefreshTime();

        //界面在后台时，接通后显示浮窗
        if (mIsBackground) {
            openFloatingCall();
        }
    }

    @Override
    public void onPatientExit() {
        mIsConnectedVideo = false;
        MedToastUtil.showMessage("对方挂断，结束视频");
        delayFinish();
    }

    @Override
    public int[] getMainVideoParam() {
        int[] param = new int[4];
        param[0] = mTRTCVideoLayout.getLeft();
        param[1] = mTRTCVideoLayout.getTop();
        param[2] = mTRTCVideoLayout.getWidth();
        param[3] = mTRTCVideoLayout.getHeight();
        return param;
    }

    @Override
    public int[] getRemoteVideoParam() {
        int[] param = new int[4];
        param[0] = mTRTCVideoLayout.getWidth() - mTRTCVideoLayout.getSubMargin() - mTRTCVideoLayout.getSubWith();
        param[1] = mTRTCVideoLayout.getSubMargin();
        param[2] = mTRTCVideoLayout.getSubWith();
        param[3] = mTRTCVideoLayout.getSubHeight();
        return param;
    }

    private void startRefreshTime() {
        mTimeTv.setText(mIntentData.getRemainingTimeFormat());
        stopTimer();
        mTimeDisposable = Observable.interval(1, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mIntentData.setRemainingTime(mIntentData.getRemainingTime() - 1);
                        mTimeTv.setText(mIntentData.getRemainingTimeFormat());
                        if (mIntentData.isTimeOut()) {
                            closeFloatingCall();
                            mTimeDisposable.dispose();
                            MedToastUtil.showMessage(String.format("视频总时长超过%s，结束视频", mIntentData.getTotalTimeStr()));
                            getPresenter().exitRoom();
                            delayFinish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void delayFinish() {
        mIsFinish = true;
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void reportVideoSend() {
        getViewModel().reportVideoSend(mIntentData.getRoomId());
    }

    private static class TRTCCloudListenerImpl extends TRTCCloudListener {
        private WeakReference<VideoCallActivity> mWefActivity;

        TRTCCloudListenerImpl(VideoCallActivity activity) {
            super();
            mWefActivity = new WeakReference<>(activity);
        }

        /**
         * 加入房间回调
         *
         * @param elapsed 加入房间耗时，单位毫秒
         */
        @Override
        public void onEnterRoom(long elapsed) {
            final VideoCallActivity activity = mWefActivity.get();
            if (activity != null) {
                if (elapsed >= 0) {
//                    activity.reportVideoSend();
                    activity.getPresenter().updateCloudMixtureParams();
                    activity.getPresenter().setMixTranscodingConfig();
                    NotificationUtil.createCallKeepAliveNotification(activity.getApplication(), activity.mIntentData);
                } else {
                    MedToastUtil.showMessage("加入房间失败");
                    activity.getPresenter().exitRoom();
                    activity.delayFinish();
                }
            }
        }

        /**
         * 有新的主播{@link TRTCCloudDef#TRTCRoleAnchor}加入了当前视频房间
         * 该方法会在主播加入房间的时候进行回调，此时音频数据会自动拉取下来，但是视频需要有 View 承载才会开始渲染。
         * 为了更好的交互体验，Demo 选择在 onUserVideoAvailable 中，申请 View 并且开始渲染。
         * 您可以根据实际需求，选择在 onUserEnter 还是 onUserVideoAvailable 中发起渲染。
         *
         * @param userId 用户标识
         */
        @Override
        public void onUserEnter(String userId) {
            VideoCallActivity activity = mWefActivity.get();
            if (activity != null) {
                activity.onPatientEnter();
            }
        }

        /**
         * 若当对应 userId 的主播有上行的视频流的时候，该方法会被回调，available 为 true；
         * 若对应的主播通过{@link TRTCCloud#muteLocalVideo(boolean)}，该方法也会被回调，available 为 false。
         * Demo 在收到主播有上行流的时候，会通过{@link TRTCCloud#startRemoteView(String, TXCloudVideoView)} 开始渲染
         * Demo 在收到主播停止上行的时候，会通过{@link TRTCCloud#stopRemoteView(String)} 停止渲染，并且更新相关 UI
         *
         * @param userId    用户标识
         * @param available 画面是否开启
         */
        @Override
        public void onUserVideoAvailable(final String userId, boolean available) {
            TRTCVideoStream stream = new TRTCVideoStream();
            stream.userId = userId;
            stream.streamType = TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG;

            VideoCallActivity activity = mWefActivity.get();
            if (activity != null) {
                activity.getPresenter().startSDKRender(userId, available, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
                if (available) {
                    // 记录当前流的类型，以供设置混流参数时使用：
                    if (!activity.getPresenter().isContainVideoStream(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG)) {
                        activity.getPresenter().getTRTCVideoStreams().add(stream);
                    }
                } else {
                    // 移除当前记录，以供设置混流参数时使用：
                    activity.getPresenter().removeVideoStream(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
                }
                // 根据当前视频流的状态，更新混流参数
                activity.getPresenter().updateCloudMixtureParams();
            }
        }

        /**
         * 主播{@link TRTCCloudDef#TRTCRoleAnchor}离开了当前视频房间
         * 主播离开房间，要释放相关资源。
         * 1. 释放主画面、辅路画面
         * 2. 如果您有混流的需求，还需要重新发起混流，保证混流的布局是您所期待的。
         *
         * @param userId 用户标识
         * @param reason 离开原因代码，区分用户是正常离开，还是由于网络断线等原因离开。
         */
        @Override
        public void onUserExit(String userId, int reason) {
            VideoCallActivity activity = mWefActivity.get();
            if (activity != null) {
                //停止观看画面
                activity.mTRTCCloud.stopRemoteView(userId);
                activity.mTRTCCloud.stopRemoteSubStreamView(userId);

                // 回收分配的渲染的View
                activity.mTRTCVideoLayout.recyclerCloudViewView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
                activity.mTRTCVideoLayout.recyclerCloudViewView(userId, TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SUB);

                // 更新混流参数
                activity.getPresenter().updateCloudMixtureParams();
                activity.onPatientExit();
            }
        }

        /**
         * ERROR 大多是不可恢复的错误，需要通过 UI 提示用户
         * 然后执行退房操作
         *
         * @param errCode   错误码 TXLiteAVError
         * @param errMsg    错误信息
         * @param extraInfo 扩展信息字段，个别错误码可能会带额外的信息帮助定位问题
         */
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            MedToastUtil.showMessage("onError: " + errMsg + "[" + errCode + "]");
            final VideoCallActivity activity = mWefActivity.get();
            if (activity != null) {
                activity.getPresenter().exitRoom();
                activity.finish();
            }
        }
    }

    ServiceConnection mVideoServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取服务的操作对象
//            FloatVideoWindowService.MyBinder binder = (FloatVideoWindowService.MyBinder) service;
//            binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationUtil.cancelCallKeepAliveNotification(getApplication());
        getPresenter().exitRoom();
        mTRTCCloud.setListener(null);
        TRTCCloud.destroySharedInstance();
        stopTimer();
        TRTCVideoLayoutManagerHolder.instance().setIsExistVideoCallActivity(false);
        TRTCVideoLayoutManagerHolder.destroy();
        if (TRTCVideoLayoutManagerHolder.instance().isCanVideoCall()) {
            VideoCallManager.INSTANCE.getModuleService().onVideoCallFinished();
        }
    }

    private void stopTimer() {
        if (mTimeDisposable != null && !mTimeDisposable.isDisposed()) {
            mTimeDisposable.dispose();
        }
    }

    private VideoCallPresenter getPresenter() {
        if (mPresenter == null) {
            mPresenter = new VideoCallPresenter(this);
        }
        return mPresenter;
    }

    public VideoCallViewModel getViewModel() {
        if (mViewModel == null) {
            mViewModel = new ViewModelProvider(this).get(VideoCallViewModel.class);
        }
        return mViewModel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_MANAGE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
                MedToastUtil.showMessage("授权悬浮窗权限失败，请重试");
                finish();
            } else {
                initCallView(true);
                enterRoom();
            }
        }
    }

    @Override
    public void finish() {
        getViewModel().reportVideoEnd();
        super.finish();
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Home触发的回调方法
     */
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (TRTCVideoLayoutManagerHolder.instance().isFloatingWindowMode()) {

        }
    }
}