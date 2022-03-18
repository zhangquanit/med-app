package com.medlinker.widget.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medlinker.widget.R;
import com.medlinker.widget.bean.dialog.IMLActionBean;
import com.medlinker.widget.bean.dialog.MLActionBean;
import com.medlinker.widget.dialog.adapter.MLActionDialogAdapter;
import com.medlinker.widget.dialog.base.MLBaseBottomDialogFragment;
import com.medlinker.widget.dialog.listener.MLOnActionClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 底部选择项弹窗（带删除Icon和标题），点击选择选项
 *
 * @author gengyunxiao
 * @time 2021/4/21
 */
public class MLBottomActionDialog extends MLBaseBottomDialogFragment {

    private ArrayList<IMLActionBean> dataList = new ArrayList<>();

    private MLActionDialogAdapter actionDialogAdapter;

    private TextView titleTv;
    private RecyclerView doRecyclerView;
    private ImageView cancelIv;

    private int mSelectIndex = -1;
    private int[] mNoClickIndex;
    private MLOnActionClickListener mMlOnActionClickListener;
    private String mTitle;

    /**
     * 创建
     *
     * @return
     */
    public static MLBottomActionDialog newInstance() {
        return new MLBottomActionDialog();
    }

    /**
     * 设置弹窗标题
     *
     * @param title
     * @return
     */
    public MLBottomActionDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * 添加数据
     *
     * @param titles
     * @return
     */
    public MLBottomActionDialog setItems(String... titles) {
        if (titles != null && titles.length > 0) {
            for (String title : titles) {
                dataList.add(MLActionBean.build(title));
            }
        }
        return this;
    }

    /**
     * 添加数据
     *
     * @param titles
     * @return
     */
    public <T extends IMLActionBean> MLBottomActionDialog setItems(List<T> titles) {
        if (titles != null && titles.size() > 0) {
            dataList.addAll(titles);
        }
        return this;
    }

    /**
     * 设置已经选中的选项，用于展示已选中的Ui
     *
     * @param index
     * @return
     */
    public MLBottomActionDialog setSelectIndex(int index) {
        this.mSelectIndex = index;
        return this;
    }

    /**
     * 设置不可选中的数据，用于展示，不可选中
     *
     * @param index
     * @return
     */
    public MLBottomActionDialog setNoClickIndex(int... index) {
        this.mNoClickIndex = index;
        return this;
    }


    /**
     * 设置选中监听
     *
     * @param onActionClickListener
     * @return
     */
    public MLBottomActionDialog setOnActionClickListener(MLOnActionClickListener onActionClickListener) {
        this.mMlOnActionClickListener = onActionClickListener;
        return this;
    }

    @Override
    public int getDialogLayoutId() {
        return R.layout.dialog_bottom_action;
    }

    @Override
    public void setupViews(View contentView) {
        titleTv = contentView.findViewById(R.id.dialog_tv_title);
        cancelIv = contentView.findViewById(R.id.dialog_bottom_iv_close);
        doRecyclerView = contentView.findViewById(R.id.dialog_bottom_rv_action);
    }

    @Override
    public void updateViews() {

        cancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelClickListener != null) {
                    mCancelClickListener.onClick(v);
                }
                dismissDialog();
            }
        });

        if (!TextUtils.isEmpty(mTitle)) {
            titleTv.setText(mTitle);
        }

        initRecyclerView();

        actionDialogAdapter.startWork();

    }

    @Override
    public void showDialog(FragmentManager manager) {

        show(manager, "ActionDialog");
    }

    private void initRecyclerView() {
        doRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        actionDialogAdapter = new MLActionDialogAdapter(getContext(), this, dataList);

        actionDialogAdapter.setInitSelectIndex(mSelectIndex);
        actionDialogAdapter.setNoClickIndex(mNoClickIndex);
        actionDialogAdapter.setMdfActionItemClickListener(mMlOnActionClickListener);

        doRecyclerView.setAdapter(actionDialogAdapter);
    }


}
