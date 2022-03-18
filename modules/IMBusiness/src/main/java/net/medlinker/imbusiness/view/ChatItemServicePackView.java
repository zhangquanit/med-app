package net.medlinker.imbusiness.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.medlinker.lib.utils.MedAppInfo;

import net.medlinker.base.router.RouterUtil;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.entity.ServicePackEntity;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;

import java.util.List;

/**
 * 服务包
 *
 * @author hmy
 * @time 2020-04-22 11:04
 */
public class ChatItemServicePackView extends BaseChatItemView {

    private TextView mNameTv;
    private TextView mTypeTv;
    private TextView mInfoTv;
    private View mContentView;

    public ChatItemServicePackView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_service_pack_sender : R.layout.view_chat_item_service_pack_received;
    }

    @Override
    protected void onFindViewById() {
        mNameTv = findViewById(R.id.tv_name);
        mTypeTv = findViewById(R.id.tv_type);
        mInfoTv = findViewById(R.id.tv_info);
        mContentView = findViewById(R.id.ll_content);
    }

    @Override
    public void setViewData(List<MsgDbEntity> dataList, int position) {
        super.setViewData(dataList, position);

        final MsgDbEntity msgDbEntity = dataList.get(position);
        final ServicePackEntity entity;
        try {
            entity = new Gson().fromJson(msgDbEntity.getJsonString(), ServicePackEntity.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return;
        }
        mNameTv.setText(entity.getPackage().getName());
        mTypeTv.setText(entity.getPackage().getLevelStr());
        mInfoTv.setText(entity.getPackage().getIntro());

        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ServicePackEntity.PackageBean pack = entity.getPackage();
                String packageId = pack.getId();

                String url = MedAppInfo.isDebug()
                        ? "/link?url=https://r-qa.medlinker.com/cYmITJ?packageId="
                        : "/link?url=https://r.medlinker.com/d0D7H5?packageId=";
                url += packageId;
                RouterUtil.startActivity(getContext(), url);
            }
        });
    }

    @Override
    protected boolean isShowMsgSendStatus(MsgDbEntity message) {
        return true;
    }

    @Override
    protected boolean isShowJsonAvatar() {
        return true;
    }

    private String getOpenType() {
        return ModuleIMBusinessManager.INSTANCE.isDebug() ? "test" : "prod";
    }
}
