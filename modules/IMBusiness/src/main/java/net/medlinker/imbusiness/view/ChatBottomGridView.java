package net.medlinker.imbusiness.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cn.glidelib.GlideUtil;
import com.medlinker.lib.utils.MedDimenUtil;
import com.medlinker.widget.gvp.GVPAdapter;
import com.medlinker.widget.gvp.GridViewPager;
import com.medlinker.widget.gvp.OnItemClickListener;
import com.medlinker.widget.indicator.DotsIndicatorView;

import com.medlinker.lib.utils.MedDimenUtil;

import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.entity.ImMenuEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hmy
 * @time 2020/9/27 17:40
 */
public class ChatBottomGridView extends LinearLayout {

    private static final int MAX_GRID_HEIGHT = 160;
    private DotsIndicatorView mIndicatorView;
    private GridViewPager mViewPager;
    private ArrayList<ImMenuEntity> mList = new ArrayList<>();
    private GirdAdapter mAdapter;
    private OnChatListener mOnChatListener;

    public ChatBottomGridView(Context context) {
        this(context, null);
    }

    public ChatBottomGridView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatBottomGridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.bg_chat_bottom_grid_shape);
        mViewPager = new GridViewPager(getContext());
        mViewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mViewPager);
        mIndicatorView = new DotsIndicatorView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mIndicatorView.setLayoutParams(layoutParams);
        addView(mIndicatorView);
        mIndicatorView.setVisibility(GONE);
        mAdapter = new GirdAdapter(mList);
        mViewPager.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener<ImMenuEntity>() {
            @Override
            public void onItemClick(View view, int position, ImMenuEntity data) {
                try {
                    String jumpUrl = data.getJumpUrl();
                    if ("/photo/picker".equals(jumpUrl)) {
                        if (null != mOnChatListener && mOnChatListener.onSendMsgIntercept()) {
                            return;
                        }
                    }
                    ARouter.getInstance().build(jumpUrl).navigation(view.getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setList(ArrayList<ImMenuEntity> list) {
        mList.clear();
        if (null != list && list.size() > 0) {
            mList.addAll(list);
        }
        mAdapter.notifyDataSetChanged();

        int pageCount = mViewPager.getPageCount();
        mIndicatorView.init(pageCount);
        mIndicatorView.setVisibility(pageCount > 1 ? VISIBLE : GONE);
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        int maxHeight = MedDimenUtil.dip2px(getContext(), MAX_GRID_HEIGHT);
        mViewPager.getLayoutParams().height = mList.size() <= mViewPager.getColumnsNum() ? maxHeight / 2 : maxHeight;
        requestLayout();
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mIndicatorView.selectTo(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private static class GirdAdapter extends GVPAdapter<ImMenuEntity> {

        public GirdAdapter(List<ImMenuEntity> dataList) {
            super(dataList);
        }

        @Override
        public int getItemLayoutId() {
            return R.layout.item_chat_service;
        }

        @Override
        public void bindItemView(View itemView, int position, ImMenuEntity data) {
            ImageView iconIv = itemView.findViewById(R.id.iv_icon);
            TextView nameTv = itemView.findViewById(R.id.tv_title);

            nameTv.setText(data.getTitle());
            GlideUtil.load(itemView.getContext(), data.getIcon()).into(iconIv);
        }
    }

    public void setOnChatListener(OnChatListener listener) {
        mOnChatListener = listener;
    }
}
