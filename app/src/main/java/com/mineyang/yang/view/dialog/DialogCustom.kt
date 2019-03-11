package com.mineyang.yang.view.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import com.mineyang.yang.R
import kotlinx.android.synthetic.main.layout_dialog_loading.view.*

@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS", "DEPRECATED_IDENTITY_EQUALS", "DEPRECATION",
        "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SuppressLint("ObsoleteSdkInt", "ClickableViewAccessibility", "ResourceAsColor", "InflateParams")
class DialogCustom(private var mContext: Context?) {
    private var remindDialog: Dialog? = null
    private var loadingDialog: Dialog? = null
    private var screenWidth: Int = mContext?.resources?.displayMetrics?.widthPixels ?: 0

    fun unBindContext() {
        screenWidth = 0
        mContext = null
        remindDialog?.let { remindDialog = null }
        loadingDialog?.let { loadingDialog = null }
    }

    fun dismissLoadingDialog() {
        if (mContext != null && !(mContext as Activity).isFinishing) {
            loadingDialog?.let {
                if (it.isShowing) {
                    it.dismiss()
                }
            }
        }
    }

    fun showLoadingDialog() {
        loadingDialog?.let {
            if (it.isShowing) {
                return@let
            }
        }
        val view = View.inflate(mContext, R.layout.layout_dialog_loading, null)
        view.load.setImageResource(R.drawable.loading)
        val drawable = view.load.drawable as AnimationDrawable
        drawable.start()
        loadingDialog = Dialog(mContext!!, R.style.dialog_loading)
        loadingDialog?.apply {
            setContentView(view)
            window?.setGravity(Gravity.CENTER)
            setCanceledOnTouchOutside(false)
            setCancelable(true)
            val activity = mContext as Activity
            if (activity.isFinishing || (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed)) {
                return
            }
            show()
        }
    }
}