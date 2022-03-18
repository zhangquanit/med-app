package com.medlinker.widget.dialog;

import android.view.View;
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
 * 底部选择项弹窗（带'取消'文字），点击选择选项
 *
 * @author gengyunxiao
 * @time 2021/4/21
 */
public class MLBottomDoDialog extends MLBaseBottomDialogFragment {

    private ArrayList<IMLActionBean> dataList = new ArrayList<>();

    private MLActionDialogAdapter actionDialogAdapter;

    private RecyclerView doRecyclerView;
    private TextView cancelTv;

    private int mSelectIndex = -1;
    private int[] mNoClickIndex;
    private MLOnActionClickListener mMlOnActionClickListener;


    /**
     * 创建
     *
     * @return
     */
    public static MLBottomDoDialog newInstance() {
        return new MLBottomDoDialog();
    }


    /**
     * 添加数据
     *
     * @param titles
     * @return
     */
    public MLBottomDoDialog setItems(String... titles) {
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
    public <T extends IMLActionBean> MLBottomDoDialog setItems(List<T> titles) {
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
    public MLBottomDoDialog setSelectIndex(int index) {
        this.mSelectIndex = index;
        return this;
    }

    /**
     * 设置不可选中的数据，用于展示，不可选中
     *
     * @param index
     * @return
     */
    public MLBottomDoDialog setNoClickIndex(int... index) {
        this.mNoClickIndex = index;
        return this;
    }


    /**
     * 设置选中监听
     *
     * @param onActionClickListener
     * @return
     */
    public MLBottomDoDialog setOnActionClickListener(MLOnActionClickListener onActionClickListener) {
        this.mMlOnActionClickListener = onActionClickListener;
        return this;
    }


    @Override
    public int getDialogLayoutId() {
        return R.layout.dialog_bottom_do;
    }

    @Override
    public void setupViews(View contentView) {
        cancelTv = contentView.findViewById(R.id.dialog_tv_cancel);
        doRecyclerView = contentView.findViewById(R.id.dialog_bottom_rv_action);
    }

    @Override
    public void updateViews() {

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelClickListener != null) {
                    mCancelClickListener.onClick(v);
                }
                dismissDialog();
            }
        });

        initRecyclerView();

        actionDialogAdapter.startWork();

    }

    @Override
    public void showDialog(FragmentManager manager) {
        show(manager, "DoDialog");
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
