package net.medlinker.imbusiness.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cn.glidelib.GlideUtil;
import com.cn.glidelib.ImageUtil;
import com.medlinker.protocol.message.MessageOuterClass;

import net.medlinker.base.common.Observable2;
import com.medlinker.lib.utils.MedDimenUtil;

import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.emoj.EmSmileUtils;
import net.medlinker.imbusiness.emoj.EmojiconEntity;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/2/18
 */
public class ChatItemImageView extends BaseChatItemView {

    private ImageView mImageContent;

    public ChatItemImageView(Context context, boolean right) {
        super(context, right);
    }

    @Override
    protected int getItemLayoutId() {
        return mIsRight ? R.layout.view_chat_item_image_sender : R.layout.view_chat_item_image_received;
    }

    @Override
    protected void onFindViewById() {
        mImageContent = findViewById(R.id.layout_content);
    }

    @Override
    public void setViewData(final List<MsgDbEntity> dataList, final int position) {
        final MsgDbEntity message = dataList.get(position);
        boolean isPhoto = false;
        try {
            long emojId = Long.parseLong(message.getImageUrl());
            EmojiconEntity easeEmojicon = EmSmileUtils.getEaEmojData(emojId);
            if (null != easeEmojicon) {
//                GlideUtil.setUrlImageView(mImageContent, easeEmojicon.getIcon());
                Glide.with(mImageContent).load(easeEmojicon.getIcon()).apply(new RequestOptions().error(R.drawable.bg_hoder_home_small)
                        .placeholder(R.drawable.bg_hoder_home_small))
                        .into(mImageContent);
            } else {
                isPhoto = true;
            }
        } catch (NumberFormatException e) {
            isPhoto = true;
        }
        if (isPhoto) {
            final String currentImgUrl = message.getImageUrl();
            Glide.with(mContext)
                    .load(ImageUtil.checkUrl(message.getImageUrl(), MedDimenUtil.dip2px(getContext(), 130),
                            MedDimenUtil.dip2px(getContext(), 130)))
                    .apply(new RequestOptions().error(R.drawable.bg_hoder_home_small)
                            .placeholder(R.drawable.bg_hoder_home_small)
                            .override(MedDimenUtil.dip2px(getContext(), 130), MedDimenUtil.dip2px(getContext(), 130)))
                    .apply(GlideUtil.getRoundedCornersOptions(MedDimenUtil.dip2px(getContext(), 5)))
                    .into(mImageContent);
            mImageContent.setOnClickListener(new OnClickListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onClick(final View v) {
                    Observable2.from(dataList)
                            .filter(new Predicate<MsgDbEntity>() {
                                @Override
                                public boolean test(MsgDbEntity msgDbEntity) throws Exception {
                                    return msgDbEntity.getDataType() == MessageOuterClass.Message.DataCase.IMAGE.getNumber();
                                }
                            })
                            .filter(new Predicate<MsgDbEntity>() {
                                @Override
                                public boolean test(MsgDbEntity msgDbEntity) throws Exception {
                                    return msgDbEntity.getImageUrl() != null && !msgDbEntity.isWithDraw();
                                }
                            })
                            .map(new Function<MsgDbEntity, String>() {
                                @Override
                                public String apply(MsgDbEntity msgDbEntity) throws Exception {
                                    return msgDbEntity.getImageUrl();
                                }
                            })
                            .filter(new Predicate<String>() {
                                @Override
                                public boolean test(String url) throws Exception {
                                    boolean photo = true;
                                    try {
                                        long emojId = Long.parseLong(url);
                                        if (null != EmSmileUtils.getEaEmojData(emojId)) {
                                            photo = false;
                                        }
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    return photo;
                                }
                            })
                            .map(new Function<String, CharSequence>() {
                                @Override
                                public CharSequence apply(String s) throws Exception {
                                    return s;
                                }
                            })
                            .toList()
                            .subscribe(new Consumer<List<CharSequence>>() {
                                @Override
                                public void accept(List<CharSequence> strings) throws Exception {
                                    int currentPosition = 0;
                                    for (int i = 0; i < strings.size(); i++) {
                                        if (strings.get(i).equals(currentImgUrl)) {
                                            currentPosition = i;
                                        }
                                    }
                                    ModuleIMBusinessManager.INSTANCE.getBusinessService()
                                            .previewImageView((ArrayList<CharSequence>) strings, currentPosition);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });
                }
            });
        } else {
            mImageContent.setOnClickListener(null);
        }
        super.setViewData(dataList, position);
    }
}
