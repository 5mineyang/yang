package com.mineyang.yang.utils.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Handler
import android.util.Log
import com.mineyang.yang.utils.toSelfSetting
import com.mineyang.yang.view.dialog.DialogCommon
import java.io.File
import java.util.*

/**
 * 录音管理类
 */
class AudioManager(private val mDir: String) {
    private var onAccentuation: ((voiceValue: Int) -> Unit)? = null     //分贝值返回回调
    private var mMediaRecorder: MediaRecorder? = null                   //音频工具
    private var startTime = 0L                                          //开始时间
    private var recordTime = 0F                                         //音频时间
    private var currentFilePath: String? = null                         //音频路径
    private val MAX_LENGTH = 1000 * 60 * 10                             //录制最长时间
    var canSendAudio = false                                            //是否可以发送语音（防止个别手机权限提醒不一样）
    private val mHandler = Handler()                                    //子线程查询当前分贝
    private val mUpdateMicStatusTimer = Runnable {
        updateMicStatus()
    }

    /**
     * 准备录音
     */
    fun readyAudio(context: Context, onAccentuation: (voiceValue: Int) -> Unit = {}) {
        try {
            this.onAccentuation = onAccentuation
            val dir = File(mDir)
            if (!dir.exists()) {
                dir.mkdirs()
            } else {
                if (!dir.isDirectory) {
                    dir.delete()
                    dir.mkdirs()
                }
            }
            val fileName = generateFileName()
            val file = File(dir, fileName)
            currentFilePath = file.absolutePath
            mMediaRecorder = MediaRecorder()
            //设置MediaRecorder的音频源为麦克风
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            //设置音频格式
            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            //设置音频编码
            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            //设置输出文件
            mMediaRecorder?.setOutputFile(file.absolutePath)
            //最长录制时间
            mMediaRecorder?.setMaxDuration(MAX_LENGTH)
            //准备录音
            mMediaRecorder?.prepare()
            //开始
            mMediaRecorder?.start()
            startTime = System.currentTimeMillis()
            updateMicStatus()
            canSendAudio = true
        } catch (e: Exception) {
            if (e.toString() == "java.io.IOException: Permission deny!") {
                canSendAudio = false
                //提示用户去设置里设置相应权限
                DialogCommon(context, "提示", "使用该功能需要麦克风权限，请前往系统设置开启权限", rightText = "去设置",
                        onRightClick = {
                            toSelfSetting(context)
                        }).show()
            }
        }
    }

    //根据分贝改变动画回调
    private fun updateMicStatus() {
        val ratio = mMediaRecorder?.maxAmplitude
        onAccentuation!!(ratio!!)
        Log.i("ratioTest", "========================================$ratio")
        mHandler.postDelayed(mUpdateMicStatusTimer, 200)
    }

    /**
     * 随机生成文件的名称
     */
    private fun generateFileName(): String {
        return UUID.randomUUID().toString() + ".amr"
    }

    /**
     * 释放资源
     */
    fun releaseAudio(audioPathCallBack: (path: String, recordTime: Float) -> Unit = { _, _ -> }) {
        if (mMediaRecorder != null) {
            mMediaRecorder!!.reset()
            mMediaRecorder = null
            mHandler.removeCallbacks(mUpdateMicStatusTimer)
            recordTime = getRecordTime()
            audioPathCallBack(currentFilePath!!, recordTime)
        }
//        try {
//        } catch (e: Exception) {
//            if (e.toString() == "java.lang.NullPointerException: Attempt to get length of null array") {
//
//            }
//        }
    }

    fun getRecordTime(): Float {
        return (System.currentTimeMillis() - startTime) / 1000f
    }

    /**
     * 取消录音
     */
    fun cancelAudio() {
        releaseAudio()
        if (currentFilePath != null) {
            val file = File(currentFilePath)
            file.delete()
            currentFilePath = null
        }
    }

    companion object {
        private var mInstance: AudioManager? = null

        fun getInstance(dir: String): AudioManager {
            if (mInstance == null) {
                synchronized(AudioManager::class.java) {
                    if (mInstance == null) {
                        mInstance = AudioManager(dir)
                    }
                }
            }
            return mInstance!!
        }
    }
}