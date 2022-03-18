package com.medlinker.lib.pay;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author guojianming
 * @description 微信支付请求
 * @date 2021/11/22 11:05 上午
 */
public class WxPayReq implements IWXAPIEventHandler {

    private static final String TAG = WxPayReq.class.getSimpleName();

    private final AtomicBoolean mProcessing = new AtomicBoolean(false);
    WeakReference<Activity> mWeakActivity;

    /**
     * 微信支付AppId
     **/
    private String appId;
    /**
     * 微信支付商户号
     **/
    private String partnerId;
    /**
     * 预支付码
     **/
    private String prepayId;
    /**
     * "Sign=WXPay"
     **/
    private String packageValue;
    private String nonceStr;
    /**
     * 时间戳
     **/
    private String timeStamp;
    /**
     * 签名
     **/
    private String sign;

    IWXAPI mWXApi;

    public WxPayReq() {
        super();
    }

    /**
     * 发送微信支付请求
     */
    public void send() {
        send(null);
    }

    /**
     * 发送微信支付请求
     * 设置appId
     */
    public void send(String appId) {

        Activity activity = getSafetyContext();
        if (activity == null) return;

        mWXApi = WXAPIFactory.createWXAPI(activity, null);

        if (appId != null) this.appId = appId;

        mWXApi.registerApp(this.appId);

        if (!mWXApi.isWXAppSupportAPI()) {
            String errStr = "onWexinNotSupport";
            if (mCallback != null) {
                mCallback.onFailure(BaseResp.ErrCode.ERR_UNSUPPORT, errStr);
            }
            if (PayAPI.getInstance().isDebugMode()) Log.d(TAG, errStr);
            return;
        }
//        mWXApi.handleIntent(activity.getIntent(), this);

        PayReq request = new PayReq();

        request.appId = this.appId;
        request.partnerId = this.partnerId;
        request.prepayId = this.prepayId;
        request.packageValue = this.packageValue != null ? this.packageValue : "Sign=WXPay";
        request.nonceStr = this.nonceStr;
        request.timeStamp = this.timeStamp;
        request.sign = this.sign;

        mWXApi.sendReq(request);
    }

    /**
     * 确保上下文安全
     *
     * @return
     */
    public Activity getSafetyContext() {
        if (mWeakActivity != null && mWeakActivity.get() != null && !mWeakActivity.get().isDestroyed()) {
            return mWeakActivity.get();
        }
        return null;
    }

    /**
     * 构造器
     */
    public static class Builder {
        /**
         * 保证上下文安全
         **/
        private WeakReference<Activity> activityRef;
        /**
         * 微信支付AppId
         **/
        private String appId;
        /**
         * 微信支付商户号
         **/
        private String partnerId;
        /**
         * 预支付码
         **/
        private String prepayId;
        /**
         * "Sign=WXPay"
         **/
        private String packageValue;
        private String nonceStr;
        /**
         * 时间戳
         **/
        private String timeStamp;
        /**
         * 签名
         **/
        private String sign;

        public Builder() {
            super();
        }

        public Builder with(Activity activity) {
            this.activityRef = new WeakReference<>(activity);
            return this;
        }

        /**
         * 通过jsonstring直接配置，不再需要设置其他参数
         *
         * @param payInfo
         * @return
         */
        public Builder setPayInfo(String payInfo) {
            try {
                JSONObject json = new JSONObject(payInfo);
                this.appId = json.getString("appid");
                this.partnerId = json.getString("partnerid");
                this.prepayId = json.getString("prepayid");
                this.nonceStr = json.getString("noncestr");
                this.timeStamp = json.getString("timestamp");
                this.packageValue = json.getString("package");
                this.sign = json.getString("sign");
            } catch (JSONException e) {
                e.printStackTrace();
                if (this.activityRef != null && this.activityRef.get() != null && !this.activityRef.get().isDestroyed() && !this.activityRef.get().isFinishing()) {
                    if (PayAPI.getInstance().isDebugMode())
                        Toast.makeText(this.activityRef.get(), "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            return this;
        }

        /**
         * 设置微信支付AppId
         *
         * @param appId
         * @return
         */
        public Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        /**
         * 微信支付商户号
         *
         * @param partnerId
         * @return
         */
        public Builder setPartnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        /**
         * 设置预支付码（重要）
         *
         * @param prepayId
         * @return
         */
        public Builder setPrepayId(String prepayId) {
            this.prepayId = prepayId;
            return this;
        }


        /**
         * 设置
         *
         * @param packageValue
         * @return
         */
        public Builder setPackageValue(String packageValue) {
            this.packageValue = packageValue;
            return this;
        }


        /**
         * 设置
         *
         * @param nonceStr
         * @return
         */
        public Builder setNonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        /**
         * 设置时间戳
         *
         * @param timeStamp
         * @return
         */
        public Builder setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        /**
         * 设置签名
         *
         * @param sign
         * @return
         */
        public Builder setSign(String sign) {
            this.sign = sign;
            return this;
        }


        public WxPayReq create() {
            WxPayReq wechatPayReq = new WxPayReq();

            wechatPayReq.mWeakActivity = this.activityRef;
            wechatPayReq.appId = this.appId;
            wechatPayReq.partnerId = this.partnerId;
            wechatPayReq.prepayId = this.prepayId;
            wechatPayReq.packageValue = this.packageValue;
            wechatPayReq.nonceStr = this.nonceStr;
            wechatPayReq.timeStamp = this.timeStamp;
            wechatPayReq.sign = this.sign;

            return wechatPayReq;
        }

    }

    //微信支付监听
    WxPayer.IPayCallback mCallback;

    public WxPayReq setOnWechatPayCallback(WxPayer.IPayCallback onWechatPayListener) {
        this.mCallback = onWechatPayListener;
        return this;
    }


    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (mCallback != null) {
                //0	成功
                //-1 错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
                //-2 用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
                if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                    //0 成功
                    mCallback.onSuccess(resp.errCode, resp.errStr);
                } else {
                    //  -1	错误      -2	用户取消
                    mCallback.onFailure(resp.errCode, resp.errStr);
                }
                mCallback = null;
            }
        }
        mProcessing.set(false);
    }

    public boolean isProcessing() {
        return mProcessing.get();
    }

    public void setProcessing(boolean processing) {
        mProcessing.set(processing);
    }

    public boolean handleIntent(Intent intent) {
        return mWXApi.handleIntent(intent, this);
    }


    public void recycle() {
        if (mWeakActivity != null) {
            mWeakActivity.clear();
        }
        mCallback = null;
        mProcessing.set(false);
    }
}
