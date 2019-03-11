package com.mineyang.yang.utils

import AppManager
import android.content.Context
import android.content.SharedPreferences
import java.io.*

/**
 * SharedPreferences工具
 */
class SharedSingleton private constructor(context: Context) {
    private var perPreferences: SharedPreferences? = null

    init {
        if (perPreferences == null) {
            perPreferences = context.getSharedPreferences("oneMedia", Context.MODE_PRIVATE)
        }
    }

    fun setBoolean(key: String, value: Boolean) {
        perPreferences?.apply { edit().putBoolean(key, value).apply() }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return perPreferences?.getBoolean(key, defaultValue) ?: defaultValue
    }

    fun setString(key: String, value: String) {
        perPreferences?.apply { edit().putString(key, value).apply() }
    }

    fun setString(key: String) {
        perPreferences?.apply { edit().putString(key, null).apply() }
    }

    fun getString(key: String): String {
        return perPreferences?.getString(key, "") ?: ""
    }

    fun setInt(key: String, value: Int) {
        perPreferences?.apply { edit().putInt(key, value).apply() }
    }

    @JvmOverloads
    fun getInt(key: String, value: Int = 0): Int {
        return perPreferences?.getInt(key, value) ?: value
    }

    fun setLong(key: String, value: Long) {
        perPreferences?.apply { edit().putLong(key, value).apply() }
    }

    @JvmOverloads
    fun getLong(key: String, value: Long = 0L): Long {
        return perPreferences?.getLong(key, value) ?: value
    }

    companion object {
        val instance: SharedSingleton by lazy {
            SharedSingleton(AppManager.instance.currentActivity())
        }

        /**
         * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
         * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
         *
         * @param object 待加密的转换为String的对象
         * @return String   加密后的String
         */
        fun object2String(any: Any): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream: ObjectOutputStream
            return try {
                objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
                objectOutputStream.writeObject(any)
                val string = String(android.util.Base64.encode(byteArrayOutputStream.toByteArray(), android.util.Base64.DEFAULT))
                objectOutputStream.close()
                string
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        }

        /**
         * 使用Base64解密String，返回Object对象
         *
         * @param objectString 待解密的String
         * @return object      解密后的object
         */
        fun string2Object(objectString: String?): Any? {
            try {
                val mobileBytes = android.util.Base64.decode(objectString!!.toByteArray(), android.util.Base64.DEFAULT)
                val byteArrayInputStream = ByteArrayInputStream(mobileBytes)
                val objectInputStream: ObjectInputStream
                objectInputStream = ObjectInputStream(byteArrayInputStream)
                val any = objectInputStream.readObject()
                objectInputStream.close()
                return any
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    }
}