package com.medlinker.widget.gvp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.medlinker.widget.R;

/**
 * @author hmy
 * @time 2020/5/19 17:37
 */
public class GridViewPager extends ViewPager {

    //每页列数
    private int mColumnsNum = 4;
    //没有行数
    private int mRawNum = 2;
    private int mItemCount;
    private int mPageCount;
    private int mPageSize;
    private GVPAdapter mAdapter;
    private PageAdapter mPageAdapter;
    private OnDataChangedListener mOnDataChangedListener;
    private boolean nestedScroll;

    public GridViewPager(@NonNull Context context) {
        super(context);
    }

    public GridViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(GVPAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("GVPAdapter不能为空");
        }
        mAdapter = adapter;
        mAdapter.bindGridViewPager(this);

        check();
    }

    private void check() {
        mItemCount = mAdapter.getCount();
        if (mItemCount <= 0) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        mPageSize = mColumnsNum * mRawNum;
        mPageCount = mItemCount % mPageSize == 0 ? mItemCount / mPageSize : mItemCount / mPageSize + 1;
        if (mPageAdapter == null) {
            mPageAdapter = new PageAdapter();
            setAdapter(mPageAdapter);
        } else {
            mPageAdapter.notifyDataSetChanged();
        }
    }

    public void notifyDataSetChanged() {
        check();
        if (null != mOnDataChangedListener) {
            mOnDataChangedListener.notifyDataSetChanged();
        }
    }

    public int getPageCount() {
        return mPageCount;
    }

    public void setColumnsNum(int columnsNum) {
        this.mColumnsNum = columnsNum;
    }

    public int getColumnsNum() {
        return mColumnsNum;
    }

    public void setRawNum(int rawNum) {
        this.mRawNum = rawNum;
    }

    public void setNestScroll(boolean nestedScroll) {
        this.nestedScroll = nestedScroll;
    }

    private class PageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            mPageCount = mItemCount % mPageSize == 0 ? mItemCount / mPageSize : mItemCount / mPageSize + 1;
            return mPageCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.wiew_recycler_view_pager, null);
            RecyclerView rvPager = view.findViewById(R.id.rv_rvg_pager);
//            rvPager.setFocusableInTouchMode(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(container.getContext(), mColumnsNum);
            if (nestedScroll) {
                rvPager.setHasFixedSize(true);
                rvPager.setNestedScrollingEnabled(false);
                gridLayoutManager = new GridLayoutManager(container.getContext(), mColumnsNum) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };
            }
            rvPager.setLayoutManager(gridLayoutManager);
            setGVAdapterOrRefresh(rvPager, position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            // 无论如何都刷新
            return POSITION_NONE;
        }

        private void setGVAdapterOrRefresh(RecyclerView rv, final int position) {
            RecyclerView.Adapter adapter = rv.getAdapter();
            if (null != adapter) {
                adapter.notifyDataSetChanged();
            } else {
                adapter = new GVAdapter(mAdapter, position);
                rv.setAdapter(adapter);
                // 设置item间分割线
                RecyclerView.ItemDecoration itemDecoration = ((GVAdapter) adapter).getItemDecoration();
                if (null != itemDecoration) {
                    rv.addItemDecoration(itemDecoration);
                }
            }
        }
    }

    private class GVAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private GVPAdapter<T> mAdapter;
        private int mIndex;

        GVAdapter(GVPAdapter<T> adapter, int index) {
            this.mAdapter = adapter;
            this.mIndex = index;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(mAdapter.getItemLayoutId(), null);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int index = mIndex * mPageSize + position;
            holder.itemView.setTag(index);
            mAdapter.bindItem(holder.itemView, index, mAdapter.getData(index));
        }

        @Override
        public int getItemCount() {
            int count = mItemCount;
            if (mIndex < mPageCount - 1) {
                count = mPageSize;
            } else {
                count -= mPageSize * (mPageCount - 1);
            }
            return count;
        }

        public RecyclerView.ItemDecoration getItemDecoration() {
            return mAdapter.getItemDecoration();
        }

        private class Holder extends RecyclerView.ViewHolder {

            Holder(View itemView) {
                super(itemView);
            }
        }
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        mOnDataChangedListener = listener;
    }

    public interface OnDataChangedListener {

        void notifyDataSetChanged();
    }
}
