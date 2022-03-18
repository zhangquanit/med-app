package com.medlinker.widget.dialog;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.medlinker.widget.R;
import com.medlinker.widget.dialog.base.MLBaseDialogFragment;
import com.medlinker.widget.dialog.listener.MLOnLayoutDealListener;

/**
 * 头部有个小圆图标的ConfirmDialogFragment
 * 1。温馨提示
 *
 * @author gengyunxiao
 * @time 2021/4/30
 */
public class MLTipDialog extends MLBaseDialogFragment implements View.OnClickListener {


    private TextView titleTv, desTv;
    private TextView cancelTv, confirmTv;
    private FrameLayout contentFl;//自定义布局容器
    private ImageView tipIv;

    private boolean isHideTitle = false;
    private String mTitle;//标题
    private CharSequence mMessage;//描述

    private TipIcon mTipIcon;

    private int mMessageLayoutId;
    private MLOnLayoutDealListener mMlOnLayoutDealListener;

    private String mConfirmTxt, mCancelTxt;
    private View.OnClickListener mConfirmClickListener, mCancelClickListener;

    private boolean mIsDismissAfterClick = true;


    private boolean mIsOneButton = false;//不是一个按钮，取消键默认显示(默认文案'取消')

    private int mMessageGravity = Gravity.CENTER;

    /**
     * 创建
     *
     * @return
     */
    public static MLTipDialog newInstance() {
        MLTipDialog mlTipDialogFragment = new MLTipDialog();
        return mlTipDialogFragment;
    }

    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    public MLTipDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }


    public void setHideTitle(boolean hideTitle) {
        isHideTitle = hideTitle;
    }


    public MLTipDialog setTopTipIcon(TipIcon topTipIcon) {
        this.mTipIcon = topTipIcon;
        return this;
    }

    /**
     * 设置提示信息
     *
     * @param message
     * @return
     */
    public MLTipDialog setMessage(CharSequence message) {
        this.mMessage = message;
        return this;
    }

    /**
     * message文字方向
     *
     * @param gravity
     * @return
     */
    public MLTipDialog setMessageGravity(int gravity) {
        this.mMessageGravity = gravity;
        return this;
    }

    /**
     * 自定义布局
     *
     * @param layoutId
     * @param onLayoutDealListener
     * @return
     */
    public MLTipDialog setMessageLayoutId(int layoutId, MLOnLayoutDealListener onLayoutDealListener) {
        this.mMessageLayoutId = layoutId;
        this.mMlOnLayoutDealListener = onLayoutDealListener;
        return this;
    }


    /**
     * 设置一个按钮文案
     *
     * @param confirmTxt
     * @return
     */
    public MLTipDialog setOneButton(String confirmTxt) {
        this.mIsOneButton = true;
        this.mConfirmTxt = confirmTxt;
        return this;
    }

    /**
     * 设置一个按钮点击监听
     *
     * @param confirmClickListener
     * @return
     */
    public MLTipDialog setOneButtonListener(View.OnClickListener confirmClickListener) {
        this.mIsOneButton = true;
        this.mConfirmClickListener = confirmClickListener;
        return this;
    }

    /**
     * 设置确认按钮文案
     * 设置后则： 取消按钮默认显示，文案为'取消'
     *
     * @param confirmTxt
     * @return
     */
    public MLTipDialog setConfirmButton(String confirmTxt) {
        this.mConfirmTxt = confirmTxt;
        return this;
    }

    /**
     * 设置确认按钮文案
     * 设置后则： 取消按钮默认显示，文案为'取消'
     *
     * @param confirmTxt
     * @param confirmClickListener
     * @return
     */
    public MLTipDialog setConfirmButton(String confirmTxt, View.OnClickListener confirmClickListener) {
        this.mConfirmTxt = confirmTxt;
        this.mConfirmClickListener = confirmClickListener;
        return this;
    }

    /**
     * 设置确认按钮点击监听
     * 设置后则：取消按钮默认显示，文案为'取消'；确定按钮不设置默认文案为'确定'
     *
     * @param confirmClickListener
     * @return
     */
    public MLTipDialog setConfirmButtonListener(View.OnClickListener confirmClickListener) {
        this.mConfirmClickListener = confirmClickListener;
        return this;
    }

    /**
     * 设置取消按钮文案
     *
     * @param cancelTxt
     * @return
     */
    public MLTipDialog setCancelButton(String cancelTxt) {
        this.mCancelTxt = cancelTxt;
        return this;
    }

    /**
     * 设置取消按钮文案
     *
     * @param cancelTxt
     * @param cancelClickListener
     * @return
     */
    public MLTipDialog setCancelButton(String cancelTxt, View.OnClickListener cancelClickListener) {
        this.mCancelTxt = cancelTxt;
        this.mCancelClickListener = cancelClickListener;
        return this;
    }

    /**
     * 设置取消按钮点击监听
     *
     * @param cancelClickListener
     * @return
     */
    public MLTipDialog setCancelButtonListener(View.OnClickListener cancelClickListener) {
        this.mCancelClickListener = cancelClickListener;
        return this;
    }

    /**
     * 点击按钮后是否取消对话框
     *
     * @param isDismissAfterClick
     * @return
     */
    public MLTipDialog setDismissAfterClick(boolean isDismissAfterClick) {
        this.mIsDismissAfterClick = isDismissAfterClick;
        return this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_tv_cancel) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(v);
            }
            if (mIsDismissAfterClick) {
                dismissDialog();
            }
        } else if (id == R.id.dialog_tv_confirm) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(v);
            }
            if (mIsDismissAfterClick) {
                dismissDialog();
            }
        }
    }

    @Override
    public int getDialogLayoutId() {
        return R.layout.dialog_confirm_tip;
    }

    @Override
    public void setupViews(View contentView) {
        titleTv = contentView.findViewById(R.id.dialog_tv_title);
        desTv = contentView.findViewById(R.id.dialog_tv_des);
        cancelTv = contentView.findViewById(R.id.dialog_tv_cancel);
        confirmTv = contentView.findViewById(R.id.dialog_tv_confirm);
        contentFl = contentView.findViewById(R.id.dialog_fl_content);
        tipIv = contentView.findViewById(R.id.dialog_iv_top);

        //自定义布局
        if (mMessageLayoutId != 0) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View messageView = layoutInflater.inflate(mMessageLayoutId, contentFl, true);
            if (mMlOnLayoutDealListener != null) {
                mMlOnLayoutDealListener.setupViews(messageView);
            }
        }
    }

    @Override
    public void updateViews() {

        //topIcon
        if (mTipIcon != null) {
            tipIv.setImageResource(mTipIcon.getIcon());
        } else {
            tipIv.setImageResource(TipIcon.TIP_SWEET.getIcon());
        }

        //title
        if (!TextUtils.isEmpty(mTitle)) {
            titleTv.setText(mTitle);
        } else {
            if (!isHideTitle)
                //标题是一定要有的，不设置默认为'提示'
                titleTv.setText(R.string.dialog_title_tip_default);
            else
                titleTv.setVisibility(View.GONE);
        }

        //message
        if (!TextUtils.isEmpty(mMessage)) {
            desTv.setGravity(mMessageGravity);
            desTv.setText(mMessage);
        } else {
            desTv.setVisibility(View.GONE);
        }
        //自定义布局
        if (mMlOnLayoutDealListener != null) {
            mMlOnLayoutDealListener.updateViews();
        }


        if (mIsOneButton) {
            //cancel button
            cancelTv.setVisibility(View.GONE);
            //confirm button
            if (mConfirmClickListener != null || !TextUtils.isEmpty(mConfirmTxt)) {
                if (!TextUtils.isEmpty(mConfirmTxt)) {
                    confirmTv.setText(mConfirmTxt);
                } else {
                    confirmTv.setText(R.string.dialog_confirm_default);
                }
                confirmTv.setOnClickListener(this);

            } else {
                confirmTv.setVisibility(View.GONE);
            }

        } else {

            //confirm button
            if (mConfirmClickListener != null || !TextUtils.isEmpty(mConfirmTxt)) {
                if (!TextUtils.isEmpty(mConfirmTxt)) {
                    confirmTv.setText(mConfirmTxt);
                } else {
                    confirmTv.setText(R.string.dialog_confirm_default);
                }
                confirmTv.setOnClickListener(this);

                //cancel button
                if (!TextUtils.isEmpty(mCancelTxt)) {
                    cancelTv.setText(mCancelTxt);
                } else {
                    cancelTv.setText(R.string.dialog_cancel_default);
                }
                cancelTv.setOnClickListener(this);

            } else {
                confirmTv.setVisibility(View.GONE);

                //cancel button
                cancelTv.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void showDialog(FragmentManager manager) {
        show(manager, "SweetTipDialog");
    }

    public void showDialog(FragmentManager manager, String tag) {
        show(manager, tag);
    }

    enum TipIcon {
        TIP_SWEET(R.mipmap.dialog_icon_tip_sweet);
        int mIcon;

        TipIcon(int iconId) {
            this.mIcon = iconId;
        }

        public int getIcon() {
            return mIcon;
        }
    }


}
