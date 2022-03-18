# 模板项目介绍
## 从med-app克隆项目，删除.git 或者修改.git/config文件中的remote地址为自己项目的git地址

## 替换资源
### 1、config.gradle中替换appName、applicationId、clientName以及accounts相关账号
```bash
appName：应用名称
applicationId：应用包名
appIcon：应用图标
clientName：客户端名称，一般接口调用ua或者hybrid协议ua用到  一般由@王厚伟给出
  
```
### 2、替换app模块的资源
#### res/color/button_bg_blue_selector 统一弹框确认按钮 替换BaseApp模块colors.xml中的color_main主题色
#### res/drawable/med_player_seekbar 播放组件拖动进度条
#### res/drawable-xhdpi/  push&push_small  推送通知图标
#### res/drawable-xxhdpi 启动界面切图
#### mipmap目录下的icon_app app图标
#### mipmap-xxxhdpi dialog_icon_tip_privacy 隐私权限弹框顶部图标 update_ic_dialog_top 升级弹框顶部图标

### 3、替换ApiPath中的隐私和用户协议url

##相关SDK初始化
### 检查BaseApp中相关SDK初始化

 
## 开发说明
```bash
1、UI&屏幕适配
dp和sp尽量使用dimens.xml中定义的dp_xx和sp_xx
UI切图使用mipmap-xxxhdpi ,使用tinypng压缩后才放入项目中 
2、没有Context参数时，可使用Utils.getApp()或者MedAppInfo.getAppContext()获取。
   比如：MedRouterHelper.withUrl(path).queryTarget().navigation(Utils.getApp());
3、Toast使用MedToastUtil
4、dp转px
SizeUtils.dp2px()
或MedDimenUtil.dip2px()

```
### 1.LibBase
```bash
BaseActivity
      --BaseCompatActivity 封装标题栏、LoadingView
      --BaseDialogActivity
BaseFragment
```

### 网络
[med-retrofit](https://git.medlinker.com/android/med-network-lib)
### 数据存储
1、键值对
KVUtil：基于腾讯MMKV封装(强烈建议) <br/>
2、数据库 <br/> 
room数据库 <br/>
### 日志打印
LibLog库<br/>
参考：com.medlinker.lib.log.LogUtil
### 图片加载
LibGlide库
### 工具库
工具类建议优先使用blankj和libUtil<br/>
[AndroidUtilCode使用文档](https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md)
### 路由
[Med-Router](https://git.medlinker.com/android/lib-medrouter) <br/>
支持Arouter+自研MedRouterMapping <br/>
使用RouterUtil统一跳转。
### UI组件库
[LibWidget](https://git.medlinker.com/android/LibWidget) <br/>
UI组件优先使用LibWidget封装的。<br/>
项目中特殊业务用到的UI组件，请勿放到LibWidget中。

### 事件通知
基于EventBus <br/>
参考：net.medlinker.base.event.EventBusUtils

### Apm&Crash
异常捕获：bugtags、bugly<br/>
统计：growingIO


## APP打包
1.在android目录下执行：
> QA环境：python3 x_scripts/releaseApk.py <br/>
> 生产环境：python3 x_scripts/releaseApk.py -e online <br/>
> DEV环境：python3 x_scripts/releaseApk.py -e dev

添加打包描述在命令后加上： -m 描述内容 <br/>
**脚本执行完成后，自动上传APK到蒲公英，并自动发送邮件。**

查看python脚本帮助：
> python3 x_scripts/releaseApk.py -h

2.正式包发布
>加固：android目录下执行  python3 x_scripts/releaseApk.py jiagu ，完成后执行下一步 <br/>
>生成渠道包：python x_scripts/ApkResigner.py <br/>
>发渠道包：将x_scripts/makeChannels/channels目录下的apk文件发送给发包人员

    ```
