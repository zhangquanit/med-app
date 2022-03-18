package net.medlinker.imbusiness.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;

import com.medlinker.protocol.message.MessageOuterClass;

import net.medlinker.im.im.util.LogUtil;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.msg.MsgJsonConstants;
import net.medlinker.imbusiness.view.ChatItemCardView;
import net.medlinker.imbusiness.view.ChatItemImageView;
import net.medlinker.imbusiness.view.ChatItemMedicineNewView;
import net.medlinker.imbusiness.view.ChatItemNoticeView;
import net.medlinker.imbusiness.view.ChatItemReservationView;
import net.medlinker.imbusiness.view.ChatItemServicePackView;
import net.medlinker.imbusiness.view.ChatItemSportPrescriptionView;
import net.medlinker.imbusiness.view.ChatItemTextView;
import net.medlinker.imbusiness.view.ChatItemVideoInviteView;
import net.medlinker.imbusiness.view.ChatItemVoiceView;
import net.medlinker.imbusiness.view.ChatItemWithdrawView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author hmy
 * @time 2020/9/24 15:12
 */
public class MsgViewFactory {

    public static final int MSG_TYPE_DEFAULT = 0;
    public static final int MSG_TYPE_WITHDRAW = 1;

    public static final int MSG_TYPE_CARD_VIEW_ME = 5;
    public static final int MSG_TYPE_CARD_VIEW_OTHER = 6;
    //灰色的条
    public static final int MSG_TYPE_NOTICE = 7;
    //基本数据类型，分左右
    public static final int MSG_TYPE_TEXT_ME = 11;
    public static final int MSG_TYPE_TEXT_OTHER = 12;
    public static final int MSG_TYPE_IMAGE_ME = 13;
    public static final int MSG_TYPE_IMAGE_OTHER = 14;
    public static final int MSG_TYPE_VOICE_ME = 15;
    public static final int MSG_TYPE_VOICE_OTHER = 16;
    public static final int MSG_TYPE_LOCATION_ME = 17;
    public static final int MSG_TYPE_LOCATION_OTHER = 18;
    public static final int MSG_TYPE_BUSINESS_CARD_ME = 19;
    public static final int MSG_TYPE_BUSINESS_CARD_OTHER = 20;
    //发送方的药品处方信息
    public static final int MSG_TYPE_MEDICINE_PRES_ME_NEW = 21;
    //接收方的药品处方信息
    public static final int MSG_TYPE_MEDICINE_PRES_OTHER_NEW = 22;
    //发送方的药品处方信息
    public static final int MSG_TYPE_MEDICINE_PRES_ME = 23;
    //接收方的药品处方信息
    public static final int MSG_TYPE_MEDICINE_PRES_OTHER = 24;
    //认知障碍评估结果
    public static final int MSG_TYPE_EVALUATION_COGNITIVE_DISORDER_ME = 25;
    //认知障碍评测
    public static final int MSG_TYPE_EVALUATION_COGNITIVE_DISORDER_OTHER = 26;
    //服务包
    public static final int MSG_TYPE_SERVICE_PACK_ME = 27;
    //服务包
    public static final int MSG_TYPE_SERVICE_PACK_OTHER = 28;
    //中药
    public static final int MSG_TYPE_PRESCRIPTION_CHINESE_ME = 29;
    //中药
    public static final int MSG_TYPE_PRESCRIPTION_CHINESE_OTHER = 30;
    public static final int MSG_TYPE_JSON_RESERVATION_CALL_ME = 31;
    public static final int MSG_TYPE_JSON_RESERVATION_CALL_OTHER = 32;
    public static final int MSG_TYPE_JSON_RESERVATION_VIDEO_ME = 33;
    public static final int MSG_TYPE_JSON_RESERVATION_VIDEO_OTHER = 34;
    public static final int MSG_TYPE_JSON_VIDEO_INVITE_ME = 35;
    public static final int MSG_TYPE_JSON_VIDEO_INVITE_OTHER = 36;
    // 用药建议
    public static final int MSG_TYPE_USE_MEDICATION_SUGGEST_ME = 37;
    public static final int MSG_TYPE_USE_MEDICATION_SUGGEST_OTHER = 38;
    public static final int MSG_TYPE_PRESCRIBE_MEDICINE_ME = 39;
    public static final int MSG_TYPE_PRESCRIBE_MEDICINE_OTHER = 40;
    //运动处方
    public static final int MSG_TYPE_SPORT_PRESCRIPTION = 41;

    public static final int MSG_TYPE_NOT_SET_ME = 100001;
    public static final int MSG_TYPE_NOT_SET_OTHER = 100002;

    @IntDef({
            MSG_TYPE_DEFAULT,
            MSG_TYPE_WITHDRAW,
            MSG_TYPE_TEXT_ME,
            MSG_TYPE_TEXT_OTHER,
            MSG_TYPE_CARD_VIEW_ME,
            MSG_TYPE_CARD_VIEW_OTHER,
            MSG_TYPE_NOTICE,
            MSG_TYPE_IMAGE_ME,
            MSG_TYPE_IMAGE_OTHER,
            MSG_TYPE_VOICE_ME,
            MSG_TYPE_VOICE_OTHER,
            MSG_TYPE_LOCATION_ME,
            MSG_TYPE_LOCATION_OTHER,
            MSG_TYPE_BUSINESS_CARD_ME,
            MSG_TYPE_BUSINESS_CARD_OTHER,
            MSG_TYPE_NOT_SET_ME,
            MSG_TYPE_NOT_SET_OTHER,
            MSG_TYPE_MEDICINE_PRES_ME,
            MSG_TYPE_MEDICINE_PRES_OTHER,
            MSG_TYPE_MEDICINE_PRES_ME_NEW,
            MSG_TYPE_MEDICINE_PRES_OTHER_NEW,
            MSG_TYPE_EVALUATION_COGNITIVE_DISORDER_ME,
            MSG_TYPE_EVALUATION_COGNITIVE_DISORDER_OTHER,
            MSG_TYPE_SERVICE_PACK_ME,
            MSG_TYPE_SERVICE_PACK_OTHER,
            MSG_TYPE_PRESCRIPTION_CHINESE_ME,
            MSG_TYPE_PRESCRIPTION_CHINESE_OTHER,
            MSG_TYPE_JSON_RESERVATION_CALL_ME,
            MSG_TYPE_JSON_RESERVATION_CALL_OTHER,
            MSG_TYPE_JSON_RESERVATION_VIDEO_ME,
            MSG_TYPE_JSON_RESERVATION_VIDEO_OTHER,
            MSG_TYPE_JSON_VIDEO_INVITE_ME,
            MSG_TYPE_JSON_VIDEO_INVITE_OTHER,
            MSG_TYPE_USE_MEDICATION_SUGGEST_ME,
            MSG_TYPE_USE_MEDICATION_SUGGEST_OTHER,
            MSG_TYPE_PRESCRIBE_MEDICINE_ME,
            MSG_TYPE_PRESCRIBE_MEDICINE_OTHER,
            MSG_TYPE_SPORT_PRESCRIPTION,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface MsgViewType {
    }


    ViewHolder msgViewCreator(ViewGroup viewGroup, @MsgViewType int type) {
        switch (type) {
            case MSG_TYPE_NOTICE:
                return ViewHolder.createViewHolder(new ChatItemNoticeView(viewGroup.getContext(), false));
            case MSG_TYPE_WITHDRAW:
                return ViewHolder.createViewHolder(new ChatItemWithdrawView(viewGroup.getContext(), false));
            case MSG_TYPE_TEXT_ME:
                return ViewHolder.createViewHolder(new ChatItemTextView(viewGroup.getContext(), true));
            case MSG_TYPE_TEXT_OTHER:
                return ViewHolder.createViewHolder(new ChatItemTextView(viewGroup.getContext(), false));
            case MSG_TYPE_IMAGE_ME:
                return ViewHolder.createViewHolder(new ChatItemImageView(viewGroup.getContext(), true));
            case MSG_TYPE_IMAGE_OTHER:
                return ViewHolder.createViewHolder(new ChatItemImageView(viewGroup.getContext(), false));
            case MSG_TYPE_VOICE_ME:
                return ViewHolder.createViewHolder(new ChatItemVoiceView(viewGroup.getContext(), true));
            case MSG_TYPE_VOICE_OTHER:
                return ViewHolder.createViewHolder(new ChatItemVoiceView(viewGroup.getContext(), false));
            case MSG_TYPE_CARD_VIEW_ME:
                return ViewHolder.createViewHolder(new ChatItemCardView(viewGroup.getContext(), true));
            case MSG_TYPE_CARD_VIEW_OTHER:
                return ViewHolder.createViewHolder(new ChatItemCardView(viewGroup.getContext(), false));
//            case MSG_TYPE_MEDICINE_PRES_ME:
//                return ViewHolder.createViewHolder(new ChatItemMedicineView(viewGroup.getContext(), true));
//            case MSG_TYPE_MEDICINE_PRES_OTHER:
//                return ViewHolder.createViewHolder(new ChatItemMedicineView(viewGroup.getContext(), false));
//            case MSG_TYPE_MEDICINE_PRES_ME_NEW:
//            case MSG_TYPE_PRESCRIPTION_CHINESE_ME:
//            case MSG_TYPE_USE_MEDICATION_SUGGEST_ME:
//            case MSG_TYPE_PRESCRIBE_MEDICINE_ME:
//                return ViewHolder.createViewHolder(new ChatItemMedicineNewView(viewGroup.getContext(), true));
//            case MSG_TYPE_MEDICINE_PRES_OTHER_NEW:
//            case MSG_TYPE_PRESCRIPTION_CHINESE_OTHER:
//            case MSG_TYPE_USE_MEDICATION_SUGGEST_OTHER:
//            case MSG_TYPE_PRESCRIBE_MEDICINE_OTHER:
//                return ViewHolder.createViewHolder(new ChatItemMedicineNewView(viewGroup.getContext(), false));
//            case MSG_TYPE_EVALUATION_COGNITIVE_DISORDER_ME:
//                return ViewHolder.createViewHolder(new ChatItemEvaluationCardView(viewGroup.getContext(), true));
//            case MSG_TYPE_EVALUATION_COGNITIVE_DISORDER_OTHER:
//                return ViewHolder.createViewHolder(new ChatItemEvaluationCardView(viewGroup.getContext(), false));
            case MSG_TYPE_SERVICE_PACK_ME:
                return ViewHolder.createViewHolder(new ChatItemServicePackView(viewGroup.getContext(), true));
            case MSG_TYPE_SERVICE_PACK_OTHER:
                return ViewHolder.createViewHolder(new ChatItemServicePackView(viewGroup.getContext(), false));
            case MSG_TYPE_JSON_RESERVATION_CALL_ME:
            case MSG_TYPE_JSON_RESERVATION_VIDEO_ME:
                return ViewHolder.createViewHolder(new ChatItemReservationView(viewGroup.getContext(), true));
            case MSG_TYPE_JSON_RESERVATION_CALL_OTHER:
            case MSG_TYPE_JSON_RESERVATION_VIDEO_OTHER:
                return ViewHolder.createViewHolder(new ChatItemReservationView(viewGroup.getContext(), false));
            case MSG_TYPE_JSON_VIDEO_INVITE_ME:
                return ViewHolder.createViewHolder(new ChatItemVideoInviteView(viewGroup.getContext(), true));
            case MSG_TYPE_JSON_VIDEO_INVITE_OTHER:
                return ViewHolder.createViewHolder(new ChatItemVideoInviteView(viewGroup.getContext(), false));
            case MSG_TYPE_SPORT_PRESCRIPTION:
                return ViewHolder.createViewHolder(new ChatItemSportPrescriptionView(viewGroup.getContext(), false));
            case MSG_TYPE_DEFAULT:
            default:
                //兼容消息显示空view
                return ViewHolder.createViewHolder(new View(viewGroup.getContext()));
        }
    }

    @MsgViewFactory.MsgViewType
    int getChatType(MsgDbEntity entityCase) {
        LogUtil.d("msg", " getChatType = " + entityCase.toString());
        //如果是撤回消息，返回这个type
        if (entityCase.isWithDraw()) {
            return MSG_TYPE_WITHDRAW;
        }
        MessageOuterClass.Message.DataCase dataCase = MessageOuterClass.Message.DataCase.forNumber(entityCase.getDataType());
        if (dataCase == null) {
            return MSG_TYPE_DEFAULT;
        }
        boolean isSelfMsg = entityCase.isSelfMsg();

        switch (dataCase) {
            case TEXT:
                return isSelfMsg ? MSG_TYPE_TEXT_ME : MSG_TYPE_TEXT_OTHER;
            case VOICE:
                return isSelfMsg ? MSG_TYPE_VOICE_ME : MSG_TYPE_VOICE_OTHER;
            case IMAGE:
                return isSelfMsg ? MSG_TYPE_IMAGE_ME : MSG_TYPE_IMAGE_OTHER;
            case LOCATION:
                return isSelfMsg ? MSG_TYPE_LOCATION_ME : MSG_TYPE_LOCATION_OTHER;
            case BUSINESSCARD:
                return isSelfMsg ? MSG_TYPE_BUSINESS_CARD_ME : MSG_TYPE_BUSINESS_CARD_OTHER;
            case JSON:
                return checkJson(entityCase);
            case CARD:
                return isSelfMsg ? MSG_TYPE_CARD_VIEW_ME : MSG_TYPE_CARD_VIEW_OTHER;
            case NOTICE:
                return MSG_TYPE_NOTICE;
            case DATA_NOT_SET:
                return isSelfMsg ? MSG_TYPE_NOT_SET_ME : MSG_TYPE_NOT_SET_OTHER;
            default:
                return MSG_TYPE_DEFAULT;
        }
    }

    private int checkJson(MsgDbEntity entityCase) {
        int jsonType = entityCase.getJsonType();
        boolean isSelfMsg = entityCase.isSelfMsg();
//        if (jsonType == MsgConstants.MSG_TYPE_JSON_PRESCRIPTION //2
//                || jsonType == MsgConstants.MSG_TYPE_JSON_PRESCRIPTION_AGAIN) { //13
//            return isSelfMsg ? MSG_TYPE_MEDICINE_PRES_ME : MSG_TYPE_MEDICINE_PRES_OTHER;
//        } else
        if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIPTION_NEW //17
                || jsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIPTION_AGAIN_NEW //18
                || jsonType == MsgJsonConstants.MSG_TYPE_JSON_REVIEW_PRESCRIPTION_NEW) { //20
            return isSelfMsg ? MSG_TYPE_MEDICINE_PRES_ME_NEW : MSG_TYPE_MEDICINE_PRES_OTHER_NEW;
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_EVALUATION_COGNITIVE_DISORDER) {
            return isSelfMsg ? MSG_TYPE_EVALUATION_COGNITIVE_DISORDER_ME : MSG_TYPE_EVALUATION_COGNITIVE_DISORDER_OTHER;
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_SERVICE_PACK) {
            return isSelfMsg ? MSG_TYPE_SERVICE_PACK_ME : MSG_TYPE_SERVICE_PACK_OTHER;
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIPTION_CHINESE) {
            return isSelfMsg ? MSG_TYPE_PRESCRIPTION_CHINESE_ME : MSG_TYPE_PRESCRIPTION_CHINESE_OTHER;
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_RESERVATION_CALL) {
            return isSelfMsg ? MSG_TYPE_JSON_RESERVATION_CALL_ME : MSG_TYPE_JSON_RESERVATION_CALL_OTHER;
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_RESERVATION_VIDEO) {
            return isSelfMsg ? MSG_TYPE_JSON_RESERVATION_VIDEO_ME : MSG_TYPE_JSON_RESERVATION_VIDEO_OTHER;
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_VIDEO_INVITE) {
            return isSelfMsg ? MSG_TYPE_JSON_VIDEO_INVITE_ME : MSG_TYPE_JSON_VIDEO_INVITE_OTHER;
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_USE_WESTERN_MEDICAL_ADVICE
                || jsonType == MsgJsonConstants.MSG_TYPE_JSON_USE_CHINA_MEDICAL_ADVICE) {
            return isSelfMsg ? MSG_TYPE_USE_MEDICATION_SUGGEST_ME : MSG_TYPE_USE_MEDICATION_SUGGEST_OTHER;
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIBE_MEDICINE) {
            return isSelfMsg ? MSG_TYPE_PRESCRIBE_MEDICINE_ME : MSG_TYPE_PRESCRIBE_MEDICINE_OTHER;
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_SPORTS_PRESCRIPTION) {
            return MSG_TYPE_SPORT_PRESCRIPTION;
        }
        return MSG_TYPE_DEFAULT;
    }
}
