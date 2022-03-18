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
import com.medlinker.lib.imagepicker.event.OnPhotoClickListener;
import com.medlinker.lib.imagepicker.utils.MediaStoreHelper;
import com.medlinker.lib.utils.MedDeviceUtil;

import java.util.List;

/**
 * @Description: 裁剪头像选择适配器
 * @author: lichaofeng
 * @since: V3.3
 * @copyright © Medlinker
 * @Date: 2016/6/6 15:37
 */
public class ClipPhotoGridAdapter extends SelectableAdapter<ClipPhotoGridAdapter.PhotoViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    public final static int ITEM_TYPE_CAMERA = 100;//相机
    public final static int ITEM_TYPE_PHOTO = 101;//照片

    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;

    public ClipPhotoGridAdapter(Context mContext, List<PhotoDirectory> photoDirectories) {
        this.photoDirectories = photoDirectories;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.pick_album_item_photo, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
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
            GlideUtil.load(holder.ivPhoto.getContext(), photo.getFilePath())
                    .apply(new RequestOptions()
                            .error(R.mipmap.pick_ic_broken_image_black)
                            .placeholder(R.mipmap.pick_ic_photo_black)
                            .override(MedDeviceUtil.getScreenWidth() / 3, MedDeviceUtil.getScreenWidth() / 3)
                    )
                    .into(holder.ivPhoto);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPhotoClickListener != null) {
                        onPhotoClickListener.onClick(view, position, showCamera());
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
        private ImageView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
            vSelected.setVisibility(View.GONE);
        }
    }

    public boolean showCamera() {
        return getCurrentDirectoryIndex() == MediaStoreHelper.INDEX_ALL_PHOTOS;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }


    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }
}
