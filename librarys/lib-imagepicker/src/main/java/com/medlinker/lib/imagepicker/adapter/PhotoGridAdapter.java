package com.medlinker.lib.imagepicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.cn.glidelib.GlideUtil;
import com.medlinker.lib.imagepicker.R;
import com.medlinker.lib.imagepicker.entity.ImageEntity;
import com.medlinker.lib.imagepicker.entity.PhotoDirectory;
import com.medlinker.lib.imagepicker.event.OnItemCheckListener;
import com.medlinker.lib.imagepicker.event.OnPhotoClickListener;
import com.medlinker.lib.imagepicker.utils.MediaStoreHelper;


import java.util.List;


/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/17 16:36
 */
public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

    private LayoutInflater inflater;
    private Context mContext;
    public final static int ITEM_TYPE_CAMERA = 100;//相机
    public final static int ITEM_TYPE_PHOTO = 101;//照片


    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;

    public PhotoGridAdapter(Context mContext, List<PhotoDirectory> photoDirectories) {
        this.photoDirectories = photoDirectories;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = inflater.inflate(R.layout.pick_album_item_photo, viewGroup, false);
        PhotoViewHolder holder = new PhotoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.vSelected.setVisibility(View.GONE);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onClick(view);
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, final int position) {
        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {
            List<ImageEntity> photos = getCurrentPhotos();
            final ImageEntity photo;
            if (showCamera()) {
                photo = photos.get(position - 1);
            } else {
                photo = photos.get(position);
            }
//            holder.ivPhoto.loadLoacalFile(photo.getFileUrl(), width, width);
            GlideUtil.load(holder.ivPhoto.getContext(), photo.getFilePath())
                    .apply(new RequestOptions().placeholder(R.mipmap.pick_ic_photo_black).error(R.mipmap.pick_ic_broken_image_black))
                    .into(holder.ivPhoto);
            final boolean isChecked = isSelected(photo);
            holder.vSelected.setSelected(isChecked);
            holder.vSelected.setTag(position);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPhotoClickListener != null) {
                        onPhotoClickListener.onClick(view, position, showCamera());
                    }
                }
            });
            holder.vSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isEnable = true;
                    if (onItemCheckListener != null) {
                        isEnable = onItemCheckListener.OnItemCheck(position, photo, isSelected(photo),
                                getSelectedPhotos().size());
                    }
                    if (isEnable) {
                        toggleSelection(photo);
                        View vSelected = view.findViewWithTag(position);
                        vSelected.setSelected(isSelected(photo));
                    }
                }
            });

        } else {
            holder.ivPhoto.setImageResource(R.mipmap.pick_ic_camera_album);
            holder.ivPhoto.setBackgroundColor(mContext.getResources().getColor(R.color.font_color_dedede));
        }
    }


    @Override
    public int getItemViewType(int position) {
        return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
    }

    @Override
    public int getItemCount() {
        int photosCount = photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
        if (showCamera()) {
            return photosCount + 1;
        }
        return photosCount;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);

        }
    }


    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }


    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }


    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }


    public boolean showCamera() {
        return getCurrentDirectoryIndex() == MediaStoreHelper.INDEX_ALL_PHOTOS;
    }

}
