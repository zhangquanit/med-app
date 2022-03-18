package com.cn.glidelib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cn.glidelib.shapetransform.GlideCircleBorderTransform;
import com.cn.glidelib.shapetransform.GlideCircleTransform;
import com.medlinker.lib.utils.MedAppInfo;
import com.medlinker.lib.utils.MedDeviceUtil;

/**
 * Author: KindyFung.
 * CreateTime:  2017/2/9 11:42
 * Email：fangjing@medlinker.com.
 * Description: Glide图片处理工具类
 */
public class GlideUtil {

    private static volatile RequestOptions mRoundedRequestOptions;

    /**
     * 加载网络图片，默认不指定尺寸。
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestBuilder<Drawable> load(Context context, String url) {
        return load(context, url, ImageSize.UNSPECIFIED);
    }

    public static RequestBuilder<Drawable> load(Fragment fragment, String url) {
        return load(fragment, url, ImageSize.UNSPECIFIED);
    }

    public static RequestBuilder<Drawable> load(Fragment fragment, String url, @NonNull ImageSize size) {
        if (!ImageUtil.hasChecked(url)) {
            int[] params = parseSize(fragment.getContext(), url, size);
            url = checkUrl(url, params[0], params[1]);
        }
        return initRequest(Glide.with(fragment).load(url));
    }

    /**
     * @param context
     * @param url
     * @param size    加载图片尺寸。
     * @return
     */
    public static RequestBuilder<Drawable> load(Context context, String url, @NonNull ImageSize size) {
        if (!ImageUtil.hasChecked(url)) {
            int[] params = parseSize(context, url, size);
            url = checkUrl(url, params[0], params[1]);
        }
        return initRequest(Glide.with(context).load(url));
    }

    public static RequestBuilder<Drawable> load(Context context, Uri uri) {
        return initRequest(Glide.with(context).load(uri));
    }

    public static RequestBuilder<Drawable> load(Fragment fragment, Uri uri) {
        return initRequest(Glide.with(fragment).load(uri));
    }

    public static RequestBuilder<Drawable> load(Context context, int resId) {
        return initRequest(Glide.with(context).load(resId));
    }

    public static RequestBuilder<Drawable> load(Fragment fragment, int resId) {
        return initRequest(Glide.with(fragment).load(resId));
    }

    /**
     * 加载图片尺寸
     */
    public enum ImageSize {

        SMALL(30),
        MIDDLE(80),
        BIG(210),
        MAX(290),
        /**
         * 未指定，就加载屏幕宽高。
         */
        UNSPECIFIED(0),
        /**
         * 原尺寸。
         */
        ORIGINAL(-1);

        int value;

        ImageSize(int dimen) {
            this.value = dimen;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 拼接七牛和upyun图片地址缩放参数。
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static String checkUrl(String url, int width, int height) {
        return ImageUtil.checkUrl(url, width, height);
    }

    /**
     * 基于原图大小，按指定百分比缩放。Scale取值范围1-999。
     */
    public static String checkUrl(String url, int scale) {
        return ImageUtil.checkUrl(url, scale);
    }

    public static String checkUrl(Context context, String url, @NonNull ImageSize imageSize) {
        if (!ImageUtil.hasChecked(url)) {
            int[] params = parseSize(context, url, imageSize);
            return checkUrl(url, params[0], params[1]);
        } else {
            return url;
        }
    }

    /**
     * 初始化共有配置
     *
     * @param request
     * @param <T>
     * @return
     */
    @Deprecated
    private static <T> RequestBuilder<T> initRequest(RequestBuilder<T> request) {
        return request;
//        return request.apply(getBaseRequestOptions());
    }


    /**
     * 带圆角处理的Options
     *
     * @return
     */
    public static RequestOptions getRoundedCornersOptions(int roundingRadius) {
        if (roundingRadius <= 0) {
            return new RequestOptions();
        }
        return new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(roundingRadius));
    }

    private static int[] parseSize(Context context, String url, ImageSize size) {
        //默认0，不缩放。
        int width = 0;
        int height = 0;
        switch (size) {
            case UNSPECIFIED://加载屏幕宽高
                width = MedDeviceUtil.getScreenWidth();
                height = MedDeviceUtil.getScreenHeight();
                break;

            case SMALL:
            case MIDDLE:
            case BIG:
            case MAX:
                width = height = dip2px(context, size.getValue());
                break;
            default:
                break;
        }
        return new int[]{width, height};
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 设置图片
     *
     * @param internetUrl     图片地址
     * @param targetImageView
     */
    @Deprecated
    public static void setUrlImageView(ImageView targetImageView, String internetUrl) {
        if (null==targetImageView)
            return;

        load(targetImageView.getContext(), internetUrl)
                .into(targetImageView);
//        Glide.with(targetImageView.getContext())
//                .load(internetUrl)
//                .placeholder(R.drawable.ic_default_icon_details)
//                .error(R.drawable.ic_default_icon_details)
//                .centerCrop()
//                .into(targetImageView);
    }

    /**
     * 设置图片
     *
     * @param imgResId
     * @param targetImageView
     */
    @Deprecated
    public static void setUrlImageView(ImageView targetImageView, int imgResId) {
        if (null==targetImageView)
            return;

        Glide.with(targetImageView).load(imgResId)
//                .apply(getBaseRequestOptions())
                .into(targetImageView);
    }


    /**
     * 设置图片
     *
     * @param imgResId
     * @param targetImageView
     */
    @Deprecated
    public static void setUrlImageView(ImageView targetImageView, String imgResId, int placeHolderResId) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView).load(imgResId)
                .apply(new RequestOptions().placeholder(placeHolderResId))
                .into(targetImageView);
    }

    /**
     * 设置图片
     */
    @Deprecated
    public static void setUrlImageView(ImageView targetImageView, String imgResId, int placeHolderResId, int errorResId) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView).load(imgResId)
                .apply(new RequestOptions().placeholder(placeHolderResId).error(errorResId))
                .into(targetImageView);
    }

    /**
     * 设置固定大小的图片
     *
     * @param internetUrl
     * @param width
     * @param height
     * @param targetImageView
     */
    @Deprecated
    public static void setUrlImageViewWithHW(ImageView targetImageView, String internetUrl, int width, int height) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView.getContext())
                .load(ImageUtil.checkUrl(internetUrl, width, height))
                .apply(new RequestOptions().override(width, height))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片
     *
     * @param internetUrl
     * @param targetImageView
     */
    @Deprecated
    public static void setCircleImageView(ImageView targetImageView, String internetUrl) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView.getContext())
                .load(internetUrl)
                .apply(new RequestOptions().transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片
     *
     * @param internetResid
     * @param targetImageView
     */
    @Deprecated
    public static void setCircleImageView(ImageView targetImageView, int internetResid) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView.getContext())
                .load(internetResid)
                .apply(new RequestOptions().transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片 带边框
     *
     * @param targetImageView
     * @param internetUrl
     * @param placeHolderResId
     * @param borderColor
     * @param borderWidth      单位dp
     */
    @Deprecated
    public static void setCircleImageViewWithBorder(ImageView targetImageView, String internetUrl, int placeHolderResId, int borderWidth, int borderColor) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView).load(internetUrl)
                .apply(new RequestOptions().transform(new GlideCircleBorderTransform(targetImageView.getContext(), borderWidth, borderColor)))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片
     *
     * @param targetImageView
     * @param bytes
     * @param placeHolderResId
     */
    @Deprecated
    public static void setCircleImageView(ImageView targetImageView, byte[] bytes, int placeHolderResId) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView)
                .load(bytes)
                .apply(new RequestOptions().transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }

    /**
     * 设置圆形的图片 带边框
     *
     * @param targetImageView
     * @param bytes
     * @param placeHolderResId
     * @param borderColor
     * @param borderWidth      单位dp
     */
    @Deprecated
    public static void setCircleImageViewWithBorder(ImageView targetImageView, byte[] bytes, int placeHolderResId, int borderWidth, int borderColor) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView.getContext())
                .load(bytes)
                .apply(new RequestOptions().placeholder(placeHolderResId).transform(new GlideCircleBorderTransform(targetImageView.getContext(), borderWidth, borderColor)))
                .into(targetImageView);
    }

    /**
     * 设置指定圆角的图片
     *
     * @param internetUrl
     * @param targetImageView
     */
    @Deprecated
    public static void setRoundImageView(ImageView targetImageView, String internetUrl, int cornerRadios) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView.getContext())
                .load(internetUrl)
                .apply(getRoundedCornersOptions(dip2px(targetImageView.getContext(), cornerRadios)))
                .into(targetImageView);
    }


    /**
     * 设置头部图片(圆形)
     *
     * @param headerUrl        头像地址
     * @param placeHolderResId 头像展位图
     * @param targetImageView  目标控件
     */
    @Deprecated
    public static void setCircleImageView(ImageView targetImageView, String headerUrl, int placeHolderResId) {
        if (null==targetImageView)
            return;
        Glide.with(targetImageView.getContext())
                .load(headerUrl)
                .apply(new RequestOptions().placeholder(placeHolderResId).transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }

    /**
     * 设置头部图片(圆形)
     *
     * @param headerUrl        头像地址
     * @param placeHolderResId 头像展位图
     * @param targetImageView  目标控件
     */
    @Deprecated
    public static void setHeaderCircleImageView(ImageView targetImageView, String headerUrl, int placeHolderResId) {
        if (null==targetImageView)
            return;
        int w = dip2px(targetImageView.getContext(), ImageSize.SMALL.getValue());
        Glide.with(targetImageView.getContext())
                .load(checkUrl(headerUrl, w, w))
                .apply(new RequestOptions().transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }

    /**
     * 设置头部图片(圆形)
     *
     * @param headerUrl       头像地址
     * @param targetImageView 目标控件
     */
    @Deprecated
    public static void setHeaderCircleImageView(ImageView targetImageView, String headerUrl) {
        if (null==targetImageView)
            return;
        setHeaderImageViewFade0(targetImageView, headerUrl);
    }

    /**
     * 设置头部图片(圆形)
     *
     * @param headerUrl       头像地址
     * @param targetImageView 目标控件
     */
    @Deprecated
    public static void setHeaderImageViewFade0(ImageView targetImageView, String headerUrl) {
        if (null==targetImageView)
            return;
        int w = dip2px(targetImageView.getContext(), ImageSize.SMALL.getValue());
        Glide.with(targetImageView.getContext())
                .load(ImageUtil.checkUrl(headerUrl, w, w))
                .apply(new RequestOptions().transform(new GlideCircleTransform(targetImageView.getContext())))
                .into(targetImageView);
    }
}

