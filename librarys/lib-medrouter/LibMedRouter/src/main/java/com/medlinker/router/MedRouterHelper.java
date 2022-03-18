package com.medlinker.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.medlinker.router.router.BaseMedRouterMapping;
import com.medlinker.router.router.RnRouter;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 6.0
 * @description
 * @time 2018/2/6
 */
public final class MedRouterHelper {

    private static final Map<String, Class<? extends BaseMedRouterMapping>> ROUTER_MAP = new HashMap<>();

    private static final String MED = "med:";
    private static Config sConfig;

    static {
        ROUTER_MAP.put(RnRouter.NEW_REACT_NATIVE, RnRouter.class);
    }

    public static void init(Map<String, Class<? extends BaseMedRouterMapping>> routerMap, Config config) {
        sConfig = config;
        if (null != routerMap && !routerMap.isEmpty()) {
            ROUTER_MAP.putAll(routerMap);
        }
    }

    public static void registerRouter(String router, Class<? extends BaseMedRouterMapping> routerMapping) {
        if (router != null && routerMapping != null) {
            ROUTER_MAP.put(router, routerMapping);
        }
    }

    public static Config getConfig() {
        return sConfig;
    }

    public static MedRouter withUrl(String moduleName, String routeName) {
        return withUrl(moduleName, routeName, null);
    }

    public static MedRouter withUrl(String moduleName, String routeName, @Nullable String extra) {
        return withUrl(RnRouter.createJumpUrl(moduleName, routeName, extra));
    }

    public static MedRouter withUrl(String targetUrl) {
        return new MedRouter(targetUrl);
    }

    public static void navigation(String targetUrl, Context context) {
        new MedRouter(targetUrl).queryTarget().navigation(context);
    }

    public static final class MedRouter {
        private Uri uri = Uri.EMPTY;
        private String sourceUrl = "";
        private String schema = "";
        private String host = "";
        private String path = "";
        private Map<String, String> params = new HashMap<>();
        private Bundle mExtraData;
        public BaseMedRouterMapping mMedRouter;
        public boolean isArouterInject;

        private MedRouter(String targetUrl) {
            if (TextUtils.isEmpty(targetUrl)) {
                return;
            }

            if (targetUrl.startsWith("/")) {
                targetUrl = MED + targetUrl;
            }
            this.sourceUrl = targetUrl;
            Uri uri = Uri.parse(targetUrl);
            if (uri != null) {
                this.uri = uri;
                this.schema = uri.getScheme();
                this.host = uri.getHost();
                this.path = uri.getPath();
                try {
                    for (String key : uri.getQueryParameterNames()) {
                        this.params.put(key, uri.getQueryParameter(key));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public Uri getUri() {
            return uri;
        }

        public String getSchema() {
            return schema;
        }

        public String getHost() {
            return host;
        }

        public String getPath() {
            return path;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

        public MedRouterTarget queryTarget() {
            return queryTarget(false);
        }

        public MedRouterTarget queryTarget(boolean isNeedLogin) {
            if (isNeedLogin && null != sConfig) {
                BaseMedRouterMapping loginRouter = sConfig.checkLogin(this);
                if (null != loginRouter) {
                    mMedRouter = loginRouter;
                    return new MedRouterTarget(this);
                }
            }

            try {
                Class<? extends BaseMedRouterMapping> aClass = ROUTER_MAP.get(path);
                if (aClass != null) {
                    mMedRouter = aClass.getDeclaredConstructor(MedRouter.class).newInstance(this);
                }
                if (null == mMedRouter) {
                    //使用Arouter注解的MedRouter
                    Class<?> routeCls = getRouteClass(path);
                    isArouterInject = null != routeCls;
                    if (null != routeCls && BaseMedRouterMapping.class.isAssignableFrom(routeCls)) {
                        mMedRouter = (BaseMedRouterMapping) routeCls.getDeclaredConstructor(MedRouter.class).newInstance(this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new MedRouterTarget(this);
        }

        public MedRouter withString(String key, String value) {
            params.put(key, value);
            return this;
        }

        public MedRouter withParcelable(String key, Parcelable data) {
            if (null == mExtraData) {
                mExtraData = new Bundle();
            }
            mExtraData.putParcelable(key, data);
            return this;
        }

        public MedRouter withSeriablele(String key, Serializable data) {
            if (null == mExtraData) {
                mExtraData = new Bundle();
            }
            mExtraData.putSerializable(key, data);
            return this;
        }

        public Bundle getExtraData() {
            return mExtraData;
        }

        private Class<?> getRouteClass(String path) {
            try {
                Postcard postcard = ARouter.getInstance().build(path);
                LogisticsCenter.completion(postcard);
                return postcard.getDestination();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String toString() {
            return "MedRouter{" +
                    "uri=" + uri +
                    ", sourceUrl='" + sourceUrl + '\'' +
                    ", schema='" + schema + '\'' +
                    ", host='" + host + '\'' +
                    ", path='" + path + '\'' +
                    ", params=" + params +
                    ", mExtraData=" + mExtraData +
                    ", mMedRouter=" + mMedRouter +
                    ", isArouterInject=" + isArouterInject +
                    '}';
        }
    }

    public static final class MedRouterTarget {
        private MedRouterHelper.MedRouter router;

        public MedRouterTarget(MedRouterHelper.MedRouter medRouter) {
            this.router = medRouter;
        }

        public Intent getIntent(Context context) {
            if (null != router.mMedRouter) return router.mMedRouter.getIntent(context);
            return null;
        }

        public void navigation(Context context) {
            navigation(context, 0);
        }

        public void navigation(Context context, int requestCode) {
            if (null != router.mMedRouter) {
                try {
                    //needInterrupt为protected  为了不更改之前的实现类
                    Method method = router.mMedRouter.getClass().getSuperclass().getDeclaredMethod("needInterrupt", Context.class, int.class);
                    method.setAccessible(true);
                    boolean needInterrupt = (boolean) method.invoke(router.mMedRouter, context, requestCode);
                    if (needInterrupt) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = getIntent(context);
                if (null == intent.getComponent()) {
                    Toast.makeText(context, context.getResources().getString(R.string.med_router_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (requestCode == 0) {
                    context.startActivity(intent);
                } else if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    activity.startActivityForResult(intent, requestCode);
                }
                return;
            }

            //兼容处理Arouter
            if (router.isArouterInject) {
                Postcard postcard = ARouter.getInstance().build(router.getPath());
                if (null != router.getExtraData()) {
                    postcard.with(router.getExtraData());
                }
                if (!router.getParams().isEmpty()) {
                    for (Map.Entry<String, String> entry : router.getParams().entrySet()) {
                        postcard.withString(entry.getKey(), entry.getValue());
                    }
                }
                if (requestCode == 0) {
                    postcard.navigation(context);
                } else if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    postcard.navigation(activity, requestCode);
                }
                return;
            }

            //容错处理
            Config config = MedRouterHelper.getConfig();
            if (null != config) {
                config.onLostPage(context);
            }
        }
    }
}
