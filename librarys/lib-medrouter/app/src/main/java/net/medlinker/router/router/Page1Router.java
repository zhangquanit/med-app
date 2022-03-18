package net.medlinker.router.router;

import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.router.BaseMedRouterMapping;

import net.medlinker.router.MedRouterActivity;

/**
 * @author zhangquan
 */
public class Page1Router extends BaseMedRouterMapping {
    public Page1Router(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public String getRouterKey() {
        return RoutePath.MED_PATH1;
    }

    @Override
    public Class getTargetClass() {
        return MedRouterActivity.class;
    }
}
