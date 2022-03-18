package com.medlinker.lib.pay;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.medlinker.lib.pay.core.AsyncTask2;
import com.medlinker.lib.pay.core.ITaskManager;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author guojianming
 * @description 支付宝支付请求 安全的的支付宝支付流程，用法
 * @date 2021/11/22 11:05 上午
 */
public class AliPayReq {

    private static final String TAG = AliPayReq.class.getSimpleName();

    WeakReference<Activity> mWeakActivity;

    //未签名的订单信息
    private String rawAliPayOrderInfo;
    //服务器签名成功的订单信息
    private String signedAliPayOrderInfo;
    //完整支付信息
    private String payInfo;

    private final AtomicBoolean mProcessing = new AtomicBoolean(false);

    private ITaskManager mTaskManager;

    public AliPayReq() {
        super();
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
     * 发送支付宝支付请求
     */
    public void send() {
        if (TextUtils.isEmpty(payInfo)) {
            String orderInfo = rawAliPayOrderInfo;
            // 做RSA签名之后的订单信息
            String sign = signedAliPayOrderInfo;
            try {
                // 仅需对sign 做URL编码
                sign = URLEncoder.encode(sign, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // 完整的符合支付宝参数规范的订单信息
            payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                    + getSignType();
        }

        AsyncTaskCompat.executeParallel(new InternalPayTask(mTaskManager), payInfo);
    }

    private class InternalPayTask extends AsyncTask2<String, Void, String> {

        public InternalPayTask(ITaskManager manager) {
            super(manager);
        }

        private final String NULL_ACT = "activity is null";

        @Override
        protected String doInBackground(String... params) {
            Activity activity = getSafetyContext();
            if (activity == null || activity.isFinishing()) {
                return NULL_ACT;
            }
            return new PayTask(activity).pay(params[0], true);
        }

        @Override
        protected void onPostExecute(String result) {
            if (NULL_ACT.equals(result)) {
                onPayFailed(result);
                return;
            }
            PayResult payResult = new PayResult(result);
            // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
            String resultInfo = payResult.getResult();
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
            if (TextUtils.equals(resultStatus, "9000")) {
                onPaySuccess(resultInfo, payResult.getMemo(), resultStatus);
            } else {
                // 判断resultStatus 为非“9000”则代表可能支付失败
                // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(resultStatus, "8000")) {
                    onPayIndeterminate(resultInfo, payResult.getMemo(), resultStatus);
                } else {
                    // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                    onPayFailed(payResult.getMemo());
                }
            }
            super.onPostExecute(result);
        }
    }


    private void onPayFailed(String msg) {
        if (mCallback != null) {
            mCallback.onFailure(msg, "-1000");
            mCallback = null;
        }
        mProcessing.set(false);
        log(msg);
    }

    private void onPaySuccess(String resultInfo, String memo, String resultStatus) {
        if (mCallback != null) {
            mCallback.onPaySuccess(resultInfo, memo, resultStatus);
            mCallback = null;
        }
        mProcessing.set(false);
        log(resultInfo);
    }

    /**
     * 支付结果确认中,需从后台查询
     */
    private void onPayIndeterminate(String resultInfo, String memo, String resultStatus) {
        if (mCallback != null) {
            mCallback.onPayIndeterminate(resultInfo, memo, resultStatus);
            mCallback = null;
        }
        mProcessing.set(false);

        log(resultInfo);
    }

    private void log(String log) {
        if (PayAPI.getInstance().isDebugMode()) Log.d(TAG, log);
    }


    /**
     * 创建订单信息
     *
     * @param partner     签约合作者身份ID
     * @param seller      签约卖家支付宝账号
     * @param outTradeNo  商户网站唯一订单号
     * @param subject     商品名称
     * @param body        商品详情
     * @param price       商品金额
     * @param callbackUrl 服务器异步通知页面路径
     * @return
     */
    public static String getOrderInfo(String partner, String seller, String outTradeNo, String subject, String body, String price, String callbackUrl) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + partner + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + seller + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + outTradeNo + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
//		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
//				+ "\"";
        orderInfo += "&notify_url=" + "\"" + callbackUrl
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }


    public static class Builder {
        //上下文
        private WeakReference weakActivity;

        //未签名的订单信息
        private String rawAliPayOrderInfo;
        //服务器签名成功的订单信息
        private String signedAliPayOrderInfo;
        //完整支付信息
        private String payInfo;

        public Builder() {
            super();
        }

        public Builder with(Activity activity) {
            this.weakActivity = new WeakReference<>(activity);
            return this;
        }


        /**
         * 设置未签名的订单信息
         *
         * @param rawAliPayOrderInfo
         * @return
         */
        public Builder setRawAliPayOrderInfo(String rawAliPayOrderInfo) {
            this.rawAliPayOrderInfo = rawAliPayOrderInfo;
            return this;
        }

        /**
         * 设置服务器签名成功的订单信息
         *
         * @param signedAliPayOrderInfo
         * @return
         */
        public Builder setSignedAliPayOrderInfo(String signedAliPayOrderInfo) {
            this.signedAliPayOrderInfo = signedAliPayOrderInfo;
            return this;
        }

        /**
         * 设置支付完整信息
         *
         * @param payInfo
         * @return
         */
        public Builder setPayInfo(String payInfo) {
            this.payInfo = payInfo;
            return this;
        }

        public AliPayReq create() {
            AliPayReq aliPayReq = new AliPayReq();
            aliPayReq.mWeakActivity = this.weakActivity;
            aliPayReq.rawAliPayOrderInfo = this.rawAliPayOrderInfo;
            aliPayReq.signedAliPayOrderInfo = this.signedAliPayOrderInfo;
            aliPayReq.payInfo = this.payInfo;

            return aliPayReq;
        }

    }

    /**
     * 支付宝支付订单信息的信息类
     * <p>
     * 官方demo是暴露了商户私钥，pkcs8格式的，这是极其不安全的。官方也建议私钥签名订单这一块放到服务器去处理。
     * 所以为了避免商户私钥暴露在客户端，订单的加密过程放到服务器去处理
     */
    public static class AliOrderInfo {
        String partner;
        String seller;
        String outTradeNo;
        String subject;
        String body;
        String price;
        String callbackUrl;

        /**
         * 设置商户
         *
         * @param partner
         * @return
         */
        public AliOrderInfo setPartner(String partner) {
            this.partner = partner;
            return this;
        }

        /**
         * 设置商户账号
         *
         * @param seller
         * @return
         */
        public AliOrderInfo setSeller(String seller) {
            this.seller = seller;
            return this;
        }

        /**
         * 设置唯一订单号
         *
         * @param outTradeNo
         * @return
         */
        public AliOrderInfo setOutTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
            return this;
        }

        /**
         * 设置订单标题
         *
         * @param subject
         * @return
         */
        public AliOrderInfo setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        /**
         * 设置订单详情
         *
         * @param body
         * @return
         */
        public AliOrderInfo setBody(String body) {
            this.body = body;
            return this;
        }

        /**
         * 设置价格
         *
         * @param price
         * @return
         */
        public AliOrderInfo setPrice(String price) {
            this.price = price;
            return this;
        }

        /**
         * 设置请求回调
         *
         * @param callbackUrl
         * @return
         */
        public AliOrderInfo setCallbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        /**
         * 创建订单详情
         *
         * @return
         */
        public String createOrderInfo() {
//			(String partner, String seller, String outTradeNo, String subject, String body, String price, String callbackUrl) {
            return getOrderInfo(partner, seller, outTradeNo, subject, body, price, callbackUrl);
        }
    }


    //支付宝支付监听
    public AliPayer.IPayCallBack mCallback;

    public AliPayReq setOnAliPayCallback(AliPayer.IPayCallBack callBack) {
        this.mCallback = callBack;
        return this;
    }

    public void setTaskManager(ITaskManager mTaskManager) {
        this.mTaskManager = mTaskManager;
    }

    public boolean isProcessing() {
        return mProcessing.get();
    }

    public void setProcessing(boolean processing) {
        mProcessing.set(processing);
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public void recycle() {
        this.mCallback = null;
        mProcessing.set(false);

        if (mWeakActivity != null) {
            mWeakActivity.clear();
        }

    }
}
