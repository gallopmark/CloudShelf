package com.holike.cloudshelf.util

import cn.jpush.android.api.JPushInterface
import com.holike.cloudshelf.BuildConfig
import com.holike.cloudshelf.CurrentApp

//极光相关
class JPushUtils private constructor() {

    companion object {
        private const val SEQUENCE = 100

        fun init() {
            JPushInterface.setDebugMode(BuildConfig.LOG_DEBUG)
            JPushInterface.init(CurrentApp.getInstance())
        }

        //设置别名
        fun setAlias() {
            val deviceId = AppUtils.getDeviceUUid(CurrentApp.getInstance())?.replace(":", "")?.replace("-", "_")
            //登录成功之后 极光推送设置别名
            JPushInterface.setAlias(CurrentApp.getInstance(), SEQUENCE, deviceId)
        }

        //删除别名
        @Suppress("unused")
        fun deleteAlias() {
            JPushInterface.deleteAlias(CurrentApp.getInstance(), SEQUENCE)
        }
    }
}