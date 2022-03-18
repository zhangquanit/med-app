# lib-hybrid

**自研hybrid库**
[Hybrid协议API](https://medhybrid.medlinker.com/api)

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
    implementation 'com.medlinker.bridge:bridge:0.0.24'
    implementation 'com.medlinker.bridge:hybrid-core:0.0.24'
}
```

#### 2、初始化
在Application中初始化，参考Demo [HybridInit.kt](app/src/main/java/com/medlinker/hybridapp/HybridInit.kt)

#### 3、webView容器
容器核心类：WVHybridFragment
默认容器Activity：WVHybridWebViewActivity

#### 4、扩展插件
继承WVApiPlugin类
```
class BatteryPlugin : WVApiPlugin() {
   
    override fun supportMethodNames(): Array<String> {
        return arrayOf("getInfo")  //告诉h5 NA支持的协议
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        if ("getInfo" == methodName) {
            val jsonObject = JSONObject(params)
            val methodValue: String = jsonObject.get("onUpdate").toString()
            sendHybridStatus(methodValue, 1)

            val callbackParam = WVHybridCallbackEntity()
            callbackParam.code = 0
            callbackParam.message = "xxxxxxxx"
            callbackParam.data = "aaaaaaaa"

            sendHybridCallback(callbackContext.callbackId, callbackParam)

            sendHybridEvent("pageshow", "ui", null)
            return true
        }
        return false
    }
}
......
```

#### 5、maven私库发布
在gradle.properties中修改版本号。
上传maven，执行gradle>LibHybridMed>Tasks>publishing>publishToMedSnapshotMaven