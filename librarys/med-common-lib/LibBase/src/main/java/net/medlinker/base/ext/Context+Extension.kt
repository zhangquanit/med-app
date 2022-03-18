package net.medlinker.base.ext


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import java.io.File


/**
 */

/**
 * screen width in pixels
 */
inline val Context.screenWidth
    get() = resources.displayMetrics.widthPixels

/**
 * screen height in pixels
 */
inline val Context.screenHeight
    get() = resources.displayMetrics.heightPixels

inline val Context.isNetworkAvailable: Boolean
    @SuppressLint("MissingPermission")
    get() {
        val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

/**
 * returns dip(dp) dimension value in pixels
 * @param value dp
 */
fun Context.dp2px(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun Context.dp2px(value: Float): Int = (value * resources.displayMetrics.density).toInt()

/**
 * return sp dimension value in pixels
 * @param value sp
 */
fun Context.sp2px(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()

fun Context.sp2px(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

/**
 * converts [px] value into dip or sp
 * @param px
 */
fun Context.px2dp(px: Int): Float = px.toFloat() / resources.displayMetrics.density

fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

/**
 * return dimen resource value in pixels
 * @param resource dimen resource
 */
fun Context.dimen2px(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)


fun Context.string(@StringRes id: Int): String = getString(id)

fun Context.color(@ColorRes id: Int): Int = resources.getColor(id)

fun Context.statusBarHeight(): Int {
    val resources = this.getResources()
    val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

fun Context.inflateLayout(
        @LayoutRes layoutId: Int, parent: ViewGroup? = null,
        attachToRoot: Boolean = false
): View = LayoutInflater.from(this).inflate(layoutId, parent, attachToRoot)

/**
 * 获取当前app的版本号
 */
fun Context.getAppVersion(): String {

    val appContext = applicationContext
    val manager = appContext.getPackageManager()
    try {
        val info = manager.getPackageInfo(appContext.getPackageName(), 0)

        if (info != null)
            return info.versionName

    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return ""
}


fun Context.getAppVersionCode(): Int {

    val appContext = applicationContext
    val manager = appContext.getPackageManager()
    try {
        val info = manager.getPackageInfo(appContext.getPackageName(), 0)

        if (info != null)
            return info.versionCode

    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return 0
}

/**
 * 获取应用的包名
 *
 * @param context context
 * @return package name
 */
fun Context.getPackageName(): String = packageName

fun Any.postDelayed(time: Long = 1000, b: () -> Unit) {
    Handler().postDelayed(object : Runnable {
        override fun run() {
            b()
        }

    }, time)
}

fun Context.installApk(file: File) {
    if (!file.exists()) {
        return
    }

    var intent = Intent(Intent.ACTION_VIEW);
    //放在此处
    //由于没有在Activity环境下启动Activity,所以设置下面的标签
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    var apkUri: Uri;
    //判断版本是否是 7.0 及 7.0 以上
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        apkUri = FileProvider.getUriForFile(this, packageName + ".fileprovider", file);
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    } else {
        apkUri = Uri.fromFile(file);
    }

    intent.setDataAndType(
            apkUri,
            "application/vnd.android.package-archive"
    );
    startActivity(intent);
    //不能写这句话 有些机子要出问题 麻痹的 搞了一天
    //  Process.killProcess(android.os.Process.myPid());
}