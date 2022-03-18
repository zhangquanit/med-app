package com.medlinker.router.demo.model1.router;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.demo.model1.Model1Activity;
import com.medlinker.router.router.BaseMedRouterMapping;

import java.util.Map;

/**
 * @author zhangquan
 */
@Route(path = "/model/page1/page2")
public class Model1Router extends BaseMedRouterMapping {
    public Model1Router(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public String getRouterKey() {
        return null;
    }

    @Override
    public Class getTargetClass() {
        return Model1Activity.class;
    }

    @Override
    protected boolean needInterrupt(Context context, int requestCode) {
        System.out.println("needInterrrupt");
        return false;
    }
}
