package com.medlinker.widget.dialog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Des
 *
 * @author gengyunxiao
 * @time 2021/4/21
 */
abstract  class MLDialogAdapter<T> extends RecyclerView.Adapter<MLViewHolder> {

    protected ArrayList<T> mDataList = new ArrayList<>();
    protected Context mContext;
    private int mLayoutId;

    protected abstract void dealItem(MLViewHolder viewHolder, T t, int position);

    public MLDialogAdapter(Context context, List<T> dataList, int layoutId) {
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mDataList.addAll(dataList);
    }

    @NonNull
    @Override
    public MLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
        return new MLViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MLViewHolder holder, int position) {
        dealItem(holder, mDataList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public ArrayList<T> getDataList() {
        return mDataList;
    }

    public MLDialogAdapter<T> addData(T bean) {
        mDataList.add(bean);
        return this;
    }
}
