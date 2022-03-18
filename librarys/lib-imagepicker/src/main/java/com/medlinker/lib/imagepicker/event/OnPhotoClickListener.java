package com.medlinker.lib.imagepicker.event;

import android.view.View;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/22 11:42
 */
public interface OnPhotoClickListener {

    void onClick(View view, int position, boolean showCamera);

}
