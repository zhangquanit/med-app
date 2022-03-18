package com.medlinker.dt.dokit;

import android.content.Context;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.medlinker.dt.R;
import com.medlinker.dt.ui.MockActivity;

/**
 * 测试联调
 */
public class MockAppKit extends AbstractKit {

    public MockAppKit(){

    }

    @Override
    public int getIcon() {
        return R.drawable.ic_mock;
    }

    @Override
    public int getName() {
        return R.string.str_mock;
    }

    @Override
    public void onAppInit(Context context) {
    }

    @Override
    public void onClick(Context context) {
        MockActivity.start(context);
    }
}
