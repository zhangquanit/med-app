package com.medlinker.widget.dialog;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.medlinker.widget.R;
import com.medlinker.widget.bean.dialog.IMLWheelBean;
import com.medlinker.widget.bean.dialog.MLWheelBean;
import com.medlinker.widget.dialog.base.MLBaseBottomDialogFragment;
import com.medlinker.widget.dialog.constant.MLDialogConstant;
import com.medlinker.widget.dialog.listener.MLOnWheelClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 底部Wheel选择项弹窗,轮子滚动
 *
 * @author gengyunxiao
 * @time 2021/4/22
 */
public class MLBottomWheelDialog extends MLBaseBottomDialogFragment {


    private TextView titleTv, cancelTv, confirmTv;
    private WheelView wheelView;


    private String mTitle;
    private String mCancel;
    private String mConfirm;
    private int mOperatorTextColor;
    private boolean setCyclic = false;//是否可以循环
    private boolean mShowTitle = true;//是否显示title
    private ArrayList<IMLWheelBean> dataList = new ArrayList<>();

    private MLOnWheelClickListener mMlOnWheelClickListener;

    private int mSelectIndex = 0;



    @Override
    public int getDialogLayoutId() {
        return R.layout.dialog_bottom_wheel;
    }

    @Override
    public void setupViews(View contentView) {

        titleTv = contentView.findViewById(R.id.dialog_tv_title);
        cancelTv = contentView.findViewById(R.id.dialog_tv_cancel);
        confirmTv = contentView.findViewById(R.id.dialog_tv_confirm);
        wheelView = contentView.findViewById(R.id.dialog_bottom_wv_wheel);

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
        titleTv.setVisibility(isShowTitle() ? View.VISIBLE : View.INVISIBLE);

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
                if (mMlOnWheelClickListener != null) {
                    mMlOnWheelClickListener.onConfirmClick(v, mSelectIndex, dataList.get(mSelectIndex).getPickerViewText());
                    dismissDialog();
                }
            }
        });

        //是否循环展示
        wheelView.setCyclic(setCyclic);

        wheelView.setCurrentItem(mSelectIndex);

        //处理Wheel线
        Resources resources = getContext().getResources();
        wheelView.setDividerColor(resources.getColor(R.color.ui_line));
        wheelView.setDividerWidth(resources.getDimensionPixelSize(R.dimen.line_h_height));
        wheelView.setLineSpacingMultiplier(MLDialogConstant.WHEEL_LINE_SPACING_MULTIPLIER);

        //字体
        wheelView.setTypeface(MLDialogConstant.WHEEL_TYPEFACE_FONT);
        wheelView.setTextSize(MLDialogConstant.WHEEL_TEXT_SIZE);
        wheelView.setTextColorCenter(resources.getColor(R.color.dialog_wheel_text_center));
        wheelView.setTextColorOut(resources.getColor(R.color.dialog_wheel_text_out));
        wheelView.setAlphaGradient(MLDialogConstant.WHEEL_IS_ALPHA_GRADIENT);
        wheelView.setItemsVisibleCount(MLDialogConstant.WHEEL_ITEMS_VISIBLE_COUNT);


        wheelView.setAdapter(new ArrayWheelAdapter(dataList));

        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mSelectIndex = index;
            }
        });

    }

    @Override
    public void showDialog(FragmentManager manager) {
        show(manager, "WheelDialog");
    }

    /**
     * 创建
     *
     * @return
     */
    public static MLBottomWheelDialog newInstance() {
        return new MLBottomWheelDialog();
    }



    /**
     * 标题栏
     *
     * @param title
     * @return
     */
    public MLBottomWheelDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * 设置取消键文案，默认为'取消'
     *
     * @param cancel
     * @return
     */
    public MLBottomWheelDialog setCancelText(String cancel) {
        this.mCancel = cancel;
        return this;
    }

    /**
     * 设置确定键文案，默认为'确定'
     *
     * @param confirm
     * @return
     */
    public MLBottomWheelDialog setConfirmText(String confirm) {
        this.mConfirm = confirm;
        return this;
    }

    /**
     * 设置确认取消按钮的字体颜色
     *
     * @param colorResId
     * @return
     */
    public MLBottomWheelDialog setOperatorTextColor(int colorResId) {
        this.mOperatorTextColor = colorResId;
        return this;
    }

    /**
     * 设置是否显示title
     *
     * @param showTitle
     * @return
     */
    public MLBottomWheelDialog setShowTitle(boolean showTitle) {
        this.mShowTitle = showTitle;
        return this;
    }

    /**
     * 设置选中的位置
     *
     * @param index
     * @return
     */
    public MLBottomWheelDialog setSelectIndex(int index) {
        this.mSelectIndex = index;
        return this;
    }

    /**
     * 是否可以循环
     *
     * @param setCyclic
     * @return
     */
    public MLBottomWheelDialog setCyclic(boolean setCyclic) {
        this.setCyclic = setCyclic;
        return this;
    }

    /**
     * 设置选中监听
     *
     * @param mlOnWheelClickListener
     * @return
     */
    public MLBottomWheelDialog setOnWheelClickListener(MLOnWheelClickListener mlOnWheelClickListener) {
        this.mMlOnWheelClickListener = mlOnWheelClickListener;
        return this;
    }

    /**
     * 添加数据
     *
     * @param items
     * @return
     */
    public MLBottomWheelDialog setItems(String... items) {
        if (items != null && items.length > 0) {
            for (String item : items) {
                this.dataList.add(MLWheelBean.build(item));
            }
        }
        return this;
    }

    /**
     * 添加数据
     *
     * @param items
     * @param <T>
     * @return
     */
    public <T extends IMLWheelBean> MLBottomWheelDialog setItems(List<T> items) {
        if (items != null && items.size() > 0)
            this.dataList.addAll(items);
        return this;
    }

    public boolean isShowTitle() {
        return mShowTitle;
    }
}
