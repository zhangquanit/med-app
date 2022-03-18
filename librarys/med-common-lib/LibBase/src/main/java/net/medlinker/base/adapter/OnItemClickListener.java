package net.medlinker.base.adapter;

import android.view.View;

/**
 * @author: pengdaosong.
 * CreateTime:  2019/1/18 10:33 AM
 * Email：pengdaosong@medlinker.com. Description:
 */
public interface OnItemClickListener<T>{
	void onItemClick(int position, T item, View view);
}
