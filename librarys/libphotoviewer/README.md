# 图片浏览控件
1.在根项目的build.gradle中添加

```gradle
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

2.在app项目的build.gradle中添加

```gradle
dependencies {
    ......
    implementation 'com.medlinker:photoviewer:1.0.3'
}
```
3.图片预览
```java
PhotoViewerActivity.startPhotoViewerActivity(context, list, position);
```

4.在布局文件中使用，`app:countVisibility="invisible"`是否显示自带的数字指示器，默认为显示
```xml
<com.medlinker.photoviewer.PictureViewerView
        android:id="@+id/pvv_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:countVisibility="invisible" />
```
5.数据的初始化
字符串列表传入，initData(‘起始位置’，‘字符串列表’)
```java
ArrayList<String> list = new ArrayList<>();
for (FileEntity image : images) {
    list.add(image.getFileUrl());
}
pvv_view.initData(0, list);
```
也可以传入自定类型，需要自行实现转换接口，initData(‘起始位置’，‘列表总长度’，‘转换接口’)
```java
pvv_view.initData(0, images.size(), new PictureViewerView.OnImageDataListener() {
    @Override
    public String onInitImageUrl(int position) {
        final String image;
        //开始自定义bean处理
        CaseImageEntry entity = images.get(position);
        String markUrl = entity.tagFileUrl;
        String originUrl = entity.originPath;
        if (!TextUtils.isEmpty(markUrl)) {
            image = markUrl;
        } else {
            image = originUrl;
        }
        //最终给控件String类型的图片地址
        return image;
    }
});
```
6.图片长按底部弹框
使用默认的弹框设置，默认为【保存图片】【二维码扫码结果】【取消】
```java
pvv_view.setDefaultItems(new PictureViewerView.OnDefaultItemClickListener() {
    @Override
    public void onSaveImage(String url) {
        Toast.makeText(getApplicationContext(), "保存图片", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onScanQrcode(String scanResult) {
        Toast.makeText(getApplicationContext(), "二维码扫码结果", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_LONG).show();
    }
});    
```
也可以自定义
```java
pvv_view.setItems(new String[]{"test1", "2", "cancel"}, new PictureViewerView.OnItemsClickListener() {
    @Override
    public void onItemClick(int index, String url) {
        switch (index) {
            case 0:
                Toast.makeText(getApplicationContext(), "test1\n" + url, Toast.LENGTH_LONG).show();
                break;
            case 1:
                Toast.makeText(getApplicationContext(), "2\n" + url, Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "取消\n" + url, Toast.LENGTH_LONG).show();
                break;
        }
    }
});
```
7.图片滑动position监听
```java
pvv_view.setPageChangeListener(position -> {

});
```
