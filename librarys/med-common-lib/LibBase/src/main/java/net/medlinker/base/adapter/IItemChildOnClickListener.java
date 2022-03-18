package net.medlinker.base.adapter;


import android.view.View;

/**
 * @author: pengdaosong.
 * CreateTime:  2019/1/9 11:29 AM
 * Email：pengdaosong@medlinker.com. Description:
 */
public interface IItemChildOnClickListener<T> {
	void onItemChildViewClick(View v, int position, T data);
	void onItemChildViewClick(int position, T data);
}
