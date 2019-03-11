package com.mineyang.yang

import AppManager
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.mineyang.yang.utils.SharedSingleton
import com.mineyang.yang.utils.TimerHandler
import com.mineyang.yang.view.dialog.DialogCustom
import kotlinx.android.synthetic.main.layout_head.*

/**
 * 父Activity
 */
abstract class AppBaseActivity : Activity() {
    protected val sharedSingleton = SharedSingleton.instance
    lateinit var timerHandler: TimerHandler
    lateinit var myDialog: DialogCustom
    var totalTime = -1
    var timing = 1
    var timingOver = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        AppManager.instance.addActivity(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //禁止横屏
        myDialog = DialogCustom(AppManager.instance.currentActivity())
        setShowBack(true)
        initData()
        initView()
        initListener()
    }

    /**
     * 设置标题名
     */
    protected fun setHeadName(headName: String) {
        mTitle?.text = headName
    }

    /**
     * 根据资源id设置标题名
     */
    protected fun setHeadName(resourceId: Int) {
        mTitle?.setText(resourceId)
    }

    /**
     * 设置标题背景色
     */
    protected fun setHeadBack(color: String) {
        mTopView?.setBackgroundColor(Color.parseColor(color))
    }

    /**
     * 根据资源id设置标题背景色
     */
    protected fun setHeadBack(resourceId: Int) {
        mTopView?.setBackgroundResource(resourceId)
    }

    /**
     * 是否显示返回键   默认显示
     */
    protected fun setShowBack(isShowBack: Boolean) {
        mBack?.let { it ->
            it.visibility = if (isShowBack) {
                it.setOnClickListener { onBackPressed() }
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    abstract fun getLayoutId(): Int

    open fun initData() {}

    open fun initView(){}

    open fun initListener() {}

    override fun onDestroy() {
        super.onDestroy()
        myDialog.unBindContext()
        AppManager.instance.toFinish(javaClass)
    }
}