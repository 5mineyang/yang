package com.mineyang.yang.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation


/**
 *  Description : view隐藏展开带动画

 *  Author:yang

 *  Email:1318392199@qq.com

 *  Date: 2018/8/24
 */
class HiddenAnimUtils private constructor(context: Context, private val hideView: View, private val down: View//需要展开隐藏的布局，开关控件
                                          , height: Int) {

    private val mHeight: Int//伸展高度

    private var animation: RotateAnimation? = null//旋转动画

    init {
        val mDensity = context.resources.displayMetrics.density
        mHeight = (mDensity * height + 0.5).toInt()//伸展高度
    }

    /**
     * 开关
     */
    fun toggle() {
        startAnimation()
        if (View.VISIBLE === hideView.visibility) {
            closeAnimate(hideView)//布局隐藏
        } else {
            openAnim(hideView)//布局铺开
        }
    }

    /**
     * 开关旋转动画
     */
    private fun startAnimation() {
        if (View.VISIBLE === hideView.visibility) {
            animation = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        } else {
            animation = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        }
        animation!!.duration = 30//设置动画持续时间
        animation!!.interpolator = LinearInterpolator()
        animation!!.repeatMode = Animation.REVERSE//设置反方向执行
        animation!!.fillAfter = true//动画执行完后是否停留在执行完的状态
        down.startAnimation(animation)
    }

    private fun openAnim(v: View) {
        v.visibility = View.VISIBLE
        val animator = createDropAnimator(v, 0, mHeight)
        animator.start()
    }

    private fun closeAnimate(view: View) {
        val origHeight = view.height
        val animator = createDropAnimator(view, origHeight, 0)
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }
        })
        animator.start()
    }

    private fun createDropAnimator(v: View, start: Int, end: Int): ValueAnimator {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {

            override fun onAnimationUpdate(arg0: ValueAnimator) {
                val value = arg0.animatedValue as Int
                val layoutParams = v.layoutParams
                layoutParams.height = value
                v.layoutParams = layoutParams
            }
        })
        return animator
    }

    companion object {
        /**
         * 构造器(可根据自己需要修改传参)
         * @param context 上下文
         * @param hideView 需要隐藏或显示的布局view
         * @param down 按钮开关的view
         * @param height 布局展开的高度(根据实际需要传)
         */
        fun newInstance(context: Context, hideView: View, down: View, height: Int): HiddenAnimUtils {
            return HiddenAnimUtils(context, hideView, down, height)
        }
    }
}