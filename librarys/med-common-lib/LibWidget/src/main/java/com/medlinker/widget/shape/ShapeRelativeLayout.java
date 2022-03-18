package com.medlinker.widget.shape;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.medlinker.widget.shape.inject.ShapeAttrParser;

/**
 * 支持shape背景的RelativeLayout
 *
 * @author zhangquan
 */
public class ShapeRelativeLayout extends RelativeLayout {

    public ShapeRelativeLayout(Context context) {
        this(context, null);
    }

    public ShapeRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ShapeAttrParser.parseAttr(context, attrs,this);
    }
}
