package com.medlinker.video.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.medlinker.network.retrofit.error.ErrorConsumer;
import com.medlinker.video.entity.VideoRoomEntity;
import com.medlinker.video.network.ApiManager;

import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.base.network.SchedulersCompat;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author hmy
 */
public class VideoCallViewModel extends ViewModel {

    private VideoRoomEntity mIntentEntity;
    public MutableLiveData<Boolean> videoSendLiveData = new MutableLiveData<>();
    private String mCallId;

    private Disposable mVideoSendDisposable;
    private Disposable mVideoStartDisposable;
    private Disposable mVideoEndDisposable;

    private boolean mIsWaitVideoStart = false; // 是否等待调用视频开始接口

    public String getRecordId() {
        return mCallId;
    }

    public void setIntentEntity(VideoRoomEntity intentEntity) {
        this.mIntentEntity = intentEntity;
    }

    private boolean isNotDoctor() {
        return mIntentEntity == null || !mIntentEntity.isDoctor();
    }

    public void reportVideoSend(int roomId) {
        if (isNotDoctor()) {
            videoSendLiveData.setValue(true);
            return;
        }
        dispose(mVideoSendDisposable);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("roomId", String.valueOf(roomId));
        } catch (JSONException e) {
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),
                jsonObject.toString());
        mVideoSendDisposable = ApiManager.INSTANCE.getApi().reportVideoSend(requestBody)
                .compose(SchedulersCompat.applyIoSchedulers())
                .map(new HttpResultFunc<>())
                .subscribe(videoReportEntity -> {
                    mCallId = videoReportEntity.getRecordId();
                    videoSendLiveData.setValue(true);
                    if (mIsWaitVideoStart) {
                        reportVideoStart();
                    }
                }, new ErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        videoSendLiveData.setValue(false);
                    }
                });
    }

    public void reportVideoStart() {
        if (isNotDoctor()) {
            return;
        }
        if (TextUtils.isEmpty(mCallId)) {
            mIsWaitVideoStart = true;
            return;
        }
        mIsWaitVideoStart = false;
        dispose(mVideoStartDisposable);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),
                "{\"callId\":" + mCallId + "}");
        mVideoStartDisposable = ApiManager.INSTANCE.getApi().reportVideoStart(requestBody)
                .compose(SchedulersCompat.applyIoSchedulers())
                .map(new HttpResultFunc<>())
                .subscribe(dataEntity -> {

                }, throwable -> {

                });
    }

    public void reportVideoEnd() {
        if (isNotDoctor()) {
            return;
        }
        if (TextUtils.isEmpty(mCallId)) {
            return;
        }
        dispose(mVideoEndDisposable);
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"),
                "{\"callId\":" + mCallId + "}");
        mVideoEndDisposable = ApiManager.INSTANCE.getApi().reportVideoEnd(requestBody)
                .compose(SchedulersCompat.applyIoSchedulers())
                .map(new HttpResultFunc<>())
                .subscribe(dataEntity -> {

                }, throwable -> {

                });
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}
