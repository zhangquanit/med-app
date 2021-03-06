package net.medlinker.imbusiness.view.viewhelper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * the really implements of ViewHelper. use this class to fast set property.
 * this only can be called on main thread.
 * Created by heaven7 on 2015/8/6.
 */
public class ViewHelperImpl{

    private View v;

    /**
     * create an instance of {@link ViewHelperImpl}
     * @param target  the target to view
     */
    public ViewHelperImpl(View target) {
        this.v = target;
    }
    public ViewHelperImpl(){}

    /** change the current view to the target
     * @param target the target to view
     * @return  this */
    public ViewHelperImpl view(View target){
        if(target==null)
            throw new NullPointerException("target view can;t be null!");
        this.v = target;
        return this;
    }

    /**
     * reverse to the  t
     * @param  t  the object to reverse.
     * @param <T> the t
     * @return the t
     */
    public <T>T reverse(T t ){
        return t;
    }

    public Context getContext(){
        return v.getContext();
    }

    public ViewHelperImpl addTextChangedListener(TextWatcher watcher){
        ((TextView)v).addTextChangedListener(watcher);
        return this;
    }

    public ViewHelperImpl setVisibility(boolean visible){
        v.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }
    public ViewHelperImpl setVisibility(int visibility){
        v.setVisibility(visibility);
        return this;
    }

    public ViewHelperImpl setText(CharSequence text){
        ((TextView)v).setText(text);
        return this;
    }

    public ViewHelperImpl setEnabled(boolean enable){
        v.setEnabled(enable);
        return this;
    }

    public ViewHelperImpl toogleVisibility(){
        View view = this.v;
        if(view.getVisibility() == View.VISIBLE){
            view.setVisibility(View.GONE);
        }else{
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }
    public ViewHelperImpl setImageURI(Uri uri) {
        ((ImageView)v).setImageURI(uri);
        return this;
    }
    public ViewHelperImpl setImageResource(int imageResId) {
        ((ImageView)v).setImageResource(imageResId);
        return this;
    }
    public ViewHelperImpl setBackgroundColor(int color) {
        v.setBackgroundColor(color);
        return this;
    }
    public ViewHelperImpl setBackgroundRes(int backgroundRes) {
        v.setBackgroundResource(backgroundRes);
        return this;
    }
    public ViewHelperImpl setBackgroundDrawable(Drawable d) {
        ViewCompatUtil.setBackgroundCompatible(v, d);
        return this;
    }
    public ViewHelperImpl setTextAppearance( int redId){
        ((TextView)v).setTextAppearance(v.getContext(), redId);
        return this;
    }
    public ViewHelperImpl setTextColor(int textColor) {
        ((TextView)v).setTextColor(textColor);
        return this;
    }
    public ViewHelperImpl setTextColor(ColorStateList colorList) {
        ((TextView)v).setTextColor(colorList);
        return this;
    }
    public ViewHelperImpl setTextColorRes(int textColorResId) {
        return setTextColor(getContext().getResources().getColor(textColorResId));
    }
    public ViewHelperImpl setTextColorStateListRes(int textColorStateListResId) {
        return setTextColor(getContext().getResources().getColorStateList(textColorStateListResId));
    }

    public ViewHelperImpl setImageDrawable(Drawable d) {
        ((ImageView)v).setImageDrawable(d);
        return this;
    }

    public ViewHelperImpl setImageUrl(String url,ViewHelper.IImageLoader loader) {
        loader.load(url, (ImageView) v);
        return this;
    }
    /*  public ViewHelperImpl setImageUrl(String url,RoundedBitmapBuilder builder) {
          builder.url(url).into((ExpandNetworkImageView) v);
          return this;
      }*/
    public ViewHelperImpl setImageBitmap(Bitmap bitmap) {
        ((ImageView)v).setImageBitmap(bitmap);
        return this;
    }
    public ViewHelperImpl setAlpha(float alpha) {
        ViewCompatUtil.setAlpha(v, alpha);
        return this;
    }
    public ViewHelperImpl linkify() {
        Linkify.addLinks((TextView) v, Linkify.ALL);
        return this;
    }
    /**
     *@see  Linkify#addLinks(TextView, int)
     * @param mask the mast
     * @return this
     */
    public ViewHelperImpl linkify(int mask) {
        Linkify.addLinks((TextView) v, mask);
        return this;
    }
    public ViewHelperImpl setTypeface(Typeface typeface) {
        TextView view = (TextView) this.v;
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }
    public ViewHelperImpl setProgress(int progress) {
        ((ProgressBar)v).setProgress(progress);
        return this;
    }
    public ViewHelperImpl setProgress(int progress, int max) {
        ((ProgressBar)v).setProgress(progress);
        ((ProgressBar)v).setMax(max);
        return this;
    }
    public ViewHelperImpl setProgressMax(int max) {
        ((ProgressBar)v).setMax(max);
        return this;
    }
    public ViewHelperImpl setRating(float rating) {
        ((RatingBar)v).setRating(rating);
        return this;
    }
    public ViewHelperImpl setRating(float rating, int max) {
        ((RatingBar)v).setRating(rating);
        ((RatingBar)v).setMax(max);
        return this;
    }
    public ViewHelperImpl setTag(Object tag) {
        v.setTag(tag);
        return this;
    }
    public ViewHelperImpl setTag(int key,Object tag) {
        v.setTag(key, tag);
        return this;
    }
    public ViewHelperImpl setChecked(boolean checked) {
        ((Checkable)v).setChecked(checked);
        return this;
    }
    //======================= listener =========================//

    public ViewHelperImpl setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener l) {
        ((CompoundButton)v).setOnCheckedChangeListener(l);
        return this;
    }
    public ViewHelperImpl setOnClickListener(View.OnClickListener l) {
        v.setOnClickListener(l);
        return this;
    }
    public ViewHelperImpl setOnLongClickListener(View.OnLongClickListener l) {
        v.setOnLongClickListener(l);
        return this;
    }
    public ViewHelperImpl setOnTouchListener(View.OnTouchListener l) {
        v.setOnTouchListener(l);
        return this;
    }
    public ViewHelperImpl setAdapter(Adapter adapter) {
        ((AdapterView)v).setAdapter(adapter);
        return this;
    }
    public ViewHelperImpl setRecyclerAdapter(RecyclerView.Adapter adapter) {
        ((RecyclerView)v).setAdapter(adapter);
        return this;
    }

    public ViewHelperImpl setEnable(boolean enable) {
        v.setEnabled(enable);
        return this;
    }

    public ViewHelperImpl setTextSizeDp(float size) {
        ((TextView)v).setTextSize(size);
        return this;
    }
    public ViewHelperImpl setTextSize(float size) {
        ((TextView)v).setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        return this;
    }
    public ViewHelperImpl setScaleType(ImageView.ScaleType type) {
        ((ImageView)v).setScaleType(type);
        return this;
    }
    public GradientDrawableHelper beginGradientDrawableHelper() {
        GradientDrawable gd = (GradientDrawable) v.getBackground();
        return new GradientDrawableHelper(v.getContext(), gd);
    }

    public static class GradientDrawableHelper {

        GradientDrawable gd;
        Context context;

        public GradientDrawableHelper(Context context, GradientDrawable gd) {
            this.context = context;
            this.gd = gd;
        }

        /**
         * reverse to the  t
         * @param  t  the object to reverse.
         * @param <T> the t
         * @return the t
         */
        public <T>T reverse(T t ){
            return t;
        }
        public GradientDrawableHelper setColor(int color){
            gd.setColor(color);
            return this;
        }
        @TargetApi(21)
        public GradientDrawableHelper setColor(ColorStateList colorStateList){
            gd.setColor(colorStateList);
            return this;
        }
        public GradientDrawableHelper setAlpha(int alpha){
            gd.setAlpha(alpha);
            return this;
        }
        public GradientDrawableHelper setCornerRadius(float radius){
            gd.setCornerRadius(radius);
            return this;
        }
        public GradientDrawableHelper setStroke(int width, @ColorInt int color){
            gd.setStroke(width, color);
            return this;
        }
        public GradientDrawableHelper setStroke2(int width, @ColorRes int color){
            gd.setStroke(width, ViewCompatUtil.getColor(context, color));
            return this;
        }
        public GradientDrawableHelper setStroke(int width, @ColorRes int color,
                                                float dashWidth, float dashGap){
            gd.setStroke(width, ViewCompatUtil.getColor(context, color), dashWidth, dashGap);
            return this;
        }
        @TargetApi(21)
        public GradientDrawableHelper setStroke(int width, ColorStateList colorStateList){
            gd.setStroke(width, colorStateList);
            return this;
        }
        @TargetApi(21)
        public GradientDrawableHelper setStroke(int width, ColorStateList colorStateList,
                                                float dashWidth, float dashGap) {
            gd.setStroke(width, colorStateList, dashWidth, dashGap);
            return this;
        }

        public GradientDrawableHelper setShape(int shape) {
            gd.setShape(shape);
            return this;
        }
        public GradientDrawableHelper setCornerRadii(float[] radii) {
            gd.setCornerRadii(radii);
            return this;
        }
        public GradientDrawableHelper setSize(int width, int height) {
            gd.setSize(width, height);
            return this;
        }
        @TargetApi(21)
        public GradientDrawableHelper setTintMode(PorterDuff.Mode tintMode) {
            gd.setTintMode(tintMode);
            return this;
        }
        @TargetApi(21)
        public GradientDrawableHelper setTintList(ColorStateList tint) {
            gd.setTintList(tint);
            return this;
        }
    }
}
