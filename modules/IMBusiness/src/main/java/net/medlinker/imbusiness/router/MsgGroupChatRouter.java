package net.medlinker.imbusiness.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.router.BaseMedRouterMapping;

import net.medlinker.base.router.RouterUtil;
import net.medlinker.imbusiness.entity.intent.ChatSessionIntentData;
import net.medlinker.imbusiness.util.ImForwardUtil;

/**
 * @author hmy
 * @time 2020/9/22 17:27
 */
@Route(path = MsgGroupChatRouter.ROUTER)
public class MsgGroupChatRouter extends BaseMedRouterMapping {

    public static final String ROUTER = "/message/group";

    public MsgGroupChatRouter(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    protected boolean needInterrupt(Context context, int requestCode) {
        try {
            String sessionId = ImForwardUtil.transferGroupChat(context, mMedRouter.getUri());
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
    public String getRouterKey() {
        return ROUTER;
    }

    @Override
    public Class getTargetClass() {
        return null;
    }
}
