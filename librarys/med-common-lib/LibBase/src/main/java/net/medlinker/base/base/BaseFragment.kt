package net.medlinker.base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.medlinker.widget.dialog.MLLoadingDialog

abstract class BaseFragment : Fragment() {
    protected var mLoadingDialog: MLLoadingDialog? = null
    protected var mIsDestoryed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (getLayoutId() != 0) {
            inflater.inflate(getLayoutId(), null)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(savedInstanceState)

        var killed = false
        if (null != savedInstanceState) {
            killed = savedInstanceState.getBoolean("killed", false)
        }
        if (killed) {
            restorePage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mIsDestoryed = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("killed", true)
        super.onSaveInstanceState(outState)
    }

    open fun getPageName(): String? {
        return javaClass.simpleName
    }

    open fun isActDestroyed(): Boolean {
        return activity == null
    }

    protected open fun showDialogLoading() {
        if (null == mLoadingDialog) {
            mLoadingDialog = MLLoadingDialog.getInstance(true)
        }
        if (isActDestroyed() || mLoadingDialog!!.isAdded || mLoadingDialog!!.isVisible) {
            return
        }
        mLoadingDialog!!.show(fragmentManager)
    }

    protected open fun hideDialogLoading() {
        if (mLoadingDialog != null && !isActDestroyed() && (mLoadingDialog!!.isAdded
                    || mLoadingDialog!!.isVisible)
        ) {
            mLoadingDialog!!.dismissAllowingStateLoss()
            mLoadingDialog = null
        }
    }

    open fun <T : View?> findViewById(id: Int): T {
        val view = view?.findViewById<View>(id)
        return view as T
    }

    /**
     * 恢复页面
     */
    private fun restorePage() {

    }

    protected open fun finish() {
        activity?.finish()
    }


    protected abstract fun init(savedInstanceState: Bundle?)

    protected abstract fun getLayoutId(): Int
}