package com.mineyang.yang.utils.audio

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer

/**
 * 播放语音
 */
object MediaManager {
    private var mPlayer: MediaPlayer? = null
    private var mAudioManager: AudioManager? = null
    private var isPause = false        //是否暂停
    var mIsCall = true                 //是否是听筒模式

    //开始播放
    fun play(context: Context, filePath: String, playOk: () -> Unit, playFinish: () -> Unit) {
        if (filePath.isBlank()) {
            return
        }
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        } else {
            mPlayer?.reset()
        }
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        //默认听筒播放
        mPlayer?.setAudioStreamType(AudioManager.STREAM_VOICE_CALL)
        mPlayer?.setOnErrorListener { mp, what, extra ->
            mPlayer?.reset()
            false
        }
        mPlayer?.setDataSource(filePath)
        mPlayer?.prepare()
        mPlayer?.start()
        playOk()
        mPlayer?.setOnCompletionListener {
            playFinish()
        }
        isPause = false
    }

    //切换播放方式 true听筒 否则扬声器
    fun switchPlay(isCall: Boolean = true) {
        mIsCall = isCall
        mAudioManager?.isSpeakerphoneOn = !isCall
        if (isCall) {
            mAudioManager?.mode = AudioManager.MODE_NORMAL
        }
    }

    //是否正在播放
    fun isPlaying(): Boolean {
        return mPlayer?.isPlaying ?: false
    }

    //暂停播放
    fun pause() {
        if (mPlayer != null && mPlayer?.isPlaying!!) {
            mPlayer?.pause()
            isPause = true
        }
    }

    //继续播放
    fun resume() {
        if (mPlayer != null && isPause) {
            mPlayer?.start()
            isPause = false
        }
    }

    //清除资源
    fun release() {
        if (mPlayer != null) {
            mPlayer?.release()
            mPlayer = null
        }
    }
}