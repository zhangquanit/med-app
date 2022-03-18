package com.medlinker.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Terry on 15/11/9.
 */
public class MedInputMethodUtil {

    /**
     * 假设软键盘的最小高度
     */
    private static final int SOFT_KEY_BOARD_MIN_HEIGHT = 300;

    /**
     * 弹出输入法
     */
    public static void popInputMethod(final Context context, final View v) {
        v.setFocusable(true);
        v.requestFocus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);
    }

    public static void obtainFocus(View v) {
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.requestFocusFromTouch();
    }

    /**
     * 弹出输入法
     */
    public static void popInputMethod(final Context context, final View v, final int flags) {
        v.setFocusable(true);
        v.requestFocus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(v, flags);
                }
            }
        }, 100);
    }

    /**
     * 隐藏输入法
     *
     * @param activity Activity
     */
    public static void hintInputMethod(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 隐藏键盘
     *
     * @param context  Context
     * @param editText EditText
     */
    public static void hideInputMethod(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static boolean isSoftKeyboardVisible(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //判断隐藏软键盘是否弹出
        return imm != null && imm.isActive();
    }

    public static void registerActivity(Activity a, KeyboardVisibilityListener listener) {
        registerView(a.getWindow().getDecorView().findViewById(android.R.id.content), listener);
    }

    public static void registerView(final View rootView, final KeyboardVisibilityListener listener) {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean keyboardVisible;
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
                // if more than 300 pixels, its probably a keyboard...
                if (heightDiff > SOFT_KEY_BOARD_MIN_HEIGHT && !keyboardVisible) {
                    keyboardVisible = true;
                    if (listener != null) {
                        listener.onVisibilityChanged(true);
                    }
                } else if(heightDiff <= SOFT_KEY_BOARD_MIN_HEIGHT && keyboardVisible){
                    keyboardVisible = false;
                    if (listener != null) {
                        listener.onVisibilityChanged(false);
                    }
                }
            }
        });
    }

    public interface KeyboardVisibilityListener {
        void onVisibilityChanged(boolean keyboardVisible);
    }

}
