package net.medlinker.imbusiness.network;

import com.medlinker.lib.utils.MedApiConstants;
import com.medlinker.lib.utils.MedAppInfo;
import com.medlinker.network.retrofit.RetrofitProvider;

/**
 * @author hmy
 * @time 2020/9/23 17:41
 */
public class ApiManager {

    private static ImConfigApi sImConfigApi;
    private static MsgApi sMsgApi;
    private static ClinicApi sClinicApi;
    private static InquiryWebApi sInquiryWebApi;

    public static ImConfigApi getImConfigApi() {
        if (null == sImConfigApi) {
            sImConfigApi = RetrofitProvider.INSTANCE
                    .build(RetrofitProvider.INSTANCE.buildRetrofit(getImConfigUrl()), ImConfigApi.class);
        }
        return sImConfigApi;
    }

    public static MsgApi getMsgApi() {
        if (null == sMsgApi) {
            sMsgApi = RetrofitProvider.INSTANCE
                    .build(RetrofitProvider.INSTANCE.buildRetrofit(getIMUrl()), MsgApi.class);
        }
        return sMsgApi;
    }

    public static ClinicApi getClinicApi() {
        if (null == sClinicApi) {
            sClinicApi = RetrofitProvider.INSTANCE
                    .build(RetrofitProvider.INSTANCE.buildRetrofit(getClinicUrl()), ClinicApi.class);
        }
        return sClinicApi;
    }

    public static InquiryWebApi getInquiryWebApi() {
        if (null == sInquiryWebApi) {
            sInquiryWebApi = RetrofitProvider.INSTANCE
                    .build(RetrofitProvider.INSTANCE.buildRetrofit(getInquiryUrl()), InquiryWebApi.class);
        }
        return sInquiryWebApi;
    }

    private static String getInquiryUrl() {
        String url = "http://inquiry-web-dev.medlinker.com";
        switch (MedAppInfo.getEnvType()) {
            case MedApiConstants.API_TYPE_QA:
                url = "http://inquiry-web-qa.medlinker.com";
                break;
            //online
            case MedApiConstants.API_TYPE_ONLINE:
                url = "https://inquiry.medlinker.com";
                break;
        }
        return url;
    }

    private static String getIMUrl() {
        String url = "http://120.26.166.35:8000"; //接口服务器
        switch (MedAppInfo.getEnvType()) {
            case MedApiConstants.API_TYPE_DEV://dev
                url = "http://im-api-dev.medlinker.com";
                break;
            case MedApiConstants.API_TYPE_TEST://test
                url = "http://120.26.166.35:8000";
                break;
            case MedApiConstants.API_TYPE_SAND_BOX://sandbox
                url = "http://120.26.166.35:8000";
                break;
            case MedApiConstants.API_TYPE_ONLINE:
                url = "https://im-api.medlinker.com";
                break;
            case MedApiConstants.API_TYPE_QA:// qa
                url = "http://im-api-qa.medlinker.com";
                break;
            default:
                break;
        }
        return url;
    }

    private static String getImConfigUrl() {
        String url = "http://app.dev.pdt5.medlinker.net"; //接口服务器
        switch (MedAppInfo.getEnvType()) {
            case MedApiConstants.API_TYPE_DEV://dev
                url = "http://app-dev.medlinker.com";
                break;
            case MedApiConstants.API_TYPE_TEST://test
                url = "http://app.test.pdt5.medlinker.net";
                break;
            case MedApiConstants.API_TYPE_SAND_BOX://sandbox
                url = "http://app.sandbox.medlinker.com";
                break;
            case MedApiConstants.API_TYPE_ONLINE:
                url = "https://app.medlinker.com";
                break;
            case MedApiConstants.API_TYPE_QA:// qa
                url = "http://app-qa.medlinker.com";
                break;
            default:
                break;
        }
        return url;
    }

    private static String getAppUrl() {
        String url = "https://patient-medication-dev.medlinker.com"; //接口服务器
        switch (MedAppInfo.getEnvType()) {
            case MedApiConstants.API_TYPE_ONLINE:
                url = "https://patient-medication.medlinker.com";
                break;
            case MedApiConstants.API_TYPE_QA:// qa
                url = "https://patient-medication-qa.medlinker.com";
                break;
            default:
                break;
        }
        return url;
    }

    private static String getClinicUrl() {
        String url = "http://d2d-ph-dev.medlinker.com";
        switch (MedAppInfo.getEnvType()) {
            case MedApiConstants.API_TYPE_QA:
                url = "http://d2d-ph-qa.medlinker.com";
                break;
            case MedApiConstants.API_TYPE_ONLINE://online
                url = "https://ph.medlinker.com";
                break;
        }
        return url;
    }
}
