package com.doctorwork.android.logreport;

import android.app.Application;

import com.doctorwork.android.logreport.okhttp.OkHttpCallAdapterFactory;

/**
 * @author : HuBoChao
 * @date : 2020/11/26
 * @desc :
 */
public class BaseApplication extends Application {

    public static TestBean bean;

    @Override
    public void onCreate() {
        super.onCreate();

        LogReport.init(getApplicationContext(), "85r5c451608692876378", "native", LogReport.URL_CUSTOM)
                .setBaseUrl("https://web-monitor-qa.medlinker.com/")
                .addCallAdapterFactory(OkHttpCallAdapterFactory.create())
                .build();
        LogReport.getInstance().setPhoneNumber("18100000021");

        bean = new TestBean();
        bean.setUid(123);
        bean.setName("test");
    }

    private class TestBean {
        private int uid;
        private String name;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
