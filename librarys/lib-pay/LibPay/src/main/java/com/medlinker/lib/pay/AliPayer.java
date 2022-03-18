package com.medlinker.lib.pay;

import android.util.Log;

/**
 * @author guojianming
 * @description 支付宝支付API
 * <p>
 * 使用:
 * <p>
 * new AliPayer().sendPayReq(aliPayReq);
 * @date 2021/11/22 11:06 上午
 */
public class AliPayer {

    private static final String TAG = AliPayer.class.getSimpleName();

    private AliPayReq lastAliPayReq;

    public boolean isProcessing() {
        return lastAliPayReq != null && lastAliPayReq.isProcessing();
    }

    /**
     * 发送支付宝支付请求
     *
     * @param aliPayReq
     */
    public void sendPayReq(AliPayReq aliPayReq) {
        if (isProcessing()) {
            String errString = "alipay is processing";
            if (aliPayReq.mCallback != null) {
                aliPayReq.mCallback.onFailure(errString, PayConstants.ALIPAY_ERR_PROCESSING);
            }
            if (PayAPI.getInstance().isDebugMode()) Log.d(TAG, errString);
            return;
        }
        aliPayReq.setProcessing(true);
        lastAliPayReq = aliPayReq;

        aliPayReq.setTaskManager(PayAPI.getInstance().getTaskManager());

        aliPayReq.send();
    }

    public interface IPayCallBack {
        /**
         * 失败
         * @param errStr
         * @param resultStatus 除"9000"，"8000"以外的值
         */
        void onFailure(String errStr, String resultStatus);

        /**
         * 成功
         * @param resultInfo
         * @param memo
         * @param resultStatus "9000"
         */
        void onPaySuccess(String resultInfo, String memo, String resultStatus);

        /**
         * 不确定是否支付成功
         *
         * @param resultInfo
         * @param memo
         * @param resultStatus "8000"
         */
        void onPayIndeterminate(String resultInfo, String memo, String resultStatus);
    }
}
