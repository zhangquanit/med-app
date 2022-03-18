package com.medlinker.widget.search;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.medlinker.widget.R;

/**
 * 搜索框展示View，不可点击搜索
 *
 * @author gengyunxiao
 * @time 2021/3/29
 */
public class CommonSearchShow extends FrameLayout {

    private TextView mSearchHintTv;

    private String mSearchHint;

    public CommonSearchShow(Context context) {
        super(context);
        initView(context);
    }

    public CommonSearchShow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dealAttr(context, attrs);
        initView(context);
    }


    public CommonSearchShow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dealAttr(context, attrs);
        initView(context);
    }

    /**
     * 输入框默认字
     *
     * @param hint
     */
    public void setSearchHint(String hint) {
        this.mSearchHint=hint;
        dealSearchHint();
    }


    private void dealAttr(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CommonSearch, 0, R.style.App_UI_Default_Style);
        if (arr != null) {
            mSearchHint = arr.getString(R.styleable.CommonSearch_search_hint);
            arr.recycle();
        }
    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (layoutInflater != null) {
            layoutInflater.inflate(R.layout.search_show, this, true);
        }
        mSearchHintTv = findViewById(R.id.tv_search_hint);
        dealSearchHint();
    }

    private void dealSearchHint() {
        if (!TextUtils.isEmpty(mSearchHint)) {
            mSearchHintTv.setText(mSearchHint);
        } else {
            mSearchHintTv.setText("");
        }
    }
}
