package com.medlinker.lib.imagepicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.request.RequestOptions;
import com.cn.glidelib.GlideUtil;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import com.medlinker.lib.imagepicker.PhotoPagerActivity;
import com.medlinker.lib.imagepicker.R;
import com.medlinker.lib.imagepicker.dialog.RichBottomDialog;
import com.medlinker.lib.imagepicker.entity.ImageEntity;
import com.medlinker.lib.imagepicker.utils.ViewRecycler;
import com.medlinker.lib.utils.MedDeviceUtil;


import java.io.File;
import java.util.List;




/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/24 14:00
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private List<ImageEntity> mPaths;
    private Context mContext;
    private RichBottomDialog richBottomDialog;
    private ViewRecycler<PhotoView> mViews;

    public PhotoPagerAdapter(Context mContext, List<ImageEntity> paths) {
        this.mContext = mContext;
        this.mPaths = paths;
        if (null == paths || paths.size() <= 0) return;
        mViews = new ViewRecycler<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final String url = mPaths.get(position).getFilePath();
        final String path = GlideUtil.checkUrl(url, MedDeviceUtil.getScreenWidth(), MedDeviceUtil.getScreenHeight());
        Uri uri;
        if (TextUtils.isEmpty(path)) {
            uri = Uri.parse("");
        } else {
            uri = path.startsWith("http") ? Uri.parse(path) : Uri.fromFile(new File(path));
        }
        PhotoView photoView = mViews.getOneView();
        if (photoView == null) {
            photoView = new PhotoView(container.getContext());
        }
        GlideUtil.load(photoView.getContext(), uri)
                .apply(new RequestOptions().placeholder(R.mipmap.pick_ic_photo_black).error(R.mipmap.pick_ic_broken_image_black))
                .into(photoView);
        photoView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mContext instanceof PhotoPagerActivity) {
                    ((PhotoPagerActivity) mContext).showNavigationbar();
                }
            }
        });
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                return false;
            }
        });
        try {
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoView;
    }

    @Override
    public int getCount() {
        return mPaths.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        PhotoView view = (PhotoView) object;
        container.removeView(view);
        mViews.cacheView(view);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
