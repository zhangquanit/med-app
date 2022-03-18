package com.medlinker.lib.imagepicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.cn.glidelib.GlideUtil;
import com.medlinker.lib.imagepicker.R;
import com.medlinker.lib.imagepicker.entity.PhotoDirectory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/23 16:36
 */
public class PopupDirectoryListAdapter extends BaseAdapter {

    private Context context;

    private List<PhotoDirectory> directories = new ArrayList<>();

    private LayoutInflater mLayoutInflater;


    public PopupDirectoryListAdapter(Context context, List<PhotoDirectory> directories) {
        this.context = context;
        this.directories = directories;

        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return directories.size();
    }


    @Override
    public PhotoDirectory getItem(int position) {
        return directories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return directories.get(position).hashCode();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.pick_album_item_directory, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bindData(directories.get(position));

        return convertView;
    }


    private class ViewHolder {

        public ImageView ivCover;
        public TextView tvName;
        public TextView tvCount;

        public ViewHolder(View rootView) {
            ivCover = rootView.findViewById(R.id.iv_dir_cover);
            tvName = rootView.findViewById(R.id.tv_dir_name);
            tvCount = rootView.findViewById(R.id.tv_dir_count);
        }

        public void bindData(PhotoDirectory directory) {
            if (context instanceof Activity && ((Activity) context).isFinishing()) {
                return;
            }
//            ivCover.setImageUrl(directory.getCoverPath());
            GlideUtil.load(ivCover.getContext(), directory.getCoverPath())
                    .into(ivCover);
            tvName.setText(directory.getName());
            tvCount.setText(context.getString(R.string.pick_image_count, directory.getPhotos().size()));


        }
    }

}
