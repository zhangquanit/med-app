package com.medlinker.widget.shape;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.medlinker.widget.shape.inject.ShapeAttrParser;

/**
 * 支持shape背景的TextView
 *
 * @author zhangquan
 */
@SuppressLint("AppCompatCustomView")
public class ShapeTextView extends TextView {

    public ShapeTextView(Context context) {
        this(context, null);
    }

    public ShapeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ShapeAttrParser.parseAttr(context, attrs,this);
    }
}
