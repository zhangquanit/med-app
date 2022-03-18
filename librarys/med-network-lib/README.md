# lib-network-retrofit

**基于Retrofit2封装的网络组件库**

## 设计目的
- 上报网络错误日志[LogReport](https://wiki.medlinker.com/pages/viewpage.action?spaceKey=frontendtech&title=Native+SDK)；
- 统一网络封装
- 统一错误处理


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
    implementation 'com.medlinker.network:retrofit:1.1.0'
}
```

#### 2、初始化
在自定义的Application中
```
        //初始化日志上报sdk，更多api见LogReport文档
        LogReport.init(this,"appid ", "channel ", LogReport.URL_CUSTOM)
                       .setBaseUrl(isDebug() ? "https://web-monitor-qa.medlinker.com/" : "https://web-monitor.medlinker.com/")//其他地址请更换
                       .build();
        //初始化网络库
        RetrofitProvider.init(new INetworkConfig() {
            @Override
            public File getNetCacheDir() {
                return FileUtil.getNetCacheDir();
            }

            @Override
            public boolean isDebug() {
                return BuildConfig.DEBUG;
            }

            @Override
            public boolean isOnlineEnv() {
                return BuildConfig.API_URL_TYPE == Constants.API_TYPE_ONLINE;
            }

            @Override
            public BusinessCodeEntity getBusinessCode(String responseJson) {
                BaseEntity entity = new Gson().fromJson(responseJson, BaseEntity.class);
                return new BusinessCodeEntity(entity.isSuccess(), entity.getCode());
            }

            @Override
            public void onResponseError(int code, String msg){
               //处理错误code，一般用于处理全局的特定code，如账号下线
            }

            @Override
            public HashMap<String, String> getQueryParam() {
                HashMap<String, String> map = new HashMap<>();
                map.put("key", "value");
                .......
                return map;//传入HttpUrl.Builder.addQueryParameter的公共参数
            }

            @Override
            public HashMap<String, String> getHeaderParam() {
                return null;//传入Request.Builder.addHeader的公共参数
            }

            @Override
            public List<Interceptor> getInterceptor() {
                List<Interceptor> interceptors = new ArrayList<>();
                ......
                //返回需要添加的拦截器，sdk中已有缓存、公共参数拦截器
                return interceptors;
            }

            ......
        });
```

设置注册手机号：考虑到可能存在未登录使用的情况，手机号设置方法单独提出 默认为空，建议登录成功后调用设置，并在切换账号、退出登录时设置为“”或null，会记录最后一次设置的值
```
    LogReport.getInstance().setPhoneNumber("187.....");
```

#### 3、Retrofit获取
```
public class ApiManager {
......
		AppApi api = RetrofitProvider.INSTANCE
                    .build(RetrofitProvider.INSTANCE.buildRetrofit("baseUrl"), AppApi.class);
......
}
```
#### 4、接口业务错误检查
```
RetrofitProvider.INSTANCE.checkResponse(errCode, errMsg);
或
RetrofitProvider.INSTANCE.throwResponseError(errCode, errMsg);

//数据返回前统一处理（其他实现方式灵活使用）
public class HttpResultFunc<T extends DataEntity> implements Function<BaseEntity<T>, T> {

    @Override
    public T apply(BaseEntity<T> httpResult) throws Exception {
        RetrofitProvider.INSTANCE.checkResponse(httpResult.getCode(), httpResult.getMsg());
        return httpResult.getData() == null ? (T) T.CREATOR.createFromParcel(Parcel.obtain())
                : httpResult.getData();
    }
}
```
如果业务失败，则抛出ApiException。在抛出异常前，预留了接口方法处理特定code：
```
RetrofitProvider.init(new INetworkConfig() {
......

    @Override
    public void onResponseError(int code, String msg) {
        //处理接口错误code，一般用于处理全局的特定code，如账号下线
    }

   @Override
    public int getBusinessSuccessCode() {
        return 0;//接口成功的code值
    }
......
}
```
#### 5、ErrorConsumer：ApiException捕获和处理
ErrorConsumer中统一处理了ApiException（错误信息Toast提示）和其他异常<br/>
Java
```
ApiManager.getAppApi().getGroupInfo(groupId)
                .map(new HttpResultFunc<GroupImInfoEntity>())
                .compose(SchedulersCompat.<GroupImInfoEntity>applyIoSchedulers())
                .subscribe(new Consumer<GroupImInfoEntity>() {
                    @Override
                    public void accept(GroupImInfoEntity entity) throws Exception {
                        //请求成功
                    }
                }, new ErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);//如不需要使用统一的方式处理错误，可屏蔽super
                    }
                });

或
                ......
                .subscribe(new Consumer<GroupImInfoEntity>() {
                    @Override
                    public void accept(GroupImInfoEntity entity) throws Exception {
                        //请求成功
                    }
                }, new Consumer() {
                    @Override
                    public void accept(Throwable throwable) {
                        if(throwable instanceof ApiException){
                            //TODO
                        }
                    }
                });
```
其他处理方式可直接调用
```
RetrofitProvider.INSTANCE.errorHandler(throwable);
```
