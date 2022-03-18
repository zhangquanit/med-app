package net.medlinker.imbusiness.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cn.glidelib.GlideUtil;

import net.medlinker.base.router.RouterUtil;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: FANGJING.
 * CreateTime:  2017/2/28 20:37
 * Email：fangjing@medlinker.com.
 * Description:
 */

public class ChatItemCardView extends BaseChatItemView {


    @BindView(R2.id.tv_title)
    TextView mTitleTv;
    @BindView(R2.id.iv_conver)
    ImageView mCoverIv;
    @BindView(R2.id.tv_content)
    TextView mContentTv;
    @BindView(R2.id.tv_label)
    TextView mLabelTv;

    public ChatItemCardView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_card_view_sender : R.layout.view_chat_item_card_view_receiver;
    }

    @Override
    protected void onFindViewById() {
        ButterKnife.bind(this);
    }

    @Override
    public void setViewData(List<MsgDbEntity> dataList, int position) {
        setSystemCardData(dataList, position);
        super.setViewData(dataList, position);
    }

    protected void setSystemCardData(List<MsgDbEntity> dataList, int position) {
        final MsgDbEntity msgDbEntity = dataList.get(position);
        boolean isShowCover = !TextUtils.isEmpty(msgDbEntity.getCardImageUrl());
        boolean isShowContent = !TextUtils.isEmpty(msgDbEntity.getCardSummary());
        boolean isShowLabel = !TextUtils.isEmpty(msgDbEntity.getCardLabel());
        final String cardTitle = msgDbEntity.getCardTitle();
        mTitleTv.setText(cardTitle);
        mCoverIv.setVisibility(isShowCover ? VISIBLE : GONE);
        mContentTv.setVisibility(isShowContent ? VISIBLE : GONE);
        mLabelTv.setVisibility(isShowLabel ? VISIBLE : GONE);
        if (isShowCover) {
            GlideUtil.setRoundImageView(mCoverIv, msgDbEntity.getCardImageUrl(), 5);
        }
        if (isShowContent) {
            mContentTv.setText(msgDbEntity.getCardSummary());
        }
        if (isShowLabel) {
            mLabelTv.setText(msgDbEntity.getCardLabel());
        }

        final String cardTargetUrl = TextUtils.isEmpty(msgDbEntity.getCardExtra())
                ? msgDbEntity.getCardTargetUrl() : msgDbEntity.getCardExtra();
        if (TextUtils.isEmpty(cardTargetUrl)) {
            // 无跳转链接，显示所有内容
            mContentTv.setMaxLines(Integer.MAX_VALUE);
        }
        findViewById(R.id.layout_content).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(cardTargetUrl)) {
                    if (cardTargetUrl.startsWith("http")) {
                        RouterUtil.startActivity(mContext, String.format("/link?url=%s", cardTargetUrl));
                        return;
                    }
                    try {
                        ARouter.getInstance().build(cardTargetUrl).navigation();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
