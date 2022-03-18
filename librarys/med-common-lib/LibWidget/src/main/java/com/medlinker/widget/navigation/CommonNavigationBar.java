package com.medlinker.widget.navigation;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.medlinker.base.widget.badgeview.BadgeView;
import com.medlinker.widget.R;


/**
 * 统一标题栏
 *
 * @author gengyunxiao
 * @time 2021/3/18
 */
public class CommonNavigationBar extends FrameLayout {


    private SparseArray<Integer> ids = new SparseArray<>();//记录显示的控件


    private View contentView;


    public CommonNavigationBar(@NonNull Context context) {
        super(context);
        initView();
    }

    public CommonNavigationBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CommonNavigationBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.navigation_common, this);
    }

    public CommonNavigationBar showBackIcon(View.OnClickListener onClickListener) {
        showLeftIcon(NavigationIcon.ICON_BACK_GRAY, onClickListener);
        return this;
    }

    public CommonNavigationBar showBackWhiteIcon(View.OnClickListener onClickListener) {
        showLeftIcon(NavigationIcon.ICON_BACK_WHITE, onClickListener);
        return this;
    }

    /*---------底部线--------*/
    public CommonNavigationBar showBottomLine() {
        setNavigationViewVisible(NavigationId.BOTTOM_LINE_HORIZONTAL, View.VISIBLE);
        return this;
    }

    public CommonNavigationBar hideBottomLine() {
        setNavigationViewVisible(NavigationId.BOTTOM_LINE_HORIZONTAL, View.INVISIBLE);
        return this;
    }

    /*---------标题--------*/
    public CommonNavigationBar showTitle(String title) {
        return showCenterTitle(title);
    }

    public CommonNavigationBar showTitle(int titleRes) {
        return showCenterTitle(titleRes);
    }

    public CommonNavigationBar setTitleVisible(int visibility) {
        setCenterTitleVisible(visibility);
        return this;
    }

    public CommonNavigationBar showCenterTitle(String title) {
        setTextString(NavigationId.CENTER_TV_TITLE, title);
        return this;
    }

    public CommonNavigationBar showCenterTitle(int titleResId) {
        setTextString(NavigationId.CENTER_TV_TITLE, titleResId);
        return this;
    }

    public CommonNavigationBar setCenterTitleVisible(int visibility) {
        setNavigationViewVisible(NavigationId.CENTER_TV_TITLE, visibility);
        return this;
    }

    public CommonNavigationBar showLeftTitle(String title) {
        setTextString(NavigationId.LEFT_TV_TITLE, title);
        return this;
    }

    public CommonNavigationBar showLeftTitle(int titleResId) {
        setTextString(NavigationId.LEFT_TV_TITLE, titleResId);
        return this;
    }

    public CommonNavigationBar setTitleTextColor(int colorResId) {
        setTextColor(NavigationId.CENTER_TV_TITLE, colorResId);
        return this;
    }

    public CommonNavigationBar setLeftTitleTextColor(int colorResId) {
        setTextColor(NavigationId.LEFT_TV_TITLE, colorResId);
        return this;
    }


    public CommonNavigationBar setLeftTitleVisible(int visibility) {
        setNavigationViewVisible(NavigationId.LEFT_TV_TITLE, visibility);
        return this;
    }


    /*---------左一级图标和文字--------*/
    public CommonNavigationBar showLeftIcon(NavigationIcon navigationIcon, View.OnClickListener onClickListener) {
        setImageRes(NavigationId.LEFT_IV_ICON, navigationIcon);
        setLeftButtonOnclickListener(onClickListener);
        return this;
    }

    public CommonNavigationBar setLeftIconVisible(int visibility) {
        setNavigationViewVisible(NavigationId.LEFT_IV_ICON, visibility);
        return this;
    }

    public CommonNavigationBar showLeftText(int titleResId, View.OnClickListener onClickListener) {
        setTextString(NavigationId.LEFT_TV_TEXT, titleResId);
        setLeftButtonOnclickListener(onClickListener);
        return this;
    }

    public CommonNavigationBar showLeftText(String title, View.OnClickListener onClickListener) {
        setTextString(NavigationId.LEFT_TV_TEXT, title);
        setLeftButtonOnclickListener(onClickListener);
        return this;
    }

    public CommonNavigationBar setLeftButtonOnclickListener(OnClickListener onclickListener) {
        setNavigationViewOnclickListener(NavigationId.LEFT_FL_BTN, onclickListener);
        return this;
    }

    public CommonNavigationBar setLeftTextColor(int colorResId) {
        setTextColor(NavigationId.LEFT_TV_TEXT, colorResId);
        return this;
    }

    /*---------右一级图标和文字--------*/
    public CommonNavigationBar showRightIcon(NavigationIcon navigationIcon, View.OnClickListener onClickListener) {
        setImageRes(NavigationId.RIGHT_IV_ICON, navigationIcon);
        setRightBtnOnclickListener(onClickListener);
        return this;
    }

    public CommonNavigationBar setRightIconVisible(int visible) {
        setNavigationViewVisible(NavigationId.RIGHT_IV_ICON, visible);
        return this;
    }

    public CommonNavigationBar showRightText(int titleResId, View.OnClickListener onClickListener) {
        setTextString(NavigationId.RIGHT_TV_TEXT, titleResId);
        setRightBtnOnclickListener(onClickListener);
        return this;
    }

    public CommonNavigationBar showRightText(String title, View.OnClickListener onClickListener) {
        setTextString(NavigationId.RIGHT_TV_TEXT, title);
        setRightBtnOnclickListener(onClickListener);
        return this;
    }

    public CommonNavigationBar updateRightText(String title) {
        updateTextString(NavigationId.RIGHT_TV_TEXT, title);
        return this;
    }

    public CommonNavigationBar updateRightText(int titleResId) {
        updateTextString(NavigationId.RIGHT_TV_TEXT, getContext().getString(titleResId));
        return this;
    }

    public CommonNavigationBar setRightTextVisible(int visibility) {
        setNavigationViewVisible(NavigationId.RIGHT_TV_TEXT, visibility);
        return this;
    }

    public CommonNavigationBar setRightTextEnable(boolean enable) {
        setNavigationViewEnable(NavigationId.RIGHT_TV_TEXT, enable);
        setNavigationViewEnable(NavigationId.RIGHT_FL_BTN, enable);
        return this;
    }

    public CommonNavigationBar setRightTextColor(int colorResId) {
        setTextColor(NavigationId.RIGHT_TV_TEXT, colorResId);
        return this;
    }

    public CommonNavigationBar setRightBtnGone() {
        setNavigationViewVisible(NavigationId.RIGHT_FL_BTN, View.GONE);
        return this;
    }

    public CommonNavigationBar setRightBtnVisible() {
        setNavigationViewVisible(NavigationId.RIGHT_FL_BTN, View.VISIBLE);
        return this;
    }

    public CommonNavigationBar setRightBtnOnclickListener(OnClickListener onclickListener) {
        setNavigationViewOnclickListener(NavigationId.RIGHT_FL_BTN, onclickListener);
        return this;
    }

    /*---------右二级图标和文字--------*/
    public CommonNavigationBar showRight2Icon(NavigationIcon navigationIcon, View.OnClickListener onClickListener) {
        setImageRes(NavigationId.RIGHT2_IV_ICON, navigationIcon);
        setNavigationViewOnclickListener(NavigationId.RIGHT2_FL_BTN, onClickListener);
        return this;
    }

    public CommonNavigationBar showRight2Text(int titleResId, View.OnClickListener onClickListener) {
        setTextString(NavigationId.RIGHT2_TV_TEXT, titleResId);
        setNavigationViewOnclickListener(NavigationId.RIGHT2_FL_BTN, onClickListener);
        return this;
    }

    public CommonNavigationBar showRight2Text(String title, View.OnClickListener onClickListener) {
        setTextString(NavigationId.RIGHT2_TV_TEXT, title);
        setNavigationViewOnclickListener(NavigationId.RIGHT2_FL_BTN, onClickListener);
        return this;
    }

    public CommonNavigationBar setRight2BtnOnclickListener(OnClickListener onclickListener) {
        setNavigationViewOnclickListener(NavigationId.RIGHT2_FL_BTN, onclickListener);
        return this;
    }

    public CommonNavigationBar setRight2TextColor(int colorResId) {
        setTextColor(NavigationId.RIGHT2_TV_TEXT, colorResId);
        return this;
    }

    /*---------自定义xml按钮--------*/


    /**
     * 右1带消息的图片
     * 目前与showRight2MsgIcon只可用一次，互斥
     *
     * @param navigationIcon
     * @return
     */
    public BadgeView showRightMsgIcon(NavigationIcon navigationIcon, View.OnClickListener onClickListener) {
        addRightBtnView(R.layout.navigation_view_icon_msg);
        setRightBtnOnclickListener(onClickListener);
        setImageRes(NavigationId.RIGHT_MSG_IV_ICON, navigationIcon);
        BadgeView badgeView = getView(NavigationId.RIGHT_MSG_BV_VIEW.getId(), false);
        return badgeView;
    }

    /**
     * 右2带消息的图片
     * 目前与showRightMsgIcon只可用一次，互斥
     *
     * @param navigationIcon
     * @return
     */
    public BadgeView showRight2MsgIcon(NavigationIcon navigationIcon, View.OnClickListener onClickListener) {
        addRight2BtnView(R.layout.navigation_view_icon_msg);
        setRight2BtnOnclickListener(onClickListener);
        setImageRes(NavigationId.RIGHT_MSG_IV_ICON, navigationIcon);
        BadgeView badgeView = getView(NavigationId.RIGHT_MSG_BV_VIEW.getId(), false);
        return badgeView;
    }


    /**
     * 右竖直 图标+文字 btn
     *
     * @param title
     * @param titleColorResId
     * @param navigationIcon
     * @param onClickListener
     * @return
     */
    public CommonNavigationBar showRightBtnVertical(
            String title, int titleColorResId,
            NavigationIcon navigationIcon,
            View.OnClickListener onClickListener) {

        addRightBtnView(R.layout.navigation_view_btn_v);
        setImageRes(CommonNavigationBar.NavigationId.RIGHT_VERTICAL_IV_ICON, navigationIcon);
        setTextColor(CommonNavigationBar.NavigationId.RIGHT_VERTICAL_TV_TEXT, titleColorResId);
        setTextString(CommonNavigationBar.NavigationId.RIGHT_VERTICAL_TV_TEXT, title);
        setRightBtnOnclickListener(onClickListener);

        return this;
    }

    /*---------公用--------*/

    public CommonNavigationBar addRightBtnView(int layoutResId) {
        FrameLayout frameLayout = getView(NavigationId.RIGHT_FL_BTN.getId());
        if (frameLayout != null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            if (layoutInflater != null) {
                layoutInflater.inflate(layoutResId, frameLayout);
            }
        }
        return this;
    }

    public CommonNavigationBar addRight2BtnView(int layoutResId) {
        FrameLayout frameLayout = getView(NavigationId.RIGHT2_FL_BTN.getId());
        if (frameLayout != null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            if (layoutInflater != null) {
                layoutInflater.inflate(layoutResId, frameLayout);
            }
        }

        return this;
    }

    public CommonNavigationBar addCenterView(View view, ViewGroup.LayoutParams layoutParams) {
        FrameLayout frameLayout = getView(NavigationId.CENTER_FL.getId());
        if (frameLayout != null) {
            frameLayout.addView(view, layoutParams);
        }
        return this;
    }

    /**
     * leftBtn和right2Btn中间;
     * 若leftBtn,right2Btn,right1Btn无，则等同于addCenterView
     * 若leftBtn和right2Btn无,right1Btn有，在左屏幕和right1Btn之间
     *
     * @param view
     * @param layoutParams
     * @return
     */
    public CommonNavigationBar addConstraintCenterView(View view, ViewGroup.LayoutParams layoutParams) {
        FrameLayout frameLayout = getView(NavigationId.CENTER_FL_CONSTRAINT.getId());
        if (frameLayout != null) {
            frameLayout.addView(view, layoutParams);
        }
        return this;
    }

    /**
     * leftBtn和right2Btn中间;
     * 若leftBtn,right2Btn,right1Btn无，则等同于addCenterView
     * 若leftBtn和right2Btn无,right1Btn有，在左屏幕和right1Btn之间
     *
     * @param layoutResId
     * @param onClickListener
     * @return
     */
    public CommonNavigationBar addConstraintCenterView(int layoutResId, View.OnClickListener onClickListener) {
        FrameLayout frameLayout = getView(NavigationId.CENTER_FL_CONSTRAINT.getId());
        if (frameLayout != null) {
            LayoutInflater.from(getContext()).inflate(layoutResId, frameLayout, true);
            setNavigationViewOnclickListener(NavigationId.CENTER_FL_CONSTRAINT, onClickListener);
        }
        return this;
    }

    public int getNavigationHeight() {
        return getResources().getDimensionPixelSize(R.dimen.navigation_height);
    }

    public SparseArray<Integer> getIds() {
        return ids;
    }


    /**
     * 统一获得View
     * 为了更好维护，不建议使用此方法
     */
    public <T extends View> T getNavigationView(NavigationId navigationId) {
        if (navigationId != null) {

            if (navigationId.getId() == NavigationId.CENTER_GET_TV_TEXT.getId()) {//中间 接受中...
                ViewStub viewStub = getView(NavigationId.CENTER_GET_VS_VIEW.getId(), false);
                if (viewStub != null) {
                    View inflate = viewStub.inflate();
                    return (T) inflate;
                }
            } else if (navigationId.getId() == NavigationId.CENTER_STATE_IV_ICON.getId()) {// 中间 客服在线蓝色图标
                ViewStub viewStub = getView(NavigationId.CENTER_STATE_VS_VIEW.getId(), false);
                if (viewStub != null) {
                    View inflate = viewStub.inflate();
                    return (T) inflate;
                }
            }
            return getView(navigationId.getId());
        }
        return null;
    }

    public CommonNavigationBar setNavigationBarBackgroundColor(int colorResId) {
        setBackgroundColor(getResources().getColor(colorResId));
        return this;
    }

    /*---------基础--------*/

    private CommonNavigationBar setNavigationViewOnclickListener(NavigationId navigationId, View.OnClickListener onClickListener) {
        if (navigationId != null) {
            View view = getView(navigationId.getId(), false);
            if (view != null) {
                if (onClickListener != null) {
                    view.setOnClickListener(onClickListener);
                }
            }
        }
        return this;
    }

    private CommonNavigationBar setNavigationViewEnable(NavigationId navigationId, boolean enable) {
        if (navigationId != null) {
            View view = getView(navigationId.getId(), false);
            if (view != null) {
                view.setEnabled(enable);
            }
        }
        return this;
    }

    private CommonNavigationBar setNavigationViewVisible(NavigationId navigationId, int visibility) {
        if (navigationId != null) {
            View view = getView(navigationId.getId(), false);
            if (view != null) {
                int id = navigationId.getId();
                if (visibility == View.VISIBLE) {
                    Integer integer = ids.get(id);
                    if (integer != null) {
                        ids.put(id, integer + 1);
                    } else {
                        ids.put(id, 1);
                    }
                } else {
                    Integer integer = ids.get(id);
                    if (integer != null) {
                        ids.put(id, 0);
                    }
                }
                view.setVisibility(visibility);
            }
        }
        return this;
    }

    private CommonNavigationBar setImageRes(NavigationId navigationId, NavigationIcon navigationIcon) {
        if (navigationId != null && navigationIcon != null) {
            try {
                ImageView imageView = getView(navigationId.getId());
                if (imageView != null) {
                    imageView.setImageResource(navigationIcon.getIcon());
                }
            } catch (Exception e) {

            }
        }
        return this;
    }

    private CommonNavigationBar setTextString(NavigationId navigationId, String textString) {
        if (navigationId != null && !TextUtils.isEmpty(textString)) {
            try {
                TextView textView = getView(navigationId.getId());
                if (textView != null) {
                    textView.setText(textString);
                }
            } catch (Exception e) {

            }
        }
        return this;
    }

    private CommonNavigationBar setTextString(NavigationId navigationId, int textStringId) {
        if (navigationId != null && textStringId != 0) {
            try {
                TextView textView = getView(navigationId.getId());
                if (textView != null) {
                    textView.setText(textStringId);
                }
            } catch (Exception e) {

            }
        }
        return this;
    }

    private CommonNavigationBar updateTextString(NavigationId navigationId, String textString) {
        if (navigationId != null && !TextUtils.isEmpty(textString)) {
            try {
                TextView textView = getView(navigationId.getId(), false);
                if (textView != null) {
                    textView.setText(textString);
                }
            } catch (Exception e) {

            }
        }
        return this;
    }

    private CommonNavigationBar setTextColor(NavigationId navigationId, int colorResId) {
        if (navigationId != null && colorResId != 0) {
            try {
                TextView textView = getView(navigationId.getId());
                if (textView != null) {
                    textView.setTextColor(ContextCompat.getColorStateList(getContext(), colorResId));
                }
            } catch (Exception e) {

            }
        }
        return this;
    }

    private <T extends View> T getView(int id) {
        return getView(id, true);
    }

    private <T extends View> T getView(int id, boolean isDealShow) {
        if (contentView != null) {
            T view = contentView.findViewById(id);
            if (isDealShow && view != null) {
                view.setVisibility(View.VISIBLE);
                Integer integer = ids.get(id);
                if (integer != null) {
                    ids.put(id, integer + 1);
                } else {
                    ids.put(id, 1);
                }
            }
            return view;
        }
        return null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public enum NavigationIcon {

        ICON_BACK_GRAY("返回灰色", R.mipmap.navigation_back_gray),
        ICON_BACK_WHITE("返回白色", R.mipmap.navigation_back_white),
        ICON_ADD_GRAY("添加灰色", R.mipmap.navigation_add_gray),
        ICON_ADD_WHITE("添加白色", R.mipmap.navigation_add_white),
        ICON_SHARE_GRAY("分享灰色", R.mipmap.navigation_share_gray),
        ICON_SHARE_WHITE("分享白色", R.mipmap.navigation_share_white),
        ICON_MORE_GRAY("更多灰色", R.mipmap.navigation_more_gray),
        ICON_MORE_WHITE("更多白色", R.mipmap.navigation_more_white),
        ICON_SEARCH_GRAY("搜索灰色", R.mipmap.navigation_search_gray),
        ICON_PERSON_GRAY("人灰色", R.mipmap.navigation_person_gray),
        ICON_GROUP_GRAY("多人（患者）灰色", R.mipmap.navigation_group_gray),
        ICON_SETTING_GRAY("设置灰色", R.mipmap.navigation_setting_gray),
        ICON_MESSAGE_GRAY("消息灰色", R.mipmap.navigation_message_gray),
        ICON_SERVICE_GRAY("客服灰色", R.mipmap.navigation_service_gray),


        /*---竖直 小 图标---*/
        //ICON_V_SERVICE_GRAY("服务（人工）灰色", R.mipmap.navigation_v_service_gray),
        ICON_V_SERVICE_WHITE("服务（人工）白色", R.mipmap.navigation_v_service_white),

        ;

        int mIcon;
        String mDes;

        NavigationIcon(String des, int icon) {
            mIcon = icon;
            mDes = des;
        }

        public int getIcon() {
            return mIcon;
        }

    }

    public enum NavigationId {

        /*-------------navigation_common.xml----------------*/
        /*---左边---*/
        LEFT_IV_ICON(R.id.navigation_iv_left),
        LEFT_TV_TEXT(R.id.navigation_tv_left),
        LEFT_TV_TITLE(R.id.navigation_tv_title_left),
        LEFT_FL_BTN(R.id.navigation_fl_left),
        /*---中间---*/
        CENTER_TV_TITLE(R.id.navigation_tv_title),
        CENTER_FL(R.id.navigation_fl_center),
        CENTER_FL_CONSTRAINT(R.id.navigation_fl_center_constraint),
        /*---右一（最右边）---*/
        RIGHT_IV_ICON(R.id.navigation_iv_right),
        RIGHT_TV_TEXT(R.id.navigation_tv_right),
        RIGHT_FL_BTN(R.id.navigation_fl_right),
        /*---右二（最右边第二）---*/
        RIGHT2_IV_ICON(R.id.navigation_iv_right2),//标题栏中 从右向左的第二个图标或文字
        RIGHT2_TV_TEXT(R.id.navigation_tv_right2),
        RIGHT2_FL_BTN(R.id.navigation_fl_right2),
        /*底部水平线*/
        BOTTOM_LINE_HORIZONTAL(R.id.navigation_line_h),

        /*-------------其它xml（navigation_view_xxx.xml）----------------*/
        /*---右边竖直按钮，图片+文字---*/
        RIGHT_VERTICAL_TV_TEXT(R.id.navigation_tv_right_v),
        RIGHT_VERTICAL_IV_ICON(R.id.navigation_iv_right_v),
        /*---右边带消息的图标---*/
        RIGHT_MSG_IV_ICON(R.id.navigation_iv_right_icon_msg),
        RIGHT_MSG_BV_VIEW(R.id.navigation_bv_right_icon_msg),
        /*---标题右边的文字（如：接受中...）---*/
        CENTER_GET_TV_TEXT(R.id.navigation_tv_center_get),//中间标题右边的补充文字（如：标题右边的 接受中... 字样）
        CENTER_GET_VS_VIEW(R.id.navigation_vs_center_get),//ViewStub
        /*---标题右边的图片（如：客服的绿色，灰色点;相框选择的箭头）---*/
        CENTER_STATE_IV_ICON(R.id.navigation_iv_center_state),//中间标题右边的图片
        CENTER_STATE_VS_VIEW(R.id.navigation_vs_center_state),//ViewStub
        ;
        int mId;

        NavigationId(int id) {
            this.mId = id;
        }

        public int getId() {
            return mId;
        }
    }
}
