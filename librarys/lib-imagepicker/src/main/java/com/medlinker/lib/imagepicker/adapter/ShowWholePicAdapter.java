package com.medlinker.lib.imagepicker.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.medlinker.lib.imagepicker.entity.ImageEntity;
import com.medlinker.lib.imagepicker.widget.WholePicView;


import java.util.ArrayList;
import java.util.List;


/**
 * Author: KindyFung.
 * CreateTime:  2016/5/7 11:55
 * Email：fangjing@medlinker.com.
 * Description:
 */

public class ShowWholePicAdapter extends PagerAdapter {
    private List<ImageEntity> mFiles;
    private SparseArray<WholePicView> mViews;
    public static boolean isShowSmallPic = true;

    public ShowWholePicAdapter(List<ImageEntity> paths) {
        this.mFiles = paths;
        if (null == paths)
            mFiles = new ArrayList<>();
        mViews = new SparseArray<>(mFiles.size());
    }

    /**
     * 获取缓存的View列表
     *
     * @return
     */
    public WholePicView getCurrentView(int position) {
        return mViews.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ImageEntity item = mFiles.get(position);

        WholePicView view = mViews.get(position);
        if (view == null) {
            view = new WholePicView(container.getContext());
            mViews.put(position, view);
        }
        view.setWholePic(item);
        view.setPlaceHolder(item);


        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
    }

    @Override
    public int getCount() {
        return mFiles.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
        mViews.delete(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public void getView() {
        return;
    }


}
