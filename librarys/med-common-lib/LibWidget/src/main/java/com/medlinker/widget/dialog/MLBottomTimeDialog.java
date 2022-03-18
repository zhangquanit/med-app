package com.medlinker.widget.dialog;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.bigkoo.pickerview.listener.ISelectTimeCallback;
import com.bigkoo.pickerview.view.WheelTime;
import com.contrarywind.view.WheelView;
import com.medlinker.widget.R;
import com.medlinker.widget.dialog.base.MLBaseBottomDialogFragment;
import com.medlinker.widget.dialog.constant.MLDialogConstant;
import com.medlinker.widget.dialog.listener.MLOnTimeClickListener;
import com.medlinker.widget.dialog.view.MLWheelTime;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 底部时间选择器
 *
 * @author gengyunxiao
 * @time 2021/4/23
 */
public class MLBottomTimeDialog extends MLBaseBottomDialogFragment {


    private MLWheelTime wheelTime; //自定义控件

    private LinearLayout timePickerView;

    private TextView cancelTv, confirmTv, titleTv;


    private String mCancel, mConfirm, mTitle;
    private int mOperatorTextColor;

    //time picker
    private boolean[] mType = new boolean[]{true, true, true, false, false, false};//显示类型，默认显示： 年月日

    private Date mSelectedDate;
    private Calendar mCurrentDate;//当前选中时间
    private Calendar mStartDate;//开始时间
    private Calendar mEndDate;//终止时间
    public int startYear;//开始年份
    public int endYear;//结尾年份

    private MLOnTimeClickListener mlOnTimeClickListener;

    private boolean cyclic = false;//是否循环
    private boolean isLunarCalendar = false;//是否显示农历

    private String label_year, label_month, label_day, label_hours, label_minutes, label_seconds;//单位
    private int x_offset_year, x_offset_month, x_offset_day, x_offset_hours, x_offset_minutes, x_offset_seconds;//单位
    //-----
    private int textGravity = Gravity.CENTER;

    private boolean cancelable = true;//是否能取消
    private boolean isCenterLabel = false;//是否只显示中间的label,默认每个item都显示
    private WheelView.DividerType dividerType = WheelView.DividerType.FILL;//分隔线类型


    public MLBottomTimeDialog() {
        //super(context);
    }

    @Override
    public int getDialogLayoutId() {
        return R.layout.dialog_bottom_time;
    }

    @Override
    public void setupViews(View contentView) {
        cancelTv = contentView.findViewById(R.id.dialog_tv_cancel);
        confirmTv = contentView.findViewById(R.id.dialog_tv_confirm);
        titleTv = contentView.findViewById(R.id.dialog_tv_title);
        timePickerView = contentView.findViewById(R.id.timepicker);
    }

    @Override
    public void updateViews() {

        if (!TextUtils.isEmpty(mCancel)) {
            cancelTv.setText(mCancel);
        }
        if (!TextUtils.isEmpty(mConfirm)) {
            confirmTv.setText(mConfirm);
        }
        if (!TextUtils.isEmpty(mTitle)) {
            titleTv.setText(mTitle);
        }
        if (mOperatorTextColor != 0) {
            confirmTv.setTextColor(ContextCompat.getColor(getContext(), mOperatorTextColor));
            cancelTv.setTextColor(ContextCompat.getColor(getContext(), mOperatorTextColor));
        }

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelClickListener != null) {
                    mCancelClickListener.onClick(v);
                }
                dismissDialog();
            }
        });

        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlOnTimeClickListener != null) {
                    mlOnTimeClickListener.onConfirmClick(mSelectedDate, v);
                    dismissDialog();
                }
            }
        });

        initWheelTime(timePickerView);
    }

    @Override
    public void showDialog(FragmentManager manager) {
        show(manager, "TimeDialog");
    }

    public void showDialog(FragmentManager manager, String tag) {
        show(manager, tag);
    }

    private void initWheelTime(LinearLayout timePickerView) {
        wheelTime = new MLWheelTime(timePickerView, mType, textGravity, MLDialogConstant.WHEEL_TEXT_SIZE);
        wheelTime.setSelectChangeCallback(new ISelectTimeCallback() {
            @Override
            public void onTimeSelectChanged() {
                try {
                    Date date = WheelTime.dateFormat.parse(wheelTime.getTime());
                    mSelectedDate = date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        wheelTime.setLunarMode(isLunarCalendar);

        if (startYear != 0 && endYear != 0
                && startYear <= endYear) {
            dealRange();
        }

        //若手动设置了时间范围限制
        if (mStartDate != null && mEndDate != null) {
            if (mStartDate.getTimeInMillis() > mEndDate.getTimeInMillis()) {
                throw new IllegalArgumentException("startDate can't be later than endDate");
            } else {
                dealRangDate();
            }
        } else if (mStartDate != null) {
            if (mStartDate.get(Calendar.YEAR) < 1900) {
                throw new IllegalArgumentException("The startDate can not as early as 1900");
            } else {
                dealRangDate();
            }
        } else if (mEndDate != null) {
            if (mEndDate.get(Calendar.YEAR) > 2100) {
                throw new IllegalArgumentException("The endDate should not be later than 2100");
            } else {
                dealRangDate();
            }
        } else {//没有设置时间范围限制，则会使用默认范围。
            dealRangDate();
        }

        setTime();
        wheelTime.setLabels(label_year, label_month, label_day
                , label_hours, label_minutes, label_seconds);
        wheelTime.setTextXOffset(x_offset_year, x_offset_month, x_offset_day,
                x_offset_hours, x_offset_minutes, x_offset_seconds);

        setCanceledOnTouchOutside(cancelable);
        wheelTime.setCyclic(cyclic);


        //处理Wheel线
        Resources resources = getContext().getResources();
        wheelTime.setDividerColor(resources.getColor(R.color.ui_line));
        wheelTime.setDividerWidth(resources.getDimensionPixelSize(R.dimen.line_h_height));
        wheelTime.setDividerType(dividerType);
        wheelTime.setLineSpacingMultiplier(MLDialogConstant.WHEEL_LINE_SPACING_MULTIPLIER);

        //字体
        wheelTime.setTypeface(MLDialogConstant.WHEEL_TYPEFACE_FONT);
        wheelTime.setTextColorCenter(resources.getColor(R.color.dialog_wheel_text_center));
        wheelTime.setTextColorOut(resources.getColor(R.color.dialog_wheel_text_out));
        wheelTime.setItemsVisible(MLDialogConstant.WHEEL_ITEMS_VISIBLE_COUNT);
        wheelTime.setAlphaGradient(MLDialogConstant.WHEEL_IS_ALPHA_GRADIENT);
        wheelTime.isCenterLabel(isCenterLabel);

        mSelectedDate = mCurrentDate.getTime();
    }


    public static MLBottomTimeDialog newInstance() {
        return new MLBottomTimeDialog();
    }

    public MLBottomTimeDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public MLBottomTimeDialog setCancel(String cancel) {
        this.mCancel = cancel;
        return this;
    }

    public MLBottomTimeDialog setConfirm(String confirm) {
        this.mConfirm = confirm;
        return this;
    }

    /**
     * 设置确认取消按钮的字体颜色
     *
     * @param colorResId
     * @return
     */
    public MLBottomTimeDialog setOperatorTextColor(int colorResId) {
        this.mOperatorTextColor = colorResId;
        return this;
    }

    /**
     * 是否可以循环
     *
     * @param setCyclic
     * @return
     */
    public MLBottomTimeDialog setCyclic(boolean setCyclic) {
        this.cyclic = setCyclic;
        return this;
    }

    /**
     * new boolean[]{true, true, true, false, false, false}
     * control the "year","month","day","hours","minutes","seconds " display or hide.
     * 分别控制“年”“月”“日”“时”“分”“秒”的显示或隐藏。
     *
     * @param type 布尔型数组，长度需要设置为6。
     * @return TimePickerBuilder
     */
    public MLBottomTimeDialog setType(boolean[] type) {
        this.mType = type;
        return this;
    }


    /**
     * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
     *
     * @param date
     * @return MLBottomTimeDialog
     */
    public MLBottomTimeDialog setDate(Calendar date) {
        this.mCurrentDate = date;
        return this;
    }

    /**
     * 设置起始时间
     * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
     */

    public MLBottomTimeDialog setRangDate(Calendar startDate, Calendar endDate) {
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        return this;
    }

    public MLBottomTimeDialog setOnTimeClickListener(MLOnTimeClickListener mlOnTimeClickListener) {
        this.mlOnTimeClickListener = mlOnTimeClickListener;
        return this;
    }

    /**
     * 目前暂时只支持设置1900 - 2100年
     *
     * @param lunar 农历的开关
     */
    public MLBottomTimeDialog setLunarCalendar(boolean lunar) {
        try {
            int year, month, day, hours, minute, seconds;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(WheelTime.dateFormat.parse(wheelTime.getTime()));
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);

            wheelTime.setLunarMode(lunar);
            wheelTime.setLabels(label_year, label_month, label_day,
                    label_hours, label_minutes, label_seconds);
            wheelTime.setPicker(year, month, day, hours, minute, seconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean isLunarCalendar() {
        return wheelTime.isLunarMode();
    }


    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private void dealRange() {
        wheelTime.setStartYear(startYear);
        wheelTime.setEndYear(endYear);

    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private void dealRangDate() {
        wheelTime.setRangDate(mStartDate, mEndDate);
        initDefaultSelectedDate();
    }

    private void initDefaultSelectedDate() {
        //如果手动设置了时间范围
        if (mStartDate != null && mEndDate != null) {
            //若默认时间未设置，或者设置的默认时间越界了，则设置默认选中时间为开始时间。
            if (mCurrentDate == null || mCurrentDate.getTimeInMillis() < mStartDate.getTimeInMillis()
                    || mCurrentDate.getTimeInMillis() > mEndDate.getTimeInMillis()) {
                mCurrentDate = mStartDate;
            }
        } else if (mStartDate != null) {
            //没有设置默认选中时间,那就拿开始时间当默认时间
            mCurrentDate = mStartDate;
        } else if (mEndDate != null) {
            mCurrentDate = mEndDate;
        }
    }

    /**
     * 设置选中时间,默认选中当前时间
     */
    private void setTime() {
        int year, month, day, hours, minute, seconds;
        Calendar calendar = Calendar.getInstance();

        if (mCurrentDate == null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
            mCurrentDate = calendar;
        } else {
            year = mCurrentDate.get(Calendar.YEAR);
            month = mCurrentDate.get(Calendar.MONTH);
            day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
            hours = mCurrentDate.get(Calendar.HOUR_OF_DAY);
            minute = mCurrentDate.get(Calendar.MINUTE);
            seconds = mCurrentDate.get(Calendar.SECOND);
        }

        wheelTime.setPicker(year, month, day, hours, minute, seconds);
    }


}
