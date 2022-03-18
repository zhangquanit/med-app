对Glide图片加载的简单封装。

## 库的初始化
在使用Glide加载图片之前可以对库进行初始化。设置Glide全局统一参数。Glide的网络库替换成Okhttp
```
MedlinkerGlideModule.setGlobalOptions(@NonNull RequestOptions options)

如果不调用该方法，将使用库内部定义默认的options
private static RequestOptions DEFAULT_OPTION = new RequestOptions()
            .set(GifOptions.DECODE_FORMAT, DecodeFormat.DEFAULT)
            .placeholder(R.drawable.ic_default_icon_details)
            .error(R.drawable.ic_default_icon_details);
```
## 库中工具类
```
GlideCircleBorderTransform 加载圆形带边框图片
FastBlur 图片模糊工具
RSBlur 图片模糊工具
```