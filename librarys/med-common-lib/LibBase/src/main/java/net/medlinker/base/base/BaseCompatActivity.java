package net.medlinker.base.base;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.medlinker.widget.loading.MLLoadingView;
import com.medlinker.widget.navigation.CommonNavigationBar;
import org.greenrobot.eventbus.EventBus;

/**
 * @author hmy
 * @time 2020/9/22 11:30
 */
public class BaseCompatActivity extends BaseActivity {
    protected CommonNavigationBar mNavigation;
    private View mContent;
    protected MLLoadingView mLoadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        mNavigation = new CommonNavigationBar(this);
        mNavigation.showBackIcon(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initActionBar(mNavigation);

        if (registerEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        mContent = view;
        View contentView = mContent;
        //标题栏
        if (needNavigation()) {
            FrameLayout frameLayout = new FrameLayout(this);
            frameLayout.addView(this.mNavigation, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.topMargin = mNavigation.getNavigationHeight();
            frameLayout.addView(mContent, params);
            contentView = frameLayout;
            if (needNavigationShadow()) {
                mNavigation.showBottomLine();
            }
        }
        //loading view
        if (needLoading()) {
            FrameLayout frameLayout = new FrameLayout(this);
            frameLayout.addView(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mLoadingView = new MLLoadingView(this);
            mLoadingView.setVisibility(View.GONE);
            frameLayout.addView(this.mLoadingView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            contentView = frameLayout;
        }
        super.setContentView(contentView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registerEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }


    /**
     * 是否注册EventBus，默认不注册
     *
     * @return
     */
    protected Boolean registerEventBus() {
        return false;
    }

    //--------------------------标题栏  start ---------------
    protected boolean needNavigation() {
        return true;
    }

    protected boolean needNavigationShadow() {
        return true;
    }

    public CommonNavigationBar getNavigationBar() {
        return mNavigation;
    }

    protected void initActionBar(CommonNavigationBar navigation) {
    }

    public void hideNavigation() {
        if (null != mNavigation && mNavigation.getVisibility() != View.GONE) {
            mNavigation.setVisibility(View.GONE);
            if (mContent != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContent.getLayoutParams();
                layoutParams.topMargin = 0;
                mContent.setLayoutParams(layoutParams);
            }
        }
    }

    public void showNavigation() {
        if (null != mNavigation && mNavigation.getVisibility() != View.VISIBLE) {
            mNavigation.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContent.getLayoutParams();
            layoutParams.topMargin = mNavigation.getNavigationHeight();
            mContent.setLayoutParams(layoutParams);
        }
    }
    //--------------------------标题栏  end ---------------

    //--------------------------loading  start ---------------
    protected boolean needLoading() {
        return false;
    }

    public MLLoadingView getLoadingView() {
        return mLoadingView;
    }

    public void showLoading() {
        if (mLoadingView != null) {
            mLoadingView.showLoading();
        }
    }

    public void hideLoading() {
        if (mLoadingView != null) {
            mLoadingView.dismiss();
        }
    }
    //--------------------------loading  end ---------------
}
