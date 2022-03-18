package com.medlinker.lib.imagepicker.widget;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.cn.glidelib.GlideUtil;
import com.medlinker.lib.imagepicker.R;
import com.cn.glidelib.ImageUtil;

import java.io.File;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/12/22 14:08
 */
public class MedlinkerImageView extends ImageView {

    public MedlinkerImageView(Context context) {
        super(context);
    }

    public MedlinkerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MedlinkerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 加载小图
     * 50dp左右
     *
     * @param url
     */
    public void setSmallImageUrl(String url) {
        setImageUrl(url, ImageUtil.SMALL_WIDTH, ImageUtil.SMALL_WIDTH, R.drawable.ic_default_icon_details);
    }

    /**
     * 加载中图
     * 100dp左右
     *
     * @param url
     */
    public void setMiddleImageUrl(String url) {
        setImageUrl(url, ImageUtil.MIDDLE_WIDTH, ImageUtil.MIDDLE_WIDTH, R.drawable.ic_default_icon_details);
    }

    /**
     * 加载大图
     * 240dp左右
     *
     * @param url
     */
    public void setBigImageUrl(String url) {
        GlideUtil.load(getContext(), url, GlideUtil.ImageSize.BIG)
//                .crossFade()
                .into(this);
    }

    /**
     * 加载大图++
     * 300dp左右
     *
     * @param url
     */
    public String setMaxImageUrl(String url) {
        GlideUtil.load(getContext(), url, GlideUtil.ImageSize.MAX)
//                .crossFade()
                .into(this);
        return ImageUtil.checkUrl(url, ImageUtil.MAX_WIDTH, ImageUtil.MAX_WIDTH);
    }

    /**
     * 普通图片
     *
     * @param url
     */
    public void setImageUrl(String url, int defaultResId) {
        GlideUtil.load(getContext(), fomaterURI(url))
                .apply(new RequestOptions().placeholder(defaultResId))
                .into(this);
    }

    /**
     *
     */
    public void setImageUrl(String url) {
        setImageUrl(url, R.drawable.ic_default_icon_details);
    }

    /**
     * 缩略图
     *
     * @param url
     */
    public void setImageUrl(String url, int width, int heigh) {
        setImageUrl(url, width, heigh, R.drawable.ic_default_icon_details);
    }

    /**
     *
     */
    public void setImageUrl(String url, int width, int heigh, int defaultUrl) {
        url = GlideUtil.checkUrl(url, width, heigh);
        GlideUtil.load(getContext(), url)
                .apply(new RequestOptions().placeholder(defaultUrl))
                .into(this);
    }

    /**
     * 加载资源文件
     *
     * @param resourceId
     */
    public void setImageUrl(int resourceId) {
        GlideUtil.load(getContext(), resourceId)
                .into(this);
    }




    private Uri fomaterURI(String url) {
        return url.startsWith("http://") || url.startsWith("https://") ? Uri.parse(url) : Uri.fromFile(new File(url));
    }
}
