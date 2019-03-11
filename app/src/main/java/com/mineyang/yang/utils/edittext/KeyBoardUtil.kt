package com.mineyang.yang.utils.edittext

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 *  Description :系统键盘监听

 *  Author:yang

 *  Email:1318392199@qq.com

 *  Date: 2019/1/14
 */
class KeyBoardUtil(private val activity: Activity, private val editText: EditText) {
    private var mInputManager: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private var mOnKeyboardListener: OnKeyboardListener? = null
    private var lastheight = 0

    //设置监听 返回打开键盘和关闭键盘回调
    fun setListener(listener: OnKeyboardListener) {
        setOnKeyboardListener(listener)
        //拿到页面的共同布局
        val decorView = activity.window.decorView
        //设置最底层布局的变化监听
        decorView.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            //int measuredHeight = decorView.getMeasuredHeight();//拿尺寸不好用
            val rect = Rect()
            //拿这个控件在屏幕上的可见区域
            decorView.getWindowVisibleDisplayFrame(rect)
            val height = rect.height()
            //第一次刚进来的时候,给上一次的可见高度赋一个初始值,
            // 然后不需要再做什么比较了,直接return即可
            if (lastheight == 0) {
                lastheight = height
                return@OnGlobalLayoutListener
            }
            //当前这一次的可见高度比上一次的可见高度要小(有比较大的高度差,大于300了),
            // 认为是软键盘弹出
            if (lastheight - height > 300) {
                //隐藏这个RoomFragment中的控件
                if (mOnKeyboardListener != null) {
                    mOnKeyboardListener!!.onKeyboardShow(lastheight - height)
                }
            }
            //当前这一次的可见高度比上一次的可见高度要大,认为是软键盘收缩
            if (height - lastheight > 300) {
                if (mOnKeyboardListener != null) {
                    mOnKeyboardListener!!.onKeyboardHide(height - lastheight)
                }
            }
            //记录下来
            lastheight = height
        })
    }

    private fun setOnKeyboardListener(keyboardListener: OnKeyboardListener) {
        mOnKeyboardListener = keyboardListener
    }

    interface OnKeyboardListener {
        fun onKeyboardShow(i: Int)

        fun onKeyboardHide(i: Int)
    }

    /**
     * 展示输入法软键盘
     */
    fun showInputKeyboard() {
        //设置可获得焦点
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        //设置获取焦点
        editText.requestFocus()
        mInputManager.showSoftInput(editText, 0)
    }

    /**
     * 隐藏输入法键盘
     */
    fun hideInputKeyboard() {
        //输入框失去焦点
        editText.isFocusable = false
        //取消调用系统输入法
        mInputManager.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
    }

    /**
     * 键盘是否显示
     */
    fun isShowInputKeyboard(): Boolean {
        //获取当屏幕内容的高度
        val screenHeight = activity.window.decorView.height
        //获取View可见区域的bottom
        val rect = Rect()
        //DecorView即为activity的顶级view
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom
    }
}