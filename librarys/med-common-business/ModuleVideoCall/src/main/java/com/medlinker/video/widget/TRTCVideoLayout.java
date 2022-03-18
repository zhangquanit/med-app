package com.medlinker.video.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.medlinker.lib.utils.MedScreenUtil;
import com.medlinker.video.R;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * 封装了{@link TXCloudVideoView} 以及业务逻辑 UI 控件
 *
 * @author hmy
 */
public class TRTCVideoLayout extends RelativeLayout {

    private TXCloudVideoView mVideoView;

    private GestureDetector mSimpleOnGestureListener;
    private OnClickListener mClickListener;
    private boolean mMovable;
    private int mScreenW, mScreenH;

    public TRTCVideoLayout(Context context) {
        this(context, null);
    }

    public TRTCVideoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TRTCVideoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_trtc_video, this);
        mVideoView = findViewById(R.id.v_trtc_video);

        mScreenW =  MedScreenUtil.getScreenWidth(context);
        mScreenH =  MedScreenUtil.getScreenHeight(context);
        initGestureListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initGestureListener() {
        mSimpleOnGestureListener = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (mClickListener != null) {
                    mClickListener.onClick(TRTCVideoLayout.this);
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!mMovable) return false;
                ViewGroup.LayoutParams params = TRTCVideoLayout.this.getLayoutParams();
                // 当 TRTCVideoView 的父容器是 RelativeLayout 的时候，可以实现拖动
                if (params instanceof LayoutParams) {
                    LayoutParams layoutParams = (LayoutParams) TRTCVideoLayout.this.getLayoutParams();
                    int newX = (int) (layoutParams.leftMargin + (e2.getX() - e1.getX()));
                    int newY = (int) (layoutParams.topMargin + (e2.getY() - e1.getY()));

                    if (newX < 0) {
                        newX = 0;
                    } else if (newX + layoutParams.width > mScreenW) {
                        newX = mScreenW - layoutParams.width;
                    }
                    if (newY < 0) {
                        newY = 0;
                    } else if (newY + layoutParams.height > mScreenH) {
                        newY = mScreenH - layoutParams.height;
                    }
                    layoutParams.leftMargin = newX;
                    layoutParams.topMargin = newY;

                    TRTCVideoLayout.this.setLayoutParams(layoutParams);
                }
                return true;
            }
        });
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mSimpleOnGestureListener.onTouchEvent(event);
            }
        });
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mClickListener = l;
    }

    public void setMovable(boolean enable) {
        mMovable = enable;
    }

    public TXCloudVideoView getVideoView() {
        return mVideoView;
    }
}
