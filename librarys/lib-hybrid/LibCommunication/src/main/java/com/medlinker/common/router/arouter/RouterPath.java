package com.medlinker.common.router.arouter;

import com.medlinker.base.utils.AppUtil;

/**
 * arouter路由
 *
 * @author hmy
 */
public class RouterPath {

    private static final String MODULE_APP = "app";
    public static final String MAIN_ACTIVITY = "/" + MODULE_APP + "/" + "MainActivity";
    public static final String LOGIN_ACTIVITY = "/" + MODULE_APP + "/" + "LoginActivity";
    public static final String LOGIN_LITE_ACTIVITY = "/" + MODULE_APP + "/" + "LoginLiteActivity";
    public static final String PHOTO_VIEWER_ACTIVITY = "/" + MODULE_APP + "/" + "PhotoViewerActivity";
    public static final String PHOTO_PICKER_ACTIVITY = "/" + MODULE_APP + "/" + "PhotoPickerActivity";
    public static final String CLIP_PHOTO_PICKER_ACTIVITY = "/" + MODULE_APP + "/" + "ClipPhotoPickerActivity";
    public static final String FEATHER_ACTIVITY = "/" + MODULE_APP + "/" + "FeatherActivity";
    public static final String ADD_DOCTOR_SCHEDULE_ACTIVITY = "/" + MODULE_APP + "/" + "AddDoctorScheduleActivity";
    public static final String WORK_TAB_FRAGMENT = "/" + MODULE_APP + "/" + "WorkTabFragment";
    public static final String STUDY_TAB_FRAGMENT = "/" + MODULE_APP + "/" + "StudyTabFragment";
    public static final String MEDLINKER_REACT_FRAGMENT = "/" + MODULE_APP + "/" + "MedlinkerReactFragment";
    public static final String WORK_BENCH_ACTIVITY = "/" + MODULE_APP + "/" + "WorkbenchActivity";
    public static final String WAIT_DEAL_ACTIVITY = "/" + MODULE_APP + "/" + "WaitDealActivity";
    public static final String WAIT_DEAL_LIST_ACTIVITY = "/" + MODULE_APP + "/" + "DealWithListActivity";
    public static final String HEALTH_ADVISORY_ACTIVITY = "/" + MODULE_APP + "/" + "HealthAdvisoryActivity";
    public static final String INFO_MANAGE_ACTIVITY = "/" + MODULE_APP + "/" + "InfoManageActivity";
    public static final String DOCTOR_CARD_DIALOG_ACTIVITY = "/" + MODULE_APP + "/" + "DoctorCardDialogActivity";
    public static final String CHAT_SESSION_ACTIVITY = "/" + MODULE_APP + "/" + "ChatSessionActivity";
    public static final String CHAT_SESSION_HISTORY_ACTIVITY = "/" + MODULE_APP + "/" + "ChatSessionHistoryActivity";
    public static final String CALL_INQUIRY_RESERVE_LIST_ACTIVITY = "/" + MODULE_APP + "/" + "CallInquiryReserveListActivity";
    public static final String OFFLINE_DIALOG_ACTIVITY = "/" + MODULE_APP + "/" + "OfflineDialogActivity";
    public static final String CREATE_CONCLUSION_INQUIRY_ACTIVITY = "/" + MODULE_APP + "/" + "CreateConclusionInquiryActivity";

    private static final String MODULE_IMUI = "LibImUi";
    public static final String CHAT_TIM_ACTIVITY = "/" + MODULE_IMUI + "/" + "ChatTIMActivity";
    public static final String TENCENT_INQUIRY_LIST_FRAGMENT = "/" + MODULE_IMUI + "/" + "TencentInquiryListFragment";
    public static final String CREATE_CONCLUSION_ACTIVITY = "/" + MODULE_IMUI + "/" + "CreateConclusionActivity";

    private static final String MODULE_LITE = "ModuleLiteApp";
    public static final String MAIN_LITE_ACTIVITY = "/" + MODULE_LITE + "/" + "MainLiteActivity";
    public static final String FEATHER_LITE_ACTIVITY = "/" + MODULE_LITE + "/" + "FeatherLiteActivity";
    public static final String PRACTICE_PERSON_INFO_ACTIVITY = "/" + MODULE_LITE + "/" + "PracticePersonInfoActivity";
    public static final String WORK_MY_SERVICE_ACTIVITY = "/" + MODULE_LITE + "/" + "WorkMyServiceActivity";

    private static final String MODULE_VIDEO_CALL = "ModuleVideoCall";
    public static final String VIDEO_CALL_ACTIVITY = "/" + MODULE_VIDEO_CALL + "/" + "VideoCallActivity";

    //场景直播
    public static final String MODULE_CCLIVE = "CCLive";
    public static final String CCLIVE_SERVICE = "/" + MODULE_APP + "/CCLiveService";
    public static final String CCLIVE_LIST = "/" + MODULE_CCLIVE + "/CCLiveListActivity";
    public static final String CCLIVE_STATISTICS = "/" + MODULE_CCLIVE + "/CCStatisticsActivity";
    public static final String MY_DATA = "/me/mineInfo";
    public static final String ORDER_POOL="/hospital/robOrder";
    public static  final  String ORDER_POOL_DETAIL="/oder/poolDetail";


    /**
     * 视频播放
     */
    //点播
    public static final String VIDEOPLAYER_VOD = "/videoplayer/vod";

    /**
     * 阿里云视频
     */
    //录制
    public static final String ALIVIC_RECORD = "/shortvideo/record";
    //发布视频
    public static final String ALIVIC_PUBLISH = "/shortvideo/publish";
    //我的-短视频入口
    public static final String ALIVIC_MINE = "/shortvideo/mine";

    /**
     * 上传服务
     */
    public static final String UPLOAD_SERVICE = "/upload/service";

    /**
     * 路由服务
     */
    public static final String ROUTER_SERVICE = "/router/service";

    public static String getMainActivityRouter() {
        return AppUtil.isLiteApp() ? RouterPath.MAIN_LITE_ACTIVITY : RouterPath.MAIN_ACTIVITY;
    }

    public static String getLoginActivityRouter() {
        return AppUtil.isLiteApp() ? RouterPath.LOGIN_LITE_ACTIVITY : RouterPath.LOGIN_ACTIVITY;
    }
}
