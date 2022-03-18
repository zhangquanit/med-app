package com.medlinker.widget.shape;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.medlinker.widget.shape.inject.ShapeAttrParser;

/**
 * @author zhangquan
 */
public class ShapeView extends View {
    public ShapeView(Context context) {
        this(context, null);
    }

    public ShapeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ShapeAttrParser.parseAttr(context, attrs, this);
    }
}
