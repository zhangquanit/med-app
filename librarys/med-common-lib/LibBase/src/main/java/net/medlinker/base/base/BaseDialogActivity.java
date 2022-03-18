package net.medlinker.base.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import net.medlinker.base.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 弹窗activity 基类
 *
 * @author hmy
 */
public abstract class BaseDialogActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isTranslucentOrFloating(this)) {
            fixOrientation(this);
        }
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(canCancelOutside());
        setContentView(getLayoutId());
        onFullScreen();
        onCreated();
    }

    protected void onFullScreen() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected void onCreated() {
    }

    protected abstract int getLayoutId();

    protected boolean canCancelOutside() {
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isTranslucentOrFloating(this)) {
//            KLog.debug("api 26 全屏横竖屏切换 crash");
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }


    /**
     * 针对 Android 27 的情况进行处理
     * 横竖屏设置了方向会崩溃的问题
     *
     * @return
     */
    private boolean isTranslucentOrFloating(Activity activity) {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            TypedArray ta = activity.obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    /**
     * 修复横竖屏 crash 的问题
     *
     * @return
     */
    private boolean fixOrientation(Activity activity) {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(activity);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
