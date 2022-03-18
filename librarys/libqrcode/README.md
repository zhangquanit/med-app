### 功能介绍
扫描二维码并返回结果, 整个识别过程都封装在ScanView中,并通过回调返回结果.
使用ScanView的Activity需要自己申请相机权限

目前版本: com.medlinker:LibQrCode:1.1.0-SNAPSHOT
使用示例: https://git.medlinker.com/android/libsdev

### 使用方法
1. 布局文件中添加ScanView
```
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/transparent"
    android:orientation="vertical">

    <com.medlinker.lib.qrcode.view.ScanView
        android:id="@+id/capture_preview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
<!-- 添加其他view, 比如扫描框动画, 提示文本等 -->
</RelativeLayout>
```

2. 在activity中获取扫描结果
```
ScanView scanView = findViewById(R.id.capture_preview)
//设置扫描成功是否播放声音,默认播放
scanView.playBeep(true)
//在onResume中启动扫描
scanView.startScan(new OnDecodeListener{
	 /**
     * 返回扫描结果
     * @param result 结果字符串
     * @param qrBmp 被识别的图片，jpeg格式
     */
    void onDecodeResult(String result, byte[] qrBmp){
	}

    /**
     * 获取屏幕上识别的矩形框，表示在整个预览图片上的识别范围
     * @return
     */
    Rect getCropRect(){
		//返回裁剪矩形框
	}
})

//onPause中停止扫描
scanView.stopScan()
```

3. 通过ScanImageUtils工具类可以直接识别图片中二维码并返回结果