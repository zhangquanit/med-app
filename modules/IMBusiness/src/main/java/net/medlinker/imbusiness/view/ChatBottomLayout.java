package net.medlinker.imbusiness.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;

import com.medlinker.lib.utils.MedInputMethodUtil;

import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author hmy
 * @time 2020/9/23 20:54
 */
public class ChatBottomLayout extends LinearLayout {

    @BindView(R2.id.vs_chat_bottom_root)
    ViewSwitcher mRootViewSwitcher;
    @BindView(R2.id.vs_menu)
    ViewSwitcher mMenuViewSwitcher;
    @BindView(R2.id.et_chat)
    EditText mInputEt;
    @BindView(R2.id.layout_chat_bottom_float)
    FrameLayout mBottomFloatLayout;

    private View mMenuView;

    private boolean mIsKeyboardVisible;
    private OnChatListener mOnChatListener;

    public ChatBottomLayout(Context context) {
        this(context, null);
    }

    public ChatBottomLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatBottomLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_chat_bottom, this);
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.vs_menu)
    public void onClickMenu() {
        boolean isMenuOpened = mMenuViewSwitcher.getDisplayedChild() == 0;
        if (!isMenuOpened) {
            MedInputMethodUtil.hintInputMethod((Activity) getContext());
        }
        switchMenuBtn(!isMenuOpened);
    }

    @OnClick(R2.id.btn_send)
    public void onClickSend() {
        String text = mInputEt.getText().toString();
        if (null != mOnChatListener && mOnChatListener.onSendMsgIntercept()) {
            return;
        }
        mInputEt.setText("");
        if (!TextUtils.isEmpty(text)) {
            if (null != mOnChatListener) {
                mOnChatListener.onSendText(text);
            }
        }
    }

    public void bindMenuView(View view) {
        mMenuView = view;
    }

    public void switchFloatView(View floatView) {
        mBottomFloatLayout.removeAllViews();
        mBottomFloatLayout.addView(floatView);
        mRootViewSwitcher.setDisplayedChild(1);
        switchMenuBtn(true);
    }

    public void switchInputView() {
        mRootViewSwitcher.setDisplayedChild(0);
    }

    public void onKeyboardVisibility(boolean keyboardVisible) {
        mIsKeyboardVisible = keyboardVisible;
        if (keyboardVisible) {
            switchMenuBtn(false);
        }
    }

    public void setInputText(String inputText) {
        mInputEt.setText(inputText);
    }

    public CharSequence getInputText() {
        return mInputEt.getText();
    }

    public EditText getInputEditText() {
        return mInputEt;
    }

    public boolean onBackPressed() {
        if (mIsKeyboardVisible) {
            switchMenuBtn(false);
            MedInputMethodUtil.hintInputMethod((Activity) getContext());
            return false;
        } else {
            return true;
        }
    }

    private void switchMenuBtn(boolean isOpenMenu) {
        mMenuViewSwitcher.setDisplayedChild(isOpenMenu ? 0 : 1);
        if (null != mMenuView) {
            mMenuView.setVisibility(!isOpenMenu ? GONE : VISIBLE);
            if (isOpenMenu && null != mOnChatListener) {
                mOnChatListener.onGirdMenuShow();
            }
        }
    }

    public void setOnChatListener(OnChatListener listener) {
        mOnChatListener = listener;
    }
}
