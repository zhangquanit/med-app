package com.medlinker.debugtools.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.didichuxing.doraemonkit.kit.loginfo.util.StringUtil;
import com.medlinker.debugtools.R;

/**
 * @author: pengdaosong. CreateTime:  2018/12/12 11:18 PM Email：pengdaosong@medlinker.com. Description:
 */
public class DtDialog extends Dialog implements OnClickListener {

    private boolean mIsSetRatio;
    private int mLayoutId = -1;

    private ImageView mRightImage;
    private TextView mTitleTv;
    private TextView mDialogContent;
    private View mHorizontalDividingLine;
    private TextView mCancelAction;
    private View mDividingLine;
    private TextView mOkButton;

    private Context mContext;

    private String mTitle = "提示";
    private String mContent;
    private String mLeft;
    private String mRight;
    private View.OnClickListener mLeftListener;
    private View.OnClickListener mRightListener;
    private boolean mIsVisibility;
    private boolean mIsConsumeBackPressed;

    private float mWidthPercent = 0.8f;
    private float mHeightPercent = 0.7f;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DtDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public DtDialog(@NonNull Context context, int layoutId) {
        super(context);
        mContext = context;
        mLayoutId = layoutId;
    }

    public DtDialog(@NonNull Context context, int layoutId, boolean isSetRatio) {
        super(context);
        mContext = context;
        mLayoutId = layoutId;
        mIsSetRatio = isSetRatio;
    }

    public DtDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    public DtDialog setContent(String content) {
        mContent = content;
        if (null != mDialogContent) {
            mDialogContent.setText(content);
        }
        return this;
    }

    public DtDialog setLeft(String left) {
        mLeft = left;
        return this;
    }

    public DtDialog setRight(String right) {
        mRight = right;
        if (null != mOkButton) {
            mOkButton.setText(right);
        }
        return this;
    }

    public String getRight() {
        return mRight;
    }

    public DtDialog setLeftClickListener(View.OnClickListener onClickListener) {
        mLeftListener = onClickListener;
        return this;
    }

    public DtDialog setRightClickListener(View.OnClickListener onClickListener) {
        mRightListener = onClickListener;
        return this;
    }

    public DtDialog setImageView(boolean isVi) {
        mIsVisibility = isVi;
        return this;
    }

    public DtDialog setConsumeBackPressed(boolean isConsumeBackPressed) {
        mIsConsumeBackPressed = isConsumeBackPressed;
        return this;
    }

    public float getmWidthPercent() {
        return mWidthPercent;
    }

    public void setmWidthPercent(float mWidthPercent) {
        this.mWidthPercent = mWidthPercent;
    }

    public float getmHeightPercent() {
        return mHeightPercent;
    }

    public void setmHeightPercent(float mHeightPercent) {
        this.mHeightPercent = mHeightPercent;
    }

    public View getContentView() {
        int id = mLayoutId <= 0 ? R.layout.dt_dialog_point_style : mLayoutId;
        View view = LayoutInflater.from(mContext).inflate(id, null);
        return view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        View view = getContentView();
        if (mLayoutId <= 0) {
            this.setContentView(view);

            mRightImage = findViewById(R.id.rightImage);
            mTitleTv = findViewById(R.id.title);
            mDialogContent = findViewById(R.id.dialog_content);
            mHorizontalDividingLine = findViewById(R.id.horizontalDividingLine);
            mCancelAction = findViewById(R.id.cancel_action);
            mDividingLine = findViewById(R.id.dividingLine);
            mOkButton = findViewById(R.id.okButton);

            if (null != mLeftListener) {
                mCancelAction.setOnClickListener(mLeftListener);
            }

            if (null != mRightListener) {
                mOkButton.setOnClickListener(mRightListener);
            }

            mRightImage.setVisibility(mIsVisibility ? View.VISIBLE : View.GONE);
            mRightImage.setOnClickListener(this);

            if (!StringUtil.isEmptyOrWhitespaceOnly(mTitle)) {
                mTitleTv.setText(mTitle);
            }

            if (!StringUtil.isEmptyOrWhitespaceOnly(mLeft)) {
                mCancelAction.setText(mLeft);
            } else {
                mCancelAction.setVisibility(View.GONE);
                if (null != mDividingLine) {
                    mDividingLine.setVisibility(View.GONE);
                }
            }

            if (!StringUtil.isEmptyOrWhitespaceOnly(mRight)) {
                mOkButton.setText(mRight);
            }
            if (!StringUtil.isEmptyOrWhitespaceOnly(mContent)) {
                mDialogContent.setText(mContent);
            }
        } else {
            this.setContentView(view);
            initView(view);
        }
        if (mIsSetRatio) {
            initWidthAndHeightByPercent(mWidthPercent, mHeightPercent);
        }

    }

    public void initView(View view) {

    }

    /**
     * 用于根据传入的宽高的比例显示window
     */
    private void initWidthAndHeightByPercent(float widthPercent, float heightPercent) {
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        //decorView是window中的最顶层view，可以从window中获取到decorView,获取状态栏的高度


        int statusBarHeight = getStatusBarHeight();
        lp.width = (int) (display.getWidth() * widthPercent); //设置宽度
        //高度值需要减去状态栏的高度
        lp.height = (int) ((display.getHeight() - statusBarHeight) * heightPercent);
        getWindow().setAttributes(lp);
    }

    private int getStatusBarHeight() {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rightImage) {
            dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mIsConsumeBackPressed) {
            super.onBackPressed();
        }
    }
}
