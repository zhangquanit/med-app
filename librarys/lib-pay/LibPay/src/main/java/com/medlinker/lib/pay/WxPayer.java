package com.medlinker.lib.pay;

import android.content.Intent;
import android.util.Log;

import com.medlinker.lib.pay.core.IResetable;


/**
 * @author guojianming
 * @description 微信支付API
 * @date 2021-11-22
 * <p>
 * 需要在app的manifest中配置meta-data WECHAT_APPID
 * <p>
 * <meta-data
 * android:name="WECHAT_APPID"
 * android:value="wx123..." />
 */
public class WxPayer implements IResetable {

    private static final String TAG = WxPayer.class.getSimpleName();
    private WxPayReq lastWxPayReq;

    public WxPayer() {

    }

    public boolean onHandleIntent(Intent intent) {
        if (lastWxPayReq != null) return lastWxPayReq.handleIntent(intent);
        return false;
    }

    public boolean isProcessing() {
        return lastWxPayReq != null && lastWxPayReq.isProcessing();
    }

    public void send(WxPayReq wxPayReq) {
        if (isProcessing()) {
            String errStr = "wxpay is processing";
            if (wxPayReq.mCallback != null) {
                wxPayReq.mCallback.onFailure(PayConstants.WXPAY_ERR_PROCESSING, errStr);
            }
            if (PayAPI.getInstance().isDebugMode()) Log.d(TAG, errStr);
            return;
        }
        wxPayReq.setProcessing(true);
        lastWxPayReq = wxPayReq;

        String appId = PayAPI.getInstance().getWxPayAppId(wxPayReq.getSafetyContext());

        //发起请求
        wxPayReq.send(appId);
    }


    public interface IPayCallback {
        /**
         * @param errCode -1：可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
         *                -2：用户取消
         *                -3：发送失败
         *                -4：授权被拒绝
         *                -5:一般是没安装微信;
         *                -6：被禁用
         * @param errStr
         */
        void onFailure(int errCode, String errStr);

        /**
         * 状态值 0 成功
         *
         * @param errCode
         * @param errStr
         */
        void onSuccess(int errCode, String errStr);
    }

    /**
     * called on activity destroied
     */
    @Override
    public void reset() {
        if (lastWxPayReq != null) lastWxPayReq.recycle();
        lastWxPayReq = null;
    }

}
