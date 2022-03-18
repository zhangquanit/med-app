**文件下载组件**
## 使用方法
#### 1、引用依赖

在项目的.gradle下添加
```
allprojects {
    repositories {
        ......
        maven {
            url 'http://nexus.medlinker.com/repository/group-android/'
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
    implementation 'com.medlinker.download:filedownloader:1.0.0'
}
```

#### 2、使用
单文件下载
```
      //1、创建task
       val task = MedFileDownloadTask.Builder(url, parentFile)
//                .setHttpCall(HttpCall.OKHTTP) 默认使用OkHttp下载
//                .useCache(true)  默认为true 即断点下载
//                .retryCount(3)   默认重试3次
            .build()


       //2、执行task
        task?.start(object : FileDownloadCallback {
            override fun onDownloadProgress(progress: Int, offset: Long, total: Long) {
                

            override fun onDownloadFail() {
               
            }

            override fun onDownloadSuccess() {
              
            }

        })

      //3.取消
     task?.cancel()

      //其他
     task?.isCompleted() //是否下载完成
     task?.getInfo() //下载文件信息
```
2、多文件同时下载
```
      //1、创建task
      val task=MedFileBatchDownloadTask.Builder()
         .withTask(task1)
         .withTask(task2)
         .build()

       //2、执行task
        task?.start(object : FileDownloadCallback {
            override fun onDownloadProgress(progress: Int, offset: Long, total: Long) {
                //progress :当前进度  已下载文件数/总文件数
                //offset:  已下载文件数
                //total：总下载文件数
            override fun onDownloadFail() {
               
            }

            override fun onDownloadSuccess() {
              
            }

        })

      //3.取消
     task?.cancel()
  
```