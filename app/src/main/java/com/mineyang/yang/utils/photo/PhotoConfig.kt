package com.mineyang.yang.utils.photo

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment

/**
 * Created by wangru
 * Date: 2017/9/22  9:57
 * mail: 1902065822@qq.com
 * describe:获取图片
 */
class PhotoConfig {
    var context: Context? = null
    var activity: Activity? = null
    var fragment: Fragment? = null
    var tag: String? = null//标记
    var isCamera = false//获取照片方式 ture 拍照 false 图库
    var isCuted: Boolean = false//是否需要裁剪
    var cameraPath: String? = null//拍照路径
    var cutPath: String? = null//裁剪路径
    var isCutAuto: Boolean = false//true为如果原图小于裁剪大小则按原图占比小的标准裁剪
    var isDeleteOld: Boolean = false//是否删除拍照裁剪之前的图片
    var cutWidth = CUT_WIDTH_HEIGHT_DEFAULT
    var cutHeight = CUT_WIDTH_HEIGHT_DEFAULT
    var onPathCallback: ((path: String) -> Unit)? = null

    constructor(activity: Activity, isCamera: Boolean, onPathCallback: (path: String) -> Unit) {
        this.context = activity
        this.activity = activity
        this.isCamera = isCamera
        this.onPathCallback = onPathCallback
    }

    constructor(fragment: Fragment, context: Context, isCamera: Boolean, onPathCallback: (path: String) -> Unit) {
        this.context = activity
        this.fragment = fragment
        this.isCamera = isCamera
        this.onPathCallback = onPathCallback
    }

    override fun toString(): String {
        return "PhotoConfig{" +
                "tag=" + tag +
                "isCamera=" + isCamera +
                ", isCuted=" + isCuted +
                ", cameraPath='" + cameraPath + '\'' +
                ", cutPath='" + cutPath + '\'' +
                ", isCutAuto=" + isCutAuto +
                ", isDeleteOld=" + isDeleteOld +
                ", cutWidth=" + cutWidth +
                ", cutHeight=" + cutHeight +
                '}'
    }

    companion object {
        val CUT_WIDTH_HEIGHT_DEFAULT = 256//
    }
}

