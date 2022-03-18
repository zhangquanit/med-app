package net.medlinker.imbusiness.view;

import android.content.Context;
import android.widget.TextView;

import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;

import java.util.List;

/**
 * @author hmy
 * @time 2020/9/24 17:26
 */
public class ChatItemNoticeView extends BaseChatItemView {

    private TextView mNoticeTv;

    public ChatItemNoticeView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_chat_notice_view;
    }

    @Override
    protected void onFindViewById() {
        mNoticeTv = findViewById(R.id.tv_notice);
    }

    @Override
    public void setViewData(List<MsgDbEntity> dataList, int position) {
        mNoticeTv.setText(dataList.get(position).getNoticeContent());
    }
}
