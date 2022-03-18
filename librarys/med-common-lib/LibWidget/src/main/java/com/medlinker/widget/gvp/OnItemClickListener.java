package com.medlinker.widget.gvp;

import android.view.View;

/**
 * @author hmy
 * @time 2020/5/19 17:51
 */
public interface OnItemClickListener<T> {

    void onItemClick(View view, int position, T data);
}
