# lib-rn-codepush

**自研RN热更新组件库**

## 设计目的
- 封装自有RN热更新


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
    implementation 'com.medlinker.reactnative:codepush:1.1.8'
}
```

#### 2、初始化
在自定义的Application中
```
RNCodePush.init(getApplication(), isDebug(), getRNCodePushServerUrl(isDebug()), new Config() {
    @Override
    public void onInitCompleted() {
        //
    }

    @Override
    public void sendFeedback(String feedback) {
        //
    }
});

private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {

    ......
    @Override
    protected List<ReactPackage> getPackages() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        List<ReactPackage> packages = new PackageList(this).getPackages();
        packages.add(RNCodePush.getInstance());

        ......
        return packages;
    }
};
```

#### 3、使用
```
RNCodePush.getInstance().checkReload(BuildConfig.DEBUG);//检查rn热更新和重新加载
RNCodePush.getInstance().checkUpdate(context, BuildConfig.DEBUG, isAutoUpdate);//检查rn热更新
......
}
```

#### 4、maven私库发布
在gradle.properties中修改版本号。
上传maven，执行gradle>LibRNCodePush>publishing>publishDebugPublicationToMavenRepository