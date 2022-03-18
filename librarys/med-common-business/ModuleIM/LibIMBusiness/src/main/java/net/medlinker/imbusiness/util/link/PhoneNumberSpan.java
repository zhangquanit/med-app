package net.medlinker.imbusiness.util.link;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextPaint;
import android.view.View;

import com.medlinker.lib.utils.MedViewUtil;

import net.medlinker.base.R;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;

import java.util.regex.Pattern;

/**
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * @version 3.1
 * @description 功能描述
 * @time 2016/3/12 14:48
 */
public class PhoneNumberSpan extends android.text.style.ClickableSpan {
    //cellphone 和 固话格式（028-87658888/02887658888/87658888）
    public static final Pattern PATTERN_PHONE = Pattern.compile("^[1][\\d]{10}" + "|" + "^[0]\\d{2,3}[\\-]*\\d{7,8}" + "|" + "^[1-9]\\d{6,7}");
    public static final String SCHEME_PHONE = "tel:";

    private String phoneNumber;
    private boolean isSelf;

    public PhoneNumberSpan(String phoneNumber, boolean self) {
        this.phoneNumber = phoneNumber;
        isSelf = self;
    }

    @Override
    public void onClick(View widget) {
        if (MedViewUtil.isFastDoubleClick()) {
            return;
        }
        dialPhoneNumber(widget.getContext(), phoneNumber);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(isSelf ? Color.WHITE : Color.parseColor("#0064ff"));
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param phoneNumber
     */
    private void dialPhoneNumber(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(phoneNumber));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
