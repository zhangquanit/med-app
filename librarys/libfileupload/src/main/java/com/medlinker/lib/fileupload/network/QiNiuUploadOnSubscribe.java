package com.medlinker.lib.fileupload.network;

import android.annotation.SuppressLint;

import com.medlinker.lib.fileupload.UploadConstant;
import com.medlinker.lib.fileupload.entity.FileEntity;
import com.medlinker.lib.fileupload.entity.UpFileEntity;
import com.medlinker.lib.fileupload.utils.FileUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


public class QiNiuUploadOnSubscribe implements ObservableOnSubscribe<UpFileEntity> {

    private FileEntity mFileEntity;
    private UploadManager uploadManager;
    private String uploadBucket;
    private UploadOptions uploadOptions;

    public QiNiuUploadOnSubscribe(FileEntity mFileEntity, UploadManager uploadManager, String uploadBucket, UploadOptions uploadOptions) {
        this.mFileEntity = mFileEntity;
        this.uploadManager = uploadManager;
        this.uploadBucket = uploadBucket;
        this.uploadOptions = uploadOptions;
    }

    @SuppressLint("CheckResult")
    @Override
    public void subscribe(final ObservableEmitter<UpFileEntity> subscriber) throws Exception {
        final UpCompletionHandler upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(final String key, final ResponseInfo info, final JSONObject response) {
                Observable
                        .just(mFileEntity)
                        .doOnNext(new Consumer<FileEntity>() {
                            @Override
                            public void accept(FileEntity fileEntity) throws Exception {
                                if (info.isOK()) {
                                    fileEntity.setFileName(key);
                                    if (!uploadBucket.equals(UploadConstant.UPLOAD_MEDIA)) {
                                        try {
                                            fileEntity.setWidth(response.getInt("w"));
                                            fileEntity.setHeight(response.getInt("h"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    throw new RuntimeException(info.error);
                                }
                            }
                        })
                        .map(new Function<FileEntity, UpFileEntity>() {
                            @Override
                            public UpFileEntity apply(FileEntity file) throws Exception {
                                UpFileEntity upFile = new UpFileEntity();
                                upFile.fileName = file.getFileName();
                                upFile.height = file.getHeight();
                                upFile.width = file.getWidth();
                                upFile.duration = file.getDuration();
                                upFile.setUniqueKey(file.getUniqueKey());
                                return upFile;
                            }
                        })
                        .subscribe(new Consumer<UpFileEntity>() {
                            @Override
                            public void accept(UpFileEntity upFileEntity) throws Exception {
                                if (!subscriber.isDisposed()) {
                                    subscriber.onNext(upFileEntity);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (!subscriber.isDisposed()) {
                                    subscriber.onError(throwable);
                                }
                            }
                        });
            }
        };
        if (mFileEntity.getBitmap() != null) {
            uploadManager.put(FileUtil.bitmapToByte(mFileEntity.getBitmap()), FileUtil.getMyUUID(), mFileEntity.getTokenEntity(), upCompletionHandler, uploadOptions);
            return;
        }
        if (mFileEntity.getIsSuportOriginal() == 1 || mFileEntity.getDuration() > 0) {
            uploadManager.put(new File(mFileEntity.getFileUrl()), FileUtil.getMyUUID(), mFileEntity.getTokenEntity(), upCompletionHandler, uploadOptions);
        } else {
            uploadManager.put(FileUtil.bitmapToByte(mFileEntity.getFileUrl()), FileUtil.getMyUUID(), mFileEntity.getTokenEntity(), upCompletionHandler, uploadOptions);
        }
    }

}
