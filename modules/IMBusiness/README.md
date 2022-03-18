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
[im-base README](../LibIMBase/README.md) <br/>
其中ModuleMsgService，在该Module中的实现类为：ModuleMsgServiceImpl，部分方法需要修改

#### 2、初始化
```
IMGlobalManager.INSTANCE.loadImConfigInfo(); //在application中、登录成功后执行
......
ModuleIMBusinessManager.INSTANCE.setApplication(application)
        .setDebug(isDebug)
        .setBusinessService(new ModuleIMBusinessService() {
            //方法省略
        });
```

#### 3、聊天页面
ChatSessionFragment：聊天UI及主要逻辑<br/>
ChatSessionActivity: 聊天页面，根据实际项目修改

#### 4、路由
MsgGroupChatRouter：群聊<br/>
MsgChatRouter：单聊

#### 5、获取历史会话
用于第一次进入APP时，拉去会话，显示IM会话列表
```
IMGlobalManager.INSTANCE.getHistorySession(mHistorySessionLiveData);
```

#### 6、IM JSON消息类型
MsgJsonConstants

#### 7、IM相关事件
消息发生成功：IMEventMsg.MSG_SEND_STATUS_SUCCESS<br/>
Realm变化：IMEventMsg.REALM_UNREAD_COUNT_CHANGED (一般用于消息红点刷新时机)
