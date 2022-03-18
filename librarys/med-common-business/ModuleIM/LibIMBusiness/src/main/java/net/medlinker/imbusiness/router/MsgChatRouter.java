package net.medlinker.imbusiness.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.router.BaseMedRouterMapping;

import net.medlinker.base.router.RouterUtil;
import net.medlinker.imbusiness.entity.intent.ChatSessionIntentData;
import net.medlinker.imbusiness.util.ImForwardUtil;

/**
 * @author hmy
 * @time 2020/9/22 17:28
 */
public class MsgChatRouter extends BaseMedRouterMapping {

    public static final String ROUTER = "/message/chat";

    public MsgChatRouter(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public String getRouterKey() {
        return ROUTER;
    }

    @Override
    protected boolean needInterrupt(Context context, int requestCode) {
        try {
            String sessionId = ImForwardUtil.transferPrivateChat(context, mMedRouter.getUri());
            Intent intent = new Intent(context, RouterUtil.getClass(ModuleIMRouterPath.ROUTER_CHAT));
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(RouterUtil.DATA_KEY, new ChatSessionIntentData(sessionId));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Class getTargetClass() {
        return null;
    }

}
