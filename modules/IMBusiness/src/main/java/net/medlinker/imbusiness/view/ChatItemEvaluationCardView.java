package net.medlinker.imbusiness.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import net.medlinker.base.router.RouterUtil;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.R2;
import net.medlinker.imbusiness.entity.EvaluationBean;
import net.medlinker.imbusiness.entity.EvaluationEntity;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 认知障碍测评（齐鲁）
 *
 * @author hmy
 */
public class ChatItemEvaluationCardView extends BaseChatItemView {

    @BindView(R2.id.tv_evaluation_person)
    TextView mPersonTv;
    @BindView(R2.id.ll_content)
    View mContentLayout;
    TextView mScoreTv;

    public ChatItemEvaluationCardView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_evaluation_card_sender
                : R.layout.view_chat_item_evaluation_card_received;
    }

    @Override
    protected void onFindViewById() {
        ButterKnife.bind(this);
        mScoreTv = findViewById(R.id.tv_evaluation_score);
    }

    @Override
    public void setViewData(List<MsgDbEntity> dataList, int position) {
        super.setViewData(dataList, position);

        final MsgDbEntity msgDbEntity = dataList.get(position);
        String jsonStr = msgDbEntity.getJsonString();
        EvaluationEntity evaluationEntity = new Gson().fromJson(jsonStr, EvaluationEntity.class);
        final EvaluationBean evaluationBean = evaluationEntity.getEvaluation();
        mPersonTv.setText(String.format("测评人：%s", evaluationBean.getEvaluationUserName()));
        if (mIsRight) {
            mScoreTv.setText(String.format("分数：%s", evaluationBean.getScore()));
        }
        mContentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.format("/link?url=%s/h5/ih2/#/evaluation/result/%s?_opentype=%s",
                        ModuleIMBusinessManager.INSTANCE.getBusinessService().getWebEnv(), evaluationBean.getId(), getOpenType());
                RouterUtil.startActivity(v.getContext(), url);
            }
        });
    }

    private String getOpenType() {
        return ModuleIMBusinessManager.INSTANCE.isDebug() ? "test" : "prod";
    }

    @Override
    protected boolean isShowJsonAvatar() {
        return true;
    }
}
