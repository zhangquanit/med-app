package com.medlinker.lib.fileupload;

import android.annotation.SuppressLint;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.medlinker.lib.fileupload.callback.UpFileCallBack;
import com.medlinker.lib.fileupload.entity.FileEntity;
import com.medlinker.lib.fileupload.entity.UploadConfig;
import com.medlinker.lib.fileupload.entity.TokenEntity;
import com.medlinker.lib.fileupload.entity.UpFileEntity;
import com.medlinker.lib.fileupload.entity.UpFileIdEntity;
import com.medlinker.lib.fileupload.network.FileUploadApi;
import com.medlinker.lib.fileupload.network.Observable2;
import com.medlinker.lib.fileupload.network.QiNiuUploadOnSubscribe;
import com.medlinker.lib.fileupload.utils.FileUtil;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;

import com.qiniu.android.storage.UploadOptions;


import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.base.network.SchedulersCompat;
import net.medlinker.libhttp.BaseEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.schedulers.Schedulers;


public class RxMedFileUpload implements ObservableOnSubscribe<ArrayList<FileEntity>> {

    private UploadConfig uploadConfig;

    private UploadManager uploadManager = new UploadManager();
    private UpFileCallBack completionCallBack;
    /**
     * 上传进度的显示
     */
    private UploadOptions mUploadOptions;

    private RxMedFileUpload(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig;
    }


    private RxMedFileUpload(UploadConfig configEntity, UpFileCallBack callBack) {
        this.uploadConfig = configEntity;
        this.completionCallBack = callBack;

        mUploadOptions = new UploadOptions(null, null, false, new UpProgressHandler() {

            @Override
            public void progress(String key, double percent) {
                if (null != completionCallBack) {
                    FileEntity fileEntity = null;
                    if (null != uploadConfig) {
                        fileEntity = uploadConfig.getFileEntity();
                    }
                    if (null != fileEntity) {
                        fileEntity.setFileName(key);
                    }
                    completionCallBack.upProgress(fileEntity, percent);
                }
            }
        }, null);
    }

    @CheckResult
    @NonNull
    public static Observable<ArrayList<FileEntity>> startUpload(@NonNull UploadConfig uploadConfig) {
        ObjectHelper.requireNonNull(uploadConfig, "qnConfigEntity == null");
        return Observable.create(new RxMedFileUpload(uploadConfig));
    }

    @CheckResult
    @NonNull
    public static Observable<ArrayList<FileEntity>> startUpload(@NonNull UploadConfig uploadConfig, UpFileCallBack callBack) {
        ObjectHelper.requireNonNull(uploadConfig, "qnConfigEntity == null");
        return Observable.create(new RxMedFileUpload(uploadConfig, callBack));
    }

    @SuppressLint("CheckResult")
    @Override
    public void subscribe(final ObservableEmitter<ArrayList<FileEntity>> subscriber) throws Exception {
        getFileEntity()
                .filter(new Predicate<FileEntity>() {
                    @Override
                    public boolean test(FileEntity fileEntity) throws Exception {
                        if (fileEntity.getDuration() > 0 || fileEntity.getBitmap() != null) {
                            return true;//语音上传
                        }
                        if (fileEntity.getIsSuportOriginal() != 1 && null == FileUtil.bitmapToByte(fileEntity.getFileUrl())) {
                            throw new RuntimeException("图片损坏，请重新上传");
                        } else if (!(new File(fileEntity.getFileUrl()).exists())) {//判断文件是否存在
                            throw new RuntimeException("文件本地不存在，请重新选择上传");
                        }
                        return true;
                    }
                })
                .flatMap(new Function<FileEntity, ObservableSource<UpFileEntity>>() {
                    @Override
                    public ObservableSource<UpFileEntity> apply(FileEntity fileEntity) throws Exception {
                        return Observable.create(new QiNiuUploadOnSubscribe(fileEntity, uploadManager, uploadConfig.getUploadBucket(), mUploadOptions))
                                .subscribeOn(Schedulers.io());
                    }
                })
                .buffer(uploadConfig.getFileList().size())
                .flatMap(new Function<List<UpFileEntity>, ObservableSource<UpFileIdEntity>>() {
                    @Override
                    public ObservableSource<UpFileIdEntity> apply(List<UpFileEntity> upFileList) throws Exception {
                        Map<String, String> params = new HashMap<>();
                        params.put(UploadConstant.PARAMS_KEY_WATER_TEXT, uploadConfig.getWaterText());
                        params.put(UploadConstant.PARAMS_KEY_IMG_LIST, new Gson().toJson(upFileList));//文件信息
                        params.put(UploadConstant.PARAMS_KEY_BUCKET, uploadConfig.getUploadBucket());
                        return FileUploadApi.getFileUploadApi().addQiNiuWaterMarket(params)
                                .compose(SchedulersCompat.<BaseEntity<UpFileIdEntity>>applyIoSchedulers())
                                .map(new HttpResultFunc<UpFileIdEntity>());
                    }
                })
                .map(new Function<UpFileIdEntity, ArrayList<FileEntity>>() {
                    @Override
                    public ArrayList<FileEntity> apply(UpFileIdEntity upFileIdEntity) throws Exception {
                        return upFileIdEntity.getSuccess();
                    }
                })
                .subscribe(new Consumer<ArrayList<FileEntity>>() {
                    @Override
                    public void accept(ArrayList<FileEntity> fileEntities) throws Exception {
                        if (!subscriber.isDisposed()) {
                            subscriber.onNext(fileEntities);
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

    private Observable<FileEntity> getFileEntity() {
        if (uploadConfig.getFileEntity() != null) {
            uploadConfig.getFileList().add(uploadConfig.getFileEntity());
        }
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put(UploadConstant.PARAMS_KEY_BUCKET, uploadConfig.getUploadBucket());
        stringMap.put(UploadConstant.PARAMS_KEY_NEED_PUBLISH, String.valueOf(uploadConfig.isPublic()));
        stringMap.put(UploadConstant.PARAMS_KEY_NEED_WATERMARK, String.valueOf(uploadConfig.isWaterMark()));
        return FileUploadApi.getFileUploadApi().getQiNiuToken(stringMap)
                .map(new HttpResultFunc<TokenEntity>())
                .compose(SchedulersCompat.<TokenEntity>applyIoSchedulers())
                .map(new Function<TokenEntity, String>() {
                    @Override
                    public String apply(TokenEntity tokenEntity) throws Exception {
                        /*if (!TextUtils.isEmpty(tokenEntity.getDomain())) {
                            Log.e("RxMedFileUpload", "domain is = " + tokenEntity.getDomain());
                        }*/
                        return tokenEntity.getToken();
                    }
                })
                .flatMap(new Function<String, Observable<FileEntity>>() {
                    @Override
                    public Observable<FileEntity> apply(final String token) throws Exception {
                        return Observable2.from(uploadConfig.getFileList())
                                .map(new Function<FileEntity, FileEntity>() {
                                    @Override
                                    public FileEntity apply(FileEntity fileEntity) throws Exception {
                                        fileEntity.setTokenEntity(token);
                                        return fileEntity;
                                    }
                                });
                    }
                });
    }

}
