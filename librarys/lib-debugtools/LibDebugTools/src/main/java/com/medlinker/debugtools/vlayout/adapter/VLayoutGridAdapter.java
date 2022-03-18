package com.medlinker.debugtools.vlayout.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter.Adapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.medlinker.debugtools.vlayout.viewhold.ViewHolder;

import java.util.List;

/**
 * @author: pengdaosong.
 * @CreateTime: 2019/1/9 3:36 PM
 * @Email：pengdaosong@medlinker.com.
 * @Description:
 */
public abstract class VLayoutGridAdapter<T> extends Adapter {

	public List<T> mDataList;
	private GridLayoutHelper mGridLayoutHelper;
	private int mGap;

	public void setDataList(List<T> dataList) {
		mDataList = dataList;
		notifyDataSetChanged();
	}

	public void setSpanCount(int spanCount) {
		if (null != mGridLayoutHelper) {
			mGridLayoutHelper.setSpanCount(spanCount);
		}
	}

	@Override
	public LayoutHelper onCreateLayoutHelper() {
		mGridLayoutHelper = new GridLayoutHelper(3);
		mGridLayoutHelper.setAutoExpand(false);
		mGridLayoutHelper.setGap(mGap);
		return mGridLayoutHelper;
	}

	public int getGap() {
		return mGap;
	}

	public abstract View createItemView(ViewGroup parent, int viewType);

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(createItemView(parent, viewType));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		final ViewHolder viewHolder = (ViewHolder) holder;
		T data = mDataList.get(position);
		convert(viewHolder, data, position);

	}

	public abstract void convert(ViewHolder viewHolder, T data, int position);

	@Override
	public int getItemCount() {
		return null == mDataList ? 0 : mDataList.size();
	}
}
