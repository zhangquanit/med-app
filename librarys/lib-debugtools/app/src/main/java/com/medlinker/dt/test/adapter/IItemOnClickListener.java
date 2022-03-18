package com.medlinker.dt.test.adapter;


import android.view.View;

public interface IItemOnClickListener<T> {
	void onItemChildViewClick(View v, int position, T data);
	void onItemChildViewClick(int position, T data);
	void onItemClick(View v,int position, T data);
}
