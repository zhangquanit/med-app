package com.medlinker.debugtools.vlayout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter.Adapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.medlinker.debugtools.vlayout.viewhold.ViewHolder;

import java.util.List;

/**
 * @author: pengdaosong. CreateTime:  2019/1/12 5:32 PM Email：pengdaosong@medlinker.com.
 * Description:
 */
public class VLayoutListAdapter<T> extends Adapter {

    private int mLayoutId;
    private List<T> mDataList;

    public VLayoutListAdapter() {
    }

    public VLayoutListAdapter(@LayoutRes int id) {
        this.mLayoutId = id;
    }

    public VLayoutListAdapter(@LayoutRes int id, List<T> data) {
        this.mLayoutId = id;
        mDataList = data;
    }

    public void setDataList(List<T> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (mLayoutId <= 0) {
            itemView = getItemView(parent, viewType);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        }
        dataBinding(itemView);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (null == mDataList || position >= mDataList.size()) {
            return;
        }
        T entity = mDataList.get(position);
        onBindView((ViewHolder) holder, position, entity);
    }

    public void onBindView(ViewHolder holder, int position, T data) {

    }

    public void dataBinding(View itemView) {

    }

    public View getItemView(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemCount() {
        return null == mDataList ? 0 : mDataList.size();
    }
}
