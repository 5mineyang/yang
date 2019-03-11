package com.mineyang.yang.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * 简单存储工具类
 */
class SharedPreferencesHelper(context:Context,fileName:String){
    private var sharedPreferences:SharedPreferences
    private var editor:SharedPreferences.Editor

    init {
        //储存 输入文件名
        sharedPreferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    /**
     * 存储
     */
    fun put(key:String,value:Any){
        if (value is String) {
            editor.putString(key, value)
        } else if (value is Int) {
            editor.putInt(key, value)
        } else if (value is Boolean) {
            editor.putBoolean(key, value)
        } else if (value is Float) {
            editor.putFloat(key, value)
        } else if (value is Long) {
            editor.putLong(key, value)
        } else {
            editor.putString(key, value.toString())
        }
        editor.commit()
    }

    /**
     * 获取存储的数据
     */
    fun getSharedPreference(key:String,default:Any):Any{
        return if (default is String) {
            return sharedPreferences.getString(key, default)
        } else if (default is Int) {
            return sharedPreferences.getInt(key, default)
        } else if (default is Boolean) {
            return sharedPreferences.getBoolean(key, default)
        } else if (default is Float) {
            return sharedPreferences.getFloat(key, default)
        } else if (default is Long) {
            return sharedPreferences.getLong(key, default)
        } else {
           return sharedPreferences.getString(key, null)
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    fun remove(key:String) {
        editor.remove(key)
        editor.commit()
    }

    /**
     * 清除所有数据
     */
     fun clear() {
        editor.clear()
        editor.commit()
    }

    /**
     * 查询某个key是否存在
     */
    fun contain(key:String): Boolean{
        return sharedPreferences.contains(key)
    }

    /**
     * 返回所有的键值对
     */
    fun getAll(): Map<String, *>{
        return sharedPreferences.getAll()
    }
}