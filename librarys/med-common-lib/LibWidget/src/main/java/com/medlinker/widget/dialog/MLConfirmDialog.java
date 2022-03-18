package com.medlinker.widget.dialog;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.medlinker.base.widget.RoundCornerTextView;
import androidx.fragment.app.FragmentManager;

import com.medlinker.widget.R;
import com.medlinker.widget.dialog.base.MLBaseCenterDialogFragment;
import com.medlinker.widget.dialog.listener.MLOnLayoutDealListener;

/**
 * 确认框
 * 两个按钮，一个按钮
 *
 * @author gengyunxiao
 * @time 2021/4/19
 */
public class MLConfirmDialog extends MLBaseCenterDialogFragment implements View.OnClickListener {

    private TextView titleTv, desTv;
    private RoundCornerTextView cancelTv, confirmTv;

    private FrameLayout contentFl;//自定义布局容器

    //private ImageView closeIv;

    private int mTitleVisible = View.VISIBLE;
    private String mTitle;//标题
    private CharSequence mMessage;//描述

    private int mMessageLayoutId;
    private MLOnLayoutDealListener mMlOnLayoutDealListener;

    private String mConfirmTxt, mCancelTxt;
    private int mConfirmSolidColor, mCancelSolidColor;
    private View.OnClickListener mConfirmClickListener, mCancelClickListener;

    private boolean mIsDismissAfterClick = true;

    /**
     * 是否显示关闭Icon
     */
    //private boolean mIsShowClose = false;

    private boolean mIsOneButton = false;//不是一个按钮，取消键默认显示(默认文案'取消')

    private int mMessageGravity = Gravity.CENTER;


    /**
     * 创建
     *
     * @return
     */
    public static MLConfirmDialog newInstance() {
        MLConfirmDialog mlConfirmDialogFragment = new MLConfirmDialog();
        return mlConfirmDialogFragment;
    }

    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    public MLConfirmDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }


    public MLConfirmDialog setTitleVisible(int visible) {
        mTitleVisible = visible;
        return this;
    }

    /**
     * 设置提示信息
     *
     * @param message
     * @return
     */
    public MLConfirmDialog setMessage(CharSequence message) {
        this.mMessage = message;
        return this;
    }

    /**
     * message文字方向
     *
     * @param gravity
     * @return
     */
    public MLConfirmDialog setMessageGravity(int gravity) {
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
    public MLConfirmDialog setContentView(int layoutId, MLOnLayoutDealListener onLayoutDealListener) {
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
    public MLConfirmDialog setOneButton(String confirmTxt) {
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
    public MLConfirmDialog setOneButtonListener(View.OnClickListener confirmClickListener) {
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
    public MLConfirmDialog setConfirmButton(String confirmTxt) {
        this.mConfirmTxt = confirmTxt;
        return this;
    }


    public MLConfirmDialog setConfirmButtonColor(int solidColor) {
        mConfirmSolidColor = solidColor;
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
    public MLConfirmDialog setConfirmButton(String confirmTxt, View.OnClickListener confirmClickListener) {
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
    public MLConfirmDialog setConfirmButtonListener(View.OnClickListener confirmClickListener) {
        this.mConfirmClickListener = confirmClickListener;
        return this;
    }

    /**
     * 设置取消按钮文案
     *
     * @param cancelTxt
     * @return
     */
    public MLConfirmDialog setCancelButton(String cancelTxt) {
        this.mCancelTxt = cancelTxt;
        return this;
    }

    public MLConfirmDialog setCancelButtonColor(int solidColor) {
        mCancelSolidColor = solidColor;
        return this;
    }

    /**
     * 设置取消按钮文案
     *
     * @param cancelTxt
     * @param cancelClickListener
     * @return
     */
    public MLConfirmDialog setCancelButton(String cancelTxt, View.OnClickListener cancelClickListener) {
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
    public MLConfirmDialog setCancelButtonListener(View.OnClickListener cancelClickListener) {
        this.mCancelClickListener = cancelClickListener;
        return this;
    }

    /**
     * 点击按钮后是否取消对话框
     *
     * @param isDismissAfterClick
     * @return
     */
    public MLConfirmDialog setDismissAfterClick(boolean isDismissAfterClick) {
        this.mIsDismissAfterClick = isDismissAfterClick;
        return this;
    }

    /**
     * 设置是否显示CloseIcon
     *
     * @return
     */
    /*public MLConfirmDialog setShowClose(boolean isShowClose) {
        this.mIsShowClose = isShowClose;
        return this;
    }*/
    @Override
    public void showDialog(FragmentManager manager) {
        show(manager, "ConfirmDialog");
    }

    public void showDialog(FragmentManager manager, String tag) {
        show(manager, tag);
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
        } /*else if (id == R.id.dialog_iv_close) {
            dismissDialog();
        }*/
    }


    @Override
    public void setupViews(View contentView) {

        titleTv = contentView.findViewById(R.id.dialog_tv_title);
        desTv = contentView.findViewById(R.id.dialog_tv_des);
        cancelTv = contentView.findViewById(R.id.dialog_tv_cancel);
        confirmTv = contentView.findViewById(R.id.dialog_tv_confirm);
        contentFl = contentView.findViewById(R.id.dialog_fl_content);
        //closeIv = contentView.findViewById(R.id.dialog_iv_close);

        //自定义布局
        if (mMessageLayoutId != 0) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View messageView = layoutInflater.inflate(mMessageLayoutId, contentFl, false);
            contentFl.addView(messageView);
            if (mMlOnLayoutDealListener != null) {
                mMlOnLayoutDealListener.setupViews(messageView);
            }
        }

    }

    @Override
    public void updateViews() {
        //title
        titleTv.setVisibility(mTitleVisible);
        if (mTitleVisible == View.VISIBLE) {
            if (!TextUtils.isEmpty(mTitle)) {
                titleTv.setText(mTitle);
            } else {
                //标题是一定要有的，不设置默认为'提示'
                titleTv.setText(R.string.dialog_title_default);
            }
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

        if (mConfirmSolidColor != 0) {
            confirmTv.setSolidColor(mConfirmSolidColor);
        }
        if (mCancelSolidColor != 0) {
            cancelTv.setSolidColor(mCancelSolidColor);
        }

        //close icon
        /*if (mIsShowClose) {
            closeIv.setVisibility(View.VISIBLE);
            closeIv.setOnClickListener(this);
        }*/

    }

    @Override
    public int getDialogLayoutId() {
        return R.layout.dialog_confirm;
    }

}
