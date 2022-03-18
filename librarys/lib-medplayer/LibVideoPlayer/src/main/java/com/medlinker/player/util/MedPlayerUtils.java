package com.medlinker.player.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class MedPlayerUtils {

    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }

    public static void log(String message) {
        Log.d(MedPlayerConfig.TAG, message);
    }

    public static void log(String tag, String message) {
        Log.d(tag, message);
    }

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }
}
