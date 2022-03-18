package com.medlinker.widget.loading;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresPermission;

import com.medlinker.widget.R;
import com.medlinker.widget.button.CommonButton;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;


/**
 * Created by kuiwen on 2015/8/21.
 */
public class MLLoadingView extends FrameLayout {

    private ImageView mProgress;
    private ImageView mIcon, mIconBack;
    private TextView mDesc;
    private CommonButton mRetry;
    private ViewGroup mRetryGroup;
    private FrameLayout mCustomEmptyLayout;

    private FrameAnimDrawable mProgressDrawable;
    private boolean isInterruptTouchEvent;

    private boolean interruptTouchEvent;

    public MLLoadingView(Context context) {
        super(context);
        init();
    }

    public MLLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MLLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (interruptTouchEvent) return true;
        return super.dispatchTouchEvent(ev);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_loading_view, this);
        setFocusable(true);
        mProgress = findViewById(R.id.loading_progressbar);
        mRetryGroup = findViewById(R.id.loading_group);
        mIconBack = findViewById(R.id.iv_back);

        mIcon = mRetryGroup.findViewById(R.id.loading_image);
        mDesc = mRetryGroup.findViewById(R.id.loading_desc);
        mRetry = mRetryGroup.findViewById(R.id.loading_retry);

        setBackgroundColor(Color.WHITE);

        int[] RES_IDS = new int[]{R.mipmap.loading_00000, R.mipmap.loading_00001, R.mipmap.loading_00002, R.mipmap.loading_00003, R.mipmap.loading_00004,
                R.mipmap.loading_00005, R.mipmap.loading_00006, R.mipmap.loading_00007, R.mipmap.loading_00008, R.mipmap.loading_00009,
                R.mipmap.loading_00010, R.mipmap.loading_00011, R.mipmap.loading_00012, R.mipmap.loading_00013, R.mipmap.loading_00014,
                R.mipmap.loading_00015, R.mipmap.loading_00016, R.mipmap.loading_00017, R.mipmap.loading_00018, R.mipmap.loading_00019,
                R.mipmap.loading_00020, R.mipmap.loading_00021, R.mipmap.loading_00022, R.mipmap.loading_00023, R.mipmap.loading_00024,
                R.mipmap.loading_00025, R.mipmap.loading_00026, R.mipmap.loading_00027, R.mipmap.loading_00028, R.mipmap.loading_00029,
                R.mipmap.loading_00030, R.mipmap.loading_00031, R.mipmap.loading_00032, R.mipmap.loading_00033, R.mipmap.loading_00034,
                R.mipmap.loading_00035, R.mipmap.loading_00036, R.mipmap.loading_00037, R.mipmap.loading_00038, R.mipmap.loading_00039,
                R.mipmap.loading_00040, R.mipmap.loading_00041, R.mipmap.loading_00042, R.mipmap.loading_00043, R.mipmap.loading_00044
        };

        mProgressDrawable = new FrameAnimDrawable(23, RES_IDS, getResources());
        mProgress.setImageDrawable(mProgressDrawable);
    }


    public void setRetryListener(OnClickListener listener) {
        mRetry.setOnClickListener(listener);
    }

    public void setOnBackListener(OnClickListener listener) {
        mIconBack.setOnClickListener(listener);
    }

    public void setInterruptTouchEvent(boolean isInterruptTouchEvent) {
        this.isInterruptTouchEvent = isInterruptTouchEvent;
    }

    public void showIconBack() {
        mIconBack.setVisibility(VISIBLE);
    }

    public void setCustomEmptyView(View customView) {
        mCustomEmptyLayout = findViewById(R.id.layout_custom_empty);
        mCustomEmptyLayout.setVisibility(GONE);
        mCustomEmptyLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCustomEmptyLayout.addView(customView);
    }

    public void showLoading() {
        interruptTouchEvent = isInterruptTouchEvent;
        setVisibility(VISIBLE);
        mRetryGroup.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        mProgressDrawable.start();
    }

    public boolean isLoading() {
        return (mProgress.getVisibility() == View.VISIBLE);
    }

    /**
     * 显示空数据
     */
    public void showEmpty() {
        showEmpty(R.string.widget_loadingview_empty);
    }

    public void showEmpty(int emptyTextId) {
        showEmpty(emptyTextId, -1);
    }

    public void showEmpty(String emptyText) {
        showEmpty(emptyText, -1);
    }

    public void showEmpty(int emptyTextId, int emptyIconId) {
        showEmpty(getResources().getString(emptyTextId), emptyIconId);
    }

    public void showEmpty(String emptyTextId, int emptyIconId) {
        interruptTouchEvent = isInterruptTouchEvent;
        setVisibility(VISIBLE);
        mProgress.setVisibility(GONE);
        mProgressDrawable.stop();
        mRetryGroup.setVisibility(VISIBLE);
        mRetry.setVisibility(GONE);
        if (mCustomEmptyLayout == null) {
            mDesc.setText(emptyTextId);
            if (emptyIconId == -1) {
                mIcon.setImageResource(EmptyViewType.DEFAULT_EMPTY.getIcon());
            } else {
                mIcon.setImageResource(emptyIconId);
            }
        } else {
            mCustomEmptyLayout.setVisibility(VISIBLE);
        }
    }

    public void showEmpty(EmptyViewType type) {
        showEmpty(type.getDesc(), type.getIcon());
    }

    /*------------新ui------start------*/

    /**
     * 带按钮的空页面
     */
    public void showEmptyAndButton() {
        showEmptyHasButton("", 0, "");
    }

    /**
     * 带按钮的空页面
     */
    public void showEmptyAndButton(String buttonText) {
        showEmptyHasButton("", EmptyViewType.DEFAULT_EMPTY.getIcon(), buttonText);
    }

    /**
     * 带按钮的空页面
     */
    public void showEmptyAndButton(String emptyText, String buttonText) {
        showEmptyHasButton(emptyText, EmptyViewType.DEFAULT_EMPTY.getIcon(), buttonText);
    }

    /**
     * 带按钮的空页面
     *
     * @param emptyViewType
     * @return 返回的CommonButton可以用ButtonHelper处理样式
     */
    public void showEmptyAndButton(EmptyViewType emptyViewType, String buttonText) {
        if (emptyViewType != null) {
            showEmptyHasButton(this.getContext().getString(emptyViewType.getDesc()),
                    emptyViewType.getIcon(), buttonText);
        } else {
            showEmptyHasButton(this.getContext().getString(EmptyViewType.DEFAULT_EMPTY.getDesc()),
                    EmptyViewType.DEFAULT_EMPTY.getIcon(), buttonText);
        }
    }

    /**
     * @param emptyText
     * @param emptyIconId
     * @return CommonButton
     */
    private void showEmptyHasButton(String emptyText, int emptyIconId, String buttonText) {
        interruptTouchEvent = isInterruptTouchEvent;
        setVisibility(VISIBLE);
        mProgress.setVisibility(GONE);
        mProgressDrawable.stop();
        mRetryGroup.setVisibility(VISIBLE);
        mRetry.setVisibility(VISIBLE);
        if (mCustomEmptyLayout == null) {
            mDesc.setText(TextUtils.isEmpty(emptyText) ? this.getContext().getText(EmptyViewType.DEFAULT_EMPTY.mDesc) : emptyText);
            if (emptyIconId <= 0) {
                mIcon.setImageResource(EmptyViewType.DEFAULT_EMPTY.getIcon());
            } else {
                mIcon.setImageResource(emptyIconId);
            }
        } else {
            mCustomEmptyLayout.setVisibility(VISIBLE);
        }

        if (!TextUtils.isEmpty(buttonText)) {
            mRetry.setText(buttonText);
        }
    }


    /*------------新ui------end------*/

    public void showRetry(String msgStr) {
        if (TextUtils.isEmpty(msgStr)) {
            msgStr = getContext().getResources().getString(R.string.network_not_connected_tip);
        }

        boolean isConnected = isConnected();
        int iconId = isConnected ? EmptyViewType.LOAD_ERROR_SERVICE.getIcon() : EmptyViewType.LOAD_ERROR_NETWORK.getIcon();


        setVisibility(VISIBLE);
        mProgress.setVisibility(GONE);
        mProgressDrawable.stop();
        mRetryGroup.setVisibility(VISIBLE);
        mRetry.setVisibility(VISIBLE);
        mDesc.setText(isConnected ? msgStr : getContext().getString(EmptyViewType.LOAD_ERROR_NETWORK.getDesc()));
        mIcon.setImageResource(iconId);
        if (mCustomEmptyLayout != null) {
            mCustomEmptyLayout.setVisibility(GONE);
        }
    }

    public void dismiss() {
        interruptTouchEvent = false;
        setVisibility(GONE);
        if (mProgress != null) {
            mProgress.setVisibility(GONE);
            mProgressDrawable.stop();
        }
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    public boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return null;
        return cm.getActiveNetworkInfo();
    }

    /**
     * 空界面类型
     */
    public enum EmptyViewType {
        //------------8.5之后，新ui--------------
        DEFAULT_EMPTY(R.string.widget_loadingview_empty, R.mipmap.load_page_empty),
        LOAD_ERROR_NETWORK(R.string.network_not_avliable, R.mipmap.load_page_network_error),
        LOAD_ERROR_SERVICE(R.string.network_service_bad, R.mipmap.load_page_service_error),
        ;

        int mDesc;
        int mIcon;

        EmptyViewType(int desc, int icon) {
            mDesc = desc;
            mIcon = icon;
        }

        /*临时兼容，应用默认图片*/
        EmptyViewType(int desc) {
            mDesc = desc;
            mIcon = R.mipmap.load_page_empty;
        }

        public int getIcon() {
            return mIcon;
        }

        public int getDesc() {
            return mDesc;
        }

        public void setDesc(int mDesc) {
            this.mDesc = mDesc;
        }

    }
}
