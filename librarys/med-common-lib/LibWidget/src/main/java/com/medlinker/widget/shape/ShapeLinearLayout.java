package com.medlinker.widget.shape;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.medlinker.widget.shape.inject.ShapeAttrParser;

/**
 * 支持shape背景的LinearLayout
 *
 * @author zhangquan
 */
public class ShapeLinearLayout extends LinearLayout {

    public ShapeLinearLayout(Context context) {
        this(context, null);
    }

    public ShapeLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ShapeAttrParser.parseAttr(context, attrs,this);
    }
}
