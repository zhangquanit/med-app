package com.medlinker.lib.imagepicker.event;


import com.medlinker.lib.imagepicker.entity.ImageEntity;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/17 13:53
 */
public interface OnItemCheckListener {

    /**
     *
     * @param position 所选图片的位置
     * @param path 所选的图片
     * @param isCheck 当前状态
     * @param selectedItemCount 已选数量
     * @return enable check
     */
    boolean OnItemCheck(int position, ImageEntity path, boolean isCheck, int selectedItemCount);

}


