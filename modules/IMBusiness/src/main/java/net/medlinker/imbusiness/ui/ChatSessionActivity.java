package net.medlinker.imbusiness.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.medlinker.widget.navigation.CommonNavigationBar;

import net.medlinker.base.base.BaseActivity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.entity.intent.ChatSessionIntentData;
import net.medlinker.imbusiness.eventbus.IMEventMsg;
import net.medlinker.imbusiness.router.ModuleIMRouterPath;
import net.medlinker.imbusiness.ui.viewmodel.ChatSessionViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Route(path = ModuleIMRouterPath.ROUTER_CHAT)
public class ChatSessionActivity extends BaseActivity {

    public static final String DATA_KEY = "DATA_KEY";

    private CommonNavigationBar mNavigationBar;
    private ChatSessionFragment mChatSessionFragment;
    private ChatSessionIntentData mIntentData;

    private ChatSessionViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_session);
        EventBus.getDefault().register(this);
        if (null != savedInstanceState) {
            ChatSessionIntentData intentData = savedInstanceState.getParcelable(DATA_KEY);
            if (null != intentData) {
                getIntent().putExtra(DATA_KEY, intentData);
            }
        }
        mNavigationBar = findViewById(R.id.bar_chat);
        notifyChatByIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            setIntent(intent);
        }
        notifyChatByIntent();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(DATA_KEY, mIntentData);
    }

    private void notifyChatByIntent() {
        getViewModelStore().clear();
        mViewModel = new ViewModelProvider(this).get(ChatSessionViewModel.class);
        ChatSessionIntentData intentData = getIntent().getParcelableExtra(DATA_KEY);
        if (intentData != null) {
            mIntentData = intentData;
        } else {
            finish();
            return;
        }
        initObserver();
        mChatSessionFragment = ChatSessionFragment.newInstance(mIntentData);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_chat_root, mChatSessionFragment)
                .commitAllowingStateLoss();
    }

    private void initObserver() {
        mNavigationBar.showCenterTitle("我的医生");
        mNavigationBar.showBackIcon(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewModel.memberLiveData.observe(this, clinicMemberEntity -> {
            if (clinicMemberEntity != null && clinicMemberEntity.getDoctor() != null) {
                mNavigationBar.showCenterTitle(clinicMemberEntity.getDoctor().getName());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(IMEventMsg msg) {
        int type = msg.getType();
        switch (type) {
            case IMEventMsg.REALM_UNREAD_COUNT_CHANGED:
                //TODO
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (null != mChatSessionFragment) {
            mChatSessionFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}