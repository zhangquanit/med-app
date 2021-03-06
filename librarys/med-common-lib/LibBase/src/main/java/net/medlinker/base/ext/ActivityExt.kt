package net.medlinker.base.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

/**
 * Description: Activity相关
 * Create by lxj, at 2018/12/7
 */

fun Fragment.startActivity(target: KClass<out Activity>, flag: Int = -1, bundle: Array<out Pair<String, Any?>>? = null){
    activity?.startActivity(target, flag, bundle)
}

fun Context.startActivity(target: KClass<out Activity>, flag: Int = -1, bundle: Array<out Pair<String, Any?>>? = null){
    val intent = Intent(this, target.java).apply {
        if(flag!=-1) this.addFlags(flag)
        if (bundle!=null) bundle.toBundle()?.let { putExtras(it) }
    }
    startActivity(intent)
}
