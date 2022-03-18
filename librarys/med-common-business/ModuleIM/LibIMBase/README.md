**医联自研IM Base库**
基于socket、protobuf、realm开发

## 使用方法
#### 1、引用依赖

在项目的.gradle下添加
[VPN配置](https://wiki.medlinker.com/pages/viewpage.action?pageId=37862400)
```
allprojects {
    repositories {
        ......
        maven {
            url 'http://nexus.medlinker.com/repository/group-android/' //公司maven私服，外网需要配vpn
        }
    }
}
```
在module的.gradle中添加
```
dependencies {
    ......
    implementation 'com.medlinker.im:im-base:0.0.8'
}
```

#### 2、配置
在IM业务Module的build.gradle中设置应用包名
```
android {
    defaultConfig {
        resValue("string", "applicationId", "\"${rootProject.ext.android.applicationId}\"")
    }
}
```

在IM业务Module的AndroidManifest.xml中配置权限、服务等
```
    <!-- im 消息权限声明 -->
    <permission
        android:name="android.intent.permission.im.${applicationId}_receiver_permission"
        android:protectionLevel="signature" />
    <uses-permission android:name="android.intent.permission.im.${applicationId}_receiver_permission" />
    <!-- im 消息service权限声明 -->
    <permission
        android:name="${applicationId}.permission.ImService"
        android:protectionLevel="signature" />
    <uses-permission android:name="${applicationId}.permission.ImService" />

    <application>
        <!-- 医联im start -->
        <service
            android:name="net.medlinker.im.im.core.ImService"
            android:exported="true"
            android:permission="${applicationId}.permission.ImService"
            android:process=":imremote" />
        <service
            android:name="net.medlinker.im.im.core.ImService$GrayInnerService"
            android:enabled="true"
            android:exported="false"
            android:process=":imremote" />

        <receiver
            android:name="net.medlinker.im.im.MedImReceiver"
            android:exported="true"
            android:permission="android.intent.permission.im.${applicationId}_receiver_permission">
            <intent-filter>
                <action android:name="android.intent.action.im.receiver_action.${applicationId}" />

                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </receiver>
        <!-- 医联im end -->
    </application>
```

#### 3、其他
消息常量：MsgConstants

#### 4、maven私库发布
在gradle.properties中修改版本号。
上传maven，执行gradle>ModuleIM>LibIMBase>Tasks>publishing>publishDebugPublicationToMavenRepository