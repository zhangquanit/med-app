**1、引用依赖：**

在项目的.gradle添加
```
allprojects {
    repositories {
    	......
        maven { url "https://nexus.doctorwork.com/nexus/repository/public/" }
    }
}
```
在model的.gradle添加
```
dependencies {
    ......
    implementation 'com.doctorwork.android.sdk:logreport:0.0.16-SNAPSHOT'
}
```
**2、初始化：**

在自定义的 `Application`中
```
LogReport.init(getApplicationContext(), "appid", "channel","环境（开发、生产、自定义）")
        .build();
```
如果需要上报到其他地址
```
LogReport.init(getApplicationContext(), "appid", "channel", LogReport.URL_CUSTOM)
        .setCustomUrl("自定义上报地址")
        .build();
```
如果只需要修改域名
```
LogReport.init(getApplicationContext(), "appid", "channel", LogReport.URL_CUSTOM)
        .setBaseUrl("域名")
        .build();
```
**3、设置注册手机号：**

考虑到可能存在未登录使用的情况，手机号设置方法单独提出
默认为空，建议登录成功后调用设置，并在切换账号、退出登录时设置为“”或null，会记录最后一次设置的值
```
LogReport.getInstance().setPhoneNumber("187.....");
```
**4、上传策略设置：**

暂不支持设置，使用默认配置，调用立即上传!!
```
LogReport.init(getApplicationContext(),"appid", "channel")
        .setFileLimit(3)//文件大小限制，单位M
        .setFilePath("xxx.txt")//本地文件名称
        .setReportNow(false)//是否立即上传
        .setRetryCount(3)//重试次数
        .enableTraceId(falase)//网络性能上报是否开启上报traceId功能,默认true
        .setTraceIdHeaderName("x-qexr-trace-id")//设置traceId的headerName
        .setReportNetWorkPerformance(false)//设置是否上报网络性能数据,默认true上报
        .setNetworkElapsedTimeExceed(1500)//单位毫秒，默认1500ms,网络请求耗时超过1.5s时上报，设置为0时所用网络请求性能数据都上报
        .build();
```
**5、错误上报：**

5.1、网络请求错误（响应码非200）：
```
LogReport.getInstance().reportHttpError("url","请求参数","请求方式","响应码",<描述>,<发生页面>);
``` 
5.2、业务请求错误（网络请求成功，但业务失败）：
```
LogReport.getInstance().reportBusinessError("url","请求参数","请求方式","业务状态码",<描述>,<发生页面>);
``` 
5.3、图片加载失败：
```
LogReport.getInstance().reportImageError("url",描述,<发生页面>);
``` 
5.4、自定义错误:
```
LogReport.getInstance().reportCustomsError("描述",<发生页面>);
```
5.5、冷启动，热启动耗时上报
```java
/**
     * 上报冷启动，热启动事件数据
     *
     * @param isHot    是否是热启动
     * @param page     页面
     * @param duration 耗时，单位ms
     */
LogReport.getInstance().reportStartUpEvent(boolean isHot, String page, int duration)
```
5.6、网络性能上报
```java
/**
     * 上报网络性能数据
     *
     * @param url              http请求url
     * @param requestParameter http url参数
     * @param requestMethod    http请求方法
     * @param responseCode     http response code
     * @param responseSize     http response 大小，单位字节
     * @param responseTraceId  http response header里携带的traceId
     * @param duration         耗时，单位ms
     */
    LogReport.getInstance().reportHttpPerformanceEvent(String url, String requestParameter,
                                           String requestMethod, int responseCode,
                                           int responseSize, String responseTraceId, int duration)
```

**6、自定义埋点：**
```
LogReport.getInstance().reportPointLog("埋点key",new Object());
```
