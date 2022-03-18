# lib-hybrid

**mp3录音库**

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
    implementation 'com.medlinker.record:record-mp3:0.0.1-SNAPSHOT'
}
```

#### 2、使用
```
MP3RecorderManager.getInstance().startRecord(mCurrentFilePath);
MP3RecorderManager.getInstance().stopRecord();
```

#### 3、maven私库发布
在gradle.properties中修改版本号。
上传maven，执行gradle>lib-record-mp3>Tasks>publishing>publishToMedSnapshotMaven
