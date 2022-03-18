package com.medlinker.widget.gvp;

import android.view.View;

/**
 * @author hmy
 * @time 2020/5/19 17:52
 */
public interface OnItemLongClickListener<T> {

    boolean onItemLongClick(View view, int position, T data);
}