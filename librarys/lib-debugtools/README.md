### 简介
DebugTools调试链工具致力于提高开发，测试效率，节约人力，时间成本。DebugTools调试链工具实现与主工程完全解耦；开发，测试阶段通过脚本自动接入，但发布到线上自动去除，对主工程代码达到零污染。同时经过对[DoKit](http://xingyun.xiaojukeji.com/docs/dokit/#/intro)进行二次开发，加入适用于公司特有功能，已经对插件二次修改，实现更加灵活的配置。

### 版本
- 1.0.0

  实现泳道，路由跳转，抓包功能
  
- 1.0.1

  泳道加入url端口匹配和替换

### 接入指南
- 添加公司maven私服地址

```
maven {
    url 'http://nexus.medlinker.com/repository/local-android/' //公司maven私服，外网需要配vpn
}
```

-  添加依赖

```
implementation ('com.medlinker:debugtools:1.0.6')
```
- 配置插件

```
classpath 'com.medlinker:dokitx-plugin:1.0.0'
```
在主工程应用插件（可以根据自己需求选择是否使用插件）

```
apply plugin: 'com.med.dokit'
```
说明：不使用插件部分功能可能使用不了

- 开关配置

   这里主要说的是扩展的开关字段（gradle.properties中配置）

   1. DOKIT_RELEASE_PLUGIN_SWITCH：RELEASE版本dokit插件是否可用

### 业务模块
#### https抓包支持
Android 27以上版本不在信任用户证书，所以需要通过xml方式配置应用信任用户证书，以便可以对https进行抓包。

1. 在主app目录AndroidManifest.xml添加
```
android:networkSecurityConfig="@xml/network_security_config"
```
2. 选择一个其它的module库，在res/xml目录创建network_security_config.xml文件，如果主工程已经存在，可以直接移动到其它module中。

##### <font color=#0000FF size=3 >说明：</font>
  DebugTools工具链库在res/xml下包含了network_security_config.xml文件，里面配置了信任用户证书，当工程集成了DebugTools，将会对除主工程的其它module中的network_security_config.xml配置进行优先级选择。

##### <font color=#FF0000 size=3 >遇到的问题</font>

电脑端抓包软件不能抓起https，以Charles为例，排除问题如下：

1. 检查Charles代理是否打开，配置和手机端输入的是否一致。
2. 在手机端安装charles提供的证书，不明白的，请度娘。
3. mac安装charles根证书，并且在钥匙串 -> 登陆 -> 找到Charles证书，选择始终信任。
4. 检查Charles SSL设置是否正确，Proxy -> SSL Proxying Settings ->  SSL Proxying -> Enable SSL Proxying，然后add，Host，port都输入 *，当然也可以根据自己需求设置。

#### 泳道切换
##### 配置
```
//变量是 HashMap,其中key随意，value为需要配置的工程使用的host，用于请求泳道列表数据
private static Map<String,String> DEFAULT_QA_DOMAINS;

//变量是 HashMap,其中key为qa环境host，value需要替换的线上host，用于切换到线上
private static Map<String,String> DEFAULT_ONLINE_DOMAINS;

DTConfig.instance()
        .initLaneConfig(new DTLaneConfig()
                .authorization(AUTHORIZATION)
                .qADomains(DEFAULT_QA_DOMAINS)
                .onlineDomains(DEFAULT_ONLINE_DOMAINS)
                .callBack(new CallBack<DTLaneStorage.LaneData>() {
                    @Override
                    public void value(DTLaneStorage.LaneData laneData) {
                        doLaneConfigSuccess(laneData);
                    }
                }))

private static void initQaDomains(){
    if (null == DEFAULT_QA_DOMAINS){
        DEFAULT_QA_DOMAINS = new HashMap<>();
    }
    DEFAULT_QA_DOMAINS.put("app-qa", "app-qa.medlinker.com");
}

private static void initOnlineDomains(){
    if (null == DEFAULT_ONLINE_DOMAINS){
        DEFAULT_ONLINE_DOMAINS = new HashMap<>();
    }
    DEFAULT_ONLINE_DOMAINS.put("app-qa.medlinker.com", "@app.medlinker.com");
}
```
##### <font color=#0000FF size=3 >说明：</font>
线上host加@表示需要将http换成https
##### <font color=#0000FF size=3 >原理：</font>
DebugTools调试链工具会通过插件，对okhttp添加网络拦截器，在请求之前对域名进行替换。所以要使用该功能，需要使用上面提供的插件。

替换原理：不管是从网络获取到的需要替换的域名，还是本地设置的，在切换的时候，会以qa环境的host作为key，然后从泳道数据匹配对应需要替换的host，如果找到就替换，否则不替换。

##### <font color=#0000FF size=3 >备注：</font>
泳道切换是对okhttp添加网络拦截器方式实现，所以对于工程由其它实现方式的，需要做特色处理，下面给出三种方案：

1. 新建两个module，一个提供方法调用，但是实现为空，另一个按照DebugTools集成编写完整代码，根据项目需求，集成不同的moudle。好处，简单，但是有点点的代码污染。
2. 新建一个module用于DebugTools初始化，通过反射调用初始化代码，好处工程解藕，但是对代码有一定污染，并且要注意混淆问题。
3. 使用[droidassist](https://github.com/didi/DroidAssist)进行字节码操作,集成DebugTools时候在关键节点插入代码。具体可以参考“医联app”和工程提供的例子。

#### 应用内抓包
通过对okhttp添加网络拦截器方式实现，对网络数据进行解析。目前支持抓取okhttp实现的网络请求。

#### 路由跳转
先通过[Arouter](https://github.com/alibaba/ARouter)跳转，如果跳转失败，会把控制权交给开发者。

关于Arouter参数调整逻辑如下：

```
for(String key : keys){
    String value = uri.getQueryParameter(key);
    if (TextUtils.isEmpty(value)){
        continue;
    }
    if (value.startsWith("I") && value.endsWith("I")){
        String d = value.substring(1,value.length() - 1);
        postcard.withInt(key,Integer.parseInt(d));
    }else if (value.startsWith("L") && value.endsWith("L")){
        String d = value.substring(1,value.length() - 1);
        postcard.withLong(key,Integer.parseInt(d));
    }else {
        postcard.withString(key,value);
    }
}
```

#### <font color=#FF0000 size=3 >问题：</font>

- 应用弹框变形，可以拖动。

  请点开DebugTools菜单，滑到底部，把悬浮窗模式选成系统并打开权限。

- 打包编译不过，报数组越界。

  把gpsSwitch（地图经纬度开关）改成false，如果需要开启该功能，请不混淆百度定位sdk。

#### <font color=#0000FF size=3 >拓展：</font>
为了和项目完全解藕，可以考虑使用[droidassist](https://github.com/didi/DroidAssist)进行字节码操作，指定需要插桩的类，减少编译时间。
