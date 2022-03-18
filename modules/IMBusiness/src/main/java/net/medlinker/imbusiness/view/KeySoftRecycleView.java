package net.medlinker.imbusiness.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.medlinker.lib.utils.MedInputMethodUtil;


/**
 * @author hmy
 * @time 2020/9/23 21:12
 */
public class KeySoftRecycleView extends RecyclerView {

    private OnHideMenuListener mHideMenuListener;
    private GestureDetector mGestureDetector;

    public void setOnHideMenuListener(KeySoftRecycleView.OnHideMenuListener listener) {
        mHideMenuListener = listener;
    }

    public interface OnHideMenuListener {
        void hideMenu();
    }

    public KeySoftRecycleView(Context context) {
        super(context);
        init();
    }

    public KeySoftRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeySoftRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private class ChatPageGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            MedInputMethodUtil.hintInputMethod((Activity) getContext());
            if (null != mHideMenuListener) mHideMenuListener.hideMenu();
            return super.onDown(e);
        }
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), new ChatPageGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

}
