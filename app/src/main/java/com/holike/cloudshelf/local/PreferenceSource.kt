package com.holike.cloudshelf.local

import android.content.Context
import android.content.SharedPreferences
import com.holike.cloudshelf.CurrentApp
import com.holike.cloudshelf.bean.LoginBean

//登录成功持久化保存值
class PreferenceSource {
    companion object {
        private const val SOURCE_NAME = "cloudShelf"  //本地文件名

        private const val CLIENT_ID = "client_id" //http 请求cliid
        private const val TOKEN = "token" //http 请求token

        private const val PHONE = "phone" //当前登录用户的手机号码

        private const val LOGIN_STATUS = "login_status"  //登录状态 判断是否已经登录

        private fun getPreferences(): SharedPreferences = CurrentApp.getInstance().getSharedPreferences(SOURCE_NAME, Context.MODE_PRIVATE)

        fun setData(bean: LoginBean) {
            val sp = getPreferences()
            val editor = sp.edit().apply {
                putString(CLIENT_ID, bean.cliId)
                putString(TOKEN, bean.token)
                putString(PHONE, bean.phone)
                putBoolean(LOGIN_STATUS, true)
            }
            editor.apply()
        }

        fun getCliId(): String? = getPreferences().getString(CLIENT_ID, "")

        fun getToken(): String? = getPreferences().getString(TOKEN, "")

        fun getPhone(): String? = getPreferences().getString(PHONE, "")

        fun isLogin(): Boolean = getPreferences().getBoolean(LOGIN_STATUS, false)

        fun clear() {
            getPreferences().edit().clear().apply()
        }
    }
}