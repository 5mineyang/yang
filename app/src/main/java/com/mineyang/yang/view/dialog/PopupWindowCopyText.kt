package com.mineyang.yang.view.dialog

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.mineyang.yang.R
import com.mineyang.yang.utils.ToastUtil
import com.mineyang.yang.utils.dp2px
import kotlinx.android.synthetic.main.layout_copydialog.view.*


/**
 *  Description :复制弹出框

 *  Author:yang

 *  Email:1318392199@qq.com

 *  Date: 2018/10/23
 */
class PopupWindowCopyText : PopupWindow {
    private var mActivity: Activity? = null
    private val mPopupWindow: PopupWindow

    constructor(activity: Activity, text: String, view: View, x: Int, y: Int) : super(activity) {
        mActivity = activity
        val popupView = LayoutInflater.from(activity).inflate(R.layout.layout_copydialog, null)

        mPopupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        mPopupWindow.isTouchable = true
        mPopupWindow.isOutsideTouchable = true
        mPopupWindow.showAsDropDown(view, x - activity.dp2px(80), y - activity.dp2px(60))

        popupView.tvCopyText.setOnClickListener {
            copyText(text)
            mPopupWindow.dismiss()
        }
    }

    private fun copyText(copiedText: String) {
        val clipboardManager = mActivity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText(null, copiedText)
        ToastUtil.showShort("已复制到剪贴板")
    }
}