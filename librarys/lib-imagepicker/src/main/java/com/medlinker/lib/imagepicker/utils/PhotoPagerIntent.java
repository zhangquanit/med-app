package com.medlinker.lib.imagepicker.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.medlinker.lib.imagepicker.PhotoPagerActivity;
import com.medlinker.lib.imagepicker.entity.ImageEntity;


import java.util.ArrayList;


/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述 大图浏览操作
 * @time 2015/10/17 14:02
 */
public class PhotoPagerIntent extends Intent {

    public PhotoPagerIntent(Context packageContext) {
        super(packageContext, PhotoPagerActivity.class);
    }

    public void putPhotoExtra(int position, ArrayList<ImageEntity> photoPaths, View view) {
//        int[] screenLocation = new int[2];
//        view.getLocationOnScreen(screenLocation);
        this.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
        this.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, photoPaths);
//        this.putExtra(PhotoPagerActivity.EXTRA_SCREENLOCATION, screenLocation);
//        this.putExtra(PhotoPagerActivity.EXTRA_VIEW_WIDTH, view.getWidth());
//        this.putExtra(PhotoPagerActivity.EXTRA_VIEW_HEIGHT, view.getHeight());
    }

    public void putPhotoExtra(int position, ArrayList<ImageEntity> photoPaths) {
        putPhotoExtra(position, photoPaths, null);
    }

}
