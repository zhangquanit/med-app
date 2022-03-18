package net.medlinker.imbusiness.util.link;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.medlinker.lib.utils.MedViewUtil;

import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;

/**
 * @author <a href="mailto:ganyu@medlinker.com">ganyu</a>
 * @version 3.4
 * @description 文本链接span
 * @time 2016/7/9 15:18
 */
public class TextLinkSpan extends URLSpan {

    public TextLinkSpan(String url) {
        super(url);
    }

    @Override
    public void onClick(View widget) {
        if (MedViewUtil.isFastDoubleClick()) {
            return;
        }
        ModuleIMBusinessManager.INSTANCE.getBusinessService().gotoWebActivity(widget.getContext(), getURL());
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(ModuleIMBusinessManager.INSTANCE.getApplication().getResources().getColor(R.color.c_0064ff));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected TextLinkSpan(Parcel in) {
        super(in);
    }

    public static final Creator<TextLinkSpan> CREATOR = new Creator<TextLinkSpan>() {
        @Override
        public TextLinkSpan createFromParcel(Parcel source) {
            return new TextLinkSpan(source);
        }

        @Override
        public TextLinkSpan[] newArray(int size) {
            return new TextLinkSpan[size];
        }
    };
}
