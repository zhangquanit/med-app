package com.medlinker.debugtools.vlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;

/**
 * @author: pengdaosong
 * @CreateTime: 2019-08-17 14:33
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class VLayoutRecycleView extends RecyclerView {

    private DelegateAdapter mDelegateAdapter;

    public VLayoutRecycleView(Context context) {
        super(context);
        init();
    }

    public VLayoutRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VLayoutRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DelegateAdapter getDelegateAdapter() {
        return mDelegateAdapter;
    }

    private void init() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        final VirtualLayoutManager layoutManager = new VirtualLayoutManager(getContext());
        setLayoutManager(layoutManager);
        mDelegateAdapter = new DelegateAdapter(layoutManager) {
            /**
             * 有些情况下， 这个方法会导致崩溃，这里做容错处理
             * @param index
             * @return
             */
            @Override
            public Adapter findAdapterByIndex(int index) {
                try {
                    return super.findAdapterByIndex(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        setAdapter(wrapAdapter(mDelegateAdapter));
    }

    protected Adapter wrapAdapter(Adapter adapter) {
        return adapter;
    }
}
