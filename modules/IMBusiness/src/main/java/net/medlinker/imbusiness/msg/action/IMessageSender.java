package net.medlinker.imbusiness.msg.action;

import net.medlinker.base.common.CommonCallBack;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.imbusiness.entity.MsgCardEntity;

/**
 * @author hmy
 * @time 2020/9/24 16:03
 */
public interface IMessageSender {

    /**
     * 发送文本
     *
     * @param session
     * @param content
     */
    void sendMsg(MsgSessionDbEntity session, String content);

    /**
     * 发送图片
     *
     * @param session
     * @param imagePath
     */
    void sendImage(MsgSessionDbEntity session, String imagePath);

    void sendMsgCard(MsgSessionDbEntity session, MsgCardEntity cardMsg);
//    /**
//     * 发送语音消息
//     *
//     * @param session
//     * @param fileEntity
//     */
//    void sendVoice(MsgSessionDbEntity session, FileEntity fileEntity);

    /**
     *
     */
    void reSendMsg(MsgDbEntity message);


    /**
     *
     */
    void withDrawMsg(MsgDbEntity msg);

    void updateParentSession(MsgSessionDbEntity session);

    void reSendPrescription(final MsgDbEntity message);

    void sendJsonMsgEntity(MsgSessionDbEntity session, String json, int jsonType, String sessionContent, CommonCallBack<Boolean> commonCallBack);
}
