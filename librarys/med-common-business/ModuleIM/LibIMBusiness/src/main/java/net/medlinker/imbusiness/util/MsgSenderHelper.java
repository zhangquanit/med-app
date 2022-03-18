package net.medlinker.imbusiness.util;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.medlinker.lib.utils.MedApiConstants;
import com.medlinker.lib.utils.MedAppInfo;
import com.medlinker.lib.utils.MedToastUtil;
import com.medlinker.network.retrofit.error.ApiException;
import com.medlinker.protocol.message.MessageOuterClass;

import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.im.helper.ProtolBuildHelper;
import net.medlinker.im.im.util.LogUtil;
import net.medlinker.im.router.ModuleIMManager;
import net.medlinker.im.router.ModuleIMService;
import net.medlinker.im.util.SchedulersCompat;
import net.medlinker.imbusiness.entity.ProtolCallBackEntity;
import net.medlinker.imbusiness.network.ApiManager;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;
import net.medlinker.libhttp.BaseEntity;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author hmy
 * @time 2020/9/23 14:59
 */
public class MsgSenderHelper {

    private static final String TAG = "MsgSenderHelper";

    /**
     * 发送protocol消息体给服务器
     *
     * @param message
     */
    public static Observable<ProtolCallBackEntity> sendImMessage2Server(MessageOuterClass.Message message) {
        return Observable.just(message)
                .map(new Function<MessageOuterClass.Message, RequestBody>() {
                    @Override
                    public RequestBody apply(MessageOuterClass.Message message) throws Exception {
                        String bindData = Base64.encodeToString(message.toByteArray(), Base64.NO_WRAP);
                        LogUtil.i(TAG, bindData);
                        return RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), bindData);
                    }
                })
                .flatMap(new Function<RequestBody, ObservableSource<ProtolCallBackEntity>>() {
                    @Override
                    public ObservableSource<ProtolCallBackEntity> apply(RequestBody requestBody) throws Exception {
                        return ApiManager.getMsgApi().sendImMessage2Server(requestBody)
                                .compose(SchedulersCompat.<BaseEntity<ProtolCallBackEntity>>applyIoSchedulers())
                                .map(new Function<BaseEntity<ProtolCallBackEntity>, ProtolCallBackEntity>() {
                                    @Override
                                    public ProtolCallBackEntity apply(BaseEntity<ProtolCallBackEntity> httpResult) throws Exception {
                                        if (!httpResult.isSuccess()) {
                                            throw new ApiException(httpResult.errcode, httpResult.errmsg);
                                        }
                                        if (httpResult.data == null) {
                                            return new ProtolCallBackEntity();
                                        }
                                        return httpResult.data;
                                    }
                                });
                    }
                });
    }

    /**
     * 获取服务器分配的域名和端口
     *
     * @param callBack
     */
    @SuppressLint("CheckResult")
    public static void getHostPort(final BiConsumer<String, Integer> callBack, final Consumer<Throwable> throwableAction) {
//        LogUtil.i(TAG, "getHostPort start currentUser=%s", AccountUtil.getCurrentUser());
        ModuleIMService moduleIMService = ModuleIMManager.INSTANCE.getIMService();
        if (moduleIMService.isVisitor()) return;
        ApiManager.getMsgApi().getPort(getUrl(), moduleIMService.getCurrentUserId())
                .compose(SchedulersCompat.<BaseEntity<ProtolCallBackEntity>>applyIoSchedulers())
                .map(new HttpResultFunc<ProtolCallBackEntity>())
                .subscribe(new Consumer<ProtolCallBackEntity>() {
                    @Override
                    public void accept(ProtolCallBackEntity jsonObject) throws Exception {
                        String host = jsonObject.getHostPort().split(":")[0];
                        String port = jsonObject.getHostPort().split(":")[1];
                        LogUtil.i(TAG, "getHostPort host=%s, port=%s", host, port);
                        callBack.accept(host, Integer.parseInt(port));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        LogUtil.e(TAG, " throwable msg = %s", throwable.getMessage());
                        throwableAction.accept(throwable);
                    }
                });
    }

    private static String getUrl() {
        String url = "http://114.55.3.97:8123";
        switch (MedAppInfo.getEnvType()) {
            case MedApiConstants.API_TYPE_ONLINE:
                url = "https://im-gateway.medlinker.com";
                break;
            case MedApiConstants.API_TYPE_QA:
                url = "http://im-wss-qa.medlinker.com/gate";
                break;
        }
        return url;
    }

    /**
     * 绑定登录数据
     *
     * @param token
     */
    @SuppressLint("CheckResult")
    public static void bindUserToken(final String token) {
        sendImMessage2Server(ProtolBuildHelper.getLoginMessage(token))
                .subscribe(new Consumer<ProtolCallBackEntity>() {
                    @Override
                    public void accept(ProtolCallBackEntity protolCallBackEntity) throws Exception {
                        LogUtil.i(TAG, "im登录成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof ApiException) {
                            MedToastUtil.showMessage(ModuleIMBusinessManager.INSTANCE.getApplication(), throwable.getMessage());
                        }
                        throwable.printStackTrace();
                    }
                });
    }
}
