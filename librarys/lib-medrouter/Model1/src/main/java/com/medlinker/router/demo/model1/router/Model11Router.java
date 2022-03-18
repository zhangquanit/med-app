package com.medlinker.router.demo.model1.router;

import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.demo.model1.Model11Activity;
import com.medlinker.router.demo.model1.Model1Activity;
import com.medlinker.router.router.BaseMedRouterMapping;

import java.util.Map;

/**
 * @author zhangquan
 */
@Route(path = "/model/page2")
public class Model11Router extends BaseMedRouterMapping {
    public Model11Router(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public String getRouterKey() {
        return null;
    }

    @Override
    public Class getTargetClass() {
        return Model11Activity.class;
    }

    @Override
    protected void packageDataInIntent(Intent intent) {
        Map<String, String> params = mMedRouter.getParams();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        if (null != mMedRouter.getExtraData()) {
            intent.putExtras(mMedRouter.getExtraData());
        }
    }
}
