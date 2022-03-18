package com.medlinker.bridge


/**
 * @author hmy
 * @time 4/21/21 13:56
 */
object BridgeManager {
    private var fileDir = "/Medlinker/bridge"

    private var routerBridge: RouterBridge? = null
    private var socialBridge: SocialBridge? = null
    private var authBridge: AuthBridge? = null
    private var deviceBridge: DeviceBridge? = DeviceBridge()
    private var cameraBridge: CameraBridge? = null
    private var updaterBridge: UpdaterBridge? = null
    private var linkingBridge: LinkingBridge? = null
    private var payBridge: PayBridge? = null

    fun setFileDir(fileDir: String): BridgeManager {
        this.fileDir = fileDir
        return this
    }

    fun getFileDir(): String {
        return fileDir
    }

    fun setRouterBridge(routerBridge: RouterBridge): BridgeManager {
        this.routerBridge = routerBridge
        return this
    }

    fun getRouterBridge(): RouterBridge? {
        return routerBridge
    }

    fun setSocialBridge(socialBridge: SocialBridge): BridgeManager {
        this.socialBridge = socialBridge
        return this
    }

    fun getSocialBridge(): SocialBridge? {
        return socialBridge
    }

    fun setAuthBridge(authBridge: AuthBridge): BridgeManager {
        this.authBridge = authBridge
        return this
    }

    fun getAuthBridge(): AuthBridge? {
        return authBridge
    }

    fun setDeviceBridge(deviceBridge: DeviceBridge): BridgeManager {
        this.deviceBridge = deviceBridge
        return this
    }

    fun getDeviceBridge(): DeviceBridge? {
        return deviceBridge
    }

    fun setCameraBridge(cameraBridge: CameraBridge): BridgeManager {
        this.cameraBridge = cameraBridge
        return this
    }

    fun getCameraBridge(): CameraBridge? {
        return cameraBridge
    }

    fun setUpdaterBridge(updaterBridge: UpdaterBridge): BridgeManager {
        this.updaterBridge = updaterBridge
        return this
    }

    fun getUpdaterBridge(): UpdaterBridge? {
        return updaterBridge
    }

    fun setLinkingBridge(bridge: LinkingBridge): BridgeManager {
        this.linkingBridge = bridge
        return this
    }

    fun getLinkingBridge(): LinkingBridge? {
        return linkingBridge
    }

    fun setPayBridge(payBridge: PayBridge): BridgeManager {
        this.payBridge = payBridge
        return this
    }

    fun getPayBridge(): PayBridge? {
        return payBridge
    }
}