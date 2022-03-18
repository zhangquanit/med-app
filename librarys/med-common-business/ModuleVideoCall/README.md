# 视频问诊-组件

## 使用方法
#### 1、引用依赖

在项目的.gradle下添加
[VPN配置](https://wiki.medlinker.com/pages/viewpage.action?pageId=37862400)
```
allprojects {
    repositories {
        ......
        maven {
            url 'http://nexus.medlinker.com/repository/group-android/'  //公司maven私服，外网需要配vpn
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
    implementation 'com.medlinker:ModuleVideoCall:0.0.22'
}
```

#### 2、初始化
在开启视频问诊或Application中初始化IModuleService
```
VideoCallManager.INSTANCE.setModuleService(new IModuleService() {
            @Override
            public Notification createKeepAliveNotification(String title, String content, Intent intent, String tag, int id) {
                return NotificationUtil.createKeepAliveNotification(title, content, intent, tag, id);; // TODO 调用项目中的通知
            }
            @Override
            public void onVideoCallFinished() {
                // TODO 视频电话结束，医生端需要发事件
            }
        });
```

#### 3、启动视频问诊
```
Intent intent = new Intent(context, VideoDataLoadingActivity.class);
VideoCallIntentEntity param = new VideoCallIntentEntity();
param.setUserId("xxxx");
param.setUserType(xxx);
param.setRoomId(xxx);
intent.putExtra("DATA_KEY", param);
startActivity(intent);
```

#### 4、混淆
```
-keep class com.tencent.** { *; }
-keep class com.medlinker.video.entity.** {*;}
#rxjava
# RxJava RxAndroid
-dontwarn sun.misc.**
-keep class rx.** {*;}
-keep class io.reactivex.** {*;}
-keep class com.tbruyelle.rxpermissions2.* {
   *;
}
```

#### 5、maven私库发布
在gradle.properties中修改版本号。
上传maven，执行gradle>ModuleVideoCall>Tasks>publishing>publishDebugPublicationToMavenRepository