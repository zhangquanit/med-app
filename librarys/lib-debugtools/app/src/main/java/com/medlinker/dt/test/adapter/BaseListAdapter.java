package com.medlinker.dt.test.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 17/2/14
 */
public abstract class BaseListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected List<T> mDataList = new ArrayList<>();
    protected IItemOnClickListener mIItemChildOnClickAction;


    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDataList(List<T> dataList) {
        mDataList.clear();
        if (null != dataList){
            mDataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    public void appendData(List<T> data) {
        if (null == data || mDataList == null){
            return;
        }
        int oldPosition = mDataList.size();
        mDataList.addAll(data);
        notifyItemRangeChanged(oldPosition, mDataList.size() - 1);
    }

    public List<T> getDataList() {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        return mDataList;
    }

    public boolean isEmpty() {
        return (mDataList == null || mDataList.isEmpty());
    }

    public void add(T object) {
        this.add(this.mDataList.size(), object);
    }

    public void add(int location, T object) {
        if (location < 0 || location > this.mDataList.size()) {
            return;
        }
        if (object != null) {
            this.mDataList.add(location, object);
            this.notifyDataSetChanged();
        }
    }

    public void add(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            this.mDataList.addAll(dataList);
            this.notifyDataSetChanged();
        }
    }

    public void remove(T object) {
        if (object != null) {
            this.mDataList.remove(object);
            this.notifyDataSetChanged();
        }
    }

    public void remove(int location) {
        if (mDataList == null) return;
        if (location < 0 || location >= this.mDataList.size()) {
            return;
        }
        this.mDataList.remove(location);
        this.notifyDataSetChanged();
    }

    public void replace(List<T> dataList) {
        if (dataList != null) {
            this.mDataList = dataList;
            this.notifyDataSetChanged();
        }
    }

    public void clear() {
        if(mDataList!=null&&mDataList.size()>0){
            this.mDataList.clear();
            this.notifyDataSetChanged();
        }
    }

	public boolean hasData() {
        return mDataList!=null && mDataList.size() > 0;
	}

	public interface OnItemClickListener<T> {

        void onItemClick(int position, T item, View view);
    }

    public void setItemChildOnClickListener(IItemOnClickListener onClickListener) {
        mIItemChildOnClickAction = onClickListener;
    }
}
