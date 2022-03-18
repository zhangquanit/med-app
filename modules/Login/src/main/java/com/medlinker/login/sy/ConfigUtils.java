package com.medlinker.login.sy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig;
import com.medlinker.baseapp.ApiPath;
import com.medlinker.login.R;

import net.medlinker.base.base.BaseActivity;


public class ConfigUtils {
    /**
     * 闪验三网运营商授权页配置类
     *
     * @param context
     * @return
     */

    private static void close(Context context) {
        try {
            OneKeyLoginManager.getInstance().finishAuthActivity();
            BaseActivity activity = (BaseActivity) context;
            activity.hideDialogLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //沉浸式竖屏样式
    public static ShanYanUIConfig getCJSConfig(final Context context) {
        /************************************************自定义控件**************************************************************/
        RelativeLayout llLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.sy_page_logo, null);

        llLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        llLayout.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close(context);
            }
        });

        RelativeLayout rl_login_type_view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.login_type_item_view, null);
        rl_login_type_view.findViewById(R.id.tv_verification_code_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close(context);
            }
        });
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, SizeUtils.dp2px(410f), 0, 0);
        rl_login_type_view.setLayoutParams(layoutParams);

        /****************************************************设置授权页*********************************************************/

        Drawable logBtnImgPath = context.getResources().getDrawable(R.drawable.bg_sy_login_btn);
        Drawable uncheckedImgPath = context.getResources().getDrawable(R.mipmap.ic_uncheck);
        Drawable checkedImgPath = context.getResources().getDrawable(R.mipmap.ic_checked);

        ShanYanUIConfig uiConfig = new ShanYanUIConfig.Builder()
                //授权页导航栏：
                .setNavColor(Color.parseColor("#ffffff"))  //设置导航栏颜色
                .setNavText("")  //设置导航栏标题文字
                .setNavTextColor(0xff080808) //设置标题栏文字颜色
                .setAuthNavHidden(true)

                //设置logo隐藏
                .setLogoHidden(true)   //是否隐藏logo

                //授权页号码栏：
                .setNumberColor(0xCC000000)  //设置手机号码字体颜色
                .setNumFieldOffsetY(200)    //设置号码栏相对于标题栏下边缘y偏移
                .setNumberSize(32)

                //授权页slogan： eg:中国移动提供认证服务
                .setSloganTextColor(Color.parseColor("#9A9A9A"))  //设置slogan文字颜色
                .setSloganOffsetY(248)  //设置slogan相对于标题栏边缘y偏移
                .setSloganTextSize(12)

                //授权页登录按钮：
                .setLogBtnText("一键登录")  //设置登录按钮文字
                .setLogBtnTextColor(0xffffffff)   //设置登录按钮文字颜色
                .setLogBtnImgPath(logBtnImgPath)   //设置登录按钮图片
                .setLogBtnOffsetY(300)   //设置登录按钮相对于标题栏下边缘y偏移
                .setLogBtnTextSize(18)
                .setLogBtnHeight(44)
                .setLogBtnWidth(SizeUtils.px2dp(ScreenUtils.getScreenWidth()) - 48)

                //授权页隐私栏：
                .setAppPrivacyOne("隐私政策", ApiPath.APP_PRIVACY_STATEMENT_URL)
                .setAppPrivacyTwo("用户协议", ApiPath.USER_AGREEMENT_URL)
                .setPrivacyText("已阅读并同意", "和", "、", null, "并授权" + getAppName(context) + "获取本机号码")
                .setAppPrivacyColor(0xFF9A9A9A, 0xFF9487EA)   //	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
                .setUncheckedImgPath(uncheckedImgPath)
                .setCheckedImgPath(checkedImgPath)
                .setPrivacyState(false)
                .setCheckBoxMargin(0, 0, 0, 0)
                .setcheckBoxOffsetXY(0, 6)
                .setPrivacyGravityHorizontalCenter(false)
                .setPrivacyOffsetY(356)//设置隐私条款相对于屏幕下边缘y偏


                // 添加自定义控件:
                .addCustomView(llLayout, false, false, null)
                .addCustomView(rl_login_type_view, false, false, null)
                .build();
        return uiConfig;

    }

    private static String getAppName(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        int labelId = info.labelRes;
        return labelId == 0 ? info.nonLocalizedLabel.toString() : context.getString(labelId);
    }
}
