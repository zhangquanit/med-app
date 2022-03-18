package net.medlinker.imbusiness.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author hmy
 * @time 2020/9/23 21:26
 */
public class ChatHeaderLayout extends FrameLayout {

    private LinearLayout mContentLayout;

    public ChatHeaderLayout(@NonNull Context context) {
        this(context, null);
    }

    public ChatHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContentLayout = new LinearLayout(context);
        mContentLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        addView(mContentLayout);
    }

    public void addContentView(View view) {
        mContentLayout.addView(view);
    }

    public void removeAllContentView() {
        mContentLayout.removeAllViews();
    }

    public void removeContentView(View view) {
        mContentLayout.removeView(view);
    }

    public void addFrameChildView(View view) {
        addView(view);
    }
}
