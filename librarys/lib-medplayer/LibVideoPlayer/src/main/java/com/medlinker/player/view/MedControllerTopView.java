package com.medlinker.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medlinker.player.R;
import com.medlinker.player.util.MedPlayerConfig;

/**
 * 控制器顶部View
 */
public class MedControllerTopView extends RelativeLayout implements View.OnClickListener {
    private TextView mTitleView;
    private View mRootView;
    private ImageView mBackView;
    private int mFullScreenHeight, mNormalHeight;
    private OnClickListener mOnClickListener;


    public MedControllerTopView(Context context) {
        super(context);
        setupViews(context);
    }

    public MedControllerTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupViews(context);
    }

    public MedControllerTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews(context);
    }

    private void setupViews(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.med_video_player_controller_topview, this);
        mRootView = view.findViewById(R.id.controller_top_layout);

        mTitleView = view.findViewById(R.id.tv_title);
        mBackView = view.findViewById(R.id.iv_back);
        mBackView.setOnClickListener(this);
        mBackView.setTag(R.id.iv_back, MedPlayerConfig.ID_TOP_BACK);
        mFullScreenHeight = context.getResources().getDimensionPixelSize(R.dimen.med_player_top_view_fullscreen_height);
        mNormalHeight = context.getResources().getDimensionPixelSize(R.dimen.med_player_top_view_normal_height);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    public void setScreenMode(boolean isFullScreen) {
        setRootViewLayoutParams(isFullScreen);
    }

    public void setViewsOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    private void setRootViewLayoutParams(boolean isFullScreen) {
        int height = isFullScreen ? mFullScreenHeight : mNormalHeight;
        ViewGroup.LayoutParams layoutParams = mRootView.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = height;
        } else {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        }
        mRootView.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(view);
        }
    }

    public ImageView getBackView() {
        return mBackView;
    }

    public TextView getTitleView() {
        return mTitleView;
    }
}
