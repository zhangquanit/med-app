package net.medlinker.imbusiness.view.viewhelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.annotation.ColorRes;

public class ViewCompatUtil {

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void setBackgroundCompatible(View v, Drawable d) {
        if (Build.VERSION.SDK_INT >= 16)
            v.setBackground(d);
        else
            v.setBackgroundDrawable(d);
    }

    public static int getColor(Context context, @ColorRes int colorRes){
        if(Build.VERSION.SDK_INT >= 23){
            return context.getColor(colorRes);
        }else{
            return context.getResources().getColor(colorRes);
        }
    }


    public static void setAlpha(View v, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            v.setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            v.startAnimation(alpha);
        }
    }
}
