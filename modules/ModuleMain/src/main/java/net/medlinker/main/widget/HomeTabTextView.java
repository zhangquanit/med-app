package net.medlinker.main.widget;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class HomeTabTextView extends AppCompatTextView {

    public HomeTabTextView(@NonNull Context context) {
        super(context);
    }

    public HomeTabTextView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeTabTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setTypeface(null, Typeface.BOLD);
        } else {
            setTypeface(null, Typeface.NORMAL);
        }
    }
}
