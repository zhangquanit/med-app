package com.medlinker.widget.loading;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 帧动画Drawable。避免内存泄漏。
 * Created by jiantao on 2017/7/30.
 */

public class FrameAnimDrawable extends Drawable implements Animatable {
    private Resources mResources;
    private int fps = 25;
    private Paint mPaint = null;
    private int[] RES_IDS = null;
    private String[] RES_PATHS = null;
    private int resIndex;

    private int mRepeatCount = ValueAnimator.INFINITE; //播放次数，默认为循环播放

    private ValueAnimator mAnimator;
    private ValueAnimator.AnimatorUpdateListener mAnimUpdateListener;
    private ValueAnimator.AnimatorListener mAnimListener;

    //取第一帧，用于获取图片宽高
    private Drawable mFirstDrawable;
    private int mLength;//资源或文件数量
    private int mBgColor = Color.TRANSPARENT;

    public FrameAnimDrawable() {
    }

    public FrameAnimDrawable(int fps, @NonNull int[] RES_IDS, @NonNull Resources resources) {
        initById(fps, RES_IDS, resources);
    }

    public FrameAnimDrawable(int fps, @NonNull String[] RES_PATHS, @NonNull Resources resources) {
        initByPath(fps, RES_PATHS, resources);
    }

    public void initById(int fps, @NonNull int[] RES_IDS, @NonNull Resources resources) {
        this.fps = fps;
        this.RES_IDS = RES_IDS;
        this.mResources = resources;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        if (RES_IDS.length <= 0) {
            throw new RuntimeException(" FrameAnimDrawable RES_IDS can not empty !!!");
        }
        mLength = RES_IDS.length;
        mFirstDrawable = resources.getDrawable(RES_IDS[0]);
        createAnimator();
    }

    public void initByPath(int fps, @NonNull String[] RES_PATHS, @NonNull Resources resources) {
        this.fps = fps;
        this.RES_PATHS = RES_PATHS;
        this.mResources = resources;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);

        if (RES_PATHS.length <= 0) {
            throw new RuntimeException(" FrameAnimDrawable RES_PATHS can not empty !!!");
        }
        mLength = RES_PATHS.length;
        mFirstDrawable = getDrawableFromFile(RES_PATHS[0]);
        createAnimator();
    }

    /**
     * @param path
     * @return
     */
    private BitmapDrawable getDrawableFromFile(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) return null;
        return new BitmapDrawable(mResources, bitmap);
    }

    private void createAnimator() {
        mAnimator = ValueAnimator.ofInt(mLength - 1);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(mRepeatCount);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setDuration(mLength / fps * 1000);

        mAnimUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate(((int) animation.getAnimatedValue()));
            }
        };
    }

    /**
     * 重绘
     */
    public void invalidate(int index) {
        this.resIndex = index;
        invalidateSelf();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawColor(mBgColor);
        if (mPaint != null && mResources != null) {
            if (RES_IDS != null) {
                BitmapDrawable drawable = (BitmapDrawable) mResources.getDrawable(RES_IDS[resIndex % RES_IDS.length]);
                Bitmap bitmap = drawable.getBitmap();
                canvas.drawBitmap(bitmap, 0, 0, mPaint);
            } else if (RES_PATHS != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(RES_PATHS[resIndex % RES_PATHS.length]);
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, 0, 0, mPaint);
                }
            }
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void start() {
        // If the animators has not ended, do nothing.
        if (mAnimator == null || mAnimator.isStarted()) return;
        startAnimator();
        invalidateSelf();
    }

    private void startAnimator() {
        mAnimator.addUpdateListener(mAnimUpdateListener);
        if (mAnimListener != null) {
            mAnimator.addListener(mAnimListener);
        }
        mAnimator.start();
    }

    @Override
    public void stop() {
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.removeAllUpdateListeners();
            mAnimator.removeAllListeners();
            mAnimator.end();
        }
    }

    @Override
    public boolean isRunning() {
        return mAnimator.isRunning();
    }

    @Override
    public int getIntrinsicWidth() {
        if (mFirstDrawable != null) {
            return mFirstDrawable.getIntrinsicWidth();
        } else {
            return super.getIntrinsicWidth();
        }
    }

    @Override
    public int getIntrinsicHeight() {
        if (mFirstDrawable != null) {
            return mFirstDrawable.getIntrinsicHeight();
        } else {
            return super.getIntrinsicHeight();
        }
    }

    /**
     * @return 帧数量
     */
    public int getFrameCount() {
        return mLength;
    }

    /**
     * 设置一次动画执行时间
     */
    public void setDuration(float duration) {
        if (mAnimator != null) {
            mAnimator.setDuration((long) (duration * 1000));
        }
    }

    public void setRepeatCount(int count) {
        if (count <= 0) {
            mRepeatCount = ValueAnimator.INFINITE;
        } else {
            mRepeatCount = count - 1;
        }
        if (mAnimator != null) {
            mAnimator.setRepeatCount(mRepeatCount);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
        }
    }

    public void setAnimListener(ValueAnimator.AnimatorListener listener) {
        mAnimListener = listener;
    }

    public void setBackgroundColor(int color) {
        mBgColor = color;
    }

}
