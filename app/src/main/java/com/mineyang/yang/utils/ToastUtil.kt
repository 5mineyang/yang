package com.mineyang.yang.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.mineyang.yang.AppMainApplication
import com.mineyang.yang.R

@SuppressLint("InflateParams", "StaticFieldLeak")
object ToastUtil {
    private var isDebug = false
    private var text = ""
    private var firstTime = 0L
    var isShow = true

    var mToast: Toast? = null

    private var currentToast: Toast? = null

    private var toastView: View? = null

    private fun createToast(context: Context, msg: CharSequence, mDuration: Int): Toast? {
        val mView = LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null)
        val toastMsg_tv: TextView = mView.findViewById(R.id.toastMsg_tv)
        toastMsg_tv.text = msg
        mToast = Toast(context)
        mToast?.apply {
            view = mView
            duration = mDuration
            setGravity(Gravity.CENTER, 0, 0)
        }
        return mToast
    }

    private fun canShow(time: Long, msg: CharSequence): Boolean {
        val secondTime = System.currentTimeMillis()
        val canShow = secondTime - firstTime > time || text != msg.toString()
        if (canShow) {
            firstTime = secondTime
            text = msg.toString()
        }
        return canShow
    }

    fun showShort(message: CharSequence) {
        if (canShow(2000, message) && isShow) {
            cancel()
            mToast = createToast(AppMainApplication.Instance, message, Toast.LENGTH_SHORT)
            mToast?.show()
        }
    }

    fun showShort(resId: Int) {
        val message = AppMainApplication.Instance.getString(resId)
        if (canShow(2000, message) && isShow) {
            cancel()
            mToast = createToast(AppMainApplication.Instance, message, Toast.LENGTH_SHORT)
            mToast?.show()
        }
    }

    fun showLong(message: CharSequence) {
        if (canShow(3500, message) && isShow) {
            cancel()
            mToast = createToast(AppMainApplication.Instance, message, Toast.LENGTH_LONG)
            mToast?.show()
        }
    }

    private fun cancel() {
        mToast?.cancel()
    }

    /**
     * 使用同1个toast,避免多toast重复问题
     */
    fun makeText(context: Context?, text: CharSequence, mDuration: Int): Toast? {
        if (currentToast == null && context != null) {
            currentToast = Toast.makeText(context, text, mDuration)
            toastView = currentToast!!.view
        }
        if (toastView != null) {
            currentToast?.apply {
                view = toastView
                setText(text)
                duration = mDuration
            }
        }
        return currentToast
    }

    fun showNetError() {
        showShort("获取网络失败")
    }
}