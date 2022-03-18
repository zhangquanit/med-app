package net.medlinker.imbusiness.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.google.gson.Gson;
import com.medlinker.lib.utils.MedInputMethodUtil;
import com.medlinker.lib.utils.MedToastUtil;
import com.medlinker.network.retrofit.error.ErrorConsumer;

import net.medlinker.base.entity.DataEntity;
import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.util.SchedulersCompat;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.R2;
import net.medlinker.imbusiness.entity.MsgReservationJsonEntity;
import net.medlinker.imbusiness.network.ApiManager;
import net.medlinker.libhttp.BaseEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * 电话、视频预约
 *
 * @author hmy
 * @time 2020/8/21 16:55
 */
public class ChatItemReservationView extends BaseChatItemView {

    @BindView(R2.id.tv_title)
    TextView mTitleTv;
    @BindView(R2.id.tv_reservation_time)
    TextView mTimeTv;
    @BindView(R2.id.tv_call_duration)
    TextView mTimeIntervalTv;

    public ChatItemReservationView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_reservation_sender : R.layout.view_chat_item_reservation_received;
    }

    @Override
    protected void onFindViewById() {
        ButterKnife.bind(this);
        findViewById(R.id.tv_label).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String transNo = (String) getTag();
                showTimePicker(transNo);
            }
        });
    }

    private void showTimePicker(final String transNo) {
        MedInputMethodUtil.hintInputMethod((Activity) getContext());
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MINUTE, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 10);
        new TimePickerBuilder(getContext(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                updateTime(transNo, date.getTime());
            }
        })
                .setType(new boolean[]{true, true, true, true, true, false})
                .setRangDate(startDate, endDate)
                .build()
                .show();
    }

    @SuppressLint("CheckResult")
    private void updateTime(String transNo, long time) {
        Calendar curCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        if (calendar.getTimeInMillis() <= curCalendar.getTimeInMillis()) {
            MedToastUtil.showMessage("选择时间必须大于当前时间");
            return;
        }
        ApiManager.getInquiryWebApi().updateReservationTime(time / 1000, transNo)
                .compose(SchedulersCompat.<BaseEntity<DataEntity>>applyIoSchedulers())
                .map(new HttpResultFunc<>())
                .subscribe(new Consumer<DataEntity>() {
                    @Override
                    public void accept(DataEntity dataEntity) throws Exception {
                        MedToastUtil.showMessage("修改成功");
                    }
                }, new ErrorConsumer());
    }

    @Override
    public void setViewData(List<MsgDbEntity> mDataList, int position) {
        super.setViewData(mDataList, position);

        MsgDbEntity msgDbEntity = mDataList.get(position);
        MsgReservationJsonEntity entity = new Gson().fromJson(msgDbEntity.getJsonString(), MsgReservationJsonEntity.class);
        MsgReservationJsonEntity.MsgReservationJsonBean bean = mIsRight ? entity.getSelf() : entity.getOther();
        mTitleTv.setText(bean.getTitle());
        mTimeTv.setText(String.format("预约时间：%s", bean.getAppointTimeText()));
        mTimeIntervalTv.setText(String.format("通话总时长：%s", bean.getDuration()));
        setTag(bean.getTransNo());
    }

    @Override
    protected boolean isShowMsgSendStatus(MsgDbEntity message) {
        return true;
    }

    @Override
    protected boolean isShowJsonAvatar() {
        return true;
    }
}
