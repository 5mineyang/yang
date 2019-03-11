package com.mineyang.yang.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import java.io.FileInputStream
import java.io.FileNotFoundException

/**
 * view工具
 */

/**
 * 屏幕高
 */
fun Activity.getScreenHeight(): Int {
    val localDisplayMetrics = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
    return localDisplayMetrics.heightPixels
}

/**
 * 屏幕宽
 */
fun Activity.getScreenWidth(): Int {
    val localDisplayMetrics = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
    return localDisplayMetrics.widthPixels
}

/**
 * 获取状态栏高度
 */
fun Context.getBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

/**
 * 状态栏透明
 */
fun Activity.setStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)//隐藏状态栏但不隐藏状态栏字体
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN) //隐藏状态栏，并且不显示字体
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//实现状态栏文字颜色为暗色
    }
}


fun Activity.setStatusBar(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = resources.getColor(color)//设置状态栏颜色
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//实现状态栏图标和文字颜色为暗色
    }
}

/**
 * 隐藏状态栏
 */
fun Activity.statusHind() {
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * 是否有状态栏
 */
fun Activity.hasNavBar(): Boolean {
    val windowManager = windowManager
    val d = windowManager.defaultDisplay

    val realDisplayMetrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        d.getRealMetrics(realDisplayMetrics)
    }

    val realHeight = realDisplayMetrics.heightPixels
    val realWidth = realDisplayMetrics.widthPixels

    val displayMetrics = DisplayMetrics()
    d.getMetrics(displayMetrics)

    val displayHeight = displayMetrics.heightPixels
    val displayWidth = displayMetrics.widthPixels

    return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
}

/**
 * 设置view的margin
 */
fun View.setMarginExt(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val layout = when (layoutParams) {
        is RelativeLayout.LayoutParams -> layoutParams as RelativeLayout.LayoutParams
        is LinearLayout.LayoutParams -> layoutParams as LinearLayout.LayoutParams
        is FrameLayout.LayoutParams -> layoutParams as FrameLayout.LayoutParams
        is RecyclerView.LayoutParams -> layoutParams as RecyclerView.LayoutParams
        is ViewGroup.MarginLayoutParams -> layoutParams as ViewGroup.MarginLayoutParams
        else -> null
    }
    if (layout == null) return
    val leftResult = left ?: layout.leftMargin
    val rightResult = right ?: layout.rightMargin
    val topResult = top ?: layout.topMargin
    val bottomResult = bottom ?: layout.bottomMargin

    layout.let {
        it.setMargins(leftResult, topResult, rightResult, bottomResult)
        layoutParams = it
    }
}

/**
 * 设置view的padding
 */
fun View.setPaddingExt(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    setPadding(left ?: paddingLeft, top ?: paddingTop, right ?: paddingRight, bottom ?: paddingBottom)
}

/**
 * 设置view的高度
 */
fun View.setWidthHeightExt(width: Int? = null, height: Int? = null) {
    val layout = when (layoutParams) {
        is RelativeLayout.LayoutParams -> layoutParams as RelativeLayout.LayoutParams
        is LinearLayout.LayoutParams -> layoutParams as LinearLayout.LayoutParams
        is FrameLayout.LayoutParams -> layoutParams as FrameLayout.LayoutParams
        is RecyclerView.LayoutParams -> layoutParams as RecyclerView.LayoutParams
        is ViewGroup.MarginLayoutParams -> layoutParams as ViewGroup.MarginLayoutParams
        else -> null
    }
    if (layout == null) return
    val widthResult = width ?: layout.width
    val heightResult = height ?: layout.height
    layout.let {
        it.width = widthResult
        it.height = heightResult
        layoutParams = it
    }
}

/**
 * 设置view的Scale
 */
fun View.setWidthScaleExt(width: Int? = null) {
    val layout = when (layoutParams) {
        is RelativeLayout.LayoutParams -> layoutParams as RelativeLayout.LayoutParams
        is LinearLayout.LayoutParams -> layoutParams as LinearLayout.LayoutParams
        is FrameLayout.LayoutParams -> layoutParams as FrameLayout.LayoutParams
        is RecyclerView.LayoutParams -> layoutParams as RecyclerView.LayoutParams
        is ViewGroup.MarginLayoutParams -> layoutParams as ViewGroup.MarginLayoutParams
        else -> null
    }
    if (layout == null) return
    val widthResult = width ?: layout.width
    val heightResult = height ?: layout.height
    layout.let {
        it.width = widthResult
        it.height = heightResult
        layoutParams = it
    }
}

/**
 * view转成图片
 */
fun View.toBitmapExt(): Bitmap {
    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    draw(c)
    return b
}

/**
 * view是否在屏幕中可见
 */
fun Activity.viewIsVisible(view: View): Boolean {
    val p = Point()
    windowManager.defaultDisplay.getSize(p)
    val screenWidth = p.x
    val screenHeight = p.y
    //屏幕范围
    val rect = Rect(0, 0, screenWidth, screenHeight)
    //view是否在屏幕中
    return view.getLocalVisibleRect(rect)
}

/**
 * 屏幕密度
 */
fun Activity.getDensity(): Int = resources.displayMetrics.density.toInt()

fun Activity.getDensityDpi(): Int = resources.displayMetrics.densityDpi

/**
 * 数值转dp
 */
fun Context.dp2px(dpVal: Int): Int = dp2px(dpVal.toFloat()).toInt()

fun Context.dp2px(dpVal: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, this.resources.displayMetrics)

/**
 * 改变背景亮度
 */
fun Activity.backgroundAlphaExt(bgAlpha: Float) {
    val lp = this.window?.attributes
    //0.0-1.0
    lp?.alpha = bgAlpha
//    if (bgAlpha == 1f) {
//        //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
//        this.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//    } else {
//        //此行代码主要是解决在华为手机上半透明效果无效的bug
//        this.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//    }
    this.window?.attributes = lp
}

/**
 * 加载本地图片 转bitmap
 */
fun getLoacalBitmap(url: String): Bitmap? {
    try {
        val fis = FileInputStream(url)
        return BitmapFactory.decodeStream(fis)  ///把流转化为Bitmap图片
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        return null
    }
}

/**
 * 跳转到自己应用的设置页面
 */
fun toSelfSetting(context: Context) {
    val mIntent = Intent()
    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (Build.VERSION.SDK_INT >= 9) {
        mIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        mIntent.data = Uri.fromParts("package", context.packageName, null)
    } else if (Build.VERSION.SDK_INT <= 8) {
        mIntent.action = Intent.ACTION_VIEW
        mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails")
        mIntent.putExtra("com.android.settings.ApplicationPkgName", context.packageName)
    }
    context.startActivity(mIntent)
}