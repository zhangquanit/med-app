package net.medlinker.imbusiness.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.medlinker.lib.utils.MedDimenUtil;

import net.medlinker.im.helper.MsgVoiceManager;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;

import java.util.List;

import io.realm.Realm;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/2/18
 */
public class ChatItemVoiceView extends BaseChatItemView {

    private TextView mTvVoiceTime;
    private ImageView mIvVoicePlay;
    private FrameLayout mRlContent;
    private ImageView mRedPoint;

    public ChatItemVoiceView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_voice_sender : R.layout.view_chat_item_voice_received;
    }

    @Override
    protected void onFindViewById() {
        mTvVoiceTime = findViewById(R.id.tv_voice_time);
        mIvVoicePlay = findViewById(R.id.iv_play);
        mRedPoint = findViewById(R.id.iv_red_point);
        mRlContent = findViewById(R.id.layout_content);
    }

    @Override
    public void setViewData(List<MsgDbEntity> dataList, final int position) {
        final MsgDbEntity message = dataList.get(position);
        showStatus(message);
        mRlContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MsgVoiceManager.getInstance().isPlayingCurrent(message.getTimestamp())) { //如果点击的是正在播放的语音，则停止播放
                    MsgVoiceManager.getInstance().stopPlay();
                    return;
                }
                final boolean isSelfMsg = message.isSelfMsg();
                if (message.getVoiceLocalPath() != null) {
                    if (!isSelfMsg && !message.isHasPlayVoice()) {
                        mRedPoint.setVisibility(GONE);
                    }
                    mIvVoicePlay.setImageResource(isSelfMsg ? R.drawable.msg_voice_play_self : R.drawable.msg_voice_play_other);
                    final AnimationDrawable anim = (AnimationDrawable) mIvVoicePlay.getDrawable().mutate();
                    anim.start();
                    MsgVoiceManager.getInstance().startPlay(message.getVoiceLocalPath(), new MsgVoiceManager.DefaultVoicePlayStatusListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            anim.stop();
                            mIvVoicePlay.setImageResource(isSelfMsg ? R.drawable.msg_voice_play_self_3 : R.drawable.msg_voice_play_other_3);
                            setHasPlay(message);
                        }

                        @Override
                        public void onStop(MediaPlayer mp) {
                            anim.stop();
                            mIvVoicePlay.setImageResource(isSelfMsg ? R.drawable.msg_voice_play_self_3 : R.drawable.msg_voice_play_other_3);
                            setHasPlay(message);
                        }

                    }, message.getTimestamp());
                }
            }
        });
        setVioceContentLength(message.getVoiceDuration()); //语音内容根据语音时长变化
        super.setViewData(dataList, position);
    }

    private void setHasPlay(final MsgDbEntity message) {
        if (message == null || !message.isValid() || message.isSelfMsg() || message.isHasPlayVoice()) {
            return; //如果是自己的语音或者是已经播放过了，就不需要修改数据库了
        }
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    message.setHasPlayVoice(true);
                }
            });
        }
    }

    private void showStatus(MsgDbEntity message) {
        if (message.getMsgSendStatus() != MsgDbEntity.MSG_STATUS_SENDING) {
            mTvVoiceTime.setVisibility(View.VISIBLE);
            mTvVoiceTime.setText(message.getVoiceDuration() + "\"");
        } else { //发送中，不显示语音时长
            mTvVoiceTime.setVisibility(View.GONE);
        }
        if (!message.isSelfMsg()) {
            mRedPoint.setVisibility(message.isHasPlayVoice() ? GONE : VISIBLE);
        }
    }

    private void setVioceContentLength(int time) {
        int widthDP = (time - 1) * 4 + 60;
        int layoutWidth = MedDimenUtil.dip2px(getContext(), widthDP);
        ViewGroup.LayoutParams params = mRlContent.getLayoutParams();
        params.width = layoutWidth;
    }
}
