package com.medlinker.dt.test.adapter;

import android.view.View;

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-27 19:34
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class SimpleItemOnClickListener<T> implements IItemOnClickListener<T> {
    @Override
    public void onItemChildViewClick(View v, int position, T data) {

    }

    @Override
    public void onItemChildViewClick(int position, T data) {

    }

    @Override
    public void onItemClick(View v, int position, T data) {

    }
}
