package com.medlinker.lib.push.med;

import android.app.Activity;

/**
 * @author zhangquan
 */
public class PushOptions {
    private int mSource;
    private PushMsgReceiver mMsgReceiver;
    private Class mIntentClass;

    PushOptions(PushOptions.Builder builder) {
        mSource = builder.mSource;
        mMsgReceiver = builder.mMsgReceiver;
        mIntentClass = builder.mClass;
    }

    public int getSource() {
        return mSource;
    }

    public PushMsgReceiver getMsgReceiver() {
        return mMsgReceiver;
    }

    public Class getIntentCls() {
        return mIntentClass;
    }

    public static class Builder {
        private int mSource;
        private PushMsgReceiver mMsgReceiver;
        private Class mClass;

        /**
         * 参考值:1医联 6糖尿病 7骨科
         *
         * @param source
         * @return
         */
        public Builder setSourse(int source) {
            mSource = source;
            return this;
        }

        /**
         * 监听push消息，非必需
         *
         * @param receiver
         * @return
         */
        public Builder setMsgReceiver(PushMsgReceiver receiver) {
            mMsgReceiver = receiver;
            return this;
        }

        /**
         * 透传消息通知 打开的Activity
         * 默认跳转到启动页面
         *
         * @param cls
         * @return
         */
        public Builder setIntentCls(Class<? extends Activity> cls) {
            mClass = cls;
            return this;
        }

        public PushOptions build() {
            if (mSource == 0) {
                throw new RuntimeException("source必须设置 参考值:1医联 6糖尿病 7骨科");
            }
            return new PushOptions(this);
        }
    }
}
