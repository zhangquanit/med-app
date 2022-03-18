package net.medlinker.imbusiness.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import net.medlinker.base.common.Observable2;
import net.medlinker.im.MsgConstants;
import net.medlinker.im.realm.ImTextLink;
import net.medlinker.im.realm.ImUserDbEntity;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.emoj.EmSmileUtils;
import net.medlinker.imbusiness.util.link.LinkUtil;
import net.medlinker.imbusiness.util.link.TextLinkSpan;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author hmy
 * @time 2020/9/24 15:26
 */
public class ChatItemTextView extends BaseChatItemView {

    private TextView mTvContent;

    public ChatItemTextView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_text_sender : R.layout.view_chat_item_text_received;
    }

    @Override
    protected void onFindViewById() {
        mTvContent = findViewById(R.id.tv_content);
    }

    @Override
    protected View getContentView() {
        return mTvContent;
    }

    @SuppressLint("CheckResult")
    @Override
    public void setViewData(List<MsgDbEntity> dataList, int position) {
        final MsgDbEntity message = dataList.get(position);
        final Spannable span = EmSmileUtils.getSmiledText(getContext(), LinkUtil.addLink(getContext(), message.getTextContent(), mIsRight));
        Observable2.from(message.getLinks())
                .subscribe(new Consumer<ImTextLink>() {
                    @Override
                    public void accept(ImTextLink imTextLink) throws Exception {
                        span.setSpan(new TextLinkSpan(imTextLink.getLinkUrl()),
                                imTextLink.getStartIndex(), imTextLink.getEndIndex(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        mTvContent.setText(span);
        for (ImUserDbEntity imUserDbEntity : message.getAtUsers()) {
            if (imUserDbEntity.getId() == MsgConstants.GROUP_TIPS_USER_ID) {
                mTvContent.setText(getResources().getString(R.string.group_tips_all, span));//如果是@所有人的消息，就加上这个显示
            }
        }
        mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
        super.setViewData(dataList, position);
    }
}
