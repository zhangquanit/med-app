package net.medlinker.imbusiness.util.link;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.View;

import com.medlinker.lib.utils.MedViewUtil;


/**
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * @version 3.1
 * @description @医生Span  目前没处理点击事件
 * @time 2016/3/1 19:38
 */
public class MentionSpan extends android.text.style.ClickableSpan {

    private CharSequence mUsername;
    private int mMid;
    private Context mContext;
    private int mColor;

    public MentionSpan(Context context, CharSequence userName) {
        this(context, userName, 0, 0);
    }

    public MentionSpan(Context context, CharSequence userName, int uid) {
        this(context, userName, uid, 0);
    }

    public MentionSpan(Context context, CharSequence userName, int uid, int txtColor) {
        this.mContext = context;
        this.mUsername = userName;
        this.mMid = uid;
        this.mColor = txtColor;
    }

    public void onClick(View widget) {
        if (MedViewUtil.isFastDoubleClick()) {
            return;
        }
        // FIXME: 2016/3/1 这里可处理点击事件
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mContext.getResources().getColor(mColor));
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }

    //    @Override
//    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
////        int width = Math.round(measureText(paint, text, start, end));
//        int width = Math.round( paint.measureText(mUsername.toString()));
////        LogUtil.e("yjt", "MentionSpan getSize  width=" + width);
//        LogUtil.e("yjt", "MentionSpan getSize start=" +start + "; end =" + end+"; drawTxt="+mUsername);
//        return width;
//    }
//
//    @Override
//    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
//        if(mColor > 0){
//            int color = mContext.getResources().getColor( mColor);
//            paint.setColor( color);
//        }
//        canvas.drawText(mUsername.toString(), start, end, x, y, paint);
//        LogUtil.e("yjt", "MentionSpan draw  x=" + x + "; y =" + y+"; drawTxt="+mUsername);
//    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }
}