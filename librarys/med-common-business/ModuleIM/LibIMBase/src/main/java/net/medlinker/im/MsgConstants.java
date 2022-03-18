package net.medlinker.im;

/**
 * @author hmy
 * @time 11/24/21 19:18
 */
public interface MsgConstants {
    // 聊天会话类型
    /**
     * 普通群
     */
    int SESSION_TYPE_COMMON = 0;
    /**
     * 问诊群
     */
    int SESSION_TYPE_INQUIRY = 1;
    /**
     * 普通患者（随诊患者）
     */
    int SESSION_TYPE_NORMAL_PATIENT = 2;
    /**
     * 小组群（经纪人聊天）
     */
    int SESSION_TYPE_TEAM = 3;
    /**
     * 随诊群
     */
    int SESSION_TYPE_SUIZHEN = 4;
    /**
     * 多学科会诊
     */
    int SESSION_TYPE_CONSULTATION = 5;
    /**
     * 开药门诊
     */
    int SESSION_TYPE_CLINIC = 6;
    /**
     * 医助群
     */
    int SESSION_TYPE_MEDICAL_AID = 8;


    // 聊天对象类型
    /**
     * 医生
     */
    int CHAT_USER_TYPE_DOCTOR = 1;
    /**
     * 机构
     */
    int CHAT_USER_TYPE_AGENCY = 2;
    /**
     * 患者
     */
    int CHAT_USER_TYPE_PATIENT = 33;
    /**
     * 系统用户
     */
    int CHAT_USER_REFERENCE_SYSTEM = 9;

    long GROUP_TIPS_USER_ID = -2222;
}
