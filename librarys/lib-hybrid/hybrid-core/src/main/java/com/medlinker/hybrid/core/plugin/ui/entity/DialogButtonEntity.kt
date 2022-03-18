package com.medlinker.hybrid.core.plugin.ui.entity


/**
 * @author hmy
 * @time 3/29/21 11:28
 */
data class DialogButtonEntity(val title: String,
                              /**
                               * 按钮类型，蓝底白字（primary）或者灰低黑字（default）
                               */
                              val type: DialogType = DialogType.DEFAULT)