package net.medlinker.main

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.medlinker.baseapp.route.RoutePath
import net.medlinker.base.base.BaseFragment

/**
 * 测试页面
 * @author zhangquan
 */
@Route(path = RoutePath.TAB_HOME)
class TestFrag : BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.test_frag
    }

    override fun init(savedInstanceState: Bundle?) {

    }
}