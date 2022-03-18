package com.medlinker.lib.imagepicker.adapter;


import androidx.recyclerview.widget.RecyclerView;


import com.medlinker.lib.imagepicker.entity.ImageEntity;
import com.medlinker.lib.imagepicker.entity.PhotoDirectory;
import com.medlinker.lib.imagepicker.event.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/17 16:37
 */
public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements Selectable {

    private List<ImageEntity> selectedPhotos;
    protected List<PhotoDirectory> photoDirectories;
    private int currentDirectoryIndex = 0;

    public SelectableAdapter() {
        selectedPhotos = new ArrayList<>();
        photoDirectories = new ArrayList<>();
    }

    /**
     * Indicates if the item at postion positon is selected
     *
     * @param photo Photo of the item to check
     * @return true if the item is selected,false otherwise
     */
    @Override
    public boolean isSelected(ImageEntity photo) {
        return getSelectedPhotos().contains(photo);
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param photo Photos of the item to toggle the selection status for
     */
    @Override
    public void toggleSelection(ImageEntity photo) {
        if (selectedPhotos.contains(photo)) {
            selectedPhotos.remove(photo);
        } else {
            selectedPhotos.add(photo);
        }

    }

    /**
     * Clear the selection status for all items
     */
    @Override
    public void clearSelection() {
        selectedPhotos.clear();
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    @Override
    public int getSelectedItemCount() {
        return selectedPhotos.size();
    }

    /**
     * Indicates the list of selected photos
     *
     * @return List of selected photos
     */
    @Override
    public List<ImageEntity> getSelectedPhotos() {
        return selectedPhotos;
    }

    public void setCurrentDirectoryIndex(int currentDirectoryIndex) {
        this.currentDirectoryIndex = currentDirectoryIndex;
    }

    public List<ImageEntity> getCurrentPhotos() {
        if (currentDirectoryIndex >= photoDirectories.size()) {
            return new ArrayList<>();
        }
        return photoDirectories.get(currentDirectoryIndex).getPhotos();
    }

    public int getCurrentDirectoryIndex() {
        return currentDirectoryIndex;
    }
}
