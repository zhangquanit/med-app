/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.medlinker.lib.qrcode.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.Result;
import com.medlinker.lib.qrcode.OnDecodeListener;
import com.medlinker.lib.qrcode.R;
import com.medlinker.lib.qrcode.camera.CameraManager;
import com.medlinker.lib.qrcode.decode.DecodeThread;
import com.medlinker.lib.qrcode.decode.InnerOnDecodeListener;


/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class ResultHandler extends Handler {

    private final InnerOnDecodeListener mDecodeListener;
    private final DecodeThread decodeThread;
    private final CameraManager cameraManager;
    private State state;

    public ResultHandler(InnerOnDecodeListener decodeListener, CameraManager cameraManager, int decodeMode) {
        mDecodeListener = decodeListener;
        decodeThread = new DecodeThread(mDecodeListener, this, decodeMode);
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.qrcode_restart_preview) {
            restartPreviewAndDecode();

        } else if (message.what == R.id.qrcode_decode_succeeded) {
            state = State.SUCCESS;
            Bundle bundle = message.getData();

            mDecodeListener.onDecodeResult(((Result) message.obj).getText(), bundle.getByteArray(DecodeThread.BARCODE_BITMAP));

        } else if (message.what == R.id.qrcode_decode_failed) {// We're decoding as fast as possible, so when one
            // decode fails,
            // start another.
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.qrcode_decode);

        } else if (message.what == R.id.qrcode_return_scan_result) {
//            activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
//            activity.finish();

        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.qrcode_quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause()
            // will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.qrcode_decode_succeeded);
        removeMessages(R.id.qrcode_decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.qrcode_decode);
        }
    }

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

}
