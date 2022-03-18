package com.didichuxing.doraemonkit.plugin.classtransformer

import com.didichuxing.doraemonkit.plugin.*
import com.didichuxing.doraemonkit.plugin.extension.CustomExt
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExt
import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
import com.google.auto.service.AutoService
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import sun.rmi.runtime.Log
import java.util.logging.Logger

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-18:07
 * 描    述：wiki:https://juejin.im/post/5e8d87c4f265da47ad218e6b
 * 修订历史：
 * ================================================
 */
@Priority(0)
@AutoService(ClassTransformer::class)
class CommTransformer : ClassTransformer {
    private val SHADOW_URL =
        "com/didichuxing/doraemonkit/aop/urlconnection/HttpUrlConnectionProxyUtil"
    private val DESC = "(Ljava/net/URLConnection;)Ljava/net/URLConnection;"


    /**
     * 转化
     */
    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if (context.isRelease() && !DoKitExtUtil.RELEASE_PLUGIN_SWITCH) {
            return klass
        }

        if (!DoKitExtUtil.dokitPluginSwitchOpen()) {
            return klass
        }

        val className = klass.className

        if (className.contains("didihttp")) {
            "${context.projectDir.lastPath()}==className===>$className".println()
        }

        //查找DoraemonKitReal&pluginConfig方法并插入指定字节码
        if (className == "com.didichuxing.doraemonkit.DoraemonKitReal") {
            //插件配置
            klass.methods?.find {
                it.name == "pluginConfig"
            }.let { methodNode ->
                "${context.projectDir.lastPath()}->insert map to the DoraemonKitReal pluginConfig succeed".println()
                methodNode?.instructions?.insert(createPluginConfigInsnList())
            }
            //三方库信息注入
            klass.methods?.find {
                it.name == "initThirdLibraryInfo"
            }.let { methodNode ->
                "${context.projectDir.lastPath()}->insert map to the DoraemonKitReal initThirdLibraryInfo succeed".println()
                methodNode?.instructions?.insert(createThirdLibInfoInsnList())
            }
        }

        //gps字节码操作
        if (DoKitExtUtil.commExt.gpsSwitch) {
            //插入高德地图相关字节码
            if (className == "com.amap.api.location.AMapLocationClient") {
                klass.methods?.find {
                    it.name == "setLocationListener"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook amap  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createAmapLocationInsnList())
                }

            }

            //插入腾讯地图相关字节码
            if (className == "com.tencent.map.geolocation.TencentLocationManager") {
                //持续定位和单次定位
                klass.methods?.filter {
                    it.name == "requestSingleFreshLocation" || it.name == "requestLocationUpdates"
                }?.forEach { methodNode ->
                    "${context.projectDir.lastPath()}->hook tencent map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createTencentLocationInsnList())
                }
            }

            //插入百度地图相关字节码
            klass.methods?.find {
                it.name == "onReceiveLocation" && it.desc == "(Lcom/baidu/location/BDLocation;)V"
            }.let { methodNode ->
                methodNode?.name?.let {
                    "${context.projectDir.lastPath()}->hook baidu map  succeed: ${className}_${methodNode.name}_${methodNode.desc}".println()
                    methodNode.instructions?.insert(createBaiduLocationInsnList())
                }
            }
        }


        //网络 OkHttp&didi platform aop
        if (DoKitExtUtil.commExt.networkSwitch) {
            //okhttp
            if (className == "okhttp3.OkHttpClient\$Builder") {
                //空参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "()V"
                }.let { zeroConsMethodNode ->
                    "${context.projectDir.lastPath()}->hook OkHttp  succeed: ${className}_${zeroConsMethodNode?.name}_${zeroConsMethodNode?.desc}".println()
                    zeroConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        zeroConsMethodNode.instructions.insertBefore(
                            it,
                            createOkHttpZeroConsInsnList()
                        )
                    }
                }


                //一个参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "(Lokhttp3/OkHttpClient;)V"
                }.let { oneConsMethodNode ->
                    "${context.projectDir.lastPath()}->hook OkHttp  succeed: ${className}_${oneConsMethodNode?.name}_${oneConsMethodNode?.desc}".println()
                    oneConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        oneConsMethodNode.instructions.insertBefore(
                            it,
                            createOkHttpOneConsInsnList()
                        )
                    }
                }

            }

            //didi platform
            if (className == "didihttp.DidiHttpClient\$Builder") {
                "find DidiHttpClient succeed: ${className}".println()
                //空参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "()V"
                }.let { zeroConsMethodNode ->
                    "${context.projectDir.lastPath()}->hook didi http  succeed: ${className}_${zeroConsMethodNode?.name}_${zeroConsMethodNode?.desc}".println()
                    zeroConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        zeroConsMethodNode.instructions.insertBefore(
                            it,
                            createDidiHttpZeroConsInsnList()
                        )
                    }
                }


                //一个参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "(Ldidihttp/DidiHttpClient;)V"
                }.let { oneConsMethodNode ->
                    "${context.projectDir.lastPath()}->hook didi http  succeed: ${className}_${oneConsMethodNode?.name}_${oneConsMethodNode?.desc}".println()
                    oneConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        oneConsMethodNode.instructions.insertBefore(
                            it,
                            createDidiHttpOneConsInsnList()
                        )
                    }
                }
            }

            //webView 字节码操作
            if (DoKitExtUtil.commExt.webViewSwitch) {
                //普通的webview
                klass.methods.forEach { method ->
                    method.instructions?.iterator()?.asIterable()
                        ?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                            it.opcode == INVOKEVIRTUAL &&
                                    it.name == "loadUrl" &&
                                    it.desc == "(Ljava/lang/String;)V" &&
                                    isWebViewOwnerNameMatched(it.owner)
                        }?.forEach {
                            "${context.projectDir.lastPath()}->hook WebView#loadurl method  succeed in :  ${className}_${method.name}_${method.desc} | ${it.owner}".println()
                            method.instructions.insertBefore(
                                it,
                                createWebViewInsnList()
                            )
                        }
                }
            }

            // url connection
            klass.methods.forEach { method ->
                method.instructions?.iterator()?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                        it.opcode == INVOKEVIRTUAL &&
                                it.owner == "java/net/URL" &&
                                it.name == "openConnection" &&
                                it.desc == "()Ljava/net/URLConnection;"
                    }?.forEach {
                        "${context.projectDir.lastPath()}->hook URL#openConnection method  succeed in : ${className}_${method.name}_${method.desc}".println()
                        method.instructions.insert(
                            it,
                            MethodInsnNode(INVOKESTATIC, SHADOW_URL, "proxy", DESC, false)
                        )
                    }
            }

        }

        //自定义字节码操作
        if (DoKitExtUtil.customExt.customSwitch){
            // 自定义配置信息
            if (className == "com.medlinker.debugtools.config.DTPluginExtConfig") {
                klass.methods?.find {
                    it.name == "initPluginExtConfig"
                }.let {
                    methodNode -> "${context.projectDir.lastPath()}->insert custom config toDTPluginExtConfig succeed".println()
                    methodNode?.instructions?.insert(createCustomConfigInsnList())
                }
            }
        }

        return klass
    }

    private fun isWebViewOwnerNameMatched(ownerName: String): Boolean {
        return ownerName == "android/webkit/WebView" ||
                ownerName == "com/tencent/smtt/sdk/WebView" ||
                ownerName.contentEquals("WebView") ||
                ownerName == DoKitExtUtil.WEBVIEW_CLASS_NAME
    }

    private fun createCustomConfigInsnList() : InsnList{
        return with(InsnList()) {
            add(VarInsnNode(ALOAD,0))
            add(LdcInsnNode(if (DoKitExtUtil.customExt.showLaneFun) "true" else "false" ))
            add(
                FieldInsnNode(
                    PUTFIELD,
                        "com.medlinker.debugtools.config.DTPluginExtConfig",
                        "mShowLane",
                        "Ljava/lang/String;"
                )
            )
            this
        }
    }



    /**
     * 创建pluginConfig代码指令
     */
    private fun createPluginConfigInsnList(): InsnList {
        //val insnList = InsnList()
        return with(InsnList()) {
            //new HashMap
            add(TypeInsnNode(NEW, "java/util/HashMap"))
            add(InsnNode(DUP))
            add(MethodInsnNode(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false))
            //保存变量
            add(VarInsnNode(ASTORE, 0))
            //获取第一个变量
            //put("dokitPluginSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("dokitPluginSwitch"))
            add(InsnNode(if (DoKitExtUtil.dokitPluginSwitchOpen()) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //put("gpsSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("gpsSwitch"))
            add(InsnNode(if (DoKitExtUtil.commExt.gpsSwitch) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //put("networkSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("networkSwitch"))
            add(InsnNode(if (DoKitExtUtil.commExt.networkSwitch) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //put("bigImgSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("bigImgSwitch"))
            add(InsnNode(if (DoKitExtUtil.commExt.bigImgSwitch) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //put("methodSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("methodSwitch"))
            add(InsnNode(if (DoKitExtUtil.dokitSlowMethodSwitchOpen()) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))


            //put("methodStrategy",0)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("methodStrategy"))
            add(InsnNode(if (DoKitExtUtil.SLOW_METHOD_STRATEGY == SlowMethodExt.STRATEGY_STACK) ICONST_0 else ICONST_1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Integer",
                    "valueOf",
                    "(I)Ljava/lang/Integer;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //将HashMap注入到DokitPluginConfig中
            add(VarInsnNode(ALOAD, 0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/DokitPluginConfig",
                    "inject",
                    "(Ljava/util/Map;)V",
                    false
                )
            )

            this
        }

        //return insnList

    }


    /**
     * 创建pluginConfig代码指令
     */
    private fun createThirdLibInfoInsnList(): InsnList {
        //val insnList = InsnList()
        return with(InsnList()) {
            //new HashMap
            add(TypeInsnNode(NEW, "java/util/HashMap"))
            add(InsnNode(DUP))
            add(MethodInsnNode(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false))
            //保存变量
            add(VarInsnNode(ASTORE, 0))

            for (thirdLibInfo in DoKitExtUtil.THIRD_LIB_INFOS) {
                add(VarInsnNode(ALOAD, 0))
                add(LdcInsnNode(thirdLibInfo.name))
                add(LdcInsnNode(thirdLibInfo.fileSize))

                add(
                    MethodInsnNode(
                        INVOKEINTERFACE,
                        "java/util/Map",
                        "put",
                        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                        false
                    )
                )
                add(InsnNode(POP))
            }
//
//            //将HashMap注入到DokitPluginConfig中
            add(VarInsnNode(ALOAD, 0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/DokitThirdLibInfo",
                    "inject",
                    "(Ljava/util/Map;)V",
                    false
                )
            )

            this
        }

        //return insnList

    }


    /**
     * 创建Amap地图代码指令
     */
    private fun createAmapLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy",
                    "<init>",
                    "(Lcom/amap/api/location/AMapLocationListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 1))
            this
        }

    }


    /**
     * 创建tencent地图代码指令
     */
    private fun createTencentLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/TencentLocationListenerProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 2))
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/aop/TencentLocationListenerProxy",
                    "<init>",
                    "(Lcom/tencent/map/geolocation/TencentLocationListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 2))

            this
        }

    }


    /**
     * 创建百度地图代码指令
     */
    private fun createBaiduLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/BDLocationUtil",
                    "proxy",
                    "(Lcom/baidu/location/BDLocation;)Lcom/baidu/location/BDLocation;",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 1))
            this
        }

    }


    /**
     * 创建Okhttp Build 空参数构造函数指令
     */
    private fun createOkHttpZeroConsInsnList(): InsnList {
        return with(InsnList()) {
            //插入application 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(
                FieldInsnNode(
                    GETFIELD,
                    "okhttp3/OkHttpClient\$Builder",
                    "interceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                FieldInsnNode(
                    GETSTATIC,
                    "com/didichuxing/doraemonkit/aop/OkHttpHook",
                    "globalInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/List",
                    "addAll",
                    "(Ljava/util/Collection;)Z",
                    true
                )
            )
            add(InsnNode(POP))

            //插入NetworkInterceptor 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(
                FieldInsnNode(
                    GETFIELD,
                    "okhttp3/OkHttpClient\$Builder",
                    "networkInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                FieldInsnNode(
                    GETSTATIC,
                    "com/didichuxing/doraemonkit/aop/OkHttpHook",
                    "globalNetworkInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/List",
                    "addAll",
                    "(Ljava/util/Collection;)Z",
                    true
                )
            )
            add(InsnNode(POP))
            this
        }

    }


    /**
     * 创建Okhttp Build 一个参数构造函数指令
     */
    private fun createOkHttpOneConsInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(ALOAD, 0))
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/OkHttpHook",
                    "performOkhttpOneParamBuilderInit",
                    "(Ljava/lang/Object;Ljava/lang/Object;)V",
                    false
                )
            )
            this
        }

    }


    /**
     * 创建didiClient Build 空参数构造函数指令
     */
    private fun createDidiHttpZeroConsInsnList(): InsnList {
        return with(InsnList()) {
            //插入application 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(
                FieldInsnNode(
                    GETFIELD,
                    "didihttp/DidiHttpClient\$Builder",
                    "interceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                FieldInsnNode(
                    GETSTATIC,
                    "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook",
                    "globalInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/List",
                    "addAll",
                    "(Ljava/util/Collection;)Z",
                    true
                )
            )
            add(InsnNode(POP))

            //插入NetworkInterceptor 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(
                FieldInsnNode(
                    GETFIELD,
                    "didihttp/DidiHttpClient\$Builder",
                    "networkInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                FieldInsnNode(
                    GETSTATIC,
                    "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook",
                    "globalNetworkInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/List",
                    "addAll",
                    "(Ljava/util/Collection;)Z",
                    true
                )
            )
            add(InsnNode(POP))
            this
        }

    }


    /**
     * 创建didiClient Build 一个参数构造函数指令
     */
    private fun createDidiHttpOneConsInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(ALOAD, 0))
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook",
                    "performDidiHttpOneParamBuilderInit",
                    "(Ljava/lang/Object;Ljava/lang/Object;)V",
                    false
                )
            )
            this
        }
    }


    /**
     * 创建webView函数指令集
     * 参考:https://www.jianshu.com/p/7d623f441bed
     */
    private fun createWebViewInsnList(): InsnList {
        return with(InsnList()) {
            //复制栈顶的2个指令 指令集变为 比如 aload 2 aload0 aload 2 aload0
            add(InsnNode(DUP2))
            //抛出最上面的指令 指令集变为 aload 2 aload0 aload 2  其中 aload 2即为我们所需要的对象
            add(InsnNode(POP))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/WebViewHook",
                    "inject",
                    "(Ljava/lang/Object;)V",
                    false
                )
            )
            this
        }
    }
}