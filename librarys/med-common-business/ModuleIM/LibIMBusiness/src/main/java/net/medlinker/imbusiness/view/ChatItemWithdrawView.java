package net.medlinker.imbusiness.view;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.medlinker.protocol.message.MessageOuterClass;

import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;

import java.util.List;

import static com.medlinker.protocol.message.MessageOuterClass.Message.DataCase.TEXT;

/**
 * @author hmy
 * @time 2020/9/24 17:30
 */
public class ChatItemWithdrawView extends BaseChatItemView implements View.OnClickListener {

    private TextView mEdit;
    private Handler handler;

    public ChatItemWithdrawView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_chat_withdraw_view;
    }

    @Override
    protected void onFindViewById() {
        mEdit = findViewById(R.id.tv_edit);
        mEdit.setOnClickListener(this);
    }

    @Override
    public void setViewData(List<MsgDbEntity> mDataList, int position) {
        MsgDbEntity msgDbEntity = mDataList.get(position);
        MessageOuterClass.Message.DataCase dataCase = MessageOuterClass.Message.DataCase
                .forNumber(msgDbEntity.getDataType());

        long difference = System.currentTimeMillis() - msgDbEntity.getWithdrawTimestamp();
        boolean showReditButton = difference > MsgDbEntity.MSG_RE_EDIT_TIME;
        if (dataCase == null || dataCase != TEXT || showReditButton) {
            mEdit.setVisibility(GONE);
            return;
        }
// FIXME       mEdit.setVisibility(VISIBLE);
        mEdit.setTag(msgDbEntity);
        sendDelayMsg(MsgDbEntity.MSG_RE_EDIT_TIME - difference);
    }

    private void sendDelayMsg(long cap) {
        if (cap <= 500) {
            mEdit.setVisibility(GONE);
            return;
        }
        handler = getHandler();
        if (null == handler) {
            handler = new Handler();
        }
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(mRunnable, cap);

    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mEdit.setVisibility(GONE);
        }
    };

    @Override
    public void onClick(View v) {
        Object o = v.getTag();
        if (null == o) {
            return;
        }
//        EventBusUtils
//                .post(UserEvent.MSG_REDIT, o);
    }
}
