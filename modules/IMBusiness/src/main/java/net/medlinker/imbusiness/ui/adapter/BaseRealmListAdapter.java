package net.medlinker.imbusiness.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description recyclerview的基类适配器
 * @time 17/2/14
 */
public abstract class BaseRealmListAdapter<T extends RealmObject> extends RealmRecyclerViewAdapter<T, ViewHolder> {

    private int mLayoutId;

    protected OnItemClickListener<T> mOnItemClickListener;

    public interface OnItemClickListener<T> {

        void onItemClick(int position, T item, View view);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    public BaseRealmListAdapter(RealmResults<T> dataList, int layoutId) {
        super(dataList, true);
        this.mLayoutId = layoutId;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        T object = null;
        if (getData() != null && !getData().isEmpty()) {
            object = getData().get(position);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    T object = null;
                    if (getData() != null && !getData().isEmpty()) {
                        object = getData().get(holder.getAdapterPosition() - headerCount);
                    }
                    mOnItemClickListener.onItemClick(holder.getAdapterPosition() - headerCount, object, v);
                }
            }
        });
        convert(new ViewHolder(holder.itemView), position - headerCount, object);
    }

    public abstract void convert(ViewHolder itemView, int position, T item);

}
