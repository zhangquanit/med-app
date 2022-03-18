package net.medlinker.imbusiness.router;

import android.content.Context;
import android.net.Uri;

import net.medlinker.im.realm.ImUserDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.imbusiness.entity.VideoParamEntity;

import java.util.ArrayList;

/**
 * @author hmy
 * @time 2020/9/22 16:45
 */
public interface ModuleIMBusinessService {

    int getUnLoginErrCode();

    long getCurrentUserId();

    String getCurrentUserAvatar();

    String getWebEnv();

    void previewImageView(ArrayList<CharSequence> images, int currentPosition);

    void gotoWebActivity(Context context, String url);

    void gotoVideoCall(Context context, String transNo, VideoParamEntity videoParamEntity);

    /**
     * 将当前用户数据转换为IM数据库用户数据
     *
     * @param imUserDbEntity
     * @return
     */
    ImUserDbEntity transformCurrentUserDb(ImUserDbEntity imUserDbEntity);

    /**
     * 点击通知，新用户消息跳转Uri
     *
     * @param entity
     * @return
     */
    Uri getNewFriendJumpUri(MsgSessionDbEntity entity);

    /**
     * 点击通知，是否跳转到im聊天界面
     */
    boolean isJumpToChat(MsgSessionDbEntity entity);

    /**
     * 点击通知，不跳转聊天界面
     *
     * @param entity
     * @return
     */
    Uri getNotJumpToChatUri(MsgSessionDbEntity entity);

    /**
     * 点击通知，跳转群聊聊天界面
     *
     * @param entity
     * @return
     */
    Uri getJumpToGroupChatUri(MsgSessionDbEntity entity);

    /**
     * 点击通知，跳转单聊聊天界面
     *
     * @param entity
     * @return
     */
    Uri getJumpToSingleChatUri(MsgSessionDbEntity entity);
}
