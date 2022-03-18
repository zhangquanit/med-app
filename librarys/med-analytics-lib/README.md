# 埋点SDK
1.在APP初始化时进行init

```
 MedAnalytics.init(this, new com.medlinker.analytics.Configuration()
                .debug(isDebug())//是否是debug模式
                .channel(PackageUtil.getApplicationChannel())//渠道
                .trackType(MedAnalytics.TRACK_GROWING_IO));//当前只有GrowingIO，未来会增加我们增加的埋点上传方式
```

2.上传自定义埋点数据

```
 //eventName是GrowingIO要求的参数，我们自己的日志埋点目前不需要
 MedAnalytics.track(String eventName);
 //GrowingIO要求eventLevelVariable内部不允许含有JSONObject或者JSONArray
 MedAnalytics.track(String eventName, JSONObject eventVariables);
```
3.设置用户信息
```
MedAnalytics.getInstance().setUser(getAnalyticsUser(id,user));
```
4.清除用户信息
```
MedAnalytics.clearUser();
```
5.停止采集
```
MedAnalytics.disable();
```
6.恢复采集
```
MedAnalytics.resume();
```
7.第三方接口适配

```
// methodName是第三方接口的方法名。
// values为接口需要的参数，必须与需要调用的第三方接口参数保持一致。
// 如set("setActivityPageName",activity,"主页")
MedAnalytics.set(String methodName, Object... values);
```
8.设置动态公共属性，适配神策新增方法
```
//设置动态公共属性
registerDynamicSuperProperties(DynamicSuperProperties dynamicSuperProperties)
```

9.埋点SDK分为android和RN两个版本，上传maven时,gradle.properties设置参数RN_SDK为true即为RN版本，false为android版本，
修改代码后，需要升级SDK版本VERSION_NAME
上传maven，执行gradle>LibAnalytics模块>publishing>publishDebugPublicationToMavenRepository命令

