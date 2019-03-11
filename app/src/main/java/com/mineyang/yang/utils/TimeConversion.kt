package com.mineyang.yang.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Description :时间戳转换

 *  Author:yang

 *  Email:1318392199@qq.com

 *  Date: 2018/8/20
 */
@SuppressLint("SimpleDateFormat")
object TimeConversion {
    //今天的时间 年-月-日
    private val mFormat = SimpleDateFormat("yyyy-MM-dd")
    private val mMonthFormat = SimpleDateFormat("MM-dd")
    private val mDate: String? = mFormat.format(Date(System.currentTimeMillis()))

    //如果是今天 就展示具体时间 否则展示年月日
    fun getDateToString(milSecond: Long): String {
        var strDate: String
        //如果时间是今天 那就显示详细时间 不显示日期
        if (getDateToStringDay(milSecond) == mDate) {
            strDate = getDateToStringTime(milSecond)
        } else {
            strDate = getDateToStringDay(milSecond)
        }
        return strDate
    }

    // 将时间戳转为日期字符串 月-日
    fun getDateToStringMonth(milSecond: Long): String {
        val date = Date(milSecond)
        return mMonthFormat.format(date)
    }

    // 将时间戳转为日期字符串 年-月-日
    private fun getDateToStringDay(milSecond: Long): String {
        val date = Date(milSecond)
        return mFormat.format(date)
    }

    // 将时间戳转为具体时间字符串
    private fun getDateToStringTime(milSecond: Long): String {
        val date = Date(milSecond)
        val format = SimpleDateFormat("HH:mm")
        return format.format(date)
    }

    // 将时间戳转为具体时间字符串
    fun getDateDayTime(milSecond: Long): String {
        val date = Date(milSecond)
        val format = SimpleDateFormat("MM-dd")
        return format.format(date)
    }

    // 将时间戳转为具体时间字符串
    fun getDateHourTime(milSecond: Long): String {
        val date = Date(milSecond)
        val format = SimpleDateFormat("MM-dd HH:mm")
        return format.format(date)
    }

    /**
     * @param createTime 创建时间
     */
    fun getDay(createTime: Long): String {
        val createDate = Date(createTime) // 创建时间
        val systemDate = Date(System.currentTimeMillis())// 当前时间
        val simpleDateFormatMM = SimpleDateFormat("yyyyMM") // 年月
        val simpleDateFormatdd = SimpleDateFormat("dd") // 日
        val currentMMTime = Integer.parseInt(simpleDateFormatMM.format(systemDate))        //月
        val currentDayTime = Integer.parseInt(simpleDateFormatdd.format(systemDate))       //日
        val lastMMTime = Integer.parseInt(simpleDateFormatMM.format(createDate))           //月
        val lastDayTime = Integer.parseInt(simpleDateFormatdd.format(createDate))          //日
        val differMMTime = currentMMTime - lastMMTime          //创建年月-系统年月
        val differDayTime = currentDayTime - lastDayTime       //创建日-系统日
        if (differDayTime == 0) {//日
            val minute = (System.currentTimeMillis() - createTime) / 1000 / 60 //获取分钟差
            return if (minute < 1) {
                "刚刚"
            } else if (minute < 60) {
                minute.toString() + "分钟前"
            } else {
                (minute / 60).toString() + "小时前"//小时
            }
        } else return if (differDayTime == 1) {
            "昨天"
        } else {
            return getDateDayTime(createTime)
        }
    }
}