package com.medlinker.lib.imagepicker.utils;

import android.content.Context;
import android.provider.MediaStore.Images.Media;

import androidx.loader.content.CursorLoader;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/19 15:28
 */
public class PhotoDirectoryLoader extends CursorLoader {

    final String[] IMAGE_PROJECTION = {
            Media._ID,
            Media.DATA,
            Media.BUCKET_ID,
            Media.BUCKET_DISPLAY_NAME,
            Media.DATE_ADDED
    };

    public PhotoDirectoryLoader(Context context) {
        super(context);

        setProjection(IMAGE_PROJECTION);
        setUri(Media.EXTERNAL_CONTENT_URI);
        setSortOrder(Media.DATE_ADDED + " DESC");

        setSelection(
                MIME_TYPE + "=? or " + MIME_TYPE + "=? ");

        String[] selectionArgs;
//    if (showGif) {
//      selectionArgs = new String[] { "image/jpeg", "image/png", "image/gif" };
//    } else {
        selectionArgs = new String[]{"image/jpeg", "image/png"};
//    }
        setSelectionArgs(selectionArgs);
    }

//
//  private PhotoDirectoryLoader(Context context, Uri uri, String[] projection, String selection,
//                               String[] selectionArgs, String sortOrder) {
//    super(context, uri, projection, selection, selectionArgs, sortOrder);
//  }


}
