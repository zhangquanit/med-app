# lib-medrouter

**自研医联路由库**
## 目的
#### 1、统一调用
##### 统一自研MedRouter和阿里Arouter调用方式
```
MedRouterHelper.withUrl("path").queryTarget().navigation(context)
MedRouterHelper.withUrl("path").withString("key","value").queryTarget(true).navigation(context)
MedRouterHelper.withUrl("path")
        .withParcelable("xx", data)
        .withSeriablele("xx", data)
        .queryTarget()
        .navigation(context);
```
##### url支持携带参数
```
MedRouterHelper.withUrl("/xx/xx?key1=value1&key2=value2").queryTarget().navigation(context)
```
#### 2、解决自研MedRouter无法在子Module中注册的问题
通过Arouter帮我们注册
```
@Route(path = "/group/page")
public class Model11Router extends BaseMedRouterMapping {

}
```

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
    implementation 'com.medlinker.router:medrouter:1.0.4'
}
```

#### 2、初始化
在自定义的Application中
```
val routerParam = mapOf(
    LoginRouter.ROUTER to LoginRouter::class.java
)
MedRouterHelper.init(routerParam, object : com.medlinker.router.Config() {
    override fun onLostPage(context: Context?) {
        //处理不存在的路由，如显示升级弹窗
    }

    override fun launchReactActivity(
        context: Context?, rnModuleName: String?, rnRouterName: String?,
        extra: String?, requestCode: Int
    ) {
        //启动RNActivity
    }

    override fun checkLogin(medRouter: MedRouterHelper.MedRouter?): BaseMedRouterMapping {
        //检查登录，如需跳转登录页，返回登录Router，否则返回null
        val session: String = AccountUtil.geteSesstion()
        return if (TextUtils.isEmpty(session)) {
            LoginRouter(medRouter)
        } else super.checkLogin(medRouter)
    }

})
```

#### 3、注册路由
##### 自研MedRouter 
通过ARouter帮我们注册，无需再手动注册 (记得添加@Route注解).
```
@Route(path = "/model/page2")
public class Model11Router extends BaseMedRouterMapping {
    public Model11Router(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public String getRouterKey() {
        return null;
    }

    @Override
    public Class getTargetClass() {
        return Model11Activity.class;
    }
}
```
##### Arouter
原Arouter方式
```
@Route(path = RoutePath.AROUTER_PATH1)
public class ArouterActivity extends AppCompatActivity {

}
```
#### 4、路由跳转
```
MedRouterHelper.withUrl("path").queryTarget(true).navigation(context)
MedRouterHelper.withUrl("path").withString("key","value").queryTarget(true).navigation(context)
MedRouterHelper.withUrl("rnModuleName","rnRouterName", "extra").queryTarget().navigation(context)
MedRouterHelper.withUrl("path")
        .withParcelable("xx", data)
        .withSeriablele("xx", data)
        .queryTarget()
        .navigation(context);
......
```

#### 5、maven私库发布
在gradle.properties中修改版本号。
上传maven，执行gradle>LibMedRouter>publishing>publishDebugPublicationToMavenRepository