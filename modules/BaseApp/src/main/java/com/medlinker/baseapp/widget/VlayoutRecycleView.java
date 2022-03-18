package com.medlinker.baseapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/11/30
 */
public class VlayoutRecycleView extends RecyclerView {

    private DelegateAdapter mDelegateAdapter;

    public VlayoutRecycleView(Context context) {
        super(context);
        init();
    }

    public VlayoutRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VlayoutRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DelegateAdapter getDelegateAdapter() {
        return mDelegateAdapter;
    }

    private void init() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final VirtualLayoutManager layoutManager = new VirtualLayoutManager(getContext());
        setLayoutManager(layoutManager);
        mDelegateAdapter = new DelegateAdapter(layoutManager){
            @Override
            public Adapter findAdapterByIndex(int index) {
                try {
                    return super.findAdapterByIndex(index);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        setAdapter(mDelegateAdapter);
    }
}