package com.medlinker.baseapp.util

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.medlinker.baseapp.R
import com.medlinker.widget.loading.MLLoadingView

/**
 *
 * @author zhangquan
 */
object ResourceChangeUtil {

    /**
     * 修改MLLoadingView中的图标颜色
     */
    fun changeMLProgress(view: MLLoadingView) {
        view.findViewById<ImageView>(R.id.loading_progressbar)
            .setColorFilter(ContextCompat.getColor(view.context, R.color.color_main))
    }
}