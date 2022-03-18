package com.medlinker.router.demo.model2.router;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.demo.model2.Model2Activity;
import com.medlinker.router.router.BaseMedRouterMapping;

/**
 * @author zhangquan
 */
@Route(path = "/model2/page1")
public class Model2Rrouter extends BaseMedRouterMapping {
    public Model2Rrouter(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public String getRouterKey() {
        return null;
    }

    @Override
    public Class getTargetClass() {
        return Model2Activity.class;
    }
}
