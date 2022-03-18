package com.medlinker.widget.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AutoCompleteTextView;


/**
 * Author: KindyFung.
 * CreateTime:  2015/10/23 16:16
 * Email：fangjing@medlinker.com.
 * Description: 搜索框编辑框
 */
@SuppressLint("AppCompatCustomView")
public class SearchEditText extends AutoCompleteTextView implements
        View.OnFocusChangeListener, TextWatcher {

    /**
     * 控件是否有焦点
     */
    private boolean hasFoucs;

    private EditTextFocusChangeListener mEditTextFocusChangeListener;
    //private OnClearTextListener mOnClearTextListener;
    private OnClearIconShowListener mOnClearIconShowListener;

    public SearchEditText(Context context) {
        this(context, null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setCursorVisible(true);
        // 默认设置隐藏图标
        setClearIconVisible(false);
        // 设置焦点改变的监听
        setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
        setCustomSelectionActionModeCallback(mActionModeCallback);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuItem itemCopy = menu.findItem(android.R.id.copy);
            if (itemCopy != null) {
                menu.removeItem(android.R.id.copy);
            }
            MenuItem itemCut = menu.findItem(android.R.id.cut);
            if (itemCut != null) {
                menu.removeItem(android.R.id.cut);
            }
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight() - getPaddingRight())
                        && (event.getX() < (getWidth()));
                if (touchable) {
                    this.setText("");
                    if (mOnClearTextListener != null) {
                        mOnClearTextListener.onClickClearText();
                    }
                }
            }
        }*/

        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (mEditTextFocusChangeListener != null) {
            mEditTextFocusChangeListener.onFocusChange(v, hasFocus);
        }

    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        if (mOnClearIconShowListener != null) {
            if (visible) {
                mOnClearIconShowListener.onShowClearIcon();
            } else {
                mOnClearIconShowListener.onHideClearIcon();
            }
        }
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void setOnEditTextFocusChangeListener(EditTextFocusChangeListener editTextFocusChangeListener) {
        this.mEditTextFocusChangeListener = editTextFocusChangeListener;
    }

    /*public void setOnClearTextListener(OnClearTextListener mOnClearTextListener) {
        this.mOnClearTextListener = mOnClearTextListener;
    }*/

    public void setOnClearIconShowListener(OnClearIconShowListener onClearIconShowListener) {
        this.mOnClearIconShowListener = onClearIconShowListener;
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    /**
     * 可删除按钮的焦点发生变化的回调
     */
    public interface EditTextFocusChangeListener {
        void onFocusChange(View v, boolean hasFocus);
    }

    /**
     * x监听器
     */
    public interface OnClearTextListener {
        void onClickClearText();
    }

    public interface OnClearIconShowListener {
        void onShowClearIcon();

        void onHideClearIcon();
    }
}
