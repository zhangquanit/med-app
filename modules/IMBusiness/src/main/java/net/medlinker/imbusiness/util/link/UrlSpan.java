package net.medlinker.imbusiness.util.link;

import android.graphics.Color;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.medlinker.lib.utils.MedViewUtil;

import net.medlinker.base.R;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;

/**
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * @version 3.1
 * @description 功能描述
 * @time 2016/3/3 21:21
 */
public class UrlSpan extends URLSpan {

    private boolean isSelf;

    public UrlSpan(String url, boolean self) {
        super(url);
        isSelf = self;
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
        ds.setColor(isSelf ? Color.WHITE : Color.parseColor("#0064ff"));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isSelf ? (byte) 1 : (byte) 0);
    }

    protected UrlSpan(Parcel in) {
        super(in);
        this.isSelf = in.readByte() != 0;
    }

    public static final Creator<UrlSpan> CREATOR = new Creator<UrlSpan>() {
        @Override
        public UrlSpan createFromParcel(Parcel source) {
            return new UrlSpan(source);
        }

        @Override
        public UrlSpan[] newArray(int size) {
            return new UrlSpan[size];
        }
    };
}
