package com.medlinker.video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.medlinker.lib.permission.ext.MedPermissionUtil;
import com.medlinker.lib.utils.MedImmersiveModeUtil;
import com.medlinker.lib.utils.MedToastUtil;
import com.medlinker.network.retrofit.error.ErrorConsumer;
import com.medlinker.video.entity.VideoCallIntentEntity;
import com.medlinker.video.entity.VideoRoomEntity;
import com.medlinker.video.network.ApiManager;

import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.base.network.SchedulersCompat;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author hmy
 * @time 12/6/21 17:43
 */
public class VideoDataLoadingActivity extends FragmentActivity {

    public static final String DATA_KEY = "DATA_KEY";

    private TextView mLoadingTv;
    private VideoCallIntentEntity mIntentData;
    private VideoRoomEntity mVideoRoomEntity;
    private Disposable mDisposable;
    private Disposable mIntervalDisposable;
    private int mCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_data_loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MedImmersiveModeUtil.setStatusBarTransparent(this);
        }
        mIntentData = getIntent().getParcelableExtra(DATA_KEY);
        if (mIntentData == null) {
            String json = getIntent().getStringExtra("json");
            mIntentData = new Gson().fromJson(json, VideoCallIntentEntity.class);
        }
        if (mIntentData == null) {
            MedToastUtil.showMessageLong("????????????");
            finish();
            return;
        }
        initView();

        getDetail();
    }

    private void initView() {
        mLoadingTv = findViewById(R.id.tv_loading);
        mLoadingTv.setText("???????????????");
        findViewById(R.id.btn_close_video).setOnClickListener(v -> finish());
    }

    private void getDetail() {
        mIntervalDisposable = Observable.interval(200, 200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mCount++;
                        if (mCount > 6) {
                            mCount = 0;
                            mLoadingTv.setText("???????????????");
                        } else {
                            mLoadingTv.setText(mLoadingTv.getText().toString() + ".");
                        }
                    }
                });


        mDisposable = ApiManager.INSTANCE.getApi().getVideoDetail(mIntentData.getUserId(), mIntentData.getUserType(), mIntentData.getRoomId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .map(new HttpResultFunc<>())
                .subscribe(videoRoomEntity -> {
                    if (mIntervalDisposable != null) {
                        mIntervalDisposable.dispose();
                    }

                    if (videoRoomEntity != null) {
                        videoRoomEntity.setCallIntent(mIntentData);

                        if (videoRoomEntity.isPatient() && !videoRoomEntity.hasProcessingCall()) {
                            MedToastUtil.showMessageLong("??????????????????????????????");
                            delayFinish();
                            return;
                        }
                        if (videoRoomEntity.isDoctor() && videoRoomEntity.isFinished()) {
                            MedToastUtil.showMessageLong("????????????????????????");
                            delayFinish();
                            return;
                        }

                        mVideoRoomEntity = videoRoomEntity;
                        checkPermissionGotoVideoCall();
                    } else {
                        MedToastUtil.showMessageLong("????????????");
                        delayFinish();
                    }
                }, new ErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        if (mIntervalDisposable != null) {
                            mIntervalDisposable.dispose();
                        }
                        delayFinish();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void checkPermissionGotoVideoCall() {
        new MedPermissionUtil(this)
                .requestPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .onResult(it -> {
                    if (it) {
                        checkFloatingPermission();
                    } else {
                        MedToastUtil.showMessageLong("?????????????????????????????????");
                        delayFinish();
                    }
                    return null;
                });
    }

    private void delayFinish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

    private void checkFloatingPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())), VideoCallActivity.CODE_MANAGE_OVERLAY_PERMISSION);
        } else {
            gotoVideoCall(mVideoRoomEntity);
        }
    }

    private void gotoVideoCall(VideoRoomEntity videoRoomEntity) {
        Intent intent = new Intent(VideoDataLoadingActivity.this, VideoCallActivity.class);
        intent.putExtra(VideoCallActivity.DATA_KEY, videoRoomEntity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VideoCallActivity.CODE_MANAGE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                MedToastUtil.showMessageLong("???????????????????????????????????????");
                delayFinish();
            } else {
                gotoVideoCall(mVideoRoomEntity);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (mIntervalDisposable != null && !mIntervalDisposable.isDisposed()) {
            mIntervalDisposable.dispose();
        }
    }
}
