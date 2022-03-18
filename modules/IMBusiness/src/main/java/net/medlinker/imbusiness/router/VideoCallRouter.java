package net.medlinker.imbusiness.router;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.router.BaseMedRouterMapping;
import com.medlinker.video.VideoDataLoadingActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author hmy
 */
@Route(path = VideoCallRouter.ROUTER)
public class VideoCallRouter extends BaseMedRouterMapping {

    public static final String ROUTER = "/call/video";

    public VideoCallRouter(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public String getRouterKey() {
        return ROUTER;
    }

    @Override
    protected boolean needInterrupt(Context context, int requestCode) {
        JSONObject jsonObject = new JSONObject();
        for (String key : mMedRouter.getUri().getQueryParameterNames()) {
            try {
                jsonObject.put(key, mMedRouter.getUri().getQueryParameter(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(context, VideoDataLoadingActivity.class);
        intent.putExtra("json", jsonObject.toString());
        context.startActivity(intent);
        return true;
    }

    @Override
    public Class getTargetClass() {
        return null;
    }
}
