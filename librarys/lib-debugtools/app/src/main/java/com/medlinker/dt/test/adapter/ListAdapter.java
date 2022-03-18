package com.medlinker.dt.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.medlinker.debugtools.vlayout.viewhold.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListAdapter<T> extends BaseListAdapter<T> {

    private int mLayoutId;

    public ListAdapter(Context context) {
        this(context, new ArrayList<T>(), 0);
    }

    public ListAdapter(Context context, int layoutId) {
        this(context, new ArrayList<T>(), layoutId);
    }

    public ListAdapter(Context context, T[] arrays, int layoutId) {
        this(context, Arrays.asList(arrays), layoutId);
    }

    public ListAdapter(Context context, List<T> dataList, int layoutId) {
        this.mContext = context;
        this.mDataList = dataList;
        this.mLayoutId = layoutId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(createItemView(parent, viewType));
    }

    public View createItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
    }

    public ListAdapter getAdapter() {
        return this;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        T object = null;
        if (mDataList != null && !mDataList.isEmpty()) {
            object = mDataList.get(position);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIItemChildOnClickAction != null) {
                    T object = null;
                    if (mDataList != null && !mDataList.isEmpty()) {
                        object = mDataList.get(position);
                    }
                    mIItemChildOnClickAction.onItemClick(v, position, object);
                }
            }
        });
        convert(new ViewHolder(holder.itemView), position, object);
    }

    public void convert(ViewHolder baseViewHolder, int position, T itemData) {
    }

}
