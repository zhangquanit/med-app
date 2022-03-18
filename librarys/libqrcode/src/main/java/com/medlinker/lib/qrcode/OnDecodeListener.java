package com.medlinker.lib.qrcode;

import android.graphics.Rect;

public interface OnDecodeListener {
    /**
     * 返回扫描结果
     * @param result 结果字符串
     * @param qrBmp 被识别的图片，jpeg格式
     */
    void onDecodeResult(String result, byte[] qrBmp);

    /**
     * 获取屏幕上识别的矩形框相对于ScanView的位置
     * @return
     */
    Rect getCropRect();
}
