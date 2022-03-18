package com.medlinker.widget.search;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.medlinker.widget.R;
import com.medlinker.widget.navigation.CommonNavigationBar;

/**
 * 搜索框展示View，可输入搜索文字
 *
 * @author gengyunxiao
 * @time 2021/3/29
 */
public class CommonSearchInput extends FrameLayout {


    private ImageView backIconIv, deleteIconIv;
    private TextView searchDoTv;
    private SearchEditText searchEditText;

    private String mSearchHint;

    private View.OnClickListener mOnClearTextListener;

    public CommonSearchInput(Context context) {
        super(context);
        initView(context);
    }

    public CommonSearchInput(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dealAttr(context, attrs);
        initView(context);
    }


    public CommonSearchInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dealAttr(context, attrs);
        initView(context);
    }

    /**
     * 显示返回键，并设置监听
     *
     * @param onClickListener
     */
    public void showBackIcon(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            backIconIv.setVisibility(View.VISIBLE);
            backIconIv.setImageResource(CommonNavigationBar.NavigationIcon.ICON_BACK_GRAY.getIcon());
            backIconIv.setOnClickListener(onClickListener);
        }
    }

    /**
     * 显示'搜索'按钮，并设置监听
     *
     * @param onClickListener
     */
    public void showSearchText(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            searchDoTv.setVisibility(View.VISIBLE);
            searchDoTv.setText(R.string.search_ui_do);
            searchDoTv.setOnClickListener(onClickListener);
        }
    }

    /**
     * 显示'取消'按钮，并设置监听
     *
     * @param onClickListener
     */
    public void showCancelText(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            searchDoTv.setVisibility(View.VISIBLE);
            searchDoTv.setText(R.string.search_ui_cancel);
            searchDoTv.setTextColor(getResources().getColor(R.color.search_text_color_cancel));
            searchDoTv.setOnClickListener(onClickListener);
        }
    }

    /**
     * 设置输入框点击 X图标 的监听
     *
     * @param onClearTextListener
     */
    public void setOnClearTextListener(View.OnClickListener onClearTextListener) {
        if (onClearTextListener != null) {
            this.mOnClearTextListener = onClearTextListener;
        }

    }

    /**
     * 输入框默认字
     *
     * @param hint
     */
    public void setSearchHint(String hint) {
        this.mSearchHint = hint;
        dealSearchHint();
    }

    public SearchEditText getSearchEditText() {
        return searchEditText;
    }

    /**
     * 输入框内的删除内容图标
     *
     * @return
     */
    public ImageView getDeleteIconIv() {
        return deleteIconIv;
    }

    public TextView getSearchDoTextView() {
        return searchDoTv;
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
            layoutInflater.inflate(R.layout.search_input, this, true);
        }
        backIconIv = findViewById(R.id.navigation_iv_left);
        deleteIconIv = findViewById(R.id.iv_delete);
        searchDoTv = findViewById(R.id.navigation_tv_right);
        searchEditText = findViewById(R.id.searchEditText);

        //初始化
        dealSearchHint();
        if (backIconIv.getVisibility() != View.GONE)
            backIconIv.setVisibility(View.GONE);
        searchEditText.setOnClearIconShowListener(new SearchEditText.OnClearIconShowListener() {
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
                searchEditText.setText("");
                if (mOnClearTextListener != null)
                    mOnClearTextListener.onClick(v);
            }
        });

    }

    private void dealSearchHint() {
        if (!TextUtils.isEmpty(mSearchHint)) {
            searchEditText.setHint(mSearchHint);
        } else {
            searchEditText.setHint("");
        }
    }


}
