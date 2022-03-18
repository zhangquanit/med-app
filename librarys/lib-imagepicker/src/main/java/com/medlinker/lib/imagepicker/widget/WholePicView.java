package com.medlinker.lib.imagepicker.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cn.glidelib.glide.GlideApp;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.medlinker.lib.imagepicker.PhotoPagerActivity;
import com.medlinker.lib.imagepicker.R;
import com.medlinker.lib.imagepicker.adapter.ShowWholePicAdapter;
import com.medlinker.lib.imagepicker.dialog.RichBottomDialog;
import com.medlinker.lib.imagepicker.entity.ImageEntity;

import java.io.File;


/**
 * Author: KindyFung.
 * CreateTime:  2016/5/7 10:59
 * Email：fangjing@medlinker.com.
 * Description: 查看全图View
 */
public class WholePicView extends FrameLayout {

    // TODO: 2017/11/1 查看原图view
    private PhotoView mIvWhole;
    private MedlinkerImageView mIvHolder;
    private RichBottomDialog richBottomDialog;
    private GestureDetector gestureDetector;

    public WholePicView(Context context) {
        super(context);
        initView();
    }

    public WholePicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public WholePicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.pick_view_whole_picture, this);
        mIvHolder = findViewById(R.id.iv_placeholder);
        mIvWhole = findViewById(R.id.subsamplingScaleImageView);
        if (!ShowWholePicAdapter.isShowSmallPic) {
            mIvHolder.setVisibility(GONE);
        }
    }

    public void setPlaceHolder(ImageEntity entity) {
        String url = entity.getFilePath();
        String smallPath = entity.getSmallPath();
        if (TextUtils.isEmpty(url)) return;
        if (url.startsWith("http") && !TextUtils.isEmpty(smallPath)) {
            mIvHolder.setImageUrl(smallPath);
        } else {
            mIvHolder.setImageUrl(R.mipmap.pick_ic_photo_black);
        }
        mIvHolder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof PhotoPagerActivity) {
                    ((PhotoPagerActivity) getContext()).showNavigationbar();
                }
            }
        });
    }

    public void setWholePic(final ImageEntity entity) {
        final String url = entity.getFilePath();

        if (TextUtils.isEmpty(url)) return;
        Uri uri = url.startsWith("http") ? Uri.parse(url) : Uri.fromFile(new File(url));
        GlideApp.with(getContext())
                .load(uri)
                .placeholder(R.mipmap.pick_ic_photo_black)
                .error(R.mipmap.pick_ic_broken_image_black)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mIvHolder.setVisibility(VISIBLE);
                        mIvWhole.setVisibility(GONE);
                        mIvHolder.setImageUrl(R.mipmap.pick_ic_broken_image_black);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mIvHolder.setVisibility(GONE);
                        removeView(mIvHolder);
                        mIvHolder = null;
                        return false;
                    }
                })
                .into(mIvWhole);

        mIvWhole.setTag(R.id.subsamplingScaleImageView, url);
        mIvHolder.setVisibility(url.startsWith("http") ? GONE : VISIBLE);

        mIvWhole.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (getContext() instanceof PhotoPagerActivity) {
                    ((PhotoPagerActivity) getContext()).showNavigationbar();
                }
            }
        });
        mIvWhole.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

}
