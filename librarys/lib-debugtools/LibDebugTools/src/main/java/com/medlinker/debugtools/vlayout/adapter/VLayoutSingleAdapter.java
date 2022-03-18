package com.medlinker.debugtools.vlayout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter.Adapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.medlinker.debugtools.vlayout.viewhold.ViewHolder;

/**
 * @Author: pengdaosong.
 * @CreateTime: 2018/12/2 1:50 PM
 * @Email：pengdaosong@medlinker.com.
 * @Description:
 */
public class VLayoutSingleAdapter<T> extends Adapter {

  private int mLayoutId;
  private T mData;

  public VLayoutSingleAdapter() {
  }

  public VLayoutSingleAdapter(@LayoutRes int id) {
    this.mLayoutId = id;
  }

  public VLayoutSingleAdapter(@LayoutRes int id, T data) {
    this.mLayoutId = id;
    mData = data;
  }

  public T getData() {
    return mData;
  }

  public void setData(T data) {
    mData = data;
    notifyDataSetChanged();
  }

  @Override
  public LayoutHelper onCreateLayoutHelper() {
    return new SingleLayoutHelper();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView;
    if (mLayoutId <= 0) {
      itemView = getItemView(parent, viewType);
    } else {
      itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
    }
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    onBindView((ViewHolder) holder, position, mData);
  }

  public void onBindView(ViewHolder holder, int position, T data) {

  }

  public View getItemView(ViewGroup parent, int viewType) {
    return null;
  }

  @Override
  public int getItemCount() {
    return 1;
  }
}
