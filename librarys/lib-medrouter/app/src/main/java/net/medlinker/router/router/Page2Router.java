package net.medlinker.router.router;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.router.BaseMedRouterMapping;

import net.medlinker.router.MedRouterActivity;

/**
 * @author zhangquan
 */
@Route(path =RoutePath.MED_PATH2)
public class Page2Router extends BaseMedRouterMapping {
    public Page2Router(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public String getRouterKey() {
        return null;
    }

    @Override
    public Class getTargetClass() {
        return MedRouterActivity.class;
    }
}
