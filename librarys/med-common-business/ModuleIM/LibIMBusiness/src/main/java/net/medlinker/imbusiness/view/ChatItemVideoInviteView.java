package net.medlinker.imbusiness.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.medlinker.lib.utils.MedToastUtil;
import com.medlinker.network.retrofit.error.ErrorConsumer;

import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.util.SchedulersCompat;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.entity.MsgReservationJsonEntity;
import net.medlinker.imbusiness.entity.VideoParamEntity;
import net.medlinker.imbusiness.network.ApiManager;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;
import net.medlinker.libhttp.BaseEntity;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author hmy
 * @time 2020/8/23 15:31
 */
public class ChatItemVideoInviteView extends BaseChatItemView {

    private TextView mTitleTv;
    private MsgDbEntity msgDbEntity;

    public ChatItemVideoInviteView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_video_invite_sender : R.layout.view_chat_item_video_invite_recived;
    }

    @Override
    protected void onFindViewById() {
        mTitleTv = findViewById(R.id.tv_title);
        getContentView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String transNo = (String) v.getTag();
                startVideo(transNo);
            }
        });
    }

    @Override
    public void setViewData(List<MsgDbEntity> mDataList, int position) {
        super.setViewData(mDataList, position);
        msgDbEntity = mDataList.get(position);
        MsgReservationJsonEntity.MsgReservationJsonBean entity
                = new Gson().fromJson(msgDbEntity.getJsonString(), MsgReservationJsonEntity.MsgReservationJsonBean.class);

        mTitleTv.setText(entity.getTitle());
        getContentView().setTag(entity.getTransNo());
    }

    @Override
    protected boolean isShowMsgSendStatus(MsgDbEntity message) {
        return true;
    }

    @SuppressLint("CheckResult")
    private void startVideo(final String transNo) {
        MedToastUtil.showMessage("视频数据加载中");
        ApiManager.getInquiryWebApi().getVideoParam(transNo)
                .compose(SchedulersCompat.<BaseEntity<VideoParamEntity>>applyIoSchedulers())
                .map(new HttpResultFunc<VideoParamEntity>())
                .subscribe(new Consumer<VideoParamEntity>() {
                    @Override
                    public void accept(VideoParamEntity videoParamEntity) throws Exception {
                        ModuleIMBusinessManager.INSTANCE.getBusinessService().gotoVideoCall(getContext(), transNo, videoParamEntity);
                    }
                }, new ErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                    }
                });
    }

    @Override
    protected boolean isShowJsonAvatar() {
        return true;
    }
}
