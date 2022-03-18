# AbTest SDK
1.在APP初始化时进行init

```
 ABTest.init(context,//applicagtionConext
             appId,//appId在AB管理后台申请
             isOnline//是否是生产环境
 );
 //0.7.0版本增加指定用户类型
 ABTest.init(context,//applicagtionConext
              appId,//appId在AB管理后台申请
              isOnline,//是否是生产环境
              userType//默认医生，1 医生 2 患者 3 经纪人
  );
```
2.app登录成功后设置session name，医联app中的sessName
```
 ABTest.setVerifyCode(sessionName);
```
3.获取ABTest的结果，实验id是在ABTest管理后台创建实验后得到
参数expIds为List<String>
```
  ABTest.fetchABTest(expIds, new ResultCallback() {
             @Override
             public void onSuccess(List<ABTestModel> aBTestResultList) {

             }

             @Override
             public void onError(String errMsg) {

             }
         });
```
4.获取一个实验ABTest的结果
参数expId为String
```
  ABTest.fetchABTest(expId, new SingleResultCallback() {
             @Override
             public void onSuccess(ABTestModel aBTestResult) {

             }

             @Override
             public void onError(String errMsg) {

             }
         });
```
5.从缓存中获取一个实验结果
```
ABTestModel model = ABTest.fetchCacheABTest(String expId);
```
6.先从缓存中获取ABTest,再从网络获取，参数netWorkFetchIfCacheExist可以设置如果缓存中获取成功不再请求组网络
```
ABTestModel model = ABTest.fetchABTestWithCacheFirst(String expId, boolean netWorkFetchIfCacheExist, final SingleResultCallback callback)
```
7.先从缓存中获取ABTest结果,再从网络获取,如果缓存中可以获取到，不进行网络请求
```
ABTestModel model = ABTest.fetchABTestWithCacheFirst(String expId, final SingleResultCallback callback)
```
8.清除ABTest本地缓存
```
ABTest.clearABTestCache();
```
9.获取ABTest的结果，实验id是在ABTest管理后台创建实验后得到
0.7.0版本增加
参数expIds为List<String>,isAnonymousId是否是匿名用户
```
  ABTest.fetchABTest(expIds, isAnonymousId, new ResultCallback() {
             @Override
             public void onSuccess(List<ABTestModel> aBTestResultList) {

             }

             @Override
             public void onError(String errMsg) {

             }
         });
```
10.匿名用户获取一个实验ABTest的结果
0.7.0版本增加
参数expId为String,isAnonymousId是否是匿名用户
```
  ABTest.fetchABTest(expId, isAnonymousId, new SingleResultCallback() {
             @Override
             public void onSuccess(ABTestModel aBTestResult) {

             }

             @Override
             public void onError(String errMsg) {

             }
         });
```
11.
修改代码后，需要升级SDK版本VERSION_NAME
上传maven，执行gradle>LibAbtest模块>publishing>publishDebugPublicationToMavenRepository命令

