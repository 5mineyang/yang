package com.mineyang.yang

import AppManager
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.mineyang.yang.utils.SharedSingleton
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * Created by zq on 2018/8/6
 */
@SuppressLint("StaticFieldLeak", "CheckResult")
class AppMainApplication : Application() {
    private lateinit var sharedSingleton: SharedSingleton

    init {
        AppManager.instance
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        Instance = this
        sharedSingleton = SharedSingleton.instance
        initBugly()
    }

    private fun initBugly() {
        val context = applicationContext
        // 获取当前包名
        val packageName = context.packageName
        // 获取当前进程名
        val processName = getProcessName(android.os.Process.myPid())
    }

    companion object {
        lateinit var Instance: AppMainApplication
    }

    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }
}