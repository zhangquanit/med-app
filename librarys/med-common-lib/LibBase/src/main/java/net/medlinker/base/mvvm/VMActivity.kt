package net.medlinker.base.mvvm

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.medlinker.lib.utils.MedToastUtil
import net.medlinker.base.base.BaseCompatActivity
import java.lang.reflect.ParameterizedType

/**
 * @author: pengdaosong
 * @CreateTime:  2020-09-17 13:14
 * @Email：pengdaosong@medlinker.com
 * @Description: 使用ViewModel顶层Activity
 */
open class VMActivity<VM : BaseViewModel> : BaseCompatActivity() {
    protected var mViewModel: VM? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
        initVM()
    }

    protected fun initVM() {
        val superClassType = javaClass.genericSuperclass
        if (superClassType is ParameterizedType) {
            val types = superClassType.actualTypeArguments
            try {
                val presenterClassType = types[0] as Class<VM>
                mViewModel = ViewModelProvider(this).get(presenterClassType)
                lifecycle.addObserver(mViewModel!!)
            } catch (e: Exception) {
                throw e
            }
        }

        mViewModel?.mShowLoading!!.observe(this, Observer {
            if (it) {
                showDialogLoading()
            } else {
                hideDialogLoading()
            }
        })

        mViewModel?.mShowToast!!.observe(this, Observer {
            MedToastUtil.showMessage(it)
        })
    }

    /**
     * 改方法先于创建mViewModel
     */
    protected open fun init(savedInstanceState: Bundle?) {}
}