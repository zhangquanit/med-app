package net.medlinker.main.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.medlinker.lib.log.LogUtil;

public class HomeTabIconView extends AppCompatImageView {

    private static final String TAG = "HomeTabIconView";
    private int[] mIconCheckedId;

    private int mPlaceholderId;
    private int mNormalId;

    private FastFrameAnimDrawable mDrawable;

    public HomeTabIconView(Context context) {
        super(context);
    }

    public HomeTabIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeTabIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIconCheckedId(int[] iconCheckedId) {
        if (null == iconCheckedId) {
            mIconCheckedId = new int[1];
        }
        mIconCheckedId = iconCheckedId;
        int len = mIconCheckedId.length;
        int fps = len > 22 ? len : (len * 2);
        mDrawable = new FastFrameAnimDrawable(fps, mIconCheckedId, getResources());
        mDrawable.setRepeatCount(1);
    }

    public void setPlaceholderId(int placeholderId) {
        mPlaceholderId = placeholderId;
    }

    public void setNormalId(int normalId) {
        mNormalId = normalId;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        LogUtil.d(TAG, "object:" + this.toString() + " ,selected:" + selected);
        if (selected) {
            setImageDrawable(mDrawable);
            mDrawable.start();
        } else {
            Drawable drawable = getDrawable();
            if (drawable instanceof FastFrameAnimDrawable) {
                ((FastFrameAnimDrawable) drawable).stop();
            }
            if (mNormalId > 0){
                setImageResource(mNormalId);
            }
        }
    }

    public void preAnim(){
        if (null != mDrawable){
            postDelayed(() ->{
                setImageDrawable(mDrawable);
                mDrawable.start();
            },300);
        }
    }
}
