### 功能介绍
支持分享平台
- 微博
- 微信好友
- 微信朋友圈
- QQ
- QQ空间
- 短信

### 使用方法
1. 仓库配置, 分享库使用UMeng maven依赖方式.需要在工程配置maven仓库地址
```
//新浪微博
maven { url "https://dl.bintray.com/thelasterstar/maven/" }
//uMeng
maven { url  'https://repo1.maven.org/maven2/'}
```
2. 初始化
```
//初始化,必须在调用分享前进行初始化

//必须先调用该方法才能调用后续初始化方法
ShareSdkHelper.initUM(applicationContext,  "你的umengAppkey", "渠道")
//是否打印日志true or false
            .showLog(true)
            .initWeibo("你的微博appid", "你的微博appkey")
            .initWx("你的微信appid", "你的微信appkey")
            .initQQ("你的qq appid", "你的qq appkey")


```
3. 获取分享的平台进行分享
```
//支持的平台
//object ShareSdkHelper {
//   
//        
//        //微信好友
//        @JvmField
//        val PLATFORM_WECHAT = "wechat"
//        //微信朋友圈
//        @JvmField
//        val PLATFORM_WECHAT_CIRCLE = "wechat_circle"
//        //qq好友
//        @JvmField
//        val PLATFORM_QQ = "qq"
//        //qq空间
//        @JvmField
//        val PLATFORM_QQ_ZONE = "qq_zone"
//        //新浪微博
//        @JvmField
//        val PLATFORM_SINA = "sina"
//        //短信
//        @JvmField
//        val PLATFORM_SMS = "sms"
//
//    
//}
SharePlatform mSharePlatform = ShareSdkHelper.getPlatform(ShareSdkHelper.PLATFORM_WECHAT);
//设置分享回调
mSharePlatform.setListener(new ShareListener() {
        @Override
        public void onStart(String platform) {
			//分享开始
        }

        @Override
        public void onComplete(String platform) {
            //分享完成
        }

        @Override
        public void onError(String platform, Throwable t) {
            //分享错误
        }

        @Override
        public void onCancel(String platform) {
            //取消分享
        }
    });

//开始分享, 如分享文本
mSharePlatform.shareText(activity, “分享的文本”);

```
4. 各个平台支持的分享方式
- 微信好友

  纯文本、本地图片、网络图片、url、音乐、视频、emoji
- 微信朋友圈

  纯文本、本地图片、网络图片、url、音乐、视频
- 微博

  纯文本、文本和图片、本地图片、网络图片、url
- QQ

  本地图片、网络图片、url、音乐、视频
- QQ空间

  纯文本、本地图片、网络图片、url、音乐、视频
- 短信

  纯文本、文本和图片、本地图片、网络图片

5. 登陆授权

目前只有qq、微信、微博支持登陆授权
例如使用微信登陆
```
//登陆授权
mSharePlatform.auth(activity, new AuthListener(){
                            @Override
                            public void onCancel(@Nullable String platformName, int code) {
								//取消授权
                            }

                            @Override
                            public void onError(@Nullable String platformName, int code, @Nullable Throwable throwable) {
                                //授权错误
                            }

                            @Override
                            public void onComplete(@Nullable String platformName, int code, @Nullable Map<String, String> info) {
                                
                                //授权成功,返回用户信息
                            }

                            @Override
                            public void onStart(@Nullable String platformName) {
								//授权开始
                            }
                        });

//取消授权
mSharePlatform.deleteAuth(activity, new AuthListener(){
                            @Override
                            public void onCancel(@Nullable String platformName, int code) {
								//取消操作
                            }

                            @Override
                            public void onError(@Nullable String platformName, int code, @Nullable Throwable throwable) {
                                //取消授权错误
                            }

                            @Override
                            public void onComplete(@Nullable String platformName, int code, @Nullable Map<String, String> info) {
                                
                                //取消授权成功
                            }

                            @Override
                            public void onStart(@Nullable String platformName) {
								//取消授权开始
                            }
                        });

```

6. 判断平台是否有效
```
boolean isValid = mSharePlatform.isClientValid(activity)
```

### 注意事项
1. 在应用的app module实现微信回调activity
```
public class WXEntryActivity extends WXCallbackActivity {
}
```
2. 在调用分享的activity onActivityResult中调用回调
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ShareSdkHelper.onActivityResult(this, requestCode, resultCode, data);
}
```
3. 短信分享需要在app中加权限
```
<uses-permission android:name="android.permission.SEND_SMS"/>
```
4. qqappid配置
```
//app build.gradle defaultConfig节点配置
manifestPlaceholders = [
                qqappid: "tencent你的qqappid"
        ]
```