package com.medlinker.hybrid.core.ui.widget;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.medlinker.hybrid.R;
import com.medlinker.hybrid.core.MedHybrid;

/**
 * Created by vane on 16/6/6.
 */
public class WVSearchBar extends FrameLayout {

    private ViewGroup mLeftView, mRightView;
    private LayoutInflater mInflater;
    private EditText mEditText;

    public WVSearchBar(Context context) {
        super(context);
        init();
    }

    public WVSearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WVSearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        View view = mInflater.inflate(R.layout.hybrid_widget_searchbar, this, false);
        mLeftView = view.findViewById(R.id.hybrid_searchbar_left);
        mRightView = view.findViewById(R.id.hybrid_searchbar_right);
        mEditText = view.findViewById(R.id.hybrid_searchbar_center);
        mEditText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        addView(view);
    }

    public WVSearchBar cleanNavigation(Direct direct) {
        switch (direct) {
            case LEFT:
                mLeftView.removeAllViews();
                break;
            case RIGHT:
                mRightView.removeAllViews();
                break;
        }
        return this;
    }

    public void setData(boolean focus, String hint, int bgColor, OnFocusChangeListener onClickListener, TextWatcher textWatcher) {
        mEditText.setFocusable(focus);
        mEditText.setFocusableInTouchMode(focus);
        mEditText.setHint(hint);
        if (focus) {
            mEditText.requestFocus();
        }
        setBackgroundColor(bgColor);
        try {
            if (null != onClickListener) mEditText.setOnFocusChangeListener(onClickListener);
            if (null != textWatcher) mEditText.addTextChangedListener(textWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        mEditText.clearFocus();
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    public WVSearchBar cleanSearchbar() {
        mRightView.removeAllViews();
        mLeftView.removeAllViews();
        return this;
    }

    public WVSearchBar appendSearch(Direct direct, String label, String icon, OnClickListener clickListener) {
        appendInner(direct, label, icon, clickListener);
        return this;
    }

    public WVSearchBar appendSearch(Direct direct, String label, int iconSource, OnClickListener clickListener) {
        appendInner(direct, label, iconSource, clickListener);
        return this;
    }

    private void appendInner(Direct direct, String label, Object icon, OnClickListener clickListener) {
        ViewGroup viewGroup = (ViewGroup) mInflater.inflate(R.layout.hybrid_item_navigation, direct.equals(Direct.LEFT) ? mLeftView : mRightView, false);
        ImageView iconView = viewGroup.findViewById(R.id.hybrid_icon);
        TextView textView = viewGroup.findViewById(R.id.hybrid_textview);
        if (icon instanceof String) {
            iconView.setVisibility(VISIBLE);
            MedHybrid.Config config = MedHybrid.INSTANCE.getConfig();
            if (config != null && config.getUIBridge() != null) {
                config.getUIBridge().loadIcon(getContext(), (String) icon);
            }
        } else if (icon instanceof Integer) {
            int iconSource = (int) icon;
            if (iconSource > 0) {
                iconView.setVisibility(VISIBLE);
                iconView.setImageResource(iconSource);
            } else {
                iconView.setVisibility(GONE);
            }
        }
        viewGroup.setOnClickListener(clickListener);
        textView.setText(label);
        switch (direct) {
            case LEFT:
                mLeftView.addView(viewGroup);
                break;
            case RIGHT:
                mRightView.addView(viewGroup);
                break;
        }
    }

    public enum Direct {
        RIGHT, LEFT
    }
}
