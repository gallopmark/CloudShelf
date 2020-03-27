package com.holike.cloudshelf.util

import android.text.TextUtils

class CheckUtils {

    companion object {
        fun isMobile(phone: String?): Boolean {
            if (phone.isNullOrEmpty()) return false
            val regex = Regex("[1]\\d{10}")
            return phone.matches(regex)
        }
    }

    /*正则表达式：要求6位以上，只能有大小写字母和数字，并且大小写字母和数字都要有。*/
    fun isPassword(source: String): Boolean {
        if (TextUtils.isEmpty(source)) return false
        //        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,500}$";
        val regex = Regex("^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,500}$")
        return source.matches(regex)
    }

}