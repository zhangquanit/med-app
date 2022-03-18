package com.medlinker.widget.input;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medlinker.widget.R;
import com.medlinker.widget.search.SearchEditText;

/**
 * 验证码 输入框
 *
 * @author gengyunxiao
 * @time 2021/5/21
 */
public class CommonEditCodeInput extends FrameLayout {

    private ImageView deleteIconIv;
    private InputSafeEditText mEditText;
    private TextView mGetCodeTv;

    private String mInputHint;

    private OnClickListener mOnClearTextListener;

    public CommonEditCodeInput(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CommonEditCodeInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dealAttr(context, attrs);
        initView(context);
    }

    public CommonEditCodeInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dealAttr(context, attrs);
        initView(context);
    }

    private void dealAttr(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CommonInputEdit, 0, R.style.App_UI_Default_Style);
        if (arr != null) {
            mInputHint = arr.getString(R.styleable.CommonInputEdit_input_hint);
            arr.recycle();
        }
    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (layoutInflater != null) {
            layoutInflater.inflate(R.layout.input_edit_code, this, true);
        }

        mEditText = findViewById(R.id.password_edit_view);
        deleteIconIv = findViewById(R.id.iv_delete);
        mGetCodeTv = findViewById(R.id.password_assist_view);
        //初始化
        dealInputHint();
        mEditText.setOnClearIconShowListener(new SearchEditText.OnClearIconShowListener() {
            @Override
            public void onShowClearIcon() {
                deleteIconIv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onHideClearIcon() {
                deleteIconIv.setVisibility(View.GONE);
            }
        });

        deleteIconIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
                if (mOnClearTextListener != null)
                    mOnClearTextListener.onClick(v);
            }
        });
    }

    private void dealInputHint() {
        if (!TextUtils.isEmpty(mInputHint)) {
            mEditText.setHint(mInputHint);
        } else {
            mEditText.setHint("");
        }
    }

    public void setInputHint(String inputHint) {
        this.mInputHint = inputHint;
    }

    /**
     * 设置输入框点击 X图标 的监听
     *
     * @param onClearTextListener
     */
    public void setOnClearTextListener(OnClickListener onClearTextListener) {
        if (onClearTextListener != null) {
            this.mOnClearTextListener = onClearTextListener;
        }

    }

    public InputSafeEditText getEditText() {
        return mEditText;
    }

    public TextView getGetCodeTextView() {
        return mGetCodeTv;
    }
}
