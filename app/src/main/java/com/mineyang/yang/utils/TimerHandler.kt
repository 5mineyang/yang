package com.mineyang.yang.utils

import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.widget.TextView
import com.mineyang.yang.AppBaseActivity
import com.mineyang.yang.R
import java.lang.ref.WeakReference

/**
 * handler 持有当前 Activity 的弱引用防止内存泄露（倒计时）
 */
class TimerHandler(activity: AppBaseActivity, private val tv: TextView) : Handler() {
    private var mWeakActivity = WeakReference<AppBaseActivity>(activity)
    override fun handleMessage(msg: Message?) {
        val activity = mWeakActivity.get() ?: return
        when (msg?.what) {
            activity.timingOver -> {
                tv.text = "获取验证码"
                tv.setTextColor(activity.resources.getColor(R.color.colorPrimary))
                tv.isClickable = true
                activity.totalTime = -1
            }
            activity.timing -> {
                if (activity.totalTime == 1) {
                    activity.timerHandler.sendEmptyMessageDelayed(activity.timingOver, 1000)
                } else {
                    if (activity.totalTime == -1) {
                        activity.totalTime = 60
                        tv.isClickable = false
                    } else {
                        activity.totalTime--
                    }
                    activity.timerHandler.sendEmptyMessageDelayed(activity.timing, 1000)
                }
                tv.text = "${activity.totalTime}秒后重试"
                tv.setTextColor(Color.parseColor("#999999"))
            }
        }
    }
}