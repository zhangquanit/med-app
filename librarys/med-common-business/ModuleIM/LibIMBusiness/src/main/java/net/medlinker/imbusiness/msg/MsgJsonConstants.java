package net.medlinker.imbusiness.msg;

/**
 * @author hmy
 * @time 2020/9/22 15:10
 */
public interface MsgJsonConstants {

    // json 消息回话 类型
    /**
     * JSON 消息类型： 问诊
     */
    int MSG_TYPE_JSON_WENZHEN = 1;

    /**
     * JSON 消息类型： 处方
     */
    int MSG_TYPE_JSON_PRESCRIPTION = 2;

    /**
     * JSON 消息类型：自动提示
     */
    int MSG_TYPE_JSON_IM_COMMON_CHAT_MSG = 4;

    /**
     * JSON 消息类型： 评分
     */
    int MSG_TYPE_JSON_RATING = 10;

    /**
     * JSON 消息类型：认知障碍测评
     */
    int MSG_TYPE_JSON_EVALUATION_COGNITIVE_DISORDER = 11;

    /**
     * JSON 消息类型：自定义的Card类型
     */
    int MSG_TYPE_JSON_CARD = 12;
    /**
     * JSON 消息类型：续方（处方）
     */
    int MSG_TYPE_JSON_PRESCRIPTION_AGAIN = 13;
    /**
     * JSON 消息类型：审核未通过（处方）
     */
    int MSG_TYPE_JSON_PRESCRIPTION_NO_PASS = 14;
    int MSG_TYPE_JSON_PRESCRIPTION_NOTICE = 15;
    int MSG_TYPE_JSON_REVIEW_PRESCRIPTION = 16;
    /**
     * JSON 消息类型： 新处方
     */
    int MSG_TYPE_JSON_PRESCRIPTION_NEW = 17;
    /**
     * JSON 消息类型：新续方（处方）
     */
    int MSG_TYPE_JSON_PRESCRIPTION_AGAIN_NEW = 18;
    /**
     * JSON 消息类型：新审核未通过（处方）
     */
    int MSG_TYPE_JSON_PRESCRIPTION_NO_PASS_NEW = 19;
    /**
     * JSON 消息类型：新复诊开药
     */
    int MSG_TYPE_JSON_REVIEW_PRESCRIPTION_NEW = 20;

    /**
     * JSON 消息类型：开药门诊处方
     */
    int MSG_TYPE_JSON_PRESCRIPTION_CLINIC = 21;
    /**
     * JSON 消息类型：开药门诊处方不通过
     */
    int MSG_TYPE_JSON_PRESCRIPTION_CLINIC_NO_PASS = 22;
    /**
     * JSON 消息类型：服务包
     */
    int MSG_TYPE_JSON_SERVICE_PACK = 23;
    /**
     * JSON 消息类型：中药处方
     */
    int MSG_TYPE_JSON_PRESCRIPTION_CHINESE = 24;
    /**
     * JSON 消息类型：中药处方审核不通过
     */
    int MSG_TYPE_JSON_PRESCRIPTION_CHINESE_NO_PASS = 25;
    /**
     * JSON 消息类型：协助开方
     */
    int MSG_TYPE_JSON_HELP_PRESCRIBE = 26;
    /**
     * JSON 消息类型：电话问诊
     */
    int MSG_TYPE_JSON_RESERVATION_CALL = 27;
    /**
     * JSON 消息类型：视频问诊
     */
    int MSG_TYPE_JSON_RESERVATION_VIDEO = 28;
    /**
     * JSON 消息类型：视频问诊邀请
     */
    int MSG_TYPE_JSON_VIDEO_INVITE = 29;
    /**
     * 西药用药建议
     */
    int MSG_TYPE_JSON_USE_WESTERN_MEDICAL_ADVICE = 30;
    /**
     * 中药用药建议
     */
    int MSG_TYPE_JSON_USE_CHINA_MEDICAL_ADVICE = 31;
//    /**
//     * 申请购药
//     */
//    int MSG_TYPE_JSON_BUY_MEDICAL = 32;
    /**
     * 处方开药
     */
    int MSG_TYPE_JSON_PRESCRIBE_MEDICINE = 32;
    /**
     * 医生出诊卡片邀请
     */
    int MSG_TYPE_JSON_DOCTOR_VISIT_CARD = 33;
    /**
     * 化验单
     */
    int MSG_TYPE_JSON_ASSAY = 34;
    /**
     * 运动处方
     */
    int MSG_TYPE_JSON_SPORTS_PRESCRIPTION=35;

    // 处方类型
    int PRESCRIPTION_TYPE_WESTERN_MEDICINE = 1;
    int PRESCRIPTION_TYPE_CHINESE_MEDICINE = 2;
    int PRESCRIPTION_TYPE_CHINESE_DRINKS = 3;


}
