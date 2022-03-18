package com.medlinker.video.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.medlinker.lib.utils.MedDimenUtil;
import com.tencent.rtmp.ui.TXCloudVideoView;


import java.util.ArrayList;

/**
 * @author hmy
 */
public class TRTCVideoLayoutManager extends RelativeLayout {
    public static final int MODE_FLOAT = 1;  // 前后堆叠模式
    public static final int MODE_GRID = 2;  // 九宫格模式
    public static final int MAX_USER = 2;
    private int mMode;
    private ArrayList<TRTCLayoutEntity> mLayoutEntityList;

    private String mSelfUserId;

    public static class TRTCLayoutEntity {
        public TRTCVideoLayout layout;
        public int index = -1;
        public String userId = "";
        public int streamType = -1;
    }

    public ArrayList<TRTCLayoutEntity> getLayoutEntityList() {
        return mLayoutEntityList;
    }

    public TRTCVideoLayoutManager(Context context) {
        this(context, null);
    }

    public TRTCVideoLayoutManager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TRTCVideoLayoutManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mLayoutEntityList = new ArrayList<>();
        // 初始化多个 View，以备用
        for (int i = 0; i < MAX_USER; i++) {
            TRTCVideoLayout videoLayout = new TRTCVideoLayout(context);
            videoLayout.setVisibility(View.GONE);
            videoLayout.setBackgroundColor(Color.BLACK);
            videoLayout.setMovable(false);

            TRTCLayoutEntity entity = new TRTCLayoutEntity();
            entity.layout = videoLayout;
            entity.index = i;
            mLayoutEntityList.add(entity);
        }
        // 默认为堆叠模式
        mMode = MODE_FLOAT;
        this.post(new Runnable() {
            @Override
            public void run() {
                makeFloatLayout(true);
            }
        });
    }

    /**
     * 根据 userId 和视频类型，找到已经分配的 View
     *
     * @param userId
     * @param streamType
     * @return
     */
    public TXCloudVideoView findCloudViewView(String userId, int streamType) {
        if (userId == null) return null;
        for (TRTCLayoutEntity layoutEntity : mLayoutEntityList) {
            if (layoutEntity.streamType == streamType && layoutEntity.userId.equals(userId)) {
                return layoutEntity.layout.getVideoView();
            }
        }
        return null;
    }

    /**
     * 找到第一个加入视频聊天的用户的TRTCLayoutEntity（排除自己)
     *
     * @return
     */
    public TRTCVideoLayout findFirstUserCloudView() {
        if (null != mLayoutEntityList && mLayoutEntityList.size() > 1) {
            return mLayoutEntityList.get(1).layout;
        }
        return null;
    }

    public String findFirstUserCloudUserId() {
        if (null != mLayoutEntityList && mLayoutEntityList.size() > 1) {
            return mLayoutEntityList.get(1).userId;
        }
        return null;
    }

    /**
     * 根据 userId 和 视频类型（大、小、辅路）画面分配对应的 view
     *
     * @param userId
     * @param streamType
     * @return
     */
    public TXCloudVideoView allocCloudVideoView(String userId, int streamType) {
        if (userId == null) return null;
        for (TRTCLayoutEntity layoutEntity : mLayoutEntityList) {
            if (layoutEntity.userId.equals("")) {
                layoutEntity.userId = userId;
                layoutEntity.streamType = streamType;
                layoutEntity.layout.setVisibility(VISIBLE);
                return layoutEntity.layout.getVideoView();
            }
        }
        return null;
    }

    /**
     * 根据 userId 和 视频类型，回收对应的 view
     *
     * @param userId
     * @param streamType
     */
    public void recyclerCloudViewView(String userId, int streamType) {
        if (userId == null) return;
        if (mMode == MODE_FLOAT) {
            TRTCLayoutEntity entity = mLayoutEntityList.get(0);
            // 当前离开的是处于0号位的人，那么需要将我换到这个位置
            if (userId.equals(entity.userId) && entity.streamType == streamType) {
                TRTCLayoutEntity myEntity = findEntity(mSelfUserId);
                if (myEntity != null) {
                    makeFullVideoView(myEntity.index);
                }
            }
        }

        for (TRTCLayoutEntity entity : mLayoutEntityList) {
            if (entity.streamType == streamType && userId.equals(entity.userId)) {
                entity.layout.setVisibility(GONE);
                entity.userId = "";
                entity.streamType = -1;
                break;
            }
        }
    }

    private TRTCLayoutEntity findEntity(String userId) {
        for (TRTCLayoutEntity entity : mLayoutEntityList) {
            if (entity.userId.equals(userId)) return entity;
        }
        return null;
    }

    public void setMySelfUserId(String userId) {
        mSelfUserId = userId;
    }

    public String getSelfUserId() {
        return mSelfUserId;
    }

    private void makeFloatLayout(boolean needAddView) {
        for (int i = 0; i < mLayoutEntityList.size(); i++) {
            TRTCLayoutEntity entity = mLayoutEntityList.get(i);
            RelativeLayout.LayoutParams layoutParams;
            if (i == 0) {
                entity.layout.setMovable(false);
                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            } else {
                entity.layout.setMovable(true);
                final int lrMargin = getSubMargin();
                final int subWidth = getSubWith();
                final int subHeight = getSubHeight();
                layoutParams = new RelativeLayout.LayoutParams(subWidth, subHeight);
                layoutParams.leftMargin = getWidth() - lrMargin - subWidth;
                layoutParams.topMargin = lrMargin;
            }
            entity.layout.setLayoutParams(layoutParams);
            addFloatViewClickListener(entity.layout);

            if (needAddView) {
                addView(entity.layout);
            }
        }
    }

    public int getSubMargin() {
        return MedDimenUtil.dip2px(getContext(), 15);
    }

    public int getSubWith() {
        return MedDimenUtil.dip2px(getContext(), 100);
    }

    public int getSubHeight() {
        return MedDimenUtil.dip2px(getContext(), 175);
    }

    /**
     * 对堆叠布局情况下的 View 添加监听器
     * <p>
     * 用于点击切换两个 View 的位置
     *
     * @param view
     */
    private void addFloatViewClickListener(final TRTCVideoLayout view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (TRTCLayoutEntity entity : mLayoutEntityList) {
                    if (entity.layout == v) {
                        makeFullVideoView(entity.index);
                        break;
                    }
                }
            }
        });
    }

    /**
     * 堆叠模式下，将 index 号的 view 换到 0 号位，全屏化渲染
     *
     * @param index
     */
    private void makeFullVideoView(int index) {// 1 -> 0
        if (index <= 0 || mLayoutEntityList.size() <= index) {
            return;
        }
        TRTCLayoutEntity indexEntity = mLayoutEntityList.get(index);
        ViewGroup.LayoutParams indexParams = indexEntity.layout.getLayoutParams();

        TRTCLayoutEntity fullEntity = mLayoutEntityList.get(0);
        ViewGroup.LayoutParams fullVideoParams = fullEntity.layout.getLayoutParams();

        indexEntity.layout.setLayoutParams(fullVideoParams);
        indexEntity.index = 0;

        fullEntity.layout.setLayoutParams(indexParams);
        fullEntity.index = index;

        indexEntity.layout.setMovable(false);
        indexEntity.layout.setOnClickListener(null);

        fullEntity.layout.setMovable(true);
        addFloatViewClickListener(fullEntity.layout);

        mLayoutEntityList.set(0, indexEntity); // 将 fromView 塞到 0 的位置
        mLayoutEntityList.set(index, fullEntity);

        for (int i = 0; i < mLayoutEntityList.size(); i++) {
            TRTCLayoutEntity entity = mLayoutEntityList.get(i);
            // 需要对 View 树的 zOrder 进行重排，否则在 RelativeLayout 下，存在遮挡情况
            bringChildToFront(entity.layout);
        }
    }
}
