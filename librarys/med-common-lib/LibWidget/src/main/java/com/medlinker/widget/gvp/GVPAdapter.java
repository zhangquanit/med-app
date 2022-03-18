package com.medlinker.widget.gvp;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author hmy
 * @time 2020/5/19 17:49
 */
public abstract class GVPAdapter<T> {

    private List<T> mDataList;
    private GridViewPager mViewPager;
    private RecyclerView.ItemDecoration mItemDecoration;
    private OnItemClickListener<T> mOnItemClickListener;
    private OnItemLongClickListener<T> mOnItemLongClickListener;

    public GVPAdapter(List<T> dataList) {
        this.mDataList = dataList;
    }

    public void setDataList(List<T> dataList) {
        this.mDataList = dataList;
    }

    public void bindGridViewPager(GridViewPager gvp) {
        mViewPager = gvp;
    }

    public abstract int getItemLayoutId();

    public int getCount() {
        return null == mDataList ? 0 : mDataList.size();
    }

    public final T getData(int position) {
        return mDataList.get(position);
    }

    public final void bindItem(final View item, final int position, final T data) {
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemClick(item, position, data);
                }
            }
        });

        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != mOnItemLongClickListener) {
                    return mOnItemLongClickListener.onItemLongClick(item, position, data);
                }
                return false;
            }
        });

        bindItemView(item, position, data);
    }

    public abstract void bindItemView(View itemView, int position, T data);

    public void setItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mItemDecoration = itemDecoration;
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        return mItemDecoration;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        mOnItemLongClickListener = listener;
    }

    public void notifyDataSetChanged() {
        if (null != mViewPager) {
            mViewPager.notifyDataSetChanged();
        }
    }
}
