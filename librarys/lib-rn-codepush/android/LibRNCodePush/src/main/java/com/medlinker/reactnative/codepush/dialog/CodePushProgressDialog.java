package com.medlinker.reactnative.codepush.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.medlinker.reactnative.codepush.R;

/**
 * @author hmy
 */
public class CodePushProgressDialog extends AlertDialog {

    private ProgressBar mProgressBar;
    private TextView mPercentTv;
    private TextView mNumberTv;

    private int mMax;
    private int mProgress;

    public CodePushProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_code_push_progress, null);
        setView(view);
        mProgressBar = view.findViewById(R.id.progress);
        mPercentTv = view.findViewById(R.id.progress_percent);
        mNumberTv = view.findViewById(R.id.progress_number);
        super.onCreate(savedInstanceState);

        mProgressBar.setMax(mMax);
    }

    public void setMax(int max) {
        mMax = max;
        if (mProgressBar != null) {
            mProgressBar.setMax(max);
        }
    }

    public void setProgress(int value) {
        mProgress = value;
        if (mProgressBar != null) {
            mProgressBar.setProgress(value);
        }
        if (mPercentTv != null) {
            String text = (value * 100 / mMax) + "%";
            mPercentTv.setText(text);
        }
    }

    @SuppressLint("DefaultLocale")
    public void setNumberText(long progress, long max) {
        if (mNumberTv != null) {
            String text = String.format("%.2fM/%.2fM", (float) progress / 1024 / 1024, (float) max / 1024 / 1024);
            mNumberTv.setText(text);
        }
    }
}
