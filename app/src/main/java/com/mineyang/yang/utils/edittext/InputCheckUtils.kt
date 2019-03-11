package com.mineyang.yang.utils.edittext

import java.util.regex.Pattern

/**
 *  Description :输入格式验证

 *  Author:yang

 *  Email:1318392199@qq.com

 *  Date: 2019/1/17
 */
object InputCheckUtils {
    /**
     * 正则表达式校验
     *
     * @param str   源字符串
     * @param regex 正则表达式
     * @return valid
     */
    private fun checkRegex(str: String, regex: String): Boolean {
        val p = Pattern.compile(regex)
        val m = p.matcher(str)
        return m.matches()
    }

    /**
     * 密码 6到16位区分大小写
     */
    fun checkPass6_16(pass: String): Boolean {
        return checkRegex(pass, "^[a-zA-Z0-9]{6,12}$")
    }

    /**
     * 任意字符 4到16位
     */
    fun checkString4_6(str: String): Boolean {
        return str.length in 4..16
    }

    /**
     * 手机号
     */
    fun checkPhone(phone: String): Boolean {
        return checkRegex(phone, "^[1][3,4,5,7,8][0-9]{9}$")
    }

    /**
     * 邮箱
     */
    fun checkEmail(email: String): Boolean {
        return email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*".toRegex())
    }

    /**
     * QQ 5到12位数字 不能以0开头
     */
    fun checkQQ(qq: String): Boolean {
        return checkRegex(qq, "^[1-9][0-9]{4,11}")
    }

    /**
     * 微信 6至20位字母 数字 下划线和减号（也可以是手机号）
     */
    fun checkWechat(wechat: String): Boolean {
        return checkRegex(wechat, "^[a-zA-Z][a-zA-Z0-9_-]{5,19}$") || checkPhone(wechat)
    }

    /**
     * 固定电话（只做20位限制）
     */
    fun checkTel(tel: String): Boolean {
//        //"^0(10|2[0-5789]-|//d{3})-?//d{7,8}$"
//        //"^1\\d{10}\$|^(0\\d{2,3}-?|\\(0\\d{2,3}\\))?[1-9]\\d{4,7}(-\\d{1,8})?$"
//        return checkRegex(tel, "^0(10|2[0-5789]-|//d{3})-?//d{7,8}$")

        return tel.length in 0..20
    }

    /**
     * 验证电话号码
     */
    fun checkTelephone(tel: String): Boolean {
        return checkRegex(tel, "\\d{3}-\\d{8}|\\d{4}-\\d{7}|\\d{4}-\\d{8}|\\d{11}|\\d{12}")
    }

    /**
     * 5-6位数字
     */
    fun checkFiveOrSixNum(qq: String): Boolean {
        return checkRegex(qq, "^[0-9]{5,6}")
    }

    /**
     * 验证URL
     */
    fun checkUrl(url: String): Boolean {
        return checkRegex(url, "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
    }

    /**
     * 验证IP地址
     */
    fun checkIpAddress(ip: String): Boolean {
        return checkRegex(ip, "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.")
    }

    /**
     * 验证身份证号
     */
    fun checkIdCardNo(sfz: String): Boolean {
        return checkRegex(sfz, "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$") || checkRegex(sfz, "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])(\\d{4}|\\d{3}(\\d|X|x))$")
    }
}
