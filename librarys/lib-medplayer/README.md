
**播放器组件**
## 使用方法
#### 1、引用依赖

在项目的.gradle下添加
```
allprojects {
    repositories {
        ......
        maven {
            url 'http://nexus.medlinker.com/repository/group-android/'
            credentials {
                username = 'android-developer'
                password = 'developer123'
            }
        }
    }
}
```
在module的.gradle中添加

```
dependencies {
    ......
    implementation 'com.medlinker.player:medplayer:1.0.1'
}
```

#### 2、使用
```
/**
 * 点播视频播放页
 * <pre>
 *    ARouter.getInstance().build("/videoplayer/vod")
 *                 .withString("title", title)
 *                 .withString("url", url)
 *                 .withString("cover", cover)
 *                 .withBoolean("autoPlay", !"0".equals(autoPlay))
 *                 .withString("extra", extra)
 *                 .withString("components", componentsArray.toString())
 *                 .navigation();
 * </pre>
 * <p>
 * 参数说明:
 * url:视频链接  必传
 * title：视频标题
 * cover：视频封面
 * autoPlay 是否自动播放  1自动播放(默认)
 * extra：扩展参数：{seekTo:指定播放进度,单位秒, dragDisable:禁止拖动进度条，1禁止, autoClose:播放完毕自动关闭页面,1关闭， looping:是否循环播放，1循环}
 * components:扩展组件 继承MedPlayerComponent
 * </p
 *
 * @author zhangquan
 */
@Route(path = "/videoplayer/vod")
public class MedVideoPlayerActivity extends AppCompatActivity 
```
调用实例
```
        String url = "http://pub-voice-video.v.medlinker.net/app_improve_image_brand_hd.mp4";
        String title = "视频标题";
        String extra = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("seekTo", 10);
            jsonObject.put("autoClose", 1);
            extra = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //自定义扩展组件
        JSONArray componentsArray = new JSONArray();
        componentsArray.put(ExtraComponent.class.getName());

        ARouter.getInstance().build("/videoplayer/vod")
                .withString("title", title)
                .withString("url", url)
                .withBoolean("autoPlay", true)
                .withString("extra", extra)
                .withString("components", componentsArray.toString())
                .navigation();

```
```
       public class ExtraComponent extends SimpleMedPlayerComponent {
            @Override
            public void onInited(FragmentActivity activity, MedPlayerView playerView, MedVideoInfo videoInfo) {
            }
      }
```