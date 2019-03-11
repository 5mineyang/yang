package com.mineyang.yang.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 *  Description :动态权限管理 * 记得在onRequestPermissionsResult方法里调一下当前的onRequestPermissionsResult方法

 *  Author:yang

 *  Email:1318392199@qq.com

 *  Date: 2019/1/21
 */
object PermissionUtils {
    private val RESULT_CODE_TAKE_CAMERA = 7461    //拍照
    private val RESULT_CODE_OPEN_ALBUM = 7462     //读写
    private val RESULT_CODE_SOUND_RECORD = 7463   //录音
    private val RESULT_CODE_LOCATION = 7464   //录音

    private var locationCallback: (() -> Unit)? = null        //相机回调
    private var cameraCallback: (() -> Unit)? = null        //相机回调
    private var readAndWriteCallback: (() -> Unit)? = null  //读写回调
    private var audioCallback: (() -> Unit)? = null         //录音回调

    /**
     * 相机权限申请
     */
    fun camera(context: Context, cameraCallback: () -> Unit) {
        PermissionUtils.cameraCallback = cameraCallback
        permission(context, Manifest.permission.CAMERA, RESULT_CODE_TAKE_CAMERA, cameraCallback)
    }

    /**
     * 位置权限申请
     */
    fun location(context: Context, locationCallback: () -> Unit) {
        PermissionUtils.locationCallback = locationCallback
        permission(context, Manifest.permission.ACCESS_FINE_LOCATION, RESULT_CODE_LOCATION, locationCallback)
    }

    /**
     * 读写权限申请
     */
    fun readAndWrite(context: Context, readAndWriteCallback: () -> Unit) {
        PermissionUtils.readAndWriteCallback = readAndWriteCallback
        permission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, RESULT_CODE_OPEN_ALBUM, readAndWriteCallback)
    }

    /**
     * 录音权限申请
     */
    fun audio(context: Context, audioCallback: () -> Unit) {
        PermissionUtils.audioCallback = audioCallback
        permission(context, Manifest.permission.RECORD_AUDIO, RESULT_CODE_SOUND_RECORD, audioCallback)
    }

    /**
     * 权限申请结果
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val cameraAccepted = grantResults.let {
            if (it.isNotEmpty()) {
                it[0] == PackageManager.PERMISSION_GRANTED
            } else {
                return
            }
        }
        when (requestCode) {
            RESULT_CODE_TAKE_CAMERA -> {    //拍照
                if (cameraAccepted) {
                    cameraCallback?.let { it() }
                } else {
                    //用户拒绝
                    ToastUtil.showShort("请开启应用拍照权限")
                }
            }
            RESULT_CODE_OPEN_ALBUM -> { //读写
                if (cameraAccepted) {
                    readAndWriteCallback?.let { it() }
                } else {
                    ToastUtil.showShort("请开启应用读取权限")
                }
            }
            RESULT_CODE_SOUND_RECORD -> { //录音
                if (cameraAccepted) {
                    audioCallback?.let { it() }
                } else {
                    ToastUtil.showShort("请开启应用录音权限")
                }
            }
            RESULT_CODE_LOCATION -> {
                if (cameraAccepted) {
                    locationCallback?.let { it() }
                }
            }
        }
    }

    //权限申请
    private fun permission(context: Context, systemCode: String, resultCode: Int, callback: () -> Unit) {
        //判断是否有权限
        if (ContextCompat.checkSelfPermission(context, systemCode) == PackageManager.PERMISSION_GRANTED) {
            callback()
        } else {
            //申请权限
            ActivityCompat.requestPermissions(context as Activity, arrayOf(systemCode), resultCode)
        }
    }
}