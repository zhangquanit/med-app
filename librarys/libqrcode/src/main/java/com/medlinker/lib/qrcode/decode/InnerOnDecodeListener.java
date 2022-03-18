package com.medlinker.lib.qrcode.decode;

import android.graphics.Point;

import com.medlinker.lib.qrcode.OnDecodeListener;

public interface InnerOnDecodeListener extends OnDecodeListener {
    Point getSurfaceSize();
}
