package com.medlinker.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 设置圆角
 *
 * @author hmy
 * @time 2020/5/20 14:52
 */
public class RoundRectView extends View {
    private Path mPath;
    private int mRadius;

    private int mWidth;
    private int mHeight;
    private int mLastRadius;

    public static final int MODE_NONE = 0;
    public static final int MODE_ALL = 1;
    public static final int MODE_LEFT = 2;
    public static final int MODE_TOP = 3;
    public static final int MODE_RIGHT = 4;
    public static final int MODE_BOTTOM = 5;

    private int mRoundMode = MODE_NONE;

    @IntDef({MODE_NONE, MODE_ALL, MODE_LEFT, MODE_TOP, MODE_RIGHT, MODE_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RoundType {
    }

    public RoundRectView(Context context) {
        this(context, null);
    }

    public RoundRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPath = new Path();
        mPath.setFillType(Path.FillType.EVEN_ODD);
    }

    /**
     * 设置是否圆角裁边
     *
     * @param roundMode
     */
    public void setRoundMode(@RoundType int roundMode) {
        mRoundMode = roundMode;
    }

    /**
     * 设置圆角半径
     *
     * @param radius
     */
    public void setCornerRadius(int radius) {
        mRadius = radius;
    }

    private void checkPathChanged() {
        if (getWidth() == mWidth && getHeight() == mHeight && mLastRadius == mRadius) {
            return;
        }
        mWidth = getWidth();
        mHeight = getHeight();
        mLastRadius = mRadius;

        mPath.reset();

        switch (mRoundMode) {
            case MODE_ALL:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight), mRadius, mRadius, Path.Direction.CW);
                break;
            case MODE_LEFT:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{mRadius, mRadius, 0, 0, 0, 0, mRadius, mRadius},
                        Path.Direction.CW);
                break;
            case MODE_TOP:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{mRadius, mRadius, mRadius, mRadius, 0, 0, 0, 0},
                        Path.Direction.CW);
                break;
            case MODE_RIGHT:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{0, 0, mRadius, mRadius, mRadius, mRadius, 0, 0},
                        Path.Direction.CW);
                break;
            case MODE_BOTTOM:
                mPath.addRoundRect(new RectF(0, 0, mWidth, mHeight),
                        new float[]{0, 0, 0, 0, mRadius, mRadius, mRadius, mRadius},
                        Path.Direction.CW);
                break;
        }

    }

    @Override
    public void draw(Canvas canvas) {
        if (mRoundMode != MODE_NONE) {
            int saveCount = canvas.save();
            checkPathChanged();
            canvas.clipPath(mPath);
            super.draw(canvas);
            canvas.restoreToCount(saveCount);
        } else {
            super.draw(canvas);
        }
    }

}
