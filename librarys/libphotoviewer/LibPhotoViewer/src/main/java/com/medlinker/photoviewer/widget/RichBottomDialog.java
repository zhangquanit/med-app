package com.medlinker.photoviewer.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.medlinker.photoviewer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 1.0
 * @description 公共的底部条形窗口，参考PhotoPagerAdapter 117 长按图片
 * @time 2016/4/14 17:53
 */
public class RichBottomDialog {
    public static final String RICH_BOTTOM_DIALOG = "rich_bottom_dialog";
    private FragmentManager mFragmentManager;
    private List<DialogViewItem> dialogViewItems;
    private Context context;
    private BottomDialogFragment dialogFragment;
    private LinearLayout rootView;

    public RichBottomDialog(FragmentManager fm, Context context) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(RICH_BOTTOM_DIALOG);
        // 如果有则移除
        if (prev != null) {
            ft.remove(prev);
            ft.commit();
        }
        mFragmentManager = fm;
        this.context = context;

    }

    public void show() {
        dialogFragment.setInitViewListener(new BottomDialogFragment.InitViewListener() {
            @Override
            public void setupView(View view) {
                rootView = view.findViewById(R.id.ll_root);
                setDialogView(rootView);
            }
        });
        dialogFragment.show(mFragmentManager, RICH_BOTTOM_DIALOG);
    }

    public View getDialogViewAtPosiont(int positon) {
        if (rootView != null) {
            return rootView.getChildAt(positon);
        }
        return null;
    }

    public DialogViewItem getDialogDataAtPosiont(int positon) {
        if (dialogViewItems.size() > positon) {
            return dialogViewItems.get(positon);
        }
        throw new ArrayIndexOutOfBoundsException("positon = " + positon);
    }

    private void setDialogFragment(BottomDialogFragment dialogFragment) {
        this.dialogFragment = dialogFragment;
    }

    public BottomDialogFragment getDialogFragment() {
        return dialogFragment;
    }

    private void setDialogViewItems(List<DialogViewItem> dialogViewItems) {
        this.dialogViewItems = dialogViewItems;
    }

    public void setDialogView(LinearLayout rootView) {
        if (dialogViewItems == null || dialogViewItems.size() <= 0) {
            return;
        }
        rootView.removeAllViews();
        int size = dialogViewItems.size();
        for (int i = 0; i < size; i++) {
            final int index = i;
            DialogViewItem dialogViewItem = dialogViewItems.get(i);
            String strItem = dialogViewItem.name;
            boolean isVisible = dialogViewItem.isVisible;
            final DialogViewItem.OnViewItemClickListener listener = dialogViewItem.itemClickListener;
            View view = LayoutInflater.from(context).inflate(R.layout.view_bottom_textview, null);
            LinearLayout linearLayout = view.findViewById(R.id.ll_root);
            TextView textView = view.findViewById(R.id.text);
            textView.setText(strItem);
            linearLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(index);
                    }
                    dialogFragment.dismiss();
                }
            });
            rootView.addView(view);
        }
    }

    public static class Builder {
        // Required
        private Context context;
        private FragmentManager fm;
        private List<DialogViewItem> dialogViewItems;

        public Builder(FragmentManager fm, Context context) {
            this.fm = fm;
            this.context = context;
        }

        public Builder addViewItem(String strItem, DialogViewItem.OnViewItemClickListener listener) {
            addViewItem(new DialogViewItem(strItem, listener));
            return this;
        }

        public Builder addViewItem(DialogViewItem dialogViewItem) {
            if (dialogViewItems == null) {
                dialogViewItems = new ArrayList<DialogViewItem>();
            }
            dialogViewItems.add(dialogViewItem);
            return this;
        }

        public RichBottomDialog build() {
            RichBottomDialog richBottomDialog = new RichBottomDialog(fm, context);
            richBottomDialog.setDialogViewItems(dialogViewItems);
            BottomDialogFragment dialogFragment = BottomDialogFragment.newInstance(R.layout.view_linearlayout);
            richBottomDialog.setDialogFragment(dialogFragment);

            return richBottomDialog;
        }
    }
}
