package net.medlinker.imbusiness.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.medlinker.base.router.RouterUtil;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgExtEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.entity.DiseaseTagBean;
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
                String doctorId = pack.getDoctorId();
                DiseaseTagBean diseaseTag = entity.getDiseaseTag();
                String serviceId = diseaseTag.getId();
                String serviceTag = diseaseTag.getName();
                long fromId = msgDbEntity.getFromUser().getId();

                MsgExtEntity msgExtEntity = msgDbEntity.getExt();
                if (null != msgExtEntity && msgExtEntity.isAssistant()) {
                    if (!TextUtils.isEmpty(doctorId)) {//医助发的且有绑定医生
                        String url = String.format("/link?url=%s/ih/servicePack/packDetail?serviceId=%s&packageId=%s&channel=medlinker_app&distributionDoctorId=%s&serviceTag=%s&_opentype=%s",
                                ModuleIMBusinessManager.INSTANCE.getBusinessService().getWebEnv(), serviceId, packageId, doctorId, serviceTag, getOpenType());
                        RouterUtil.startActivity(getContext(), url);
                    } else { //医助发的且无绑定医生
                        String url = String.format("/link?url=%s/ih/servicePack/packDetail?serviceId=%s&packageId=%s&channel=medlinker_app&distributionDoctorAssistantId=%s&serviceTag=%s&_opentype=%s",
                                ModuleIMBusinessManager.INSTANCE.getBusinessService().getWebEnv(), serviceId, packageId, fromId, serviceTag, getOpenType());
                        RouterUtil.startActivity(getContext(), url);
                    }
                } else {// 医生发的服务包
                    String url = String.format("/link?url=%s/ih/servicePack/packDetail?serviceId=%s&packageId=%s&channel=medlinker_app&doctorId=%s&serviceTag=%s&_opentype=%s",
                            ModuleIMBusinessManager.INSTANCE.getBusinessService().getWebEnv(), serviceId, packageId, fromId, serviceTag, getOpenType());
                    RouterUtil.startActivity(getContext(), url);
                }
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
