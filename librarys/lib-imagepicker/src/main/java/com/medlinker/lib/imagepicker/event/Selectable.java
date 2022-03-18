package com.medlinker.lib.imagepicker.event;



import com.medlinker.lib.imagepicker.entity.ImageEntity;

import java.util.List;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/17 16:30
 */
public interface Selectable {
    /**
     * Indicates if the item at postion positon is selected
     *
     * @param photo Photo of the item to check
     * @return true if the item is selected,false otherwise
     */
    boolean isSelected(ImageEntity photo);


    /**
     * Toggle the selection status of the item at a given position
     *
     * @param photo Photos of the item to toggle the selection status for
     */
    void toggleSelection(ImageEntity photo);

    /**
     * Clear the selection status for all items
     */
    void clearSelection();

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    int getSelectedItemCount();

    /**
     * Indicates the list of selected photos
     *
     * @return List of selected photos
     */
    List<ImageEntity> getSelectedPhotos();


}
