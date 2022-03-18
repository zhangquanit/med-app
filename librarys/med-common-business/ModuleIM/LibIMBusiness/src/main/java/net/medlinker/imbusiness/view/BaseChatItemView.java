package net.medlinker.imbusiness.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cn.glidelib.GlideUtil;
import com.cn.glidelib.glide.GlideApp;
import com.jakewharton.rxbinding4.view.RxView;
import com.medlinker.lib.utils.MedDimenUtil;
import com.medlinker.lib.utils.MedTimeUtil;
import com.medlinker.protocol.message.MessageOuterClass;

import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.msg.action.IMessageSender;
import net.medlinker.imbusiness.msg.actionImp.MessageSenderImp;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;

/**
 * @author hmy
 * @time 2020/9/24 15:22
 */
public abstract class BaseChatItemView extends LinearLayout {

    protected boolean mIsRight;
    protected Context mContext;

    protected TextView mTimeStampView;
    protected ImageView mAvatarView;
    protected ProgressBar mProgressBar;
    protected ImageView mStatusView;
    protected TextView mGroupSenderName;

    private View mContent;
    protected IMessageSender mSender;

    public BaseChatItemView(Context context, boolean right) {
        super(context);
        this.mIsRight = right;
        this.mContext = getContext();
        initView();
    }

    private void initView() {
        inflate(getContext(), getItemLayoutId(), this);
        mTimeStampView = findViewById(R.id.tv_time);
        mAvatarView = findViewById(R.id.iv_avatar);
        mProgressBar = findViewById(R.id.iv_loading);
        mStatusView = findViewById(R.id.iv_failure);
        mGroupSenderName = findViewById(R.id.tv_group_sender_name);
        onFindViewById();
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mContent = getContentView();
    }

    protected View getContentView() {
        return findViewById(R.id.layout_content);
    }

    protected abstract int getItemLayoutId();

    protected abstract void onFindViewById();

    protected boolean isShowJsonAvatar() {
        return false;
    }

    public void setViewData(final List<MsgDbEntity> dataList, int position) {
        final MsgDbEntity message = dataList.get(position);
        setTimeView(dataList, position);
//        if (null != mAvatarView) {
//            mAvatarView.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (!message.isValid()) {
//                        return;
//                    }
//                }
//            });
//        }
        String avatarUrl;
        boolean isJsonMsg = message.getDataType() == MessageOuterClass.Message.DataCase.JSON.getNumber();
        if (message.isSelfMsg()) {
            avatarUrl = ModuleIMBusinessManager.INSTANCE.getBusinessService().getCurrentUserAvatar();
            if (isShowMsgSendStatus(message)) {
                View failureView = findViewById(R.id.iv_failure);
                if (null != failureView) {
                    RxView.clicks(failureView)
                            .throttleFirst(2, TimeUnit.SECONDS)
                            .subscribe(new io.reactivex.rxjava3.functions.Consumer<Unit>() {
                                @Override
                                public void accept(Unit unit) throws Throwable {
                                    if (mSender == null) {
                                        mSender = new MessageSenderImp();
                                    }
                                    onReSendMsg(message);
                                }
                            });
                }
                handleMsgSendStatus(dataList.get(position).getMsgSendStatus());
            }
            if (!isJsonMsg) {
//                setWithDrawListener(message);
            } else if (!isShowJsonAvatar()) {
                //如果是卡片类型，可能不是自己发的
                return;
            }
        } else {
            avatarUrl = message.getFromUser().getAvatar();
            if (!isJsonMsg) {
//                setWithDrawListener(message);
            }
            if (mGroupSenderName != null) {
                mGroupSenderName.setVisibility(message.isGroup() ? VISIBLE : GONE);
                if (message.isGroup()) {
                    mGroupSenderName.setText(message.getFromUser().getName());
                }
            }
        }
        if (null != mAvatarView) {
            GlideApp.with(getContext()).load(GlideUtil.checkUrl(getContext(), avatarUrl, GlideUtil.ImageSize.SMALL))
                    .apply(GlideUtil.getRoundedCornersOptions(MedDimenUtil.dip2px(getContext(), 32)))
                    .dontAnimate()
                    .into(mAvatarView);
        }
    }


    /**
     * 时间戳显示逻辑
     *
     * @param mDataList
     * @param position
     */
    private void setTimeView(List<MsgDbEntity> mDataList, int position) {
        MsgDbEntity msgDbEntity = mDataList.get(position);
        if (mTimeStampView != null) {
            if (position == 0) {
                mTimeStampView.setText(MedTimeUtil.formatImTime(msgDbEntity.getTimestamp()));
                mTimeStampView.setVisibility(View.VISIBLE);
            } else {
                MsgDbEntity prevMessage = mDataList.get(position - 1);
                if (MedTimeUtil.isCloseEnough(msgDbEntity.getTimestamp(), prevMessage.getTimestamp())) {
                    mTimeStampView.setText(MedTimeUtil.formatImTime(msgDbEntity.getTimestamp()));
                    mTimeStampView.setVisibility(View.VISIBLE);
                } else {
                    mTimeStampView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 发送状态
     *
     * @param status
     */
    private void handleMsgSendStatus(int status) {
        switch (status) {
            case MsgDbEntity.MSG_STATUS_SENDING:
                mProgressBar.setVisibility(View.VISIBLE);
                mStatusView.setVisibility(View.GONE);
                break;
            case MsgDbEntity.MSG_STATUS_FAILURE:
                mProgressBar.setVisibility(View.GONE);
                mStatusView.setVisibility(View.VISIBLE);
                break;
            case MsgDbEntity.MSG_STATUS_SUCCEED:
                mProgressBar.setVisibility(View.GONE);
                mStatusView.setVisibility(View.GONE);
                break;
            default:
        }
    }

    protected boolean isShowMsgSendStatus(MsgDbEntity message) {
        return message.getDataType() != MessageOuterClass.Message.DataCase.JSON.getNumber();
    }

    protected void onReSendMsg(MsgDbEntity message) {
        mSender.reSendMsg(message);
    }
}
