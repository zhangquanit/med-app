package com.cn.glidelib.glide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.cn.glidelib.R;
import com.medlinker.lib.utils.MedAppInfo;

import java.io.InputStream;

/**
 * Generated API 目前仅可以在 Application 模块内使用。
 * 这一限制可以让我们仅持有一份 Generated API，而不是各个 Library 和 Application 中均有自己定义出来的 Generated API。
 * Created by jiantao on 2017/11/2.
 */

@GlideModule
public class MedlinkerGlideModule extends AppGlideModule {
    private static RequestOptions DEFAULT_OPTION = new RequestOptions()
            .set(GifOptions.DECODE_FORMAT, DecodeFormat.DEFAULT)
            .placeholder(R.drawable.ic_default_icon_details)
            .error(R.drawable.ic_default_icon_details);

    private static RequestOptions sGlobalOptions = DEFAULT_OPTION;

    /**
     * 设置glide全局配置
     * @param options
     */
    public static void setGlobalOptions(@NonNull RequestOptions options) {
        sGlobalOptions = options;
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setDefaultRequestOptions(sGlobalOptions);
        builder.setLogLevel(MedAppInfo.isDebug() ? Log.DEBUG : Log.ERROR);
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        //使用okhttp加载网络图片
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
}
