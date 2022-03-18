package com.medlinker.hybrid.core.plugin.ui.entity

import android.graphics.Color


/**
 * @author hmy
 * @time 3/26/21 17:38
 */
data class NavigationConfig(val title: String = "",
                            val titleColor: Int = Color.parseColor("#2A2A2A"),
                            val titleSize: Float = 16f,
                            val subtitle: String = "",
                            val subTitleColor: Int = Color.parseColor("#4A4A4A"),
                            val subTitleSize: Float = 12f,
                            val bgColor: Int = Color.WHITE,
                            /**
                             * 标题栏高度，单位：dp
                             */
                            val heightDp: Float = 48f
)