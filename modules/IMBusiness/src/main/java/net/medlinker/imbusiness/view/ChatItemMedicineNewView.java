package net.medlinker.imbusiness.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.medlinker.lib.utils.MedToastUtil;

import net.medlinker.base.router.RouterUtil;
import com.medlinker.lib.utils.MedDimenUtil;

import net.medlinker.im.im.util.LogUtil;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.R2;
import net.medlinker.imbusiness.entity.prescription.DrugNewEntity;
import net.medlinker.imbusiness.entity.prescription.ElectronicBean;
import net.medlinker.imbusiness.entity.prescription.PrescriptionNewBean;
import net.medlinker.imbusiness.entity.prescription.PrescriptionNewEntity;
import net.medlinker.imbusiness.msg.MsgJsonConstants;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;
import net.medlinker.imbusiness.view.viewhelper.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author hmy
 */
public class ChatItemMedicineNewView extends BaseChatItemView {

    @BindView(R2.id.ll_medicine_content)
    LinearLayout mLlMedcineContent;
    @BindView(R2.id.tv_pre_no)
    TextView mPreNoTv;
    @BindView(R2.id.tv_pre_type)
    TextView mPreTypeTv;
    @BindView(R2.id.tv_title)
    TextView mTitleTv;
    @BindView(R2.id.btn_detail)
    TextView mLookDetailBtn;
    @BindView(R2.id.tv_rp)
    TextView mRp;

    @BindView(R2.id.ll_content)
    View mContentView;
    private Gson mTempGson;
    private ViewRecycler<View> mViewRecycler;
    private int mJsonType;

    public ChatItemMedicineNewView(Context context, boolean right) {
        super(context, right);
        mTempGson = new Gson();
        mViewRecycler = new ViewRecycler<>();
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_medcine_sender
                : R.layout.view_chat_item_medicine_received;
    }


    @Override
    protected void onFindViewById() {
        ButterKnife.bind(this);
        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Object tag = v.getTag();
                if (tag instanceof PrescriptionNewBean) {
                    final PrescriptionNewBean prescriptionBean = (PrescriptionNewBean) tag;
                    if (mJsonType == MsgJsonConstants.MSG_TYPE_JSON_USE_CHINA_MEDICAL_ADVICE
                            || mJsonType == MsgJsonConstants.MSG_TYPE_JSON_USE_WESTERN_MEDICAL_ADVICE) {
                        String url = String.format("/link?url=%s/ih/prescription/medicationSuggest?prescriptionId=%s&_opentype=%s&channel=medlinker_app",
                                ModuleIMBusinessManager.INSTANCE.getBusinessService().getWebEnv(), prescriptionBean.getElectronic().getId(), getOpenType());
                        RouterUtil.startActivity(getContext(), url);
                        return;
                    } else if (mJsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIBE_MEDICINE) {
                        List<DrugNewEntity> list = prescriptionBean.getDrugs();
                        if (list.size() > 0) {
                            //TODO
//                            String spuPackId = list.get(0).getContentId();
//                            ChatSessionViewModel viewModel = new ViewModelProvider((FragmentActivity) getContext()).get(ChatSessionViewModel.class);
//                            String doctorId = viewModel.getGroupImInfoEntity().getBusinessInfo().getDoctorInfo().getDoctorId();
//                            String url = String.format("/link?url=%s/ih/mall/drug-detail?spuPackId=%s&doctorId=%s&_opentype=%s&channel=medlinker_app",
//                                    ModuleIMBusinessManager.INSTANCE.getBusinessService().getWebEnv(), spuPackId, doctorId, getOpenType());
//                            RouterUtil.startActivity(getContext(), url);
                        }
                        return;
                    }
                    if (mJsonType == MsgJsonConstants.MSG_TYPE_JSON_REVIEW_PRESCRIPTION_NEW) {
                        String url = String.format("/link?url=%s/h5/ih2/#/new-chufang-detail/%s?_opentype=%s",
                                ModuleIMBusinessManager.INSTANCE.getBusinessService().getWebEnv(),
                                prescriptionBean.getElectronic().getId(), getOpenType());
                        RouterUtil.startActivity(getContext(), url);
                    } else {
                        String url = String.format("/link?url=%s/ih/prescription/detail?prescriptionId=%s&shouldBind=1&_opentype=%s&channel=medlinker_app",
                                ModuleIMBusinessManager.INSTANCE.getBusinessService().getWebEnv(),
                                prescriptionBean.getElectronic().getId(), getOpenType());
                        RouterUtil.startActivity(getContext(), url);
                    }
                } else {
                    MedToastUtil.showMessage(
                            getContext().getString(R.string.toast_prescription_data_error));
                }
            }
        });
    }

    private String getOpenType() {
        return ModuleIMBusinessManager.INSTANCE.isDebug() ? "test" : "prod";
    }

    @Override
    public void setViewData(List<MsgDbEntity> mDataList, int position) {
        super.setViewData(mDataList, position);

        final MsgDbEntity msgDbEntity = mDataList.get(position);
        mJsonType = msgDbEntity.getJsonType();

        PrescriptionNewEntity prescriptionEntity;
        try {
            String json = msgDbEntity.getJsonString();
            LogUtil.d("MedicineNewView", "========>setViewData:jsonType = " + mJsonType + " json = " + json);
            prescriptionEntity = mTempGson.fromJson(json, PrescriptionNewEntity.class);
        } catch (JsonSyntaxException e) {
            LogUtil.e("Pre", e.getMessage());
            return;
        }
        final ElectronicBean electronicEntity = prescriptionEntity.getPrescription()
                .getElectronic();
        mViewRecycler.cacheViews(mLlMedcineContent);
        mLlMedcineContent.removeAllViews();
        final int prType = electronicEntity.getType();
        String diagnosisStr = "";
        if (prType == MsgJsonConstants.PRESCRIPTION_TYPE_CHINESE_DRINKS) {
            //中药饮品
            addChineseDrugs(prescriptionEntity.getPrescription());
            diagnosisStr = String.format("病名：%s    证型：%s", electronicEntity.getDisease(),
                    electronicEntity.getDiagnose());
        } else {
            // 西药处方
            final List<DrugNewEntity> drugs = prescriptionEntity.getPrescription().getDrugs();
            for (DrugNewEntity drugEntity : drugs) {
                View view = getMedicineItemView();
                new ViewHelper(view).setText(R.id.tv_medicine_name, drugEntity.getTitle())
                        .setText(R.id.tv_medicine_info, "(" + drugEntity.getSpecification() + ")")
                        .setText(R.id.tv_medicine_number, drugEntity.getUsages());
                mLlMedcineContent.addView(view);
            }
            diagnosisStr = getResources()
                    .getString(R.string.pre_type, electronicEntity.getDiagnose());
        }
        if (!TextUtils.isEmpty(electronicEntity.getTransNo())) {
            mPreNoTv.setVisibility(VISIBLE);
            mPreNoTv.setText(
                    getResources().getString(R.string.pre_no, electronicEntity.getTransNo()));
        } else {
            mPreNoTv.setVisibility(GONE);
        }
        mRp.setVisibility(VISIBLE);
        mPreTypeTv.setVisibility(VISIBLE);
        if (mJsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIPTION_AGAIN_NEW) {
            mPreNoTv.setVisibility(GONE);
            mLookDetailBtn.setText(getResources().getString(R.string.txt_click_look_pre_detail));
            mTitleTv.setText(getResources().getString(R.string.title_renewal_apply));
            mPreTypeTv.setText(diagnosisStr);
        } else if (mJsonType == MsgJsonConstants.MSG_TYPE_JSON_REVIEW_PRESCRIPTION_NEW) {
            mPreNoTv.setVisibility(GONE);
            mLookDetailBtn.setText(getResources().getString(R.string.txt_click_look_pre_detail));
            mTitleTv.setText(getResources().getString(R.string.title_continued_apply));
            mPreTypeTv.setText(diagnosisStr);
        } else if (mJsonType == MsgJsonConstants.MSG_TYPE_JSON_USE_CHINA_MEDICAL_ADVICE
                || mJsonType == MsgJsonConstants.MSG_TYPE_JSON_USE_WESTERN_MEDICAL_ADVICE) {
            mTitleTv.setText(getResources().getString(R.string.txt_use_medicine_advice));
            mLookDetailBtn.setText(getResources().getString(R.string.check_detail));
            mPreNoTv.setVisibility(GONE);
            mRp.setVisibility(GONE);

            String s = mJsonType == MsgJsonConstants.MSG_TYPE_JSON_USE_CHINA_MEDICAL_ADVICE
                    ? electronicEntity.getDisease() : electronicEntity.getDiagnose();
            mPreTypeTv.setText(s);
        } else if (mJsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIBE_MEDICINE) {
            mPreNoTv.setVisibility(GONE);
            mTitleTv.setText(getResources().getString(R.string.txt_prescribe_medicine));
            mLookDetailBtn.setText(getResources().getString(R.string.txt_drug_details));
            mPreTypeTv.setVisibility(GONE);
        } else {
            mPreNoTv.setVisibility(VISIBLE);
            mTitleTv.setText(getResources().getString(R.string.title_prescription));
            mLookDetailBtn
                    .setText(getResources().getString(R.string.check_chat_type_medicine_prescription));
            mPreTypeTv.setText(diagnosisStr);
        }
        mContentView.setTag(prescriptionEntity.getPrescription());
    }

    private void addChineseDrugs(PrescriptionNewBean prescription) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, MedDimenUtil.dip2px(getContext(), 18), 0, 0);
        linearLayout.setLayoutParams(layoutParams);

        linearLayout.setOrientation(VERTICAL);
        StringBuilder drugsStr = new StringBuilder();
        for (DrugNewEntity drugEntity : prescription.getCompleteDrugs()) {
            drugsStr.append(getTitleHtml(drugEntity.getTitle(), 15))
                    .append(getDrugAmountHtml(
                            String.format(" %s&nbsp&nbsp&nbsp", drugEntity.getSpecification())));
        }
        TextView drugTv = new TextView(getContext());
        drugTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));
        drugTv.setText(Html.fromHtml("<html>" + drugsStr.toString() + "</html>", null,
                new HtmlTagHandler("myfont")));
        linearLayout.addView(drugTv);
        TextView drugWayTv = new TextView(getContext());
        drugWayTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));
        String drugWayStr = getTitleHtml("服用方法 ", 13) +
                getDrugAmountHtml(prescription.getElectronic().getStructuredUsages().getTake());
        drugWayTv.setText(Html.fromHtml("<html>" + drugWayStr + "</html>", null,
                new HtmlTagHandler("myfont")));
        linearLayout.addView(drugWayTv);

        mLlMedcineContent.addView(linearLayout);
    }

    private String getTitleHtml(String text, int textSize) {
        return String.format("<myfont size=\"%spx\" color=\"#454553\">%s</myfont>", textSize,
                text == null ? "" : text);
    }

    private String getDrugAmountHtml(String text) {
        return "<myfont size=\"12px\" color=\"#9A9AA4\">" + (text == null ? "" : text)
                + "</myfont>";
    }

    @Override
    protected void onReSendMsg(MsgDbEntity message) {
        mSender.reSendPrescription(message);
    }

    @Override
    protected boolean isShowMsgSendStatus(MsgDbEntity message) {
        return true;
    }

    @Override
    protected boolean isShowJsonAvatar() {
        return true;
    }

    private View getMedicineItemView() {
        View view = mViewRecycler.getOneView();
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.view_sub_medicine, null);
        }
        return view;
    }
}
