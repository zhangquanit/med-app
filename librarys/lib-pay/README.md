## 功能介绍
集成了微信支付和支付宝支付。

## 配置使用
### 微信支付配置：
1. 在app module的AndroidManifest中配置微信appkey
```
<meta-data android:name="WECHAT_APPID"
            android:value="你的微信appid"/>
```

2. （可选）如果需要处理微信的扩展功能(如分享)，请继承com.medlinker.lib.pay.wxapi.WXEntryActivity，重写相关方法
```
 public class CustomActivity extends WXEntryActivity {
 
 }
```

3. 调用
```
    var req = WxPayReq.Builder()
        .with(this)
        .setPayInfo("加密后的支付信息")
        .create()
        .setOnWechatPayCallback(object : WxPayer.IPayCallback {
            override fun onFailure(errCode: Int, errStr: String?) {
                toast(errStr)
            }
    
            override fun onSuccess(errCode: Int, errStr: String?) {
                toast(errStr)
            }
    
        })
    PayAPI.getInstance().sendPayRequest(req);

```
4. errCode说明
- -1：可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
- -2：用户取消
- -3：发送失败
- -4：授权被拒绝
- -5:一般是没安装微信;
- -6：被禁用
- -10：有支付任务正在执行中
-  
- 0：成功




### 支付宝支付

1. 调用
```
    var req = AliPayReq.Builder()
            .with(this)
            .setPayInfo("加密后的支付信息")
            .create()
            .setOnAliPayCallback(object : AliPayer.IPayCallBack {
                override fun onFailure(errStr: String?, resultStatus: String?) {
                    toast(errStr)
                }

                override fun onPaySuccess(
                    resultInfo: String?,
                    memo: String?,
                    resultStatus: String?
                ) {
                    toast(resultInfo)
                }

                override fun onPayIndeterminate(
                    resultInfo: String?,
                    memo: String?,
                    resultStatus: String?
                ) {
                    toast(resultInfo)
                }

            })
    PayAPI.getInstance().sendPayRequest(req)
```
2. resultStatus说明
- "9000"：支付成功

- "8000"：支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
- "-1000"：失败
- "-1100"：有支付任务正在执行中

