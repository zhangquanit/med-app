# 七牛文件上传SDK
1.在APP初始化时进行init

```
MedFileUpload.init(isDebug());
```

2.上传文件

```
 RxMedFileUpload.startUpload(new UploadConfig())；
 //startUpload返回Observable<ArrayList<FileEntity>> ，可以直接使用RxJava进行链式编程
```
```
//UploadConfig可配置项
new UploadConfig()
                .setUploadBucket(Constants.UPLOAD_MEDIA)//医联划分的文件桶
                .setIsPublic(false)//是否公开
                .setIsWaterMark(true)//是否开启水印
                .setWaterText("Medlinker")//水印的文字
                .setFileEntity(fileEntity)//文件上传实体
```


