package com.medlinker.debugtools.fun.crash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.didichuxing.doraemonkit.kit.core.AbsDokitView;
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams;
import com.medlinker.debugtools.DTModule;
import com.medlinker.debugtools.R;
import com.medlinker.debugtools.utils.DeviceUtil;

public class DTCrashCaptureView extends AbsDokitView {

    private TextView mTitle;
    private TextView mContent;
    private TextView mLeft;
    private TextView mRight;
    private String mContentStr;

    private View.OnClickListener mLeftOnClickListener;
    private View.OnClickListener mRightOnClickListener;
    @Override
    public void onCreate(Context context) {

    }

    @Override
    public View onCreateView(Context context, FrameLayout rootView) {
        return LayoutInflater.from(context).inflate(R.layout.dt_crash_capture,rootView,true);
    }

    @Override
    public void onViewCreated(FrameLayout rootView) {
        mTitle = rootView.findViewById(R.id.title);
        mContent = rootView.findViewById(R.id.dialog_content);
        mLeft = rootView.findViewById(R.id.cancel_action);
        mRight = rootView.findViewById(R.id.okButton);

        mLeft.setOnClickListener(v -> {
            if (null != mLeftOnClickListener){
                mLeftOnClickListener.onClick(v);
            }
        });

        mRight.setOnClickListener(v -> {
            if (null != mLeftOnClickListener){
                mRightOnClickListener.onClick(v);
            }
        });
    }

    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
      params.width = (int) (0.8f * DeviceUtil.getScreenWidth(DTModule.app()));
      params.height = (int) (0.6f * DeviceUtil.getScreenHeight(DTModule.app()));
    }

    public void setContent(String content){
        mContentStr =content;
        if (null != mContent){
            mContent.setText(content);
        }
    }

    public String getContent(){
        return mContentStr;
    }

    public void setLeftOnClickListener(View.OnClickListener onClickListener){
        mLeftOnClickListener = onClickListener;
    }

    public void setRightOnClickListener(View.OnClickListener onClickListener){
        mRightOnClickListener = onClickListener;
    }
}
