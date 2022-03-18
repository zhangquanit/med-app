package net.medlinker.imbusiness.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medlinker.lib.utils.MedInputMethodUtil;
import com.medlinker.widget.dialog.MLLoadingDialog;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle3.components.support.RxFragment;

import net.medlinker.im.helper.MsgDbManager;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.R2;
import net.medlinker.imbusiness.entity.ImMenuEntity;
import net.medlinker.imbusiness.entity.ListStringEntity;
import net.medlinker.imbusiness.entity.intent.ChatSessionIntentData;
import net.medlinker.imbusiness.manager.ChatMsgReadUploadManager;
import net.medlinker.imbusiness.ui.adapter.ChatListAdapter;
import net.medlinker.imbusiness.ui.adapter.RealmRecyclerViewAdapter;
import net.medlinker.imbusiness.ui.viewmodel.ChatSessionViewModel;
import net.medlinker.imbusiness.view.ChatBottomGridView;
import net.medlinker.imbusiness.view.ChatBottomLayout;
import net.medlinker.imbusiness.view.ChatFooterLayout;
import net.medlinker.imbusiness.view.ChatHeaderLayout;
import net.medlinker.imbusiness.view.KeySoftRecycleView;
import net.medlinker.imbusiness.view.OnChatListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author hmy
 * @time 2020/9/23 20:37
 */
public class ChatSessionFragment extends RxFragment implements KeySoftRecycleView.OnHideMenuListener
        , OnChatListener {

    @BindView(R2.id.v_header_load)
    MaterialHeader mHeaderLoadView;
    @BindView(R2.id.layout_header)
    ChatHeaderLayout mHeaderLayout;
    @BindView(R2.id.layout_center_root)
    FrameLayout mCenterRootLayout;
    @BindView(R2.id.layout_refresh)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R2.id.rv_msg)
    KeySoftRecycleView mRecyclerview;
    @BindView(R2.id.layout_footer)
    ChatFooterLayout mFooterLayout;
    @BindView(R2.id.view_chat_bottom)
    ChatBottomLayout mBottomLayout;
    private ChatBottomGridView mBottomGridView;

    private ChatListAdapter mAdapter;

    private Realm mRealm = Realm.getDefaultInstance();
    protected ChatSessionIntentData mIntentData;
    private String mSessionId;
    private ChatSessionViewModel mViewModel;
    private MLLoadingDialog mLoadingDialog;

    public static ChatSessionFragment newInstance(ChatSessionIntentData intentData) {
        ChatSessionFragment fragment = new ChatSessionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("param", intentData);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mViewModel = new ViewModelProvider(getActivity()).get(ChatSessionViewModel.class);
        getLifecycle().addObserver(mViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_session, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mHeaderLoadView.setColorSchemeColors(Color.GRAY);
        MedInputMethodUtil.registerActivity(getActivity(), new MedInputMethodUtil.KeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean keyboardVisible) {
                mBottomLayout.onKeyboardVisibility(keyboardVisible);
                if (keyboardVisible) {
                    scrollChatBottom();
                }
            }
        });
        mBottomLayout.setOnChatListener(this);
        mRecyclerview.setOnHideMenuListener(this);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                // 解决偶现的崩溃问题，经源码分析，最好在这里try，catch，经查看阿里的vlayout也是这么处理的
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mViewModel.getHistoryMsg();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mIntentData = bundle.getParcelable("param");
        }
        if (mIntentData == null) {
            return;
        }
        initObserver();
        MsgSessionDbEntity session = mViewModel.onCreate(mIntentData, mRealm);
        if (null == session) {
            return;
        }
        mRecyclerview.setAdapter(getChatAdapter(mViewModel.getList()));
        mViewModel.checkFirstLoad();
        if (!TextUtils.isEmpty(session.getDraft())) {
            //恢复草稿
            restoreDraftString(session.getDraft(), null);
        }
        scrollChatBottom();
    }

    private void initObserver() {
        mViewModel.getImMenuLiveData().observe(getActivity(), new Observer<ArrayList<ImMenuEntity>>() {
            @Override
            public void onChanged(ArrayList<ImMenuEntity> imMenuList) {
                if (null == mBottomGridView) {
                    mBottomGridView = new ChatBottomGridView(getContext());
                    mFooterLayout.addView(mBottomGridView);
                    mBottomGridView.setOnChatListener(ChatSessionFragment.this);
                }
                mBottomGridView.setList(imMenuList);
                mBottomLayout.bindMenuView(mBottomGridView);
                scrollChatBottom();
            }
        });
        mViewModel.getGetHistoryLiveData().observe(getActivity(), new Observer<ListStringEntity>() {
            @Override
            public void onChanged(ListStringEntity s) {
                mRefreshLayout.finishRefresh();
                if (null != s) {
                    if (s.getList().isEmpty()) {
                        mRefreshLayout.setEnableRefresh(false);
                    }
                    mViewModel.onGetHistoryFinished(s);
                    scrollToPosition(s.getList().size() - 2, false);
                    if (mViewModel.isScrollChatBottom()) {
                        scrollChatBottom();
                    }
                }
            }
        });
    }

    @Override
    public boolean onSendMsgIntercept() {
        //TODO
        return false;
    }

    @Override
    public void onSendText(String text) {
        mViewModel.sendTextMsg(text);
    }

    @Override
    public void onGirdMenuShow() {
        scrollChatBottom();
    }

    private ChatListAdapter getChatAdapter(RealmResults<MsgDbEntity> realmResults) {
        if (null == mAdapter) {
            mAdapter = new ChatListAdapter(realmResults);
            mAdapter.setOnRealmCollectionChangedListener(new RealmRecyclerViewAdapter.OnRealmCollectionChangedAdapter() {
                @Override
                public void onDataInsert(int startIndex, int length) {
                    if (length == 1) {
                        scrollToPosition(mAdapter.getItemCount() - 1, false);
                    }
                    ChatMsgReadUploadManager.instance().doMsgUpload(null == mAdapter.getData() ? null : mAdapter.getData().last());
                }
            });
        }
        return mAdapter;
    }

    public void restoreDraftString(String draft, String draftAtUsers) {
        mBottomLayout.setInputText(draft);
        if (TextUtils.isEmpty(draftAtUsers)) {
            return;
        }
//        List<UserBaseEntity> list = new Gson().fromJson(draftAtUsers, new TypeToken<ArrayList<UserBaseEntity>>() {
//        }.getType());
//        //恢复草稿中的@消息
//        for (UserBaseEntity draftAtUser : list) {
//            LinkUtil.addAtMemberCase(draftAtUser.getName(), draftAtUser.getId());
//        }
        MedInputMethodUtil.popInputMethod(getContext(), mBottomLayout.getInputEditText());
    }

    @Override
    public void hideMenu() {
        mBottomLayout.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onBackPressed() {
        if (!mBottomLayout.onBackPressed()) {
            return false;
        }
        MsgDbManager.INSTANCE.getSessionDbDao().insertSessionDraft(getSessionId(), mBottomLayout.getInputText());
        return true;
    }

    public void scrollChatBottom() {
        if (null != mAdapter) {
            scrollToPosition(mAdapter.getItemCount() - 1, false);
        }
    }

    public void scrollToPosition(int position, boolean smooth) {
        if (mRecyclerview.getAdapter() != null && position >= 0
                && position < mRecyclerview.getAdapter().getItemCount()) {
            if (smooth) {
                mRecyclerview.smoothScrollToPosition(position);
            } else {
                mRecyclerview.scrollToPosition(position);
            }
        }
    }

    private String getSessionId() {
        if (TextUtils.isEmpty(mSessionId)) {
            mSessionId = mIntentData.getSessionId();
        }
        return mSessionId;
    }

    private void showDialogLoading(boolean cancelable) {
        if (null == mLoadingDialog) {
            mLoadingDialog = MLLoadingDialog.getInstance(cancelable);
        }
        if (null == getActivity() || getActivity().isDestroyed() || mLoadingDialog.isAdded() || mLoadingDialog.isVisible()) {
            return;
        }
        mLoadingDialog.show(getActivity().getSupportFragmentManager());
    }

    public void hideDialogLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isAdded()) {
            mLoadingDialog.dismissAllowingStateLoss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || null == data) {
            return;
        }
        //TODO
//        if (RequestCode.CODE_PICK_IMAGE == requestCode) {
//            if (!onSendMsgIntercept()) {
//                ArrayList<FileEntity> listExtra = data.getParcelableArrayListExtra("SELECTED_PHOTOS");
//                mViewModel.sendImageMessage(listExtra);
//            }
//        }
    }
}
