package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

//版本信息数据
class VersionInfoBean {
    @SerializedName("actualversion")
    var actualVersion: String? = null  //版本号 如1.0.0

    @SerializedName("version")
    var version: String? = null  //版本 如100

    @SerializedName("updatepath")
    var updatePath: String? = null //下载链接

    @SerializedName("updatetips")
    var updateTips: String? = null //版本更新提示

    fun obtainVersion():Long{
        val v = version
        if(v.isNullOrEmpty()) return 0
        return try {
            v.toLong()
        } catch (e: Exception) {
            0
        }
    }
}