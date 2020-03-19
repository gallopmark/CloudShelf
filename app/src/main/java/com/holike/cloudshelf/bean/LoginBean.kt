package com.holike.cloudshelf.bean

import com.google.gson.annotations.SerializedName

//登录返回的数据
class LoginBean {
    @SerializedName("cliId")
    var cliId: String? = null

    @SerializedName("token")
    var token: String? = null

    var phone: String? = null  //当前登录用户的手机号（非接口返回的字段）
}