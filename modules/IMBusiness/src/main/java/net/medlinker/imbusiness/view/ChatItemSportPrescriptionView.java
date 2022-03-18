package net.medlinker.imbusiness.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.medlinker.baseapp.ApiPath;
import com.medlinker.lib.utils.MedTimeUtil;
import com.medlinker.router.MedRouterHelper;

import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.entity.prescription.PrescriptEntity;
import net.medlinker.imbusiness.entity.prescription.SportPrescriptBean;
import net.medlinker.imbusiness.entity.prescription.SportPrescription;
import net.medlinker.imbusiness.entity.prescription.SportPrescriptionEntity;

import java.net.URLEncoder;
import java.util.List;

/**
 * @author hmy
 * @time 12/6/21 09:42
 */
public class ChatItemSportPrescriptionView extends BaseChatItemView {

    private TextView mDetailTv;
    private TextView mDateTv;
    private TextView mInfoTv;
    private View mContentView;
    private View mStatusView;

    public ChatItemSportPrescriptionView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_medicine_sport_sender
                : R.layout.view_chat_item_medicine_sport_received;
    }

    @Override
    protected void onFindViewById() {
        mDetailTv = findViewById(R.id.tv_detail);
        mDateTv = findViewById(R.id.tv_date);
        mInfoTv = findViewById(R.id.tv_resume_time);
        mContentView = findViewById(R.id.ll_content);
        mStatusView = findViewById(R.id.tv_status);
    }

    @Override
    public void setViewData(List<MsgDbEntity> dataList, int position) {
        super.setViewData(dataList, position);
        final MsgDbEntity msgDbEntity = dataList.get(position);
        try {
            String json = msgDbEntity.getJsonString();
            SportPrescriptionEntity prescriptionEntity = new Gson().fromJson(json, SportPrescriptionEntity.class);
            SportPrescriptBean entity = prescriptionEntity.getPrescription();
            if (entity != null) {
                mContentView.setOnClickListener(v -> {
                    String url = String.format("%s/ih-v2/orthopaedics/prescription-detail?prTransNo=%s", ApiPath.INSTANCE.getH5Host(), entity.getPrTransNo());
                    MedRouterHelper.withUrl(String.format("/link?url=%s", URLEncoder.encode(url))).queryTarget().navigation(getContext());
                });
                mDetailTv.setText(entity.getDiagnose());
                mInfoTv.setText(entity.getRehabilitationTrainingCycle());
                PrescriptEntity prescriptEntity = entity.getEntity();
                if (null != prescriptEntity) {
                    mDateTv.setText(MedTimeUtil.formatCnYearMonthDay(prescriptEntity.getSports().getSurgery().getTime()));
                } else {
                    mDateTv.setText(null);
                }

                //处方已失效
                SportPrescription prescription = entity.getPrescription();
                if (null != prescription && null != prescription.getStatusEntity()) {
                    int status = prescription.getStatusEntity().getPrStatus();
                    mStatusView.setVisibility(status == 41 ? View.VISIBLE : View.GONE);
                } else {
                    mStatusView.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
