package net.medlinker.imbusiness.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.view.BaseChatItemView;

import io.realm.OrderedRealmCollection;

/**
 * @author hmy
 * @time 2020/9/24 15:03
 */
public class ChatListAdapter extends RealmRecyclerViewAdapter<MsgDbEntity, ViewHolder> {

    private MsgViewFactory mMsgViewFactory = new MsgViewFactory();

    public ChatListAdapter(@Nullable OrderedRealmCollection<MsgDbEntity> data) {
        super(data, true);
    }

    @MsgViewFactory.MsgViewType
    @Override
    public int getItemViewType(int position) {
        return mMsgViewFactory.getChatType(getData().get(position));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mMsgViewFactory.msgViewCreator(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.itemView instanceof BaseChatItemView) {
            ((BaseChatItemView) holder.itemView).setViewData(getData(), position);
        }
    }
}
