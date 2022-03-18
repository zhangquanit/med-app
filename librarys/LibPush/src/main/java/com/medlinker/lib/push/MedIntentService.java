package com.medlinker.lib.push;

import android.content.Context;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.BindAliasCmdMessage;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.igexin.sdk.message.UnBindAliasCmdMessage;
import com.medlinker.lib.push.med.PushMsgProcessor;
import com.medlinker.lib.push.med.PushLog;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class MedIntentService extends GTIntentService {
    private static final String TAG = "GetuiSdk";

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        PushLog.log("onReceiveServicePid -> " + pid);
    }

    /**
     * 处理透传消息
     */
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);

        PushLog.log("onReceiveMessageData sendFeedbackMessage = " + (result ? "success" : "failed"));
        PushLog.log("onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);
        if (payload == null) {
            PushLog.log("onReceiveMessageData payload = null");
        } else {
            String data = new String(payload);
            PushLog.log("onReceiveMessageData payload = " + data);

            PushMsgProcessor.getInstance().onReceiveMsg(data);
        }
        PushLog.log("----------------------------------------------------------------------------------------------");
    }

    @Override
    public void onReceiveClientId(Context context, String clientId) {
        PushLog.log( "onReceiveClientId -> " + "clientId = " + clientId);
        PushMsgProcessor.getInstance().onReceiveClientId(clientId);
    }

    /**
     * 离线/上线 通知
     */
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        PushLog.log("onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    /**
     * 各种事件处理回执
     */
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
//        PushLog.log("onReceiveCommandResult -> " + cmdMessage);
//        int action = cmdMessage.getAction();
//        if (action == PushConsts.SET_TAG_RESULT) {
//            setTagResult((SetTagCmdMessage) cmdMessage);
//        } else if (action == PushConsts.BIND_ALIAS_RESULT) {
//            bindAliasResult((BindAliasCmdMessage) cmdMessage);
//        } else if (action == PushConsts.UNBIND_ALIAS_RESULT) {
//            unbindAliasResult((UnBindAliasCmdMessage) cmdMessage);
//        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
//            feedbackResult((FeedbackCmdMessage) cmdMessage);
//        }
    }

    /**
     * 通知到达，只有个推通道下发的通知会回调此方法
     * 个推自动发送通知
     */
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage message) {
        if (null != message) {
            PushLog.log("onNotificationMessageArrived -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                    + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                    + message.getTitle() + "\ncontent = " + message.getContent());
        }
    }

    /**
     * 通知点击，只有个推通道下发的通知会回调此方法
     */
    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage message) {
        if (null != message) {
            PushLog.log("onNotificationMessageClicked -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                    + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                    + message.getTitle() + "\ncontent = " + message.getContent());
        }
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();
        PushLog.log("settag result sn = " + sn + ", code = " + code);
    }

    private void bindAliasResult(BindAliasCmdMessage bindAliasCmdMessage) {
        String sn = bindAliasCmdMessage.getSn();
        String code = bindAliasCmdMessage.getCode();

        PushLog.log("bindAlias result sn = " + sn + ", code = " + code);

    }

    private void unbindAliasResult(UnBindAliasCmdMessage unBindAliasCmdMessage) {
        String sn = unBindAliasCmdMessage.getSn();
        String code = unBindAliasCmdMessage.getCode();

        PushLog.log("unbindAlias result sn = " + sn + ", code = " + code);
    }


    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        PushLog.log("onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }
}
