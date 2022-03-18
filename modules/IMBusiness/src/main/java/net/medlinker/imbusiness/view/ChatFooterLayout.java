package net.medlinker.imbusiness.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * @author hmy
 * @time 2020/9/23 21:27
 */
public class ChatFooterLayout extends LinearLayout {
    public ChatFooterLayout(Context context) {
        this(context, null);
    }

    public ChatFooterLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatFooterLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }
}
