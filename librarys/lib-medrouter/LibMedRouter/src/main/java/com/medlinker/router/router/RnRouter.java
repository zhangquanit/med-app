package com.medlinker.router.router;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.medlinker.router.Config;
import com.medlinker.router.MedRouterHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 6.0
 * @description //新的RN协议，时空后启用
 * @time 2018/2/6
 */
public class RnRouter extends BaseMedRouterMapping {

    public static final String NEW_REACT_NATIVE = "/medrn";

    public RnRouter(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public boolean needInterrupt(final Context context, int requestCode) {
        String moduleName = mMedRouter.getParams().get("moduleName");
        String routeName = mMedRouter.getParams().get("routeName");
        String extraStr = mMedRouter.getParams().get("extra");
        Config config = MedRouterHelper.getConfig();
        if (null != config) {
            config.launchReactActivity(mMedRouter, context, moduleName, routeName, extraStr, requestCode);
        }
        return true;
    }

    @Override
    public String getRouterKey() {
        return NEW_REACT_NATIVE;
    }

    @Override
    public Class getTargetClass() {
        return null;
    }

    /**
     * @param moduleName
     * @param routeName
     * @param extra      调用的地方不需要encode
     * @return
     */
    public static String createJumpUrl(String moduleName, String routeName, @Nullable String extra) {
        if (!TextUtils.isEmpty(extra)) {
            try {
                extra = URLEncoder.encode(extra, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                extra = null;
            }
        }
        final String baseUrl = NEW_REACT_NATIVE.concat("?").concat("moduleName=").concat(moduleName)
                .concat("&").concat("routeName=").concat(routeName);
        return TextUtils.isEmpty(extra) ? baseUrl : baseUrl.concat("&").concat("extra=").concat(extra);
    }
}
