package com.medlinker.widget.dialog.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Des
 *
 * @author gengyunxiao
 * @time 2021/4/21
 */
class MLViewHolder extends RecyclerView.ViewHolder {

    protected int mViewType;

    public MLViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public MLViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        this.mViewType = viewType;

    }

    public <T extends View> T getView(int id) {
        return this.itemView.findViewById(id);
    }

    public void setVisible(int id, int visibility) {
        View view = getView(id);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }
}
