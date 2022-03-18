package com.medlinker.widget.dialog.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.medlinker.widget.R;
import com.medlinker.widget.bean.dialog.IMLActionBean;
import com.medlinker.widget.dialog.base.MLBaseBottomDialogFragment;
import com.medlinker.widget.dialog.listener.MLOnActionClickListener;

import java.util.List;

/**
 * Des
 *
 * @author gengyunxiao
 * @time 2021/4/21
 */
public class MLActionDialogAdapter extends MLDialogAdapter<IMLActionBean> {

    private int oldSelectIndex = -1;
    private int[] noClickIndex;

    private MLBaseBottomDialogFragment bottomActionDialog;

    private MLOnActionClickListener mdfActionItemClickListener;

    public MLActionDialogAdapter(Context context, MLBaseBottomDialogFragment bottomActionDialog, List<IMLActionBean> dataList) {
        super(context, dataList, R.layout.dialog_item_bottom_action);
        this.bottomActionDialog = bottomActionDialog;
    }

    @Override
    protected void dealItem(MLViewHolder viewHolder, IMLActionBean mdfActionBean, final int position) {

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdfActionItemClickListener != null) {
                    IMLActionBean actionBean = mDataList.get(position);
                    //排除不可点击和已选的重复选择
                    if (!(actionBean.isActionItemNoClicked() || actionBean.isActionItemSelected())) {
                        String actionItemTitle = actionBean.getActionItemTitle();
                        mdfActionItemClickListener.onItemClick(v, position, actionItemTitle);
                        oldSelectIndex = position;
                        if (bottomActionDialog != null) {
                            bottomActionDialog.dismissDialog();
                        }
                    }
                }
            }
        });

        TextView titleTv = viewHolder.getView(R.id.dialog_bottom_item_tv);

        titleTv.setText(TextUtils.isEmpty(mdfActionBean.getActionItemTitle()) ? "无" : mdfActionBean.getActionItemTitle());

        if (mdfActionBean.isActionItemNoClicked()) {
            viewHolder.itemView.setEnabled(false);
            titleTv.setTextColor(mContext.getResources().getColor(R.color.ui_enable_false));
        } else if (mdfActionBean.isActionItemSelected()) {
            viewHolder.itemView.setEnabled(false);
            titleTv.setTextColor(mContext.getResources().getColor(R.color.ui_blue));
        } else {
            viewHolder.itemView.setEnabled(true);
            titleTv.setTextColor(mContext.getResources().getColor(R.color.ui_black1));
        }

        if (position == mDataList.size() - 1) {
            viewHolder.setVisible(R.id.dialog_bottom_item_line, View.GONE);
        } else {
            viewHolder.setVisible(R.id.dialog_bottom_item_line, View.VISIBLE);
        }
    }


    public void startWork() {
        dealNoClickData();
        if (oldSelectIndex < mDataList.size() && oldSelectIndex >= 0) {
            IMLActionBean actionBean = mDataList.get(oldSelectIndex);
            if (!actionBean.isActionItemNoClicked())
                actionBean.setActionItemSelected(true);
        }
        notifyDataSetChanged();
    }

    private void dealNoClickData() {
        if (noClickIndex != null && noClickIndex.length > 0) {
            for (int index : noClickIndex) {
                if (index < mDataList.size() && index >= 0) {
                    IMLActionBean mdfActionBean = mDataList.get(index);
                    mdfActionBean.setActionItemNoClicked(true);
                }
            }
        }
    }

    public void setMdfActionItemClickListener(MLOnActionClickListener mdfActionItemClickListener) {
        this.mdfActionItemClickListener = mdfActionItemClickListener;
    }

    public void setInitSelectIndex(int index) {
        this.oldSelectIndex = index;
    }


    public void setNoClickIndex(int... index) {
        noClickIndex = index;
    }


}
