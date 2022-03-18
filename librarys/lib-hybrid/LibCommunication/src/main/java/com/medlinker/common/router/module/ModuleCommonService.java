package com.medlinker.common.router.module;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.medlinker.base.common.CommonCallBack;

import java.util.List;
import java.util.Map;

/**
 * 跨模块调用-app module 方法调用路由服务
 *
 * @author hmy
 */
public interface ModuleCommonService extends IProvider {

    /**
     * 检查url来源 (ImageUtil.checkUrl)
     */
    String checkUrl(String url, int width, int height);

    boolean isPlayingCurrent(String msgId);

    void startPlay(String filePath, CommonCallBack<Boolean> callBack, String msgTime);

    void stopPlay();

    /**
     * 上传图片到七牛
     */
    void uploadImage(String imagePath, CommonCallBack<String> callBack);

    void startUpload7Niu(String uploadBucket, String imagePath, final CommonCallBack<String> callBack);

    /**
     * 显示选择常用语
     */
    void showCommonPhraseView(FragmentActivity activity, String[] arrays, boolean isCanDelete,
                              boolean isOnlyShowItem, CommonCallBack<String> callBack);

    /**
     * 添加常用语
     */
    void addToOftenMsg(String content, CommonCallBack<Boolean> callBack);

    /**
     * 创建通知
     */
    void createNotification(String title, String content, String jumpRouter, String tag, int id);

    void createNotification(String title, String content, Intent intent, String tag, int id);

    Notification createKeepAliveNotification(String title, String content, Intent intent, String tag, int id);

    /**
     * 获取执业待处理界面路由
     */
    String getPracticeWaitDealRouter();

    /**
     * bugtags feedback
     */
    void sendBugTagsFeedback(String msg);

    /**
     * app是否在前台
     */
    boolean isAppForeground();

    boolean router(Context context, String url);

    boolean router(Context context, String moduleName, String routeName, String extra);

    void updateUserInfo(Context context, Map<String, Object> map, CommonCallBack<Boolean> callBack);

    List<String> getSkillList();

    boolean hasWorkTabUnread();

    long getCurrentHospitalId();

    void onMainActivityDestroy(Activity activity);

    boolean checkCertifyUser(Context context);

    /**
     * 跳转到羽毛商场
     */
    void toFeatherH5Mall(Context context);

    /**
     * 获取渠道
     *
     * @return
     */
    String getApplicationChannel();

    /**
     * 是否支持复诊开药价格设置
     */
    boolean isSupportReConsultation();

    /**
     * 调用分享弹框
     */
    void shareInvoke(FragmentManager fragmentManager,String data);

    String saveImgToLocal(Context context, Bitmap bitmap);
}
