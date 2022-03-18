**基于医联IM的业务Module**
该Module实现了IM聊天页面，实际项目直接Copy改造

## 使用方法
#### 0、配置
在项目的build.gradle中
```
classpath "io.realm:realm-gradle-plugin:5.1.0"
classpath 'com.jakewharton:butterknife-gradle-plugin:10.2.1'
```
#### 1、医联im-base集成
[im-base README](../LibIMBase/README.md)

#### 2、初始化
```
ModuleIMManager.INSTANCE
        .setRealmService(new ModuleRealmService() {
            //方法省略
        })
        .setIMService(new ModuleIMService() {
            //方法省略
        })
        .setMsgService(new ModuleMsgService() { //在该Module中的实现类为：ModuleMsgServiceImpl，部分方法需要修改
            //方法省略
        });
ModuleIMBusinessManager.INSTANCE.setApplication(application)
        .setDebug(isDebug)
        .setBusinessService(new ModuleIMBusinessService() {
            //方法省略
        });

RealmHelper.init(context);
RealmHelper.updateConfig(); //切换账号、退出登录时，也需要重新调用
```

#### 3、IM相关API
启动页加载IM
```
IMGlobalManager.INSTANCE.loadImConfigInfo(); //一般在启动页、登录成功后执行
```
登录成功后
```
RealmHelper.updateConfig(); 
IMGlobalManager.INSTANCE.loadImConfigInfo();
```
退出登录或显示离线弹窗后
```
ImServiceHelper.getInstance(context).disConnect() //退出登录后执行
```

#### 4、聊天页面
ChatSessionFragment：聊天UI及主要逻辑<br/>
ChatSessionActivity: 聊天页面，根据实际项目修改

#### 5、路由
MsgGroupChatRouter：群聊<br/>
MsgChatRouter：单聊

#### 6、获取历史会话
用于第一次进入APP时，拉去会话，显示IM会话列表
```
IMGlobalManager.INSTANCE.getHistorySession(mHistorySessionLiveData);
```

#### 7、IM JSON消息类型
MsgJsonConstants

#### 8、IM相关事件
消息发生成功：IMEventMsg.MSG_SEND_STATUS_SUCCESS<br/>
Realm变化：IMEventMsg.REALM_UNREAD_COUNT_CHANGED (一般用于消息红点刷新时机)
