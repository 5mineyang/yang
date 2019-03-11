package com.mineyang.yang.constant

import com.mineyang.yang.BuildConfig
import com.mineyang.yang.utils.SDPathUtils


/**
 * 静态常量
 */
object IConstants {
    val BASE_URL = BuildConfig.SERVER_IP
    const val ROOT_NAME = BuildConfig.APP_DIR

    /**
     * ==============================路径==============================
     */
    //语音路径
    val DIR_AUDIO_STR = SDPathUtils.getPublicStorageDir("${ROOT_NAME}/audio")
}