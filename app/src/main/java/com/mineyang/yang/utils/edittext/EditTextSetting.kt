package com.mineyang.yang.utils.edittext

import android.text.InputFilter
import android.widget.EditText

/**
 * describe: 输入格式限制等工具
 */

object EditTextSetting{
    //禁止输入空格
    fun inhibitInputSpaceExt(editText: EditText) {
        val filter = InputFilter { source, start, end, dest, dstart, dend -> //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
            if ("" == source) "" else null
        }
        editText.filters = arrayOf(filter)
    }
}