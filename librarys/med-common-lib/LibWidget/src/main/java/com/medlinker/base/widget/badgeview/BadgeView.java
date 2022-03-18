package com.medlinker.base.widget.badgeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.medlinker.widget.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 小红点和带数字的红点，可以绑定到目标布局，也可以直接在xml中使用
 *
 * @author hmy
 */
public class BadgeView extends View implements Badge {

    public static final int CIRCLE = 1, POINT = 2;

    /***/
    @IntDef({CIRCLE, POINT})
    @Retention(RetentionPolicy.SOURCE)
    @interface BadgeStyle {
    }

    private static final int DEFAULT_TEXT_SIZE = 12;
    private static final int DEFAULT_PADDING = 3;
    private static final int DEFAULT_POINT_RADIUS = 3;

    private int mBadgeBackgroundColor = Color.RED;
    private int mTextColor = Color.WHITE;
    private float mTextSize;
    private int mPadding;
    private int mBadgeStyle = CIRCLE;
    private int mPointRadius;

    private int mBadgeNum = 0;
    private String mText;
    private float mOffsetX, mOffsetY;
    private int mGravity = Gravity.END | Gravity.TOP;

    private float mTextHeight;
    private RectF mBadgeBackgroundRect;
    private TextPaint mNumberPaint;
    private Paint mBackgroundPaint;

    private View mTargetView;
    private String[] mBadgeTags;

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeView);
        mBadgeBackgroundColor = typedArray.getColor(R.styleable.BadgeView_badgeBackgroundColor, Color.RED);
        mTextColor = typedArray.getColor(R.styleable.BadgeView_badgeTextColor, Color.WHITE);
        mText = typedArray.getString(R.styleable.BadgeView_badgeText);
        mTextSize = typedArray.getDimension(R.styleable.BadgeView_badgeTextSize, spToPx(DEFAULT_TEXT_SIZE));
        mPadding = typedArray.getDimensionPixelOffset(R.styleable.BadgeView_badgePadding, (int) dp2px(DEFAULT_PADDING));
        mBadgeStyle = typedArray.getInt(R.styleable.BadgeView_badge_Style, CIRCLE);
        mPointRadius = typedArray.getDimensionPixelOffset(R.styleable.BadgeView_pointRadius, (int) dp2px(DEFAULT_POINT_RADIUS));
        typedArray.recycle();

        init();
    }

    private void init() {
        mText = mText == null ? "" : mText;
        mTextHeight = getTextHeight(mTextSize);
        mBadgeBackgroundRect = new RectF();

        mNumberPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mNumberPaint.setAntiAlias(true);
        mNumberPaint.setSubpixelText(true);
        mNumberPaint.setColor(mTextColor);
        mNumberPaint.setStyle(Paint.Style.FILL);
        mNumberPaint.setTextSize(mTextSize);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBadgeBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (mBadgeStyle == POINT) {
            if (specMode != MeasureSpec.EXACTLY) {
                specSize = (int) dp2px(DEFAULT_POINT_RADIUS * 2);
            }
            result = mPointRadius > 0 ? mPointRadius * 2 : specSize;
        } else if (mBadgeStyle == CIRCLE) {
            if (!TextUtils.isEmpty(mText)) {
                float textWith = getTextWidth(mText, mTextSize);
                textWith = textWith < mTextHeight ? mTextHeight : textWith;
                result = (int) (textWith + mPadding * 2);
            } else {
                result = 0;
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (mBadgeStyle == POINT) {
            if (specMode != MeasureSpec.EXACTLY) {
                specSize = (int) dp2px(DEFAULT_POINT_RADIUS * 2);
            }
            result = mPointRadius > 0 ? mPointRadius * 2 : specSize;
        } else if (mBadgeStyle == CIRCLE) {
            if (!TextUtils.isEmpty(mText)) {
                result = (int) (mTextHeight + mPadding * 2);
            } else {
                result = 0;
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBadgeBackgroundRect.left = 0;
        mBadgeBackgroundRect.top = 0;
        mBadgeBackgroundRect.right = getMeasuredWidth();
        mBadgeBackgroundRect.bottom = getMeasuredHeight();

        float radius = getMeasuredHeight() / 2f;

        if (mBadgeStyle == POINT) {
            canvas.drawCircle(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f, radius, mBackgroundPaint);
        } else if (mBadgeStyle == CIRCLE) {
            Paint.FontMetrics fontMetrics = mNumberPaint.getFontMetrics();
            float textH = fontMetrics.descent - fontMetrics.ascent;

            if (getMeasuredWidth() == getMeasuredHeight()) {
                canvas.drawCircle(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f, radius, mBackgroundPaint);
                canvas.drawText(mText, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f + (textH / 2f - fontMetrics.descent), mNumberPaint);
            } else {
                canvas.drawRoundRect(mBadgeBackgroundRect, radius, radius, mBackgroundPaint);
                canvas.drawText(mText, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f + (textH / 2f - fontMetrics.descent), mNumberPaint);
            }
        }
    }

    @Override
    public Badge setBadgeNumber(int badgeNum) {
        mBadgeNum = badgeNum;
        if (badgeNum <= 0) {
            mText = "";
        } else if (badgeNum <= 99) {
            mText = String.valueOf(badgeNum);
        } else {
            mText = "99+";
        }
        return setBadgeText(mText);
    }

    @Override
    public int getBadgeNumber() {
        return mBadgeNum;
    }

    @Override
    public Badge setBadgeText(String badgeText) {
        mText = badgeText;
        requestLayout();
        invalidate();
        return this;
    }

    @Override
    public Badge setBadgeBackgroundColor(int color) {
        mBadgeBackgroundColor = color;
        invalidate();
        return this;
    }

    @Override
    public Badge setBadgePadding(int padding) {
        mPadding = padding;
        invalidate();
        return this;
    }

    @Override
    public Badge setGravityOffset(float offset, boolean isDpValue) {
        setGravityOffset(offset, offset, isDpValue);
        return this;
    }

    @Override
    public Badge setGravityOffset(float offsetX, float offsetY, boolean isDpValue) {
        mOffsetX = isDpValue ? dp2px(offsetX) : offsetX;
        mOffsetY = isDpValue ? dp2px(offsetY) : offsetY;
        invalidate();
        return this;
    }

    @Override
    public Badge setTextColor(int color) {
        mTextColor = color;
        invalidate();
        return this;
    }

    @Override
    public Badge setTextSize(float textSize) {
        mTextSize = textSize;
        invalidate();
        return this;
    }

    @Override
    public Badge setPointRadius(int radius) {
        mPointRadius = radius;
        return this;
    }

    @Override
    public Badge setGravity(int gravity) {
        mGravity = gravity;
        return this;
    }

    @Override
    public Badge setBadgeStyle(@BadgeStyle int style) {
        mBadgeStyle = style;
        invalidate();
        return this;
    }

    @Override
    public Badge show() {
        setVisibility(VISIBLE);
        return this;
    }

    @Override
    public Badge hide() {
        setVisibility(GONE);
        return this;
    }

    @Override
    public Badge setVisibility(boolean isShow) {
        setVisibility(isShow ? VISIBLE : GONE);
        return this;
    }

    @Override
    public Badge setBadgeTag(String... tags) {
        mBadgeTags = tags;
        return this;
    }

    @Override
    public Badge refresh(BadgeViewStatus status) {
        if (status == null) {
            hide();
            return this;
        }
        if (status.getNum(mBadgeTags) > 0) {
            setBadgeStyle(CIRCLE);
            setBadgeNumber(status.getNum(mBadgeTags));
            show();
            requestLayout();
        } else if (status.isShowPoint(mBadgeTags)) {
            setBadgeStyle(POINT);
            show();
            requestLayout();
        } else {
            hide();
        }
        return this;
    }

    @Override
    public View getTargetView() {
        return mTargetView;
    }

    @Override
    public BadgeView bindTarget(View targetView) {
        if (targetView == null) {
            return this;
        }
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }

        ViewParent targetParent = targetView.getParent();
        if (targetParent instanceof ViewGroup) {
            mTargetView = targetView;
            if (targetParent instanceof FrameLayout) {
                ((FrameLayout) targetParent).addView(this);
            } else {
                ViewGroup targetContainer = (ViewGroup) targetParent;
                int index = targetContainer.indexOfChild(targetView);
                ViewGroup.LayoutParams targetParams = targetView.getLayoutParams();
                targetContainer.removeView(targetView);
                FrameLayout badgeContainer = new FrameLayout(getContext());

                targetContainer.addView(badgeContainer, index, targetParams);
                badgeContainer.addView(targetView);
                badgeContainer.addView(this);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
                params.gravity = mGravity;
                if (params.gravity == (Gravity.END | Gravity.TOP) || params.gravity == Gravity.END) {
                    params.setMargins(0, (int) mOffsetY, (int) mOffsetX, 0);
                } else if (params.gravity == (Gravity.START | Gravity.TOP) || params.gravity == Gravity.START || params.gravity == Gravity.TOP) {
                    params.setMargins((int) mOffsetX, (int) mOffsetY, 0, 0);
                } else if (params.gravity == (Gravity.START | Gravity.BOTTOM) || params.gravity == Gravity.BOTTOM) {
                    params.setMargins((int) mOffsetX, 0, 0, (int) mOffsetY);
                } else if (params.gravity == (Gravity.END | Gravity.BOTTOM)) {
                    params.setMargins(0, 0, (int) mOffsetX, (int) mOffsetY);
                } else {
                    params.setMargins(0, (int) mOffsetX, (int) mOffsetY, 0);
                }

                setLayoutParams(params);
            }
        } else {
            throw new IllegalStateException("BadgeView: targetView must have a parent");
        }


        return this;
    }

    private float dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (float) (dp * scale + 0.5);
    }

    private int spToPx(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private float getTextHeight(float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.ascent);
    }

    private float getTextWidth(String text, float textSize) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        return paint.measureText(text);
    }
}