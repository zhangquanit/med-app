package com.medlinker.lib.imagepicker.utils;

import android.content.Context;
import android.content.Intent;

import com.medlinker.lib.imagepicker.PhotoPickerActivity;


/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/17 14:02
 */
public class PhotoPickerIntent extends Intent{

    public PhotoPickerIntent(Context packageContext) {
        super(packageContext, PhotoPickerActivity.class);
    }

    public void setPhotoCount(int photoCount) {
        this.putExtra(PhotoPickerActivity.EXTRA_MAX_COUNT, photoCount);
    }

}
